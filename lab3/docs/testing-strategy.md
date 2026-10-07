# Testing Strategy Worksheet

| Critical behavior | SUT | Type | Dependencies | Input/expected | Evidence |
|---|---|---|---|---|---|
| Mapping, binding, HTTP JSON/status | EmployeeController + MVC | @WebMvcTest/MockMvc | service mocked, MVC/validator real | valid POST → 201/Location; missing E999 → 404 | EmployeeControllerTest, build-green.txt |
| Business/delegation | EmployeeService | plain JUnit/Mockito | repository mock, validator real | null/mismatched ID rejected without write; valid call delegates once | EmployeeServiceUnitTest |
| State and duplicate atomicity | EmployeeRepository | plain JUnit | ArrayList real | CRUD; 16 concurrent same-ID creates → one winner | EmployeeRepositoryTest |
| Pagination contracts | PagingPolicy + repository | unit/parameterized | real pure code | pages disjoint; size guards; asc/desc/tie-break; MAX_VALUE offset | PagingPolicyTest, repository tests |
| Spring wiring + HTTP Unicode | full application | RANDOM_PORT integration | all beans/HTTP server real | create/update/delete and Unicode roundtrip | EmployeeApiIntegrationTest |
| Live external client contract | running Lab03 | Postman/Newman | app real on 8083 | 42 requests with paging/version/error/CRUD/Swagger checks | postman collection and newman-run.json |
| Regression sensitivity | controller production mutation | focused MVC test | service mock | POST 200 mutation makes existing 201 test fail; restore → pass | break-it-red/green.txt, summary JSON |

Each plain repository test creates a fresh fixture; mocks reset per JUnit test. Integration CRUD uses E701 and cleanup in finally; live Newman uses E901 then DELETE. Neither relies on suite method order; Newman is intentionally an ordered flow.

## Transfer to FUNews / group project (design only)

Choose News as the critical resource: service unit tests for invalid title and category references; web slice tests for create 201, missing news 404, invalid body 400 and paging contract; JPA slice only when a JpaRepository exists; real relational DB integration for FK/delete/rollback; permission/security tests only once authentication is in the project contract. This worksheet does not claim these transfer tests were implemented in another project.
