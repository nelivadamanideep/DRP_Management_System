package com.erpms.dashboard.service;

import com.erpms.budget.entity.ExpenseEntity;
import com.erpms.budget.repository.ExpenseRepository;
import com.erpms.common.security.SecurityUtils;
import com.erpms.dashboard.dto.DashboardSummaryResponse;
import com.erpms.department.repository.DepartmentRepository;
import com.erpms.equipment.repository.EquipmentRepository;
import com.erpms.inventory.repository.InventoryItemRepository;
import com.erpms.notification.repository.NotificationRepository;
import com.erpms.procurement.repository.PurchaseRequestRepository;
import com.erpms.project.entity.Project;
import com.erpms.project.repository.ProjectRepository;
import com.erpms.task.repository.TaskRepository;
import com.erpms.user.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aggregates cross-module counts for the home dashboard.
 *
 * <p>Everything is done in a single read-only transaction so the numbers on
 * the dashboard are internally consistent.
 */
@Service
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserAccountRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final EquipmentRepository equipmentRepository;
    private final InventoryItemRepository inventoryRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final NotificationRepository notificationRepository;
    private final ExpenseRepository expenseRepository;

    public DashboardService(ProjectRepository p, TaskRepository t, UserAccountRepository u,
                            DepartmentRepository d, EquipmentRepository eq, InventoryItemRepository i,
                            PurchaseRequestRepository pr, NotificationRepository n, ExpenseRepository ex) {
        this.projectRepository = p;
        this.taskRepository = t;
        this.userRepository = u;
        this.departmentRepository = d;
        this.equipmentRepository = eq;
        this.inventoryRepository = i;
        this.purchaseRequestRepository = pr;
        this.notificationRepository = n;
        this.expenseRepository = ex;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary() {
        long totalProjects = projectRepository.count();

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (String s : new String[]{"PLANNED", "IN_PROGRESS", "ON_HOLD", "COMPLETED", "CANCELLED"}) {
            byStatus.put(s, projectRepository.countByStatus(s));
        }

        long totalTasks = taskRepository.count();
        long openTasks = totalTasks
                - taskRepository.countByStatus("DONE")
                - taskRepository.countByStatus("COMPLETED");
        long completedTasks = taskRepository.countByStatus("DONE") + taskRepository.countByStatus("COMPLETED");

        String userId = SecurityUtils.currentUserIdOrNull();
        long myOpen = 0;
        if (userId != null) {
            myOpen = taskRepository.findByAssignedToUserId(userId).stream()
                    .filter(t -> !"DONE".equalsIgnoreCase(t.getStatus())
                              && !"COMPLETED".equalsIgnoreCase(t.getStatus()))
                    .count();
        }

        BigDecimal totalBudget = projectRepository.findAll().stream()
                .map(Project::getApprovedBudget)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpenses = expenseRepository.findAll().stream()
                .map(ExpenseEntity::getAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long unread = userId == null ? 0 : notificationRepository.countByRecipientUserIdAndReadFalse(userId);

        return new DashboardSummaryResponse(
                totalProjects,
                byStatus,
                userRepository.count(),
                departmentRepository.countByActiveTrue(),
                totalTasks,
                openTasks,
                completedTasks,
                myOpen,
                equipmentRepository.count(),
                equipmentRepository.countByStatus("AVAILABLE"),
                inventoryRepository.findLowStock().size(),
                purchaseRequestRepository.findByStatus("SUBMITTED").size(),
                unread,
                totalBudget,
                totalExpenses
        );
    }
}
