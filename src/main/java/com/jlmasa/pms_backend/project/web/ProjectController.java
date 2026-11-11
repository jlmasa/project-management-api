package com.jlmasa.pms_backend.project.web;

import com.jlmasa.pms_backend.common.PageResponse;
import com.jlmasa.pms_backend.project.domain.Project;
import com.jlmasa.pms_backend.project.dto.*;
import com.jlmasa.pms_backend.project.repository.ProjectRepository;
import com.jlmasa.pms_backend.project.service.ProjectService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectService projectService, ProjectRepository projectRepository){
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(PmsPrincipal principal, @Valid @RequestBody CreateProjectRequest request){
        return ProjectMapper.toResponse(projectService.create(principal, request.name(), request.code()));
    }

    @GetMapping("/{projectId}")
    public ProjectResponse get(PmsPrincipal principal, @PathVariable UUID projectId){
        return ProjectMapper.toResponse(projectService.getVisible(principal,projectId));
    }

    @GetMapping
    public PageResponse<ProjectResponse> list(PmsPrincipal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){
        Page<com.jlmasa.pms_backend.project.domain.Project> result = projectRepositoryVisiblePage(principal, page, size);
        return PageResponse.from(result,ProjectMapper::toResponse);
    }

    @PatchMapping("/{projectId}")
    public ProjectResponse update(PmsPrincipal principal, @PathVariable UUID projectId, @Valid @RequestBody UpdateProjectRequest request){
        return ProjectMapper.toResponse(projectService.update(principal, projectId, request.name(), request.description(), request.startDate(), request.dueDate()));
    }

    @PostMapping("/{projectId}/archive")
    public void archive(PmsPrincipal principal, @PathVariable UUID projectId){
        projectService.archive(principal,projectId);
    }

    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectMemberResponse addMember(PmsPrincipal principal, @PathVariable UUID projectId, @Valid @RequestBody AddProjectMemberRequest request){
        var member = projectService.addMember(principal, projectId, request.organizationMembershipId(),request.projectRole());
        return new ProjectMemberResponse(member.getOrganizationMembershipId(), member.getProjectRole(), member.getAddedAt());
    }
    @DeleteMapping("/{projectId}/members/{membershipId}")
    public void removeMember(PmsPrincipal principal, @PathVariable UUID projectId, @PathVariable UUID membershipId){
        projectService.removeMember(principal, projectId, membershipId);
    }

    private Page<com.jlmasa.pms_backend.project.domain.Project> projectRepositoryVisiblePage(PmsPrincipal principal, int page, int size){
        throw new UnsupportedOperationException("Inject ProjectRepository and call findVisibleToMembership");
    }
}
