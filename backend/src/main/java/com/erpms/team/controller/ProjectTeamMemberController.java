package com.erpms.team.controller;

import com.erpms.team.dto.ProjectTeamMemberRequest;
import com.erpms.team.dto.ProjectTeamMemberResponse;
import com.erpms.team.service.ProjectTeamMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-teams")
public class ProjectTeamMemberController {

    private final ProjectTeamMemberService service;

    public ProjectTeamMemberController(ProjectTeamMemberService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectTeamMemberResponse create(@Valid @RequestBody ProjectTeamMemberRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ProjectTeamMemberResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/project/{projectId}")
    public List<ProjectTeamMemberResponse> findByProjectId(@PathVariable String projectId) {
        return service.findByProjectId(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<ProjectTeamMemberResponse> findByUserId(@PathVariable String userId) {
        return service.findByUserId(userId);
    }

    @PutMapping("/{id}")
    public ProjectTeamMemberResponse update(
            @PathVariable String id,
            @Valid @RequestBody ProjectTeamMemberRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}