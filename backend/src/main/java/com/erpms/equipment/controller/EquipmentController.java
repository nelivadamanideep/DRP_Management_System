package com.erpms.equipment.controller;

import com.erpms.equipment.dto.*;
import com.erpms.equipment.service.EquipmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/equipment")
@Tag(name = "Equipment", description = "Equipment inventory, bookings, maintenance and calibration")
@SecurityRequirement(name = "bearerAuth")
public class EquipmentController {

    private final EquipmentService service;

    public EquipmentController(EquipmentService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LABORATORY_MANAGER','DEPARTMENT_HEAD')")
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentResponse create(@Valid @RequestBody EquipmentRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<EquipmentResponse> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public EquipmentResponse findById(@PathVariable String id) { return service.findById(id); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LABORATORY_MANAGER','DEPARTMENT_HEAD')")
    public EquipmentResponse update(@PathVariable String id, @Valid @RequestBody EquipmentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) { service.delete(id); }

    // ---- Bookings ------------------------------------------------------

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@Valid @RequestBody BookingRequest request) {
        return service.book(request);
    }

    @PostMapping("/bookings/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable String id) {
        return service.cancelBooking(id);
    }

    @GetMapping("/{id}/bookings")
    public List<BookingResponse> listBookings(@PathVariable String id) {
        return service.listBookings(id);
    }

    // ---- Maintenance ---------------------------------------------------

    @PostMapping("/maintenance")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LABORATORY_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceResponse logMaintenance(@Valid @RequestBody MaintenanceRequest request) {
        return service.logMaintenance(request);
    }

    @GetMapping("/{id}/maintenance")
    public List<MaintenanceResponse> listMaintenance(@PathVariable String id) {
        return service.listMaintenance(id);
    }
}
