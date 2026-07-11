package com.erpms.team.repository;

import com.erpms.team.entity.ProjectTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTeamMemberRepository extends JpaRepository<ProjectTeamMember, String> {
    List<ProjectTeamMember> findByProjectId(String projectId);
    List<ProjectTeamMember> findByUserId(String userId);
    boolean existsByProjectIdAndUserId(String projectId, String userId);
}