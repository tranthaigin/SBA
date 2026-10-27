package edu.slot25.security.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.slot25.security.user.AppUser;
import edu.slot25.security.user.AppUserRepository;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    private static final String EMAIL = "slot25.student@example.com";
    private static final String PASSWORD = "Sba301@2026";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AppUserRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        users.deleteAll();
    }

    @Test
    void signupLoginAndProtectedRequests_workEndToEnd() throws Exception {
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());

        AppUser saved = users.findByEmail(EMAIL).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(saved.getPassword()).isNotEqualTo(PASSWORD);
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches(PASSWORD, saved.getPassword())).isTrue();

        MvcResult loginResult = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("accessToken").asText();

        mvc.perform(get("/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(EMAIL))
                .andExpect(jsonPath("$.authorities[0].authority").value("ROLE_USER"));

        mvc.perform(get("/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(EMAIL));
    }

    @Test
    void protectedEndpointWithoutToken_returnsJson401() throws Exception {
        mvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void protectedEndpointWithMalformedToken_returnsJson401() throws Exception {
        mvc.perform(get("/users/me").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void duplicateSignup_returnsConflict() throws Exception {
        users.saveAndFlush(user(EMAIL));

        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("EMAIL_EXISTS"));
    }

    @Test
    void wrongPassword_returnsJson401() throws Exception {
        users.saveAndFlush(user(EMAIL));

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL, "password", "incorrect-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void unknownUser_returnsJson401() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "missing@example.com", "password", PASSWORD))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void tamperedSignature_returnsJson401() throws Exception {
        users.saveAndFlush(user(EMAIL));
        String token = login(EMAIL);
        int signatureStart = token.lastIndexOf('.') + 1;
        char signatureCharacter = token.charAt(signatureStart);
        String tamperedToken = token.substring(0, signatureStart)
                + (signatureCharacter == 'A' ? 'B' : 'A')
                + token.substring(signatureStart + 1);

        mvc.perform(get("/users/me").header("Authorization", "Bearer " + tamperedToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void userRole_cannotAccessAdminEndpoint() throws Exception {
        users.saveAndFlush(user(EMAIL));

        mvc.perform(get("/admin/users").header("Authorization", "Bearer " + login(EMAIL)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("FORBIDDEN"));
    }

    @Test
    void adminEndpointWithoutToken_returnsJson401() throws Exception {
        mvc.perform(get("/admin/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void adminRole_canAccessAdminEndpoint() throws Exception {
        AppUser admin = user(EMAIL);
        admin.setRole("ADMIN");
        users.saveAndFlush(admin);

        mvc.perform(get("/admin/users").header("Authorization", "Bearer " + login(EMAIL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(EMAIL));
    }

    @Test
    void tokenWithOutdatedRoleClaim_returnsJson401() throws Exception {
        AppUser saved = users.saveAndFlush(user(EMAIL));
        String token = login(EMAIL);
        saved.setRole("ADMIN");
        users.saveAndFlush(saved);

        mvc.perform(get("/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void disabledUserToken_returnsJson401() throws Exception {
        AppUser saved = users.saveAndFlush(user(EMAIL));
        String token = login(EMAIL);
        saved.setEnabled(false);
        users.saveAndFlush(saved);

        mvc.perform(get("/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void preflightAllowedOrigin_hasCorsHeaders() throws Exception {
        mvc.perform(preflight("http://localhost:5173", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods",
                        org.hamcrest.Matchers.containsString("GET")))
                .andExpect(header().string("Access-Control-Allow-Headers",
                        org.hamcrest.Matchers.containsString("authorization")));
    }

    @Test
    void allowedOriginActualRequest_hasCorsHeader() throws Exception {
        mvc.perform(post("/auth/signup")
                        .header("Origin", "http://localhost:5173")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", EMAIL, "password", PASSWORD))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void preflightDisallowedOrigin_hasNoAllowOriginHeader() throws Exception {
        mvc.perform(preflight("http://evil.example", "authorization,content-type"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void preflightDisallowedHeader_doesNotAllowRequestedHeader() throws Exception {
        mvc.perform(preflight("http://localhost:5173", "authorization,x-not-allowed"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Headers", "authorization"));
    }

    private AppUser user(String email) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setRole("USER");
        return user;
    }

    private String login(String email) throws Exception {
        MvcResult result = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    private MockHttpServletRequestBuilder preflight(String origin, String requestedHeaders) {
        return options("/users/me")
                .header("Origin", origin)
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", requestedHeaders);
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
