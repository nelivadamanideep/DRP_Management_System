package com.erpms.user.controller;

import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.common.security.SecurityUtils;
import com.erpms.user.dto.*;
import com.erpms.user.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User accounts, roles, statuses and profiles")
@SecurityRequirement(name = "bearerAuth")
public class UserManagementController {

    private final UserManagementService service;

    public UserManagementController(UserManagementService service) {
        this.service = service;
    }

    @GetMapping("/me")
    @Operation(summary = "Return the account of the currently-authenticated user")
    public UserResponse me() {
        String userId = SecurityUtils.currentUserIdOrNull();
        if (userId == null) throw ResourceNotFoundException.of("User", "current");
        return service.findUserById(userId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','DEPARTMENT_HEAD','PROJECT_DIRECTOR','AUDITOR')")
    @Operation(summary = "List every user")
    public List<UserResponse> findAllUsers() {
        return service.findAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a user by id")
    public UserResponse findUserById(@PathVariable String id) {
        return service.findUserById(id);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Change a user's role")
    public UserResponse updateRole(@PathVariable String id, @Valid @RequestBody UpdateUserRoleRequest request) {
        return service.updateRole(id, request);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Activate / suspend a user")
    public UserResponse updateStatus(@PathVariable String id, @Valid @RequestBody UpdateUserStatusRequest request) {
        return service.updateStatus(id, request);
    }

    @GetMapping("/{id}/profile")
    @Operation(summary = "Return the extended profile for a user")
    public UserProfileResponse getProfile(@PathVariable String id) {
        return service.getProfile(id);
    }

    @PutMapping("/{id}/profile")
    @Operation(summary = "Create or update the extended profile for a user")
    public UserProfileResponse upsertProfile(@PathVariable String id, @Valid @RequestBody UserProfileRequest request) {
        return service.upsertProfile(id, request);
    }
}
