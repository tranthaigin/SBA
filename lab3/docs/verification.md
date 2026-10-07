# Verification report - Lab03

Verified on 2026-10-07 in `C:/SBA301/bài tập/sba-301/lab3`, JDK21, Boot3.3.4, port8083. The API is real; the repository is in-memory.

| Check | Result | Evidence |
|---|---|---|
| Clean Maven package | 62 tests, 0 failures/errors/skips | [build log](../evidence/console/build-green.txt), [per-suite summary](../evidence/console/test-summary.json) |
| MVC slice | 23 tests PASS | EmployeeControllerTest, mocked service |
| Service unit | 10 tests PASS | EmployeeServiceUnitTest, mocked repository/real validation |
| Repository unit | 13 tests PASS | EmployeeRepositoryTest, fresh ArrayList fixture and concurrent duplicate creates |
| PagingPolicy | 13 parameterized/unit tests PASS | PagingPolicyTest |
| Real HTTP integration | 3 tests PASS | EmployeeApiIntegrationTest, random port and real beans |
| External Postman/Newman | 42 requests / 71 assertions / 0 failures | [report](../evidence/postman/newman-run.json), [summary](../evidence/postman/summary.json), [console](../evidence/postman/newman-console.txt) |
| Intentional production mutation | Expected red 201/200; restored source green, equal SHA256 | [summary](../evidence/console/break-it-summary.json), red/green logs |
| Actual paging regression | Fixed repository and final DTO overflow at MAX_VALUE page | paging-overflow-red.txt, newman-overflow-red-run.json; live response 12 below |
| Swagger browser | GET page=0,size=5 → 200, seed IDs E001-E005, total30/pages6 | [screenshot](../evidence/browser/swagger-get-page.png) |
| Cleanup | 30 original seeds, no E901 | response 40 below |

`evidence/console/verified-source-sha256.json` records the production files after all edits. Break-It restores the controller byte-for-byte. The full 62-test summary is captured before the focused mutation run (which overwrites its Surefire report with one focused test). The packaged jar used by Newman is the clean package from the restored production source.

## Live HTTP matrix

