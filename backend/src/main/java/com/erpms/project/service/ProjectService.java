package com.erpms.project.service;

import com.erpms.project.dto.ProjectRequest;
import com.erpms.project.dto.ProjectResponse;
import com.erpms.project.entity.Project;
import com.erpms.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public ProjectResponse create(ProjectRequest request) {
        if (repository.existsByProjectCodeIgnoreCase(request.projectCode())) {
            throw new IllegalArgumentException("Project code already exists");
        }

        Project project = new Project();
        applyRequest(project, request);
        project.setProjectCode(request.projectCode().trim().toUpperCase());

        return toResponse(repository.save(project));
    }

    public List<ProjectResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ProjectResponse findById(String id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found")));
    }

    public ProjectResponse update(String id, ProjectRequest request) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        applyRequest(project, request);
        return toResponse(repository.save(project));
    }

    public void delete(String id) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        repository.delete(project);
    }

    private void applyRequest(Project project, ProjectRequest request) {
        project.setTitle(request.title().trim());
        project.setSummary(request.summary());
        project.setDepartmentId(request.departmentId());
        project.setPriority(request.priority().trim().toUpperCase());
        project.setRiskLevel(request.riskLevel().trim().toUpperCase());
        project.setStatus(request.status().trim().toUpperCase());
        project.setPlannedStartDate(request.plannedStartDate());
        project.setPlannedEndDate(request.plannedEndDate());
        project.setApprovedBudget(request.approvedBudget() == null ? BigDecimal.ZERO : request.approvedBudget());
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getProjectCode(),
                project.getTitle(),
                project.getSummary(),
                project.getDepartmentId(),
                project.getPriority(),
                project.getRiskLevel(),
                project.getStatus(),
                project.getPlannedStartDate(),
                project.getPlannedEndDate(),
                project.getApprovedBudget()
        );
    }
}