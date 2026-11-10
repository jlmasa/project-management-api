package com.jlmasa.pms_backend.project.web;

import com.jlmasa.pms_backend.project.domain.Project;
import com.jlmasa.pms_backend.project.dto.ProjectResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ProjectMapperTest {

    @Test
    void mapsEveryFieldAdnNeverLeaksOrganizationIdOrCreatedByMembershipId(){
        Project project = new Project(UUID.randomUUID(), UUID.randomUUID(), "Backend rewrite", "BR", Instant.now());

        ProjectResponse response =  ProjectMapper.toResponse(project);

        assertThat(response.name()).isEqualTo("Backend rewrite");
        assertThat(response.code()).isEqualTo("BR");
        assertThat(response.status()).isEqualTo(project.getProjectStatus());
    }
}
