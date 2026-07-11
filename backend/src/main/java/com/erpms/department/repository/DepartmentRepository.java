package com.erpms.department.repository;

import com.erpms.department.entity.Department;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {
    boolean existsByCodeIgnoreCase(String code);
    Optional<Department> findByCodeIgnoreCase(String code);
    long countByActiveTrue();
}