| # | Scenario | Method | Actual HTTP | Assertions | Evidence |
|---|---|---|---|---|---|
| 01 | T01 Baseline thirty employees | GET | 200 | PASS | [response](../evidence/http/01.json) |
| 02 | T02 V1 four fields | GET | 200 | PASS | [response](../evidence/http/02.json) |
| 03 | T03 V2 extra fields | GET | 200 | PASS | [response](../evidence/http/03.json) |
| 04 | T04 Page zero | GET | 200 | PASS | [response](../evidence/http/04.json) |
| 05 | T05 Page one | GET | 200 | PASS | [response](../evidence/http/05.json) |
| 06 | T06 Salary asc | GET | 200 | PASS | [response](../evidence/http/06.json) |
| 07 | T06 Salary desc | GET | 200 | PASS | [response](../evidence/http/07.json) |
| 08 | T07 Multi sort | GET | 200 | PASS | [response](../evidence/http/08.json) |
| 09 | T08 Stable name sort | GET | 200 | PASS | [response](../evidence/http/09.json) |
| 10 | T09 First slice | GET | 200 | PASS | [response](../evidence/http/10.json) |
| 11 | T10 Last slice | GET | 200 | PASS | [response](../evidence/http/11.json) |
| 12 | T11 Beyond range | GET | 200 | PASS | [response](../evidence/http/12.json) |
| 13 | T12 Guard negative page | GET | 400 | PASS | [response](../evidence/http/13.json) |
| 14 | T12 Guard zero size | GET | 400 | PASS | [response](../evidence/http/14.json) |
| 15 | T12 Guard oversized page | GET | 400 | PASS | [response](../evidence/http/15.json) |
| 16 | T12 Guard huge page size | GET | 400 | PASS | [response](../evidence/http/16.json) |
| 17 | T12 Guard unknown sort | GET | 400 | PASS | [response](../evidence/http/17.json) |
| 18 | T12 Guard invalid direction | GET | 400 | PASS | [response](../evidence/http/18.json) |
| 19 | T12 Guard nonnumeric page | GET | 400 | PASS | [response](../evidence/http/19.json) |
| 20 | T12 Guard duplicate sort | GET | 400 | PASS | [response](../evidence/http/20.json) |
| 21 | T13 Missing employee | GET | 404 | PASS | [response](../evidence/http/21.json) |
| 22 | T14 Unsupported URI version | GET | 404 | PASS | [response](../evidence/http/22.json) |
| 23 | T15 Unsupported Accept | GET | 406 | PASS | [response](../evidence/http/23.json) |
| 24 | T16 Validation blank name | POST | 400 | PASS | [response](../evidence/http/24.json) |
| 25 | T16 Validation negative salary | POST | 400 | PASS | [response](../evidence/http/25.json) |
| 26 | T16 Validation invalid email | POST | 400 | PASS | [response](../evidence/http/26.json) |
| 27 | T16 Validation invalid ID | POST | 400 | PASS | [response](../evidence/http/27.json) |
| 28 | T17 Malformed JSON | POST | 400 | PASS | [response](../evidence/http/28.json) |
| 29 | T18 Unsupported body type | POST | 415 | PASS | [response](../evidence/http/29.json) |
| 30 | T19 Create Unicode employee | POST | 201 | PASS | [response](../evidence/http/30.json) |
| 31 | T20 Duplicate ID | POST | 409 | PASS | [response](../evidence/http/31.json) |
| 32 | T21 Read created V2 | GET | 200 | PASS | [response](../evidence/http/32.json) |
| 33 | T22 Replace employee | PUT | 200 | PASS | [response](../evidence/http/33.json) |
| 34 | T23 Read replacement | GET | 200 | PASS | [response](../evidence/http/34.json) |
| 35 | T24 Path/body mismatch | PUT | 400 | PASS | [response](../evidence/http/35.json) |
| 36 | T25 Update missing | PUT | 404 | PASS | [response](../evidence/http/36.json) |
| 37 | T26 Delete employee | DELETE | 204 | PASS | [response](../evidence/http/37.json) |
| 38 | T27 Get deleted | GET | 404 | PASS | [response](../evidence/http/38.json) |
| 39 | T28 Delete missing | DELETE | 404 | PASS | [response](../evidence/http/39.json) |
| 40 | T29 Final thirty seeds | GET | 200 | PASS | [response](../evidence/http/40.json) |
| 41 | T30 OpenAPI spec | GET | 200 | PASS | [response](../evidence/http/41.json) |
| 42 | T31 Swagger UI | GET | 200 | PASS | [response](../evidence/http/42.json) |

## Evidence mapping to the course guide

Slot15 E1-E7: V1/V2, page0/page1, asc/desc/multi-sort, metadata, Slice and invalid query responses above. E8-E10: concept-trace, API contract/README and AI verification log.

Slot16 E1-E7: Swagger startup, controller/service/repository source and green run, paging/sort/error tests. E8-E10: actual red/green logs and Maven summary. E11-E13: testing-strategy, ai-verification and README Testing. E14: Human Gate notes provided for practice; no claim the student has answered orally.

## Reproduce

Stop the API before `mvnw.cmd -B clean package` on Windows to release the jar lock. Run `scripts/run.ps1 -SkipBuild`, then `scripts/verify-api.ps1`. For the intentional mutation, run `scripts/break-it.ps1` without another build/source edit in parallel. Its test run does not replace the running packaged jar.

Text evidence is normalized to UTF-8 and trailing spaces removed for readable Git diffs. Capture timestamps/status/data are retained. Earlier red runs are labeled as historical failures, not presented as passing evidence.

## Limits

No SQL/JPA persistence extension was added to the core ArrayList lab. Restart resets seeds; Page/Slice evidence is a contract demonstration, not a SQL count-query benchmark. Only one intentional mutation is executed. Swagger was tested in the browser; Postman assertions were run through Newman, not the Postman GUI. IDE/Postman screenshots and the student's oral explanations remain personal verification if required by the instructor.
