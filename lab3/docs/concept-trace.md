# Concept trace

| Concept | Concrete source | Observable evidence |
|---|---|---|
| @RestController / mapping / @RequestBody / @Valid | EmployeeController | MockMvc POST validates body and status; mutation shows 201 sensitivity |
| Controller → service → repository | constructor dependencies and interfaces | Mockito delegation + real HTTP CRUD |
| Immutable Employee | Employee record | repository value cannot be edited outside update operation |
| Repository abstraction | IEmployeeRepository / EmployeeRepository | plain JUnit, not @DataJpaTest |
| URI versioning | EmployeeController / EmployeeV2Controller / EmployeeV1 | v1 exactly four fields; v2 adds email/department |
| Pageable/Sort | PagingPolicy, repository | offset, guards, multi-sort, deterministic ties |
| Page vs Slice | DTOs and repository methods | Page has totals; Slice fetches size+1 and hasNext; no totals |
| @ControllerAdvice | GlobalExceptionHandler | structured 400/404/409/415 responses |
| Plain unit vs MVC slice | service/repository vs controller tests | @Mock only in plain unit; @MockBean in Boot 3.3 slice |
| Full-context integration | EmployeeApiIntegrationTest | actual HTTP server on random port |
| Mutation/regression | scripts/break-it.ps1 | wrong POST status fails without changing assertions |
| Concurrency | synchronized repository methods | duplicate-ID concurrency test |
