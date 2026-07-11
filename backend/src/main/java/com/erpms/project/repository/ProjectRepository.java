package com.erpms.project.repository;

import com.erpms.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
    boolean existsByProjectCodeIgnoreCase(String projectCode);
    long countByStatus(String status);
    long countByDepartmentId(String departmentId);
}
