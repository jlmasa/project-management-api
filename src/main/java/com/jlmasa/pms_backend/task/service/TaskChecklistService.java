package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.domain.TaskChecklist;
import com.jlmasa.pms_backend.task.domain.TaskChecklistItem;
import com.jlmasa.pms_backend.task.repository.TaskChecklistItemRepository;
import com.jlmasa.pms_backend.task.repository.TaskChecklistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@Service
public class TaskChecklistService {

    private final TaskChecklistRepository checklistRepository;
    private final TaskChecklistItemRepository itemRepository;
    private final Clock clock;

    public TaskChecklistService(TaskChecklistRepository taskChecklistRepository, TaskChecklistItemRepository itemRepository, Clock clock ){
        this.checklistRepository = taskChecklistRepository;
        this.itemRepository = itemRepository;
        this.clock = clock;
    }

    @Transactional
    public TaskChecklist createCheckList(Task task, String title, int position){
        return checklistRepository.save(new TaskChecklist( task, title, position, clock.instant()));
    }

    @Transactional
    public TaskChecklistItem addItem(TaskChecklist checklist, String title, int position){
        return itemRepository.save(new TaskChecklistItem(checklist, title, position));
    }

    @Transactional
    public void CompleteItem(UUID itemId, UUID byMembershipId){
        TaskChecklistItem item = itemRepository.findById(itemId).orElseThrow();
        item.markCompleted(byMembershipId, clock.instant());
    }
}
