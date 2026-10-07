package com.example.employeemanagement;

import com.example.employeemanagement.exceptions.*;
import com.example.employeemanagement.pojos.Employee;
import com.example.employeemanagement.repositories.EmployeeRepository;
import com.example.employeemanagement.services.PagingPolicy;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.*;
import static org.assertj.core.api.Assertions.*;

// Plain JUnit; a fresh real in-memory repository per test. No Spring/JPA context.
class EmployeeRepositoryTest {
    EmployeeRepository repository;
    @BeforeEach void setup() { repository = new EmployeeRepository(); }
    Pageable page(int number, int size, String... sort) { return PagingPolicy.create(number, size, sort); }
    Employee employee(String name) { return new Employee("E101", name, "Developer", BigDecimal.TEN); }

    @Test void seed_containsThirtyUniqueEmployees() {
        Page<Employee> result = repository.getAllEmployees(page(0, 100));
        assertThat(result.getTotalElements()).isEqualTo(30);
        assertThat(result.getContent()).extracting(Employee::empId).doesNotHaveDuplicates();
    }
    @Test void createAndRead_keepsExactValue() {
        Employee input = employee("Nguyễn An");
        repository.createEmployee(input);
        assertThat(repository.getEmployeeById("E101")).isEqualTo(input);
    }
    @Test void duplicateCreate_returnsConflictWithoutAddingRow() {
        repository.createEmployee(employee("An"));
        assertThatThrownBy(() -> repository.createEmployee(employee("Binh"))).isInstanceOf(ApiException.class);
        assertThat(repository.getAllEmployees(page(0, 100)).getTotalElements()).isEqualTo(31);
        assertThat(repository.getEmployeeById("E101").empName()).isEqualTo("An");
    }
    @Test void update_replacesExistingWithoutChangingCount() {
        repository.createEmployee(employee("An"));
        repository.updateEmployee("E101", employee("Updated"));
        assertThat(repository.getEmployeeById("E101").empName()).isEqualTo("Updated");
        assertThat(repository.getAllEmployees(page(0, 100)).getTotalElements()).isEqualTo(31);
    }
    @Test void updateMissing_doesNotCreate() {
        assertThatThrownBy(() -> repository.updateEmployee("E101", employee("An")))
                .isInstanceOf(EmployeeNotFoundException.class);
        assertThat(repository.getEmployeeById("E101")).isNull();
    }
    @Test void delete_removesOnlyTargetAndRejectsSecondDelete() {
        repository.deleteEmployee("E001");
        assertThat(repository.getEmployeeById("E001")).isNull();
        assertThat(repository.getEmployeeById("E002")).isNotNull();
        assertThatThrownBy(() -> repository.deleteEmployee("E001")).isInstanceOf(EmployeeNotFoundException.class);
    }
    @Test void paging_disjointPagesAndCorrectTotals() {
        Page<Employee> first = repository.getAllEmployees(page(0, 5));
        Page<Employee> second = repository.getAllEmployees(page(1, 5));
        assertThat(first.getContent()).extracting(Employee::empId).containsExactly("E001", "E002", "E003", "E004", "E005");
        assertThat(second.getContent()).extracting(Employee::empId).containsExactly("E006", "E007", "E008", "E009", "E010");
        assertThat(first.getContent()).doesNotContainAnyElementsOf(second.getContent());
        assertThat(second.getTotalPages()).isEqualTo(6);
    }
    @Test void sortSalary_ascDescChangesOrder() {
        var asc = repository.getAllEmployees(page(0, 100, "salary,asc")).getContent();
        var desc = repository.getAllEmployees(page(0, 100, "salary,desc")).getContent();
        assertThat(asc).extracting(Employee::salary).isSorted();
        assertThat(desc).extracting(Employee::salary).isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(desc.getFirst()).isEqualTo(asc.getLast());
    }
    @Test void multiSort_equalNamesUsesSalaryThenId() {
        var result = repository.getAllEmployees(page(0, 3, "empName,asc", "salary,desc")).getContent();
        assertThat(result).extracting(Employee::empId).containsExactly("E021", "E011", "E001");
    }
    @Test void stableTieBreak_keepsEqualNamesAcrossPages() {
        var first = repository.getAllEmployees(page(0, 2, "empName,asc")).getContent();
        var second = repository.getAllEmployees(page(1, 2, "empName,asc")).getContent();
        assertThat(first).extracting(Employee::empId).containsExactly("E001", "E011");
        assertThat(second).extracting(Employee::empId).containsExactly("E021", "E002");
    }
    @Test void beyondRange_returnsEmptyPageWithTotals() {
        var result = repository.getAllEmployees(page(Integer.MAX_VALUE, 100));
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(30);
        assertThat(result.hasNext()).isFalse();
    }
    @Test void slice_firstLastAndBeyondRangeHaveCorrectHasNext() {
        assertThat(repository.getEmployeeSlice(page(0, 7)).hasNext()).isTrue();
        var last = repository.getEmployeeSlice(page(4, 7));
        assertThat(last.getContent()).hasSize(2);
        assertThat(last.hasNext()).isFalse();
        assertThat(repository.getEmployeeSlice(page(5, 7)).getContent()).isEmpty();
    }
    @Test void concurrentDuplicateCreates_allowExactlyOneWinner() throws Exception {
        try (var pool = Executors.newFixedThreadPool(8)) {
            List<Callable<Boolean>> requests = IntStream.range(0, 16).mapToObj(i -> (Callable<Boolean>) () -> {
                try { repository.createEmployee(employee("An")); return true; }
                catch (ApiException e) { return false; }
            }).toList();
            int successes = 0;
            for (Future<Boolean> result : pool.invokeAll(requests)) { if (result.get()) { successes++; } }
            assertThat(successes).isEqualTo(1);
            assertThat(repository.getAllEmployees(page(0, 100)).getTotalElements()).isEqualTo(31);
        }
    }
}
