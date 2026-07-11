package com.erpms.task.repository;

import com.erpms.task.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByProjectId(String projectId);
    List<Task> findByMilestoneId(String milestoneId);
    List<Task> findByAssignedToUserId(String assignedToUserId);
    long countByStatus(String status);
    long countByAssignedToUserIdAndStatus(String userId, String status);
}
