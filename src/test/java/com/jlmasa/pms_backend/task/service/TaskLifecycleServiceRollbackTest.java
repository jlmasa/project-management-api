package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.TestcontainersConfiguration;
import com.jlmasa.pms_backend.task.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class TaskLifecycleServiceRollbackTest {

    @Autowired TaskLifecycleService taskLifecycleService;
    @Autowired TaskRepository taskRepository;

    @Test
    void aFailureInsideCloseRollsBackTheTaskStatusToo(){

    }
}
