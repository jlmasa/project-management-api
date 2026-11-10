package com.jlmasa.pms_backend.identity;

import com.jayway.jsonpath.JsonPath;
import com.jlmasa.pms_backend.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void loginSuccess_returnsTokenWithExpectedClaims() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"email":"admin@quadstruct.test","password":"a-long-enough-password"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void loginFailure_wrongPassword_returns401() throws Exception {
        // The generic-message assertion (jsonPath("$.message") == "Invalid email, password, or organization.")
        // can only pass once GlobalExceptionHandler (Part 10, Step 12.1) maps AuthenticationException.
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"email":"admin@quadstruct.test","password":"definitely-the-wrong-password"}
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void missingToken_returns401OnSecuredEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tamperedToken_returns401OnSecuredEndpoint() throws Exception {
        String token = loginAndGetToken();
        mockMvc.perform(get("/api/v1/departments")
                        .header("Authorization", "Bearer " + token.substring(0, token.length() - 2) + "xx"))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetToken() throws Exception {
        String body = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"email":"admin@quadstruct.test","password":"a-long-enough-password"}
                        """))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.accessToken");
    }
}