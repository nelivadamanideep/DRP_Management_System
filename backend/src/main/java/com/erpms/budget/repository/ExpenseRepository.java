package com.erpms.budget.repository;

import com.erpms.budget.entity.ExpenseEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, String> {
    List<ExpenseEntity> findByProjectIdOrderByExpenseDateDesc(String projectId);
    List<ExpenseEntity> findByAllocationId(String allocationId);
}
