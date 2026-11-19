package com.jlmasa.pms_backend.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    }
}
