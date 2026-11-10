package com.jlmasa.pms_backend.project.web;

import com.jlmasa.pms_backend.project.domain.Project;
import com.jlmasa.pms_backend.project.dto.ProjectResponse;

public final class ProjectMapper {

    private ProjectMapper() {

    }
    public static ProjectResponse toResponse(Project project){
        return new ProjectResponse(project.getId(),project.getName(), project.getCode(),project.getProjectStatus(), project.getStartDate(), project.getDueDate(),project.getCreatedAt());
    }
}
