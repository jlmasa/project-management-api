package com.jlmasa.pms_backend.task.service;

import jakarta.transaction.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TaskLifecycleService {

    @Transactional
    public void closeTask(java.util.UUID taskId, java.util.UUID actorMembershipId){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {

            }
        });
    }
}
