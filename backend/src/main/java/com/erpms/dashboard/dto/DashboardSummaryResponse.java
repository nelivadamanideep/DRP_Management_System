package com.erpms.dashboard.dto;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardSummaryResponse(
        long totalProjects,
        Map<String, Long> projectsByStatus,
        long totalUsers,
        long activeDepartments,
        long totalTasks,
        long openTasks,
        long completedTasks,
        long myOpenTasks,
        long totalEquipment,
        long availableEquipment,
        long lowStockItems,
        long pendingPurchaseRequests,
        long unreadNotifications,
        BigDecimal totalApprovedBudget,
        BigDecimal totalExpenses
) {}
