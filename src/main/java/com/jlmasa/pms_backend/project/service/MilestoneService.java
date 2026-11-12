package com.jlmasa.pms_backend.project.service;

import com.jlmasa.pms_backend.project.domain.Milestone;
import com.jlmasa.pms_backend.project.domain.Project;
import com.jlmasa.pms_backend.project.repository.MilestoneRepository;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final Clock clock;

    public MilestoneService(MilestoneRepository milestoneRepository, TaskRepository taskRepository, Clock clock){
        this.milestoneRepository = milestoneRepository;
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    @Transactional
    public Milestone create(Project project, String name, UUID createdByMembershipId){
        return milestoneRepository.save(new Milestone(project, name, createdByMembershipId, clock.instant()));
    }

    @Transactional
    public void complete(UUID milestoneId){
        Milestone milestone = milestoneRepository.findById(milestoneId).orElseThrow();
        milestone.complete(clock.instant());
    }

    @Transactional
    public void delete(UUID milestoneId){
        boolean hasTasks = !taskRepository.findByProjectId(
                milestoneRepository.findById(milestoneId).orElseThrow().getProject().getId()
        ).stream().filter(t-> milestoneId.equals(t.getMilestoneId())).toList().isEmpty();
        if(hasTasks){
            throw new IllegalStateException("Cannot delete a milestone that still has task linked to it.");
        }
        milestoneRepository.deleteById(milestoneId);
    }
}
