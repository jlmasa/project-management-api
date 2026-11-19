package com.jlmasa.pms_backend.messaging.policy;

import com.jlmasa.pms_backend.assignment.repository.TaskAssignmentRepository;
import com.jlmasa.pms_backend.identity.domain.Department;
import com.jlmasa.pms_backend.identity.repository.OrganizationMembershipRepository;
import com.jlmasa.pms_backend.messaging.domain.Conversation;
import com.jlmasa.pms_backend.messaging.domain.ConversationStatus;
import com.jlmasa.pms_backend.messaging.domain.ConversationType;
import com.jlmasa.pms_backend.messaging.repository.ConversationParticipantRepository;
import com.jlmasa.pms_backend.project.domain.ProjectRole;
import com.jlmasa.pms_backend.project.repository.ProjectMemberRepository;
import com.jlmasa.pms_backend.security.authz.PermissionService;
import com.jlmasa.pms_backend.task.domain.Task;
import com.jlmasa.pms_backend.task.domain.TaskStatus;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MessagingPolicy {

    private final OrganizationMembershipRepository membershipRepository;
    private final ConversationParticipantRepository participantRepository;
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PermissionService permissionService;

    public MessagingPolicy(OrganizationMembershipRepository membershipRepository,
                           ConversationParticipantRepository participantRepository, TaskRepository taskRepository,
                           TaskAssignmentRepository taskAssignmentRepository, ProjectMemberRepository projectMemberRepository,
                           PermissionService permissionService){
        this.membershipRepository = membershipRepository;
        this.participantRepository = participantRepository;
        this.taskRepository = taskRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.permissionService = permissionService;
    }

    public boolean sameDepartment(UUID membershipIdA, UUID membershipIdB){
        Department a = membershipRepository.findById(membershipIdA).orElseThrow().getDepartment();
        Department b = membershipRepository.findById(membershipIdB).orElseThrow().getDepartment();
        return a != null && b != null && a.getId().equals(b.getId());
    }

    public MessagingDecision canCreate(UUID callerMembershipId, ConversationType type, List<UUID> participantMembershipIds){
        if(type == ConversationType.TASK){
            throw new IllegalArgumentException("Use canCreateTaskConversation for TASK type.");
        }
        boolean allSameDepartment = participantMembershipIds.stream().allMatch(other -> other.equals(callerMembershipId) || sameDepartment(callerMembershipId, other));
        return allSameDepartment ? MessagingDecision.ALLOW : MessagingDecision.deny(MessagingDecision.CROSS_DEPARTMENT_FORBIDDEN);
    }

    public MessagingDecision canCreateTaskConversation(UUID callerMembershipId, UUID taskId){
        Task task = taskRepository.findById(taskId).orElse(null);
        if(task == null){
            return MessagingDecision.deny(MessagingDecision.NOT_TASK_AUTHORIZED);
        }
        if(task.getTaskStatus() == TaskStatus.COMPLETED || task.getTaskStatus() == TaskStatus.CANCELLED){
            return MessagingDecision.deny(MessagingDecision.TASK_CLOSED);
        }
        return authorizedSet(taskId).contains(callerMembershipId) ? MessagingDecision.ALLOW : MessagingDecision.deny(MessagingDecision.NOT_TASK_AUTHORIZED);
    }

    public MessagingDecision canSend(UUID callerMembershipId, Conversation conversation){
        if (conversation.getConversationStatus() != ConversationStatus.ACTIVE){
            return MessagingDecision.deny(MessagingDecision.CONVERSATION_CLOSED );
        }
        boolean isActiveParticipant = participantRepository.findActive(conversation.getId(), callerMembershipId).isPresent();
        if(!isActiveParticipant){
            return MessagingDecision.deny(MessagingDecision.NOT_PARTICIPANT);
        }
        if(conversation.isTaskConversation()){
            Task task = taskRepository.findById(conversation.getTaskId()).orElseThrow();
            if(task.getTaskStatus() == TaskStatus.COMPLETED || task.getTaskStatus() == TaskStatus.CANCELLED){
                return MessagingDecision.deny(MessagingDecision.TASK_CLOSED);
            }
            if (!authorizedSet(task.getId()).contains(callerMembershipId)){
                return MessagingDecision.deny(MessagingDecision.NOT_TASK_AUTHORIZED);
            }
        }
        return MessagingDecision.ALLOW;
    }
    public MessagingDecision canRead(UUID callerMembershipId, Conversation conversation){
        boolean everParticipant = participantRepository.findActive(conversation.getId(), callerMembershipId).isPresent();
        if(everParticipant){
            return MessagingDecision.ALLOW;
        }
        return permissionService.hasPermission(callerMembershipId, "MESSAGE_ADMIN_ACCESS") ? MessagingDecision.ALLOW : MessagingDecision.deny(MessagingDecision.NOT_PARTICIPANT);
    }

    public MessagingDecision canMention(UUID targetMembershipId, Conversation conversation){
        if (!conversation.isTaskConversation()){
            boolean isParticipant = participantRepository.findActive(conversation.getId(), targetMembershipId).isPresent();
            return isParticipant ? MessagingDecision.ALLOW : MessagingDecision.deny(MessagingDecision.NOT_PARTICIPANT);
        }
        return authorizedSet(conversation.getTaskId()).contains(targetMembershipId) ? MessagingDecision.ALLOW : MessagingDecision.deny(MessagingDecision.NOT_TASK_AUTHORIZED);
    }
    public Set<UUID> authorizedSet(UUID taskId){
        Task task = taskRepository.findById(taskId).orElseThrow();
        Set<UUID> result = new java.util.HashSet<>();
        taskAssignmentRepository.findByTaskIdAndEndedAtIsNull(taskId).ifPresent(a -> result.add(a.getAssignedByMembershipId()));
        projectMemberRepository.findByProjectIdAndRemovedAtIsNull(task.getProjectId()).stream().filter(pm -> pm.getProjectRole() ==  ProjectRole.OWNER || pm.getProjectRole() == ProjectRole.MANAGER).forEach(pm -> result.add(pm.getOrganizationMembershipId()));
        return result;
    }

}
