package com.example.employeemanagement;

import com.example.employeemanagement.exceptions.*;
import com.example.employeemanagement.pojos.Employee;
import com.example.employeemanagement.repositories.IEmployeeRepository;
import com.example.employeemanagement.services.EmployeeService;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceUnitTest {
    @Mock IEmployeeRepository repository;
    EmployeeService service;
    ValidatorFactory validators;
    Employee employee = new Employee("E101", "An", "Developer", BigDecimal.TEN);
    @BeforeEach void setup() {
        validators = Validation.buildDefaultValidatorFactory();
        service = new EmployeeService(repository, validators.getValidator());
    }
    @AfterEach void close() { validators.close(); }

    @Test void getExisting_delegatesToRepository() {
        // Arrange
        when(repository.getEmployeeById("E101")).thenReturn(employee);
        // Act
        Employee result = service.getEmployeeById("E101");
        // Assert
        assertThat(result).isEqualTo(employee);
        verify(repository).getEmployeeById("E101");
    }
    @Test void getMissing_throwsNotFound() {
        when(repository.getEmployeeById("E999")).thenReturn(null);
        assertThatThrownBy(() -> service.getEmployeeById("E999")).isInstanceOf(EmployeeNotFoundException.class);
    }
    @Test void createValid_delegatesOnce() {
        when(repository.createEmployee(employee)).thenReturn(employee);
        assertThat(service.createEmployee(employee)).isEqualTo(employee);
        verify(repository).createEmployee(employee);
    }
    @Test void createInvalid_doesNotTouchRepository() {
        Employee invalid = new Employee("E101", "", "Developer", BigDecimal.TEN);
        assertThatThrownBy(() -> service.createEmployee(invalid)).isInstanceOf(ApiException.class);
        verifyNoInteractions(repository);
    }
    @Test void createNull_returnsBadRequest() {
        assertThatThrownBy(() -> service.createEmployee(null)).isInstanceOf(ApiException.class);
        verifyNoInteractions(repository);
    }
    @Test void updateMatchingId_delegates() {
        when(repository.updateEmployee("E101", employee)).thenReturn(employee);
        assertThat(service.updateEmployee("E101", employee)).isEqualTo(employee);
        verify(repository).updateEmployee("E101", employee);
    }
    @Test void updateMismatchedId_doesNotWrite() {
        assertThatThrownBy(() -> service.updateEmployee("E102", employee)).isInstanceOf(ApiException.class);
        verifyNoInteractions(repository);
    }
    @Test void delete_delegatesToDeleteOnly() {
        service.deleteEmployee("E101");
        verify(repository).deleteEmployee("E101");
        verifyNoMoreInteractions(repository);
    }
    @Test void page_keepsPageableAndRepositoryResult() {
        Pageable p = PageRequest.of(1, 2, Sort.by("empName"));
        Page<Employee> result = new PageImpl<>(List.of(employee), p, 30);
        when(repository.getAllEmployees(p)).thenReturn(result);
        assertThat(service.getAllEmployees(p)).isSameAs(result);
        verify(repository).getAllEmployees(p);
    }
    @Test void slice_usesSliceRepositoryMethod() {
        Pageable p = PageRequest.of(0, 2);
        Slice<Employee> result = new SliceImpl<>(List.of(employee), p, true);
        when(repository.getEmployeeSlice(p)).thenReturn(result);
        assertThat(service.getEmployeeSlice(p)).isSameAs(result);
        verify(repository).getEmployeeSlice(p);
        verify(repository, never()).getAllEmployees(any());
    }
}
