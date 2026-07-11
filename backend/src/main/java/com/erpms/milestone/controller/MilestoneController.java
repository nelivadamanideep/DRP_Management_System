package com.erpms.milestone.controller;

import com.erpms.milestone.dto.MilestoneRequest;
import com.erpms.milestone.dto.MilestoneResponse;
import com.erpms.milestone.service.MilestoneService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/milestones")
public class MilestoneController {

    private final MilestoneService service;

    public MilestoneController(MilestoneService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MilestoneResponse create(@Valid @RequestBody MilestoneRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<MilestoneResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MilestoneResponse findById(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping("/project/{projectId}")
    public List<MilestoneResponse> findByProjectId(@PathVariable String projectId) {
        return service.findByProjectId(projectId);
    }

    @PutMapping("/{id}")
    public MilestoneResponse update(@PathVariable String id, @Valid @RequestBody MilestoneRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}