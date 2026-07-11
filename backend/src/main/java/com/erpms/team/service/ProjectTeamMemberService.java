package com.erpms.team.service;

import com.erpms.team.dto.ProjectTeamMemberRequest;
import com.erpms.team.dto.ProjectTeamMemberResponse;
import com.erpms.team.entity.ProjectTeamMember;
import com.erpms.team.repository.ProjectTeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTeamMemberService {

    private final ProjectTeamMemberRepository repository;

    public ProjectTeamMemberService(ProjectTeamMemberRepository repository) {
        this.repository = repository;
    }

    public ProjectTeamMemberResponse create(ProjectTeamMemberRequest request) {
        if (repository.existsByProjectIdAndUserId(request.projectId(), request.userId())) {
            throw new IllegalArgumentException("User is already assigned to this project");
        }

        ProjectTeamMember member = new ProjectTeamMember();
        applyRequest(member, request);
        return toResponse(repository.save(member));
    }

    public List<ProjectTeamMemberResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ProjectTeamMemberResponse> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    public List<ProjectTeamMemberResponse> findByUserId(String userId) {
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    public ProjectTeamMemberResponse update(String id, ProjectTeamMemberRequest request) {
        ProjectTeamMember member = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team member not found"));

        applyRequest(member, request);
        return toResponse(repository.save(member));
    }

    public void delete(String id) {
        ProjectTeamMember member = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team member not found"));

        repository.delete(member);
    }

    private void applyRequest(ProjectTeamMember member, ProjectTeamMemberRequest request) {
        member.setProjectId(request.projectId());
        member.setUserId(request.userId());
        member.setRoleInProject(request.roleInProject().trim().toUpperCase());
        member.setAllocationPercent(request.allocationPercent());
        member.setActive(request.active() == null || request.active());
    }

    private ProjectTeamMemberResponse toResponse(ProjectTeamMember member) {
        return new ProjectTeamMemberResponse(
                member.getId(),
                member.getProjectId(),
                member.getUserId(),
                member.getRoleInProject(),
                member.getAllocationPercent(),
                member.getActive()
        );
    }
}