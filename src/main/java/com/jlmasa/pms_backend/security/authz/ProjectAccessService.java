package com.jlmasa.pms_backend.security.authz;

import com.jlmasa.pms_backend.project.domain.ProjectRole;
import com.jlmasa.pms_backend.project.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectAccessService {

    private final ProjectMemberRepository projectMemberRepository;
    private final PermissionService permissionService;

    public ProjectAccessService(ProjectMemberRepository projectMemberRepository, PermissionService permissionService){
        this.projectMemberRepository = projectMemberRepository;
        this.permissionService = permissionService;
    }

    public Optional<ProjectRole> activeRole(UUID projectId, UUID membershipId){
        return projectMemberRepository.findActiveRole(projectId, membershipId);
    }

    public boolean canView(UUID projectId, UUID membershipId){
        return activeRole(projectId, membershipId).isPresent() || permissionService.hasPermission(membershipId, "PROJECT_VIEW_ALL");
    }
    public boolean canManage(UUID projectId, UUID membershipId){
        return activeRole(projectId, membershipId)
                .map(role -> role == ProjectRole.OWNER || role== ProjectRole.MANAGER).orElse(false);
    }
}
