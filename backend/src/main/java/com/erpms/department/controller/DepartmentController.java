package com.erpms.department.controller;

import com.erpms.department.dto.DepartmentRequest;
import com.erpms.department.dto.DepartmentResponse;
import com.erpms.department.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<DepartmentResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public DepartmentResponse findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public DepartmentResponse update(
            @PathVariable String id,
            @Valid @RequestBody DepartmentRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}