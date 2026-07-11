package com.erpms.ai.controller;

import com.erpms.ai.dto.AiInsightResponse;
import com.erpms.ai.dto.ChatRequest;
import com.erpms.ai.dto.ChatResponse;
import com.erpms.ai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI", description = "Claude-powered insights: risk, delay, budget, chat, semantic search, summaries")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiService service;

    public AiController(AiService service) {
        this.service = service;
    }

    @GetMapping("/projects/{id}/risk")
    @Operation(summary = "Predict project risk and mitigation actions")
    public AiInsightResponse risk(@PathVariable String id) {
        return service.predictRisk(id);
    }

    @GetMapping("/projects/{id}/delay")
    @Operation(summary = "Predict schedule delay likelihood for a project")
    public AiInsightResponse delay(@PathVariable String id) {
        return service.predictDelay(id);
    }

    @GetMapping("/projects/{id}/budget-forecast")
    @Operation(summary = "Forecast whether a project will over- or under-spend its budget")
    public AiInsightResponse budgetForecast(@PathVariable String id) {
        return service.forecastBudget(id);
    }

    @PostMapping("/chat")
    @Operation(summary = "Multi-turn chat with the ERPMS assistant")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return service.chat(request);
    }

    @PostMapping("/meeting-summary")
    @Operation(summary = "Summarise a meeting transcript into decisions, actions and open questions")
    public AiInsightResponse summarizeMeeting(@RequestBody Map<String, String> body) {
        return service.summarizeMeeting(body.getOrDefault("transcript", ""));
    }

    @GetMapping("/semantic-search")
    @Operation(summary = "Rank documents by relevance to a natural-language query")
    public AiInsightResponse semanticSearch(@RequestParam("q") String query) {
        return service.semanticSearch(query);
    }
}
