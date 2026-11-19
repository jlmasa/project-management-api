package com.jlmasa.pms_backend.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class MessagingSecurityIT {

    @Autowired
    MockMvc
    mockMvc;

    private MessagingFixtures fixtures;

    @BeforeEach
    void setUp() {

    }
    @Test
    void n01_crossDepartmentDirectCreation_isDenied() throws Exception {
        mockMvc.perform(post("/api/v1/conversations")
                        .header("Authorization", "Bearer ")
                        .contentType("application/json")
                        .content("""
                            {"type":"DIRECT","participantMembershipIds":["%s"]}
                                """ ))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("CROSS_DEPARTMENT_FORBIDDEN"));

    }

    @Test
    void n02_nonParticipantSend_isDeniedAndNothingIsSaved() throws Exception {
    }

    @Test
    void n04_sendAfterTaskCompleted_isRejected() throws Exception {

    }
    @Test
    void n06_closedConversationCannotBeReactivated_reopenCreatesCycleTwoOnly() throws Exception {

    }
    @Test
    void n09_mentioningUnauthorizedMember_isRejected() throws Exception {

    }
    @Test
    void n11_uploadOrAttachToClosedConversation_isRejected() throws Exception {
    }

    @Test
    void n14_callingRestDirectlyBypassingAHiddenUiControl_getsTheSameDenialAsAnyOtherPath() throws Exception {

    }
    @Test
    void n15_closeAndSendConcurrently_exactlyOneOutcome_noMessageAfterClose() throws Exception {

    }
    @Test
    void n16_departmentChangedThenSend_isRejectedByReCheck() throws Exception {

    }
    private record MessagingFixtures(/* ... */) {
    }
}
