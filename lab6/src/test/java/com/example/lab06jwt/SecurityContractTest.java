package com.example.lab06jwt;

import com.example.lab06jwt.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityContractTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void protectedEndpointWithoutToken_isUnauthorized() throws Exception {
        mvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized"));
    }

    @Test
    void signupValidationRejectsBadEmailAndShortPassword() throws Exception {
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"A","email":"bad","password":"123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("validation_failed"))
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    @Test
    void signupLoginAndCurrentUser_happyPath_passwordIsHashed() throws Exception {
        signup();
        assertThat(userRepository.findByEmailIgnoreCase("student01@fpt.edu.vn").orElseThrow().getPassword())
                .startsWith("$2").isNotEqualTo("P@ssword123");

        String token = loginAndGetToken();
        mvc.perform(get("/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student01@fpt.edu.vn"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void tamperedToken_isUnauthorized() throws Exception {
        signup();
        String token = loginAndGetToken();
        char last = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1) + (last == 'a' ? 'b' : 'a');

        mvc.perform(get("/users/me").header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized"));
    }

    @Test
    void duplicateEmail_isConflict() throws Exception {
        signup();
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSignupJson()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("email_already_registered"));
    }

    @Test
    void wrongPassword_isUnauthorized() throws Exception {
        signup();
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"student01@fpt.edu.vn","password":"WrongPassword123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_credentials"));
    }

    @Test
    void malformedToken_isUnauthorized() throws Exception {
        mvc.perform(get("/users/me").header("Authorization", "Bearer abc.def"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized"));
    }

    @Test
    void wrongAuthorizationScheme_isUnauthorized() throws Exception {
        mvc.perform(get("/users/me").header("Authorization", "Basic xyz"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized"));
    }

    private void signup() throws Exception {
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSignupJson()))
                .andExpect(status().isCreated());
    }

    private String loginAndGetToken() throws Exception {
        String json = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"student01@fpt.edu.vn","password":"P@ssword123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.expiresIn").value(3600000))
                .andReturn().getResponse().getContentAsString();
        JsonNode response = objectMapper.readTree(json);
        return response.get("token").asText();
    }

    private String validSignupJson() {
        return """
                {"fullName":"SBA301 Student","email":"student01@fpt.edu.vn","password":"P@ssword123"}
                """;
    }
}
