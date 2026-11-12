package com.jlmasa.pms_backend.task.service;

import com.jlmasa.pms_backend.task.domain.TaskStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TaskTransitionTableTest {

    @ParameterizedTest
    @CsvSource({
            "BACKLOG, TO_DO, true", "BACKLOG, IN_PROGRESS, false",
            "TO_DO, IN_PROGRESS, true", "TO_DO, COMPLETED, false",
            "IN_REVIEW, COMPLETED, true",   "IN_REVIEW, TO_DO, false",
            "COMPLETED, TO_DO, true",   "COMPLETED, IN_PROGRESS, false",
            "CANCELLED, TO_DO, true",   "CANCELLED, IN_REVIEW, false"
    })
    void matchesTheWorkbookTable(TaskStatus from, TaskStatus to, boolean expectedAllowed){
        assertThat(TaskTransitionTable.isAllowed(from,to)).isEqualTo(expectedAllowed);
    }
}

