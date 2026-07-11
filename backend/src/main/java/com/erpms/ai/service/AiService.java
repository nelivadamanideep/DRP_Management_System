package com.erpms.ai.service;

import com.erpms.ai.client.AnthropicClient;
import com.erpms.ai.dto.*;
import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.document.repository.DocumentRepository;
import com.erpms.project.entity.Project;
import com.erpms.project.repository.ProjectRepository;
import com.erpms.task.repository.TaskRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Wraps the {@link AnthropicClient} with ERPMS-aware prompt scaffolding.
 * Every method builds a system prompt tuned for the requested capability
 * (risk prediction, delay prediction, budget forecasting, chat, semantic
 * search assist, meeting summary) and returns a structured response.
 */
@Service
public class AiService {

    private final AnthropicClient anthropic;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final DocumentRepository documentRepository;

    public AiService(AnthropicClient anthropic,
                     ProjectRepository projectRepository,
                     TaskRepository taskRepository,
                     DocumentRepository documentRepository) {
        this.anthropic = anthropic;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.documentRepository = documentRepository;
    }

    // ---- Risk prediction ------------------------------------------------

    public AiInsightResponse predictRisk(String projectId) {
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", projectId));

        String prompt = """
                You are a senior programme manager at a research organisation. Analyse the
                supplied project record and return:
                1. A crisp risk assessment (LOW / MEDIUM / HIGH) with a one-sentence rationale.
                2. Three concrete mitigation actions the delivery team can take in the next sprint.
                Respond in Markdown with headings for each section.
                """;
        String context = """
                Project code: %s
                Title: %s
                Priority: %s
                Stated risk level: %s
                Status: %s
                Approved budget: %s
                Planned start: %s
                Planned end: %s
                Summary: %s
                """.formatted(p.getProjectCode(), p.getTitle(), p.getPriority(), p.getRiskLevel(),
                p.getStatus(), p.getApprovedBudget(), p.getPlannedStartDate(), p.getPlannedEndDate(),
                p.getSummary());

        String reply = anthropic.chat(prompt, context);
        return new AiInsightResponse("RISK_PREDICTION", projectId, reply);
    }

    // ---- Delay prediction ----------------------------------------------

    public AiInsightResponse predictDelay(String projectId) {
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", projectId));

        long openTasks = taskRepository.findByProjectId(projectId).stream()
                .filter(t -> !"DONE".equalsIgnoreCase(t.getStatus())
                          && !"COMPLETED".equalsIgnoreCase(t.getStatus()))
                .count();

        long daysToDeadline = p.getPlannedEndDate() == null ? -1
                : ChronoUnit.DAYS.between(LocalDate.now(), p.getPlannedEndDate());

        String prompt = """
                You are the delivery lead for a research organisation. Predict schedule
                risk and delay likelihood based on the numbers provided. Respond with:
                • Probability of missing the deadline (LOW / MEDIUM / HIGH).
                • Two leading indicators from the data.
                • One recommended intervention.
                """;
        String context = """
                Project '%s' (%s), status %s.
                Days until planned end date: %s.
                Open task count: %d.
                Approved budget: %s.
                """.formatted(p.getTitle(), p.getProjectCode(), p.getStatus(),
                daysToDeadline < 0 ? "n/a" : String.valueOf(daysToDeadline), openTasks, p.getApprovedBudget());

        return new AiInsightResponse("DELAY_PREDICTION", projectId, anthropic.chat(prompt, context));
    }

    // ---- Budget forecast ------------------------------------------------

    public AiInsightResponse forecastBudget(String projectId) {
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> ResourceNotFoundException.of("Project", projectId));

        String prompt = """
                Act as a finance controller. Given the approved budget and project meta,
                forecast whether the project is likely to over- or under-spend, and
                identify the top two cost drivers to watch. Keep the response under
                180 words and formatted in Markdown.
                """;
        String context = "Project '%s' (%s) — approved budget: %s. Priority: %s. Status: %s."
                .formatted(p.getTitle(), p.getProjectCode(), p.getApprovedBudget(), p.getPriority(), p.getStatus());
        return new AiInsightResponse("BUDGET_FORECAST", projectId, anthropic.chat(prompt, context));
    }

    // ---- Meeting summary -----------------------------------------------

    public AiInsightResponse summarizeMeeting(String transcript) {
        String prompt = """
                Summarise the meeting transcript below into:
                1. Key decisions (bullet list).
                2. Action items with owner and due-date guesses.
                3. Open questions.
                """;
        return new AiInsightResponse("MEETING_SUMMARY", null, anthropic.chat(prompt, transcript));
    }

    // ---- Chat (multi-turn) ---------------------------------------------

    public ChatResponse chat(ChatRequest request) {
        String system = """
                You are the AI assistant embedded in ERPMS, an Enterprise Research Project
                Management System. Answer questions about projects, tasks, budgets,
                equipment, procurement and audit. Be concise. If you don't know, say so.
                """;
        List<Map<String, String>> messages = request.messages().stream()
                .map(m -> Map.of("role", m.role().toLowerCase(Locale.ROOT), "content", m.content()))
                .toList();
        String reply = anthropic.chatMulti(system, messages);
        return new ChatResponse(reply);
    }

    // ---- Semantic search (very simple: title similarity via LLM) --------

    public AiInsightResponse semanticSearch(String query) {
        var docs = documentRepository.findAll();
        if (docs.isEmpty()) return new AiInsightResponse("SEMANTIC_SEARCH", null, "No documents in the library yet.");

        StringBuilder catalog = new StringBuilder();
        docs.stream().limit(50).forEach(d -> catalog.append("- ")
                .append(d.getId()).append(" :: ")
                .append(d.getTitle())
                .append(" [")
                .append(d.getDocumentType())
                .append("]\n"));

        String prompt = """
                Below is a catalog of documents (id :: title [type]). Given the user's
                query, return the five most relevant IDs, ranked, with a one-line reason
                for each. If nothing seems relevant, say so.
                """;
        String user = "Query: " + query + "\n\nCatalog:\n" + catalog;
        return new AiInsightResponse("SEMANTIC_SEARCH", null, anthropic.chat(prompt, user));
    }
}
