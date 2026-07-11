package com.erpms.task.service;

import com.erpms.task.dto.TaskRequest;
import com.erpms.task.dto.TaskResponse;
import com.erpms.task.entity.Task;
import com.erpms.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        applyRequest(task, request);
        return toResponse(repository.save(task));
    }

    public List<TaskResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public TaskResponse findById(String id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found")));
    }

    public List<TaskResponse> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    public List<TaskResponse> findByMilestoneId(String milestoneId) {
        return repository.findByMilestoneId(milestoneId).stream().map(this::toResponse).toList();
    }

    public List<TaskResponse> findByAssignedToUserId(String userId) {
        return repository.findByAssignedToUserId(userId).stream().map(this::toResponse).toList();
    }

    public TaskResponse update(String id, TaskRequest request) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        applyRequest(task, request);
        return toResponse(repository.save(task));
    }

    public void delete(String id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        repository.delete(task);
    }

    private void applyRequest(Task task, TaskRequest request) {
        task.setProjectId(request.projectId());
        task.setMilestoneId(request.milestoneId());
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setAssignedToUserId(request.assignedToUserId());
        task.setPriority(request.priority().trim().toUpperCase());
        task.setStatus(request.status().trim().toUpperCase());
        task.setDueDate(request.dueDate());
        task.setProgressPercent(request.progressPercent());
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProjectId(),
                task.getMilestoneId(),
                task.getTitle(),
                task.getDescription(),
                task.getAssignedToUserId(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getProgressPercent()
        );
    }
}