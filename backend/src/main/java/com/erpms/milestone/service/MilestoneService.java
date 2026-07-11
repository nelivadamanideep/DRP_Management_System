package com.erpms.milestone.service;

import com.erpms.milestone.dto.MilestoneRequest;
import com.erpms.milestone.dto.MilestoneResponse;
import com.erpms.milestone.entity.Milestone;
import com.erpms.milestone.repository.MilestoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MilestoneService {

    private final MilestoneRepository repository;

    public MilestoneService(MilestoneRepository repository) {
        this.repository = repository;
    }

    public MilestoneResponse create(MilestoneRequest request) {
        Milestone milestone = new Milestone();
        applyRequest(milestone, request);
        return toResponse(repository.save(milestone));
    }

    public List<MilestoneResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public List<MilestoneResponse> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    public MilestoneResponse findById(String id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found")));
    }

    public MilestoneResponse update(String id, MilestoneRequest request) {
        Milestone milestone = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        applyRequest(milestone, request);
        return toResponse(repository.save(milestone));
    }

    public void delete(String id) {
        Milestone milestone = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        repository.delete(milestone);
    }

    private void applyRequest(Milestone milestone, MilestoneRequest request) {
        milestone.setProjectId(request.projectId());
        milestone.setName(request.name().trim());
        milestone.setDescription(request.description());
        milestone.setDueDate(request.dueDate());
        milestone.setProgressPercent(request.progressPercent());
        milestone.setStatus(request.status().trim().toUpperCase());
    }

    private MilestoneResponse toResponse(Milestone milestone) {
        return new MilestoneResponse(
                milestone.getId(),
                milestone.getProjectId(),
                milestone.getName(),
                milestone.getDescription(),
                milestone.getDueDate(),
                milestone.getProgressPercent(),
                milestone.getStatus()
        );
    }
}