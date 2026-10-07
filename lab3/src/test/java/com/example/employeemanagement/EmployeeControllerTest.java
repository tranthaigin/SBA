package com.example.employeemanagement;

import com.example.employeemanagement.controllers.*;
import com.example.employeemanagement.exceptions.*;
import com.example.employeemanagement.pojos.Employee;
import com.example.employeemanagement.services.IEmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({EmployeeController.class, EmployeeV2Controller.class})
class EmployeeControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockBean IEmployeeService service; // Boot 3.3 MVC slice; replaces only the service bean.
    private Employee employee() {
        return new Employee("E101", "Nguyễn An", "Developer", new BigDecimal("15000000"), "an@example.com", "IT");
    }
    private void stubPage() {
        when(service.getAllEmployees(any())).thenAnswer(inv ->
                new PageImpl<>(List.of(employee()), inv.getArgument(0), 30));
    }

    @Test void getList_returnsV1PageMetadata() throws Exception {
        // Arrange: service output and the page request are deliberately separate.
        stubPage();
        // Act + Assert: pass through MVC mapping, binding and serialization.
        mvc.perform(get("/api/employees").param("page", "0").param("size", "2"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].empId").value("E101"))
                .andExpect(jsonPath("$.content[0].email").doesNotExist())
                .andExpect(jsonPath("$.totalElements").value(30)).andExpect(jsonPath("$.totalPages").value(15))
                .andExpect(jsonPath("$.page").value(0)).andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.hasNext").value(true));
        ArgumentCaptor<Pageable> capture = ArgumentCaptor.forClass(Pageable.class);
        verify(service).getAllEmployees(capture.capture());
        assertThat(capture.getValue().getOffset()).isZero();
    }
    @Test void versionOne_keepsOriginalFourFields() throws Exception {
        when(service.getEmployeeById("E101")).thenReturn(employee());
        mvc.perform(get("/api/v1/employees/E101")).andExpect(status().isOk())
                .andExpect(jsonPath("$.empName").value("Nguyễn An"))
                .andExpect(jsonPath("$.email").doesNotExist()).andExpect(jsonPath("$.department").doesNotExist());
    }
    @Test void versionTwo_addsEmailDepartment() throws Exception {
        stubPage();
        mvc.perform(get("/api/v2/employees")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("an@example.com"))
                .andExpect(jsonPath("$.content[0].department").value("IT"));
    }
    @Test void getById_existing_returnsEmployee() throws Exception {
        when(service.getEmployeeById("E101")).thenReturn(employee());
        mvc.perform(get("/api/employees/E101")).andExpect(status().isOk())
                .andExpect(jsonPath("$.empId").value("E101"));
    }
    @Test void getById_missing_returns404Error() throws Exception {
        when(service.getEmployeeById("E999")).thenThrow(new EmployeeNotFoundException("E999"));
        mvc.perform(get("/api/employees/E999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.path").value("/api/employees/E999"))
                .andExpect(jsonPath("$.message").value("Employee E999 was not found"));
    }
    @Test void create_valid_returns201LocationAndBody() throws Exception {
        when(service.createEmployee(any())).thenAnswer(inv -> inv.getArgument(0));
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(employee())))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/employees/E101"))
                .andExpect(jsonPath("$.empId").value("E101")).andExpect(jsonPath("$.empName").value("Nguyễn An"));
        verify(service).createEmployee(employee());
    }
    @Test void create_blankName_returns400BeforeService() throws Exception {
        Employee input = new Employee("E101", " ", "Developer", BigDecimal.TEN);
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(input)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.empName").exists());
        verifyNoInteractions(service);
    }
    @Test void create_negativeSalary_returns400() throws Exception {
        Employee input = new Employee("E101", "An", "Developer", BigDecimal.valueOf(-1));
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(input)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.salary").exists());
        verifyNoInteractions(service);
    }
    @Test void create_malformedJson_returns400() throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(service);
    }
    @Test void create_unknownField_returns400WithoutWriting() throws Exception {
        String body = json.writeValueAsString(employee()).replace("}", ",\"admin\":true}");
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void create_wrongMediaType_returns415() throws Exception {
        mvc.perform(post("/api/employees").contentType(MediaType.TEXT_PLAIN).content("employee"))
                .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.status").value(415));
    }
    @Test void create_duplicate_returns409() throws Exception {
        when(service.createEmployee(any())).thenThrow(new ApiException(org.springframework.http.HttpStatus.CONFLICT, "Duplicate empId"));
        mvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(employee())))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }
    @Test void update_valid_returns200UpdatedBody() throws Exception {
        when(service.updateEmployee(eq("E101"), any())).thenReturn(employee());
        mvc.perform(put("/api/employees/E101").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(employee())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.empName").value("Nguyễn An"));
        verify(service).updateEmployee("E101", employee());
    }
    @Test void update_missing_returns404() throws Exception {
        when(service.updateEmployee(eq("E101"), any())).thenThrow(new EmployeeNotFoundException("E101"));
        mvc.perform(put("/api/employees/E101").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(employee())))
                .andExpect(status().isNotFound());
    }
    @Test void delete_existing_returns204EmptyBody() throws Exception {
        mvc.perform(delete("/api/employees/E101")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(service).deleteEmployee("E101");
    }
    @Test void delete_missing_returns404() throws Exception {
        doThrow(new EmployeeNotFoundException("E999")).when(service).deleteEmployee("E999");
        mvc.perform(delete("/api/employees/E999")).andExpect(status().isNotFound());
    }
    @Test void negativePage_returns400WithoutCallingService() throws Exception {
        mvc.perform(get("/api/employees").param("page", "-1")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void excessiveSize_returns400() throws Exception {
        mvc.perform(get("/api/employees").param("size", "101")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void invalidSort_returns400() throws Exception {
        mvc.perform(get("/api/employees").param("sort", "password,asc")).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void nonNumericPage_returnsStructured400() throws Exception {
        mvc.perform(get("/api/employees").param("page", "hello")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value("/api/employees"));
    }
    @Test void repeatedSort_preservesDirectionAndTieBreaker() throws Exception {
        stubPage();
        mvc.perform(get("/api/employees").param("sort", "empName,asc", "salary,desc")).andExpect(status().isOk());
        ArgumentCaptor<Pageable> capture = ArgumentCaptor.forClass(Pageable.class);
        verify(service).getAllEmployees(capture.capture());
        assertThat(capture.getValue().getSort().toList()).containsExactly(Sort.Order.asc("empName"),
                Sort.Order.desc("salary"), Sort.Order.asc("empId"));
    }
    @Test void getSlice_omitsTotalMetadata() throws Exception {
        when(service.getEmployeeSlice(any())).thenAnswer(inv -> new SliceImpl<>(List.of(employee()), inv.getArgument(0), true));
        mvc.perform(get("/api/employees/slice")).andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").value(true)).andExpect(jsonPath("$.totalElements").doesNotExist())
                .andExpect(jsonPath("$.totalPages").doesNotExist());
    }
    @Test void unsupportedAccept_returns406() throws Exception {
        stubPage();
        mvc.perform(get("/api/employees").accept(MediaType.APPLICATION_XML)).andExpect(status().isNotAcceptable());
    }
}
