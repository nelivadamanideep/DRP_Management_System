package com.erpms.project.controller;

import com.erpms.project.dto.ProjectRequest;
import com.erpms.project.dto.ProjectResponse;
import com.erpms.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ProjectResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable String id, @Valid @RequestBody ProjectRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}