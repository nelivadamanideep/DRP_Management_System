package com.erpms.budget.service;

import com.erpms.budget.dto.*;
import com.erpms.budget.entity.BudgetAllocationEntity;
import com.erpms.budget.entity.ExpenseEntity;
import com.erpms.budget.repository.BudgetAllocationRepository;
import com.erpms.budget.repository.ExpenseRepository;
import com.erpms.common.exception.BusinessRuleException;
import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.common.security.SecurityUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {

    private final BudgetAllocationRepository allocationRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(BudgetAllocationRepository a, ExpenseRepository e) {
        this.allocationRepository = a;
        this.expenseRepository = e;
    }

    // ---- Allocations ----------------------------------------------------

    @Transactional
    public BudgetAllocationResponse allocate(BudgetAllocationRequest req) {
        BudgetAllocationEntity e = new BudgetAllocationEntity();
        e.setProjectId(req.projectId());
        e.setFiscalYear(req.fiscalYear());
        e.setCategory(req.category().trim().toUpperCase());
        e.setAllocatedAmount(req.allocatedAmount());
        e.setSpentAmount(BigDecimal.ZERO);
        e.setNotes(req.notes());
        return toResponse(allocationRepository.save(e));
    }

    @Transactional(readOnly = true)
    public List<BudgetAllocationResponse> listByProject(String projectId) {
        return allocationRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BudgetSummaryResponse summarize(String projectId, Integer fiscalYear) {
        int year = fiscalYear == null ? LocalDate.now().getYear() : fiscalYear;
        List<BudgetAllocationEntity> allocations = allocationRepository.findByProjectIdAndFiscalYear(projectId, year);
        BigDecimal allocated = allocations.stream().map(BudgetAllocationEntity::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal spent = allocations.stream().map(BudgetAllocationEntity::getSpentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new BudgetSummaryResponse(
                projectId, year, allocated, spent, allocated.subtract(spent),
                allocations.stream().map(this::toResponse).toList()
        );
    }

    // ---- Expenses -------------------------------------------------------

    @Transactional
    public ExpenseResponse recordExpense(ExpenseRequest req) {
        ExpenseEntity e = new ExpenseEntity();
        e.setProjectId(req.projectId());
        e.setAllocationId(req.allocationId());
        e.setCategory(req.category().trim().toUpperCase());
        e.setAmount(req.amount());
        e.setExpenseDate(req.expenseDate() == null ? LocalDate.now() : req.expenseDate());
        e.setDescription(req.description());
        e.setRecordedByUserId(SecurityUtils.currentUserIdOrNull());

        if (req.allocationId() != null) {
            BudgetAllocationEntity alloc = allocationRepository.findById(req.allocationId())
                    .orElseThrow(() -> ResourceNotFoundException.of("BudgetAllocation", req.allocationId()));
            BigDecimal projected = alloc.getSpentAmount().add(req.amount());
            if (projected.compareTo(alloc.getAllocatedAmount()) > 0) {
                throw new BusinessRuleException("Expense would exceed budget allocation");
            }
            alloc.setSpentAmount(projected);
            allocationRepository.save(alloc);
        }
        ExpenseEntity saved = expenseRepository.save(e);
        return new ExpenseResponse(saved.getId(), saved.getProjectId(), saved.getAllocationId(),
                saved.getCategory(), saved.getAmount(), saved.getExpenseDate(),
                saved.getDescription(), saved.getRecordedByUserId());
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> listExpenses(String projectId) {
        return expenseRepository.findByProjectIdOrderByExpenseDateDesc(projectId).stream()
                .map(e -> new ExpenseResponse(e.getId(), e.getProjectId(), e.getAllocationId(),
                        e.getCategory(), e.getAmount(), e.getExpenseDate(), e.getDescription(),
                        e.getRecordedByUserId()))
                .toList();
    }

    private BudgetAllocationResponse toResponse(BudgetAllocationEntity a) {
        BigDecimal remaining = a.getAllocatedAmount().subtract(a.getSpentAmount());
        return new BudgetAllocationResponse(a.getId(), a.getProjectId(), a.getFiscalYear(),
                a.getCategory(), a.getAllocatedAmount(), a.getSpentAmount(), remaining, a.getNotes());
    }
}
