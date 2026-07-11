package com.erpms.user.service;

import com.erpms.user.dto.*;
import com.erpms.user.entity.UserAccount;
import com.erpms.user.entity.UserProfile;
import com.erpms.user.repository.UserAccountRepository;
import com.erpms.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {

    private final UserAccountRepository userRepository;
    private final UserProfileRepository profileRepository;

    public UserManagementService(
            UserAccountRepository userRepository,
            UserProfileRepository profileRepository
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream().map(this::toUserResponse).toList();
    }

    public UserResponse findUserById(String id) {
        return toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    public UserResponse updateRole(String id, UpdateUserRoleRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(request.role().trim().toUpperCase());
        return toUserResponse(userRepository.save(user));
    }

    public UserResponse updateStatus(String id, UpdateUserStatusRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(request.status().trim().toUpperCase());
        return toUserResponse(userRepository.save(user));
    }

    public UserProfileResponse getProfile(String userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found"));

        return toProfileResponse(profile);
    }

    public UserProfileResponse upsertProfile(String userId, UserProfileRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(UserProfile::new);

        profile.setUserId(userId);
        profile.setDesignation(request.designation());
        profile.setDepartmentId(request.departmentId());
        profile.setExperienceYears(request.experienceYears() == null ? 0 : request.experienceYears());
        profile.setSkills(request.skills());
        profile.setCertifications(request.certifications());
        profile.setPhone(request.phone());

        return toProfileResponse(profileRepository.save(profile));
    }

    private UserResponse toUserResponse(UserAccount user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getStatus()
        );
    }

    private UserProfileResponse toProfileResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getDesignation(),
                profile.getDepartmentId(),
                profile.getExperienceYears(),
                profile.getSkills(),
                profile.getCertifications(),
                profile.getPhone()
        );
    }
}