package fu.edu.sba301.assignment02;

import com.fasterxml.jackson.databind.ObjectMapper;
import fu.edu.sba301.assignment02.dto.CategoryRequest;
import fu.edu.sba301.assignment02.dto.LoginRequest;
import fu.edu.sba301.assignment02.dto.NewsArticleRequest;
import fu.edu.sba301.assignment02.dto.SystemAccountRequest;
import fu.edu.sba301.assignment02.entity.Category;
import fu.edu.sba301.assignment02.repository.CategoryRepository;
import fu.edu.sba301.assignment02.repository.NewsArticleRepository;
import fu.edu.sba301.assignment02.repository.SystemAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class Assignment02ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private NewsArticleRepository newsArticleRepository;

    @Autowired
    private SystemAccountRepository accountRepository;

    private org.springframework.mock.web.MockHttpSession session(String email,String password) throws Exception {
        return (org.springframework.mock.web.MockHttpSession) mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new LoginRequest(email,password)))).andExpect(status().isOk()).andReturn().getRequest().getSession();
    }
    @Test
    @DisplayName("Test 1: Public can view active news articles without authentication")
    void testPublicCanViewActiveNews() throws Exception {
        mockMvc.perform(get("/api/news/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].newsStatus", is(true)));
    }

    @Test
    @DisplayName("Test 2: Authentication with valid Admin & Staff credentials")
    void testAuthSuccess() throws Exception {
        LoginRequest adminLogin = new LoginRequest("admin@funews.com", "@1");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountEmail", is("admin@funews.com")))
                .andExpect(jsonPath("$.accountRole", is(1)));

        LoginRequest staffLogin = new LoginRequest("staff@funews.com", "@1");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountEmail", is("staff@funews.com")))
                .andExpect(jsonPath("$.accountRole", is(2)));
    }

    @Test
    @DisplayName("Test 3: Authentication failure with invalid password")
    void testAuthFailure() throws Exception {
        LoginRequest badLogin = new LoginRequest("admin@funews.com", "wrongPass");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Invalid email or password")));
    }

    @Test
    @DisplayName("Test 4: Category CRUD and constraint rejection on deleting used category")
    void testCategoryCrudAndConstraint() throws Exception {
        // Create Category
        CategoryRequest catReq = new CategoryRequest("AI Research", "Artificial intelligence developments", null, true);
        String createRes = mockMvc.perform(post("/api/categories").session(session("staff@funews.com","@1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryName", is("AI Research")))
                .andReturn().getResponse().getContentAsString();

        Category createdCat = objectMapper.readValue(createRes, Category.class);
        Short catId = createdCat.getCategoryId();

        // Update Category
        catReq.setCategoryDesciption("Updated AI developments description");
        mockMvc.perform(put("/api/categories/" + catId).session(session("staff@funews.com","@1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryDesciption", is("Updated AI developments description")));

        // Create News associated with this category
        NewsArticleRequest newsReq = new NewsArticleRequest();
        newsReq.setHeadline("Breakthrough in neural network training");
        newsReq.setCategoryId(catId);
        newsReq.setNewsTitle("Next-gen AI");
        newsReq.setCreatedById((short) 2);
        mockMvc.perform(post("/api/news").session(session("staff@funews.com","@1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsReq)))
                .andExpect(status().isCreated());

        // Attempt to delete category -> should be rejected with 409 Conflict
        mockMvc.perform(delete("/api/categories/" + catId).session(session("staff@funews.com","@1")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("associated with existing news articles")));
    }

    @Test
    @DisplayName("Test 5: SystemAccount CRUD and constraint rejection on deleting creator account")
    void testAccountCrudAndConstraint() throws Exception {
        SystemAccountRequest accReq = new SystemAccountRequest();
        accReq.setAccountName("Writer Specialist");
        accReq.setAccountEmail("writer" + System.currentTimeMillis() + "@funews.com");
        accReq.setAccountRole(2);
        accReq.setAccountPassword("Pass123!");

        String accRes = mockMvc.perform(post("/api/accounts").session(session("admin@funews.com","@1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Short accId = (short) objectMapper.readTree(accRes).get("accountId").asInt();

        // Create news article created by this account
        Category cat = categoryRepository.findAll().get(0);
        NewsArticleRequest newsReq = new NewsArticleRequest();
        newsReq.setHeadline("Student Hackathon Results 2026");
        newsReq.setCategoryId(cat.getCategoryId());
        newsReq.setCreatedById(accId);
        mockMvc.perform(post("/api/news").session(session(accReq.getAccountEmail(),"Pass123!"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newsReq)))
                .andExpect(status().isCreated());

        // Attempt to delete account -> should be rejected with 409 Conflict
        mockMvc.perform(delete("/api/accounts/" + accId).session(session("admin@funews.com","@1")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("creator of existing news articles")));
    }

    @Test
    @DisplayName("Test 6: News article search and filter by createdBy")
    void testNewsSearchAndCreatedByFilter() throws Exception {
        mockMvc.perform(get("/api/news?search=FPT").session(session("staff@funews.com","@1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].newsTitle", containsString("FPT")));

        mockMvc.perform(get("/api/news/created-by/2").session(session("staff@funews.com","@1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void rejectsAnonymousAndWrongRole() throws Exception {
      mockMvc.perform(get("/api/accounts")).andExpect(status().isUnauthorized());
      mockMvc.perform(get("/api/news")).andExpect(status().isUnauthorized());
      mockMvc.perform(get("/api/accounts").session(session("staff@funews.com","@1"))).andExpect(status().isForbidden());
      mockMvc.perform(get("/api/categories").session(session("admin@funews.com","@1"))).andExpect(status().isForbidden());
      mockMvc.perform(get("/api/auth/profile").session(session("staff@funews.com","@1"))).andExpect(status().isOk()).andExpect(jsonPath("$.accountPassword").doesNotExist());
    }
}
