package com.example.employeemanagement;

import com.example.employeemanagement.repositories.IEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeApiIntegrationTest {
    @Autowired TestRestTemplate http;
    @Autowired IEmployeeRepository repository;

    @Test void realHttpCrud_changesRepositoryAndPreservesUnicode() {
        var input = Map.of("empId", "E701", "empName", "Nguyễn Thị Ánh", "designation", "Tester", "salary", 12500000);
        try {
            var created = http.postForEntity("/api/employees", input, JsonNode.class);
            assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(created.getHeaders().getLocation().toString()).endsWith("/api/employees/E701");
            assertThat(http.getForObject("/api/employees/E701", JsonNode.class).get("empName").asText()).isEqualTo("Nguyễn Thị Ánh");
            assertThat(repository.getEmployeeById("E701").empName()).isEqualTo("Nguyễn Thị Ánh");
            var updated = Map.of("empId", "E701", "empName", "Trần Minh", "designation", "Developer", "salary", 15000000);
            var put = http.exchange("/api/employees/E701", HttpMethod.PUT, new HttpEntity<>(updated), JsonNode.class);
            assertThat(put.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(repository.getEmployeeById("E701").empName()).isEqualTo("Trần Minh");
            var deleted = http.exchange("/api/employees/E701", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
            assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(http.getForEntity("/api/employees/E701", JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        } finally {
            if (repository.getEmployeeById("E701") != null) { repository.deleteEmployee("E701"); }
        }
    }
    @Test void largestPage_returnsEmptyContentAndNoNextAfterV1DtoMapping() {
        JsonNode result = http.getForObject("/api/employees?page=2147483647&size=100", JsonNode.class);
        assertThat(result.path("content").size()).isZero();
        assertThat(result.path("totalElements").asLong()).isEqualTo(30);
        assertThat(result.path("hasNext").asBoolean()).isFalse();
    }
    @Test void swaggerAndOpenApi_areServedAndDescribeEndpoints() {
        var spec = http.getForEntity("/v3/api-docs", JsonNode.class);
        assertThat(spec.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(spec.getBody().path("paths").has("/api/employees")).isTrue();
        assertThat(spec.getBody().path("paths").has("/api/v2/employees")).isTrue();
        assertThat(http.getForEntity("/swagger-ui/index.html", String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
