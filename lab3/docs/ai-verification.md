# AI Verification Log

These are the assistant's checked claims/experiments, not a claim that the student has personally explained them.

| Claim/decision | Source | Experiment | Conclusion |
|---|---|---|---|
| Lab03 is core Employee REST, not Orchid/JPA | Slot15 p12, Slot16 p2/p10/p13, syllabus 45-46 | Read local documents and inspect core repository references | Use Employee in-memory; no inferred standalone Lab03 handout |
| @DataJpaTest fits ArrayList | Slot16 p7/p13 | Inspect actual repository; run plain JUnit CRUD/paging tests | Rejected; no JPA infrastructure to test |
| @SpringBootTest is plain unit | Slot16 taxonomy p4 | RANDOM_PORT test starts server; compare mocked service test | Rejected; label full-context integration correctly |
| Use @MockitoBean in this project | Slot16 p9 compatibility note | Compile/test with actual Boot 3.3.4 | Use @MockBean for this course version; @Mock for plain Mockito |
| springdoc version can be arbitrary | Official springdoc compatibility matrix | Resolve 2.6.0 with Boot3.3.4; GET Swagger and OpenAPI | Compatible pair works; pinned for reproducibility |
| PageImpl metadata always safe at extreme page | Actual first full-suite failure | page=Integer.MAX_VALUE,size=100 regression | False for hasNext int addition; use long offset comparison |
| Tests detect POST status regression | Slot16 Break-It | Mutate production POST 201→200, run same focused test, restore bytes | Specific assertion fails red; restored source passes green |
| Slice saves SQL count queries in this core | Slot15 Page/Slice explanation; actual implementation | Inspect ArrayList snapshot and Slice DTO | No SQL exists; only no-total-count contract/size+1 observation proven |

References: [springdoc compatibility](https://springdoc.org/v2/#what-is-the-compatibility-matrix-of-springdoc-openapi-with-spring-boot), [Page API](https://docs.spring.io/spring-data/commons/docs/3.3.4/api/org/springframework/data/domain/Page.html). Build/tests and runtime evidence take precedence over AI-generated snippets.
