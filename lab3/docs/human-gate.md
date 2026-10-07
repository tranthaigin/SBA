# Human Verification Gate - notes for practice

These notes support oral practice. The student still needs to explain and demonstrate; no oral completion is claimed.

1. MockMvc runs the DispatcherServlet/MVC mapping, binding, validation and Jackson serialization; calling a controller method directly skips that infrastructure.
2. In create_valid_returns201LocationAndBody: Arrange stubs the service, Act is mvc.perform(POST), Assert checks 201, Location, JSON and the service input. Service getExisting has explicit AAA blocks.
3. Removing @RequestBody would prevent expected JSON binding; the valid create test should fail. Only POST status mutation has been executed here.
4. @Mock creates a Mockito object for a plain test. @MockBean replaces a bean in this Boot3.3 MVC test context. @MockitoBean is the newer Spring Framework mechanism for later Boot3.x lines.
5. Full Spring context/server is unnecessary for pure repository/business tests; keep a small integration suite for wiring and real HTTP.
6. ArrayList has no JPA entity manager/query/schema, so @DataJpaTest would test an unrelated boundary.
7. The E999 controller test stubs EmployeeNotFoundException and asserts 404/status/path/message.
8. Paging assertions check page, size, actual content IDs, totals, hasNext/hasPrevious and non-overlapping next page.
9. Sort tests use deliberately distinct salary values and equal names to distinguish asc/desc and tie-break behavior.
10. When a mock returns null unexpectedly, compare stubbing method/arguments with actual invocation before changing expectations.
11. verify is useful for preventing wrong writes or wrong delegation. Verifying every harmless internal call makes tests brittle.
12. Fresh repository per test and unique integration IDs with cleanup prevent order dependence and state leakage.
13. The extreme-page failure was a production metadata defect: correct expectation false, implementation overflow. A compile/import error would instead be a test/tool setup problem.
14. POST mutation changes 201 to 200; the same existing test must fail. Restoring code makes it green.
15. The suggested PageImpl construction was revised after the MAX_VALUE regression. Assertions were kept and code corrected.

Practice: explain why default page=0, why size is bounded, why V1 excludes email/department, why restart resets data and why Slice here is not evidence of SQL performance.
