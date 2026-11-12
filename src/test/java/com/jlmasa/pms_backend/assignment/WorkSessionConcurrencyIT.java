package com.jlmasa.pms_backend.assignment;

import com.jlmasa.pms_backend.assignment.domain.SessionStatus;
import com.jlmasa.pms_backend.assignment.repository.WorkSessionRepository;
import com.jlmasa.pms_backend.assignment.service.WorkSessionService;
import com.jlmasa.pms_backend.security.PmsPrincipal;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@ActiveProfiles("test")
class WorkSessionConcurrencyIT {

    private static final int THREAD_COUNT = 8;

    @Autowired WorkSessionService workSessionService;
    @Autowired WorkSessionRepository workSessionRepository;

    @RepeatedTest(5)
    void exactlyOneActiveSessionSurvivesNConcurrentStarts() throws Exception {
        UUID assignmentId = UUID.randomUUID();
        PmsPrincipal principal = new PmsPrincipal(UUID.randomUUID(),UUID.randomUUID(), UUID.randomUUID());

        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> results = IntStream.range(0, THREAD_COUNT).mapToObj(i -> pool.submit(()-> {
            startGate.await();
            try {
                workSessionService.start(principal, assignmentId);
                return true;
            } catch (RuntimeException ex){
                return false;
            }
        })).toList();
        startGate.countDown();
        long successCount = results.stream().filter(f -> {
            try {return f.get();} catch (Exception e){return false;}
        }).count();
        pool.shutdown();

        assertThat(successCount).isEqualTo(1);
        assertThat(workSessionRepository.findByTaskAssignmentIdAndSessionStatus(assignmentId, SessionStatus.ACTIVE)).isPresent();
    }
}
