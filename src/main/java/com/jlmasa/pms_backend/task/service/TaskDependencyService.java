package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.domain.TaskDependency;
import com.jlmasa.pms_backend.task.repository.TaskDependencyRepository;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TaskDependencyService {

    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final java.time.Clock clock;

    public TaskDependencyService(TaskRepository taskRepository, TaskDependencyRepository taskDependencyRepository, java.time.Clock clock){
        this.taskRepository = taskRepository;
        this.taskDependencyRepository = taskDependencyRepository;
        this.clock = clock;
    }

    @Transactional
    public TaskDependency addDependency(UUID taskId, UUID dependsOnTaskId, UUID createdByMembershipId){
        Task task = taskRepository.findById(taskId).orElseThrow();
        Task dependsOn = taskRepository.findById(dependsOnTaskId).orElseThrow();
        if (!task.getProjectId().equals(dependsOn.getProjectId())){
            throw new IllegalArgumentException("Dependencies must stay within the same project.");
        }
        if(wouldCreateCycle(taskId, dependsOnTaskId)){
            throw new IllegalStateException("This dependency would create a cycle");
        }
        return taskDependencyRepository.save(new TaskDependency(task,dependsOn,createdByMembershipId,clock.instant()));
    }
    private boolean wouldCreateCycle(UUID taskId, UUID dependsOnTaskId){
        Set<UUID> visited = new HashSet<>();
        Deque<UUID> toVisit = new ArrayDeque<>();
        toVisit.push(dependsOnTaskId);
        while(!toVisit.isEmpty()){
            UUID current = toVisit.pop();
            if(current.equals(taskId)){
                return true;
            }
            if(!visited.add(current)){
                continue;
            }
            for(TaskDependency edge: taskDependencyRepository.findByTaskId(current)){
                toVisit.push(edge.getDependsOnTask().getId());
            }
        }
        return false;
    }
}
