package com.erpms.budget.controller;

import com.erpms.budget.dto.*;
import com.erpms.budget.service.BudgetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budgets")
@Tag(name = "Budget", description = "Budget allocations, expense recording and per-project financial summaries")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {

    private final BudgetService service;

    public BudgetController(BudgetService service) {
        this.service = service;
    }

    @PostMapping("/allocations")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','FINANCE_OFFICER','PROJECT_DIRECTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetAllocationResponse allocate(@Valid @RequestBody BudgetAllocationRequest r) {
        return service.allocate(r);
    }

    @GetMapping("/projects/{projectId}/allocations")
    public List<BudgetAllocationResponse> listAllocations(@PathVariable String projectId) {
        return service.listByProject(projectId);
    }

    @GetMapping("/projects/{projectId}/summary")
    public BudgetSummaryResponse summary(@PathVariable String projectId,
                                         @RequestParam(required = false) Integer fiscalYear) {
        return service.summarize(projectId, fiscalYear);
    }

    @PostMapping("/expenses")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','FINANCE_OFFICER','PROCUREMENT_OFFICER','PROJECT_DIRECTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse recordExpense(@Valid @RequestBody ExpenseRequest r) {
        return service.recordExpense(r);
    }

    @GetMapping("/projects/{projectId}/expenses")
    public List<ExpenseResponse> listExpenses(@PathVariable String projectId) {
        return service.listExpenses(projectId);
    }
}
