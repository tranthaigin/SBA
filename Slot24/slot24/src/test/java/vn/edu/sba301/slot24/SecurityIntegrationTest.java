package vn.edu.sba301.slot24;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.sba301.slot24.user.AppUser;
import vn.edu.sba301.slot24.user.AppUserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    private static final String USER_EMAIL = "student24@example.com";
    private static final String PASSWORD = "Sba301@2026";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AppUserRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @BeforeEach
    void seedUsers() {
        users.deleteAll();
        users.save(user(USER_EMAIL, "USER"));
        users.save(user("admin24@example.com", "ADMIN"));
    }

    @Test
    void publicOrchidsAndProtectedBoundariesAreEnforced() throws Exception {
        mvc.perform(get("/orchids"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Dendrobium"));
        mvc.perform(get("/users/me")).andExpect(status().isUnauthorized());
        mvc.perform(post("/orchids")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Cattleya","description":"Protected create"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signupStoresBcryptHashAndDoesNotAcceptRole() throws Exception {
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"NewStudent@example.com","password":"Sba301@2026","role":"ADMIN"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));

        AppUser saved = users.findByEmail("newstudent@example.com").orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(saved.getPasswordHash().startsWith("$2"));
        org.junit.jupiter.api.Assertions.assertNotEquals(PASSWORD, saved.getPasswordHash());

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson("newstudent@example.com", PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void loginIssuesBearerTokenAndInvalidPasswordReturns401() throws Exception {
        String response = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(USER_EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString();

        JsonNode body = objectMapper.readTree(response);
        String token = body.get("accessToken").asText();
        mvc.perform(get("/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(USER_EMAIL))
                .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"));

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(USER_EMAIL, "incorrect-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidAndTamperedTokensReturn401() throws Exception {
        String token = login(USER_EMAIL);
        int signatureStart = token.lastIndexOf('.') + 1;
        char original = token.charAt(signatureStart);
        char replacement = original == 'A' ? 'B' : 'A';
        String tampered = token.substring(0, signatureStart) + replacement + token.substring(signatureStart + 1);

        mvc.perform(get("/users/me").header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/users/me").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredSignedTokenReturns401() throws Exception {
        java.time.Instant now = java.time.Instant.now();
        String expiredToken = Jwts.builder()
                .subject(USER_EMAIL)
                .issuedAt(java.util.Date.from(now.minusSeconds(120)))
                .expiration(java.util.Date.from(now.minusSeconds(60)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .compact();

        mvc.perform(get("/users/me").header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());

        String tokenWithoutExpiration = Jwts.builder()
                .subject(USER_EMAIL)
                .issuedAt(java.util.Date.from(now))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)))
                .compact();
        mvc.perform(get("/users/me").header("Authorization", "Bearer " + tokenWithoutExpiration))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/users/me").header("Authorization", "Bearer " + String.join(".", "x", "y", "z")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userIsForbiddenAndAdminIsAllowedOnAdminEndpoint() throws Exception {
        mvc.perform(get("/admin/health").header("Authorization", "Bearer " + login(USER_EMAIL)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/admin/health").header("Authorization", "Bearer " + login("admin24@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("admin-only"));
        mvc.perform(get("/admin/health")).andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedUserCanWriteOrchids() throws Exception {
        mvc.perform(post("/orchids")
                        .header("Authorization", "Bearer " + login(USER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Cattleya","description":"Created with bearer token"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cattleya"));
    }

    private AppUser user(String email, String role) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(PASSWORD));
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }

    private String login(String email) throws Exception {
        String response = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private String loginJson(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(new LoginPayload(email, password));
    }

    private record LoginPayload(String email, String password) {
    }
}
