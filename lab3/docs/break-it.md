# Red → Green and regression evidence

## Intentional POST mutation

Run `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/break-it.ps1` from Lab03.

The script changes only the production POST return from `ResponseEntity.created(...).body(...)` to `ResponseEntity.ok(...)`. The existing `create_valid_returns201LocationAndBody` test must fail with expected 201 but actual 200. It does not weaken assertions. Source bytes are restored in finally, then the same test must pass; SHA256 before/after must match. Raw logs and machine-readable summary are in evidence/console.

This executes one intentional mutation, satisfying Slot16's minimum one red-green experiment. The optional mutations removing @RequestBody, reversing default sort and calling a wrong repository method have not been executed.

## Real boundary defect found during implementation

The first full suite failed `EmployeeRepositoryTest.beyondRange_returnsEmptyPageWithTotals`: content was empty, but `PageImpl.hasNext()` overflowed at page=Integer.MAX_VALUE because its calculation adds 1 as int. The repository now computes hasNext using long offset + numberOfElements < totalElements. The regression keeps the largest page input and expects empty content/hasNext=false. The first repository fix passed 60 tests, but the external Newman test still failed: Page.map() constructed a new PageImpl and discarded the repository override. PageResponse now calculates hasNext with a long page index, and a real HTTP regression covers the final V1 JSON. See paging-overflow-red.txt, newman-overflow-red-run.json and the final green suite.

## Windows PowerShell compatibility

The first mutation script attempt was interrupted by Windows PowerShell 5 treating a JVM stderr class-sharing warning as a terminating ErrorRecord. Source was restored in finally. The script now captures native exit codes explicitly under Continue during Maven invocation and restores Stop afterwards. The mutation is accepted only when the red log contains the specific 201/200 assertion; unrelated tool/compile failures do not count.
