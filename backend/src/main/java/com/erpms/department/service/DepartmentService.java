package com.erpms.department.service;

import com.erpms.department.dto.DepartmentRequest;
import com.erpms.department.dto.DepartmentResponse;
import com.erpms.department.entity.Department;
import com.erpms.department.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    public DepartmentResponse create(DepartmentRequest request) {
        if (repository.existsByCodeIgnoreCase(request.code())) {
            throw new IllegalArgumentException("Department code already exists");
        }

        Department department = new Department();
        department.setCode(request.code().trim().toUpperCase());
        department.setName(request.name().trim());
        department.setDescription(request.description());
        department.setActive(request.active() == null || request.active());

        return toResponse(repository.save(department));
    }

    public List<DepartmentResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DepartmentResponse findById(String id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found")));
    }

    public DepartmentResponse update(String id, DepartmentRequest request) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        department.setName(request.name().trim());
        department.setDescription(request.description());

        if (request.active() != null) {
            department.setActive(request.active());
        }

        return toResponse(repository.save(department));
    }

    public void delete(String id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        repository.delete(department);
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getCode(),
                department.getName(),
                department.getDescription(),
                department.getActive()
        );
    }
}