package com.jlmasa.pms_backend.identity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void loginSuccess_returnsTokenWithExpectedClaims() throws Exception{
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content("""
                        {"email":"admin@quadstruct.text","password":"a-long-enough-password"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void loginFailure_wrongPassword_returnGenericMessage() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content("""
                        {"email":"admin@quadstruct.text","password":"a-long-enough-password"}
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email, password, or organization"));
    }

    @Test
    void missingToken_returns401OnSecuredEndpoint() throws Exception {
        String token = loginAndGetToken();
        mockMvc.perform(get("/api/v1/departments")
                .header("Authorization", "Bearer" + token.substring(0, token.length() -2) + "xx"))
                .andExpect(status().isUnauthorized());
    }


    private String loginAndGetToken() throws Exception{
        String body = mockMvc.perform(post("/api/v1/auth/login")
                .contentType("application/json")
                .content("""
                        {"email":"admin@quadstruct.test","password":"a-long-enough-password"}
                        """))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("accessToken").asText();
    }
}
