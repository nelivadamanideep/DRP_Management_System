package com.erpms.budget.repository;

import com.erpms.budget.entity.BudgetAllocationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetAllocationRepository extends JpaRepository<BudgetAllocationEntity, String> {
    List<BudgetAllocationEntity> findByProjectId(String projectId);
    List<BudgetAllocationEntity> findByProjectIdAndFiscalYear(String projectId, int fiscalYear);
}
