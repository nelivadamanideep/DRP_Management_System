package com.erpms.task.controller;

import com.erpms.task.dto.TaskRequest;
import com.erpms.task.dto.TaskResponse;
import com.erpms.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<TaskResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping("/project/{projectId}")
    public List<TaskResponse> findByProjectId(@PathVariable String projectId) {
        return service.findByProjectId(projectId);
    }

    @GetMapping("/milestone/{milestoneId}")
    public List<TaskResponse> findByMilestoneId(@PathVariable String milestoneId) {
        return service.findByMilestoneId(milestoneId);
    }

    @GetMapping("/assigned/{userId}")
    public List<TaskResponse> findByAssignedToUserId(@PathVariable String userId) {
        return service.findByAssignedToUserId(userId);
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable String id, @Valid @RequestBody TaskRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}