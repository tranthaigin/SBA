# Employee API contract

Base URL: `http://127.0.0.1:8083`. Swagger `/swagger-ui/index.html`; OpenAPI `/v3/api-docs`.

## Endpoints

| Method | Path | Success | Failure |
|---|---|---|---|
| GET | /api/employees, /api/v1/employees | 200 Page<V1> | 400 invalid paging/sort, 406 unsupported Accept |
| GET | /api/employees/slice, /api/v1/employees/slice | 200 Slice<V1> | 400 invalid query |
| GET | /api/employees/{id}, /api/v1/employees/{id} | 200 V1 | 404 missing ID |
| POST | /api/employees, /api/v1/employees | 201 V1 + Location header | 400 validation/JSON, 409 duplicate ID, 415 media type |
| PUT | /api/employees/{id}, /api/v1/employees/{id} | 200 V1, full replacement | 400 mismatched ID/validation, 404 missing ID, 415 media type |
| DELETE | /api/employees/{id}, /api/v1/employees/{id} | 204 empty body | 404 missing ID |
| GET | /api/v2/employees, /api/v2/employees/slice, /api/v2/employees/{id} | 200 V2 Page/Slice/employee | Same read guards |

V2 is a read representation extension; write through base/V1. `/api/v3/employees` returns 404. This is URI versioning; there is no `version` query/header switching. V1 is kept, with no removal date in this lab.

## Input and validation

```json
{"empId":"E901","empName":"Nguyễn Thị Ánh","designation":"Tester","salary":12500000,"email":"anh@example.com","department":"QA"}
```

empId is required and matches `E[0-9]{3,6}`; empName/designation required, max 100; salary required, nonnegative, up to 12 integer and 2 fraction digits. Optional email must be valid/max 150, optional department max 100. Missing/null required fields, unknown JSON fields and malformed JSON return 400. Optional email/department are replace semantics: omitting them in PUT clears them. A mismatched PUT ID is rejected before any repository write. A valid matching PUT to missing employee returns 404, not an upsert.

V1 output has exactly empId/empName/designation/salary. V2 adds email/department (null if absent). Salary is a JSON number backed by BigDecimal. Employee values are immutable.

## Paging and sorting

| Parameter | Default | Rule |
|---|---|---|
| page | 0 | integer >= 0; first page is 0 |
| size | 10 | integer 1..100; invalid values return 400, not clamp |
| sort | empId,asc | `property` or `property,asc/desc`; repeat parameter for multi-sort |

Allowed fields: empId, empName, designation, salary, email, department. Unknown fields, invalid directions, malformed/duplicate sort fields return 400. Sorting is case-sensitive for text; empId ascending is appended when omitted to stabilize ties. Null optional fields sort last in ascending, first in descending.

Example: `/api/employees?page=0&size=3&sort=empName,asc&sort=salary,desc`.

Page JSON: content, page, size, numberOfElements, totalElements, totalPages, hasNext, hasPrevious. Slice JSON omits totalElements/totalPages, retaining the remaining keys. A page/slice beyond available data returns 200 with empty content and hasNext=false. Large page offsets use long; Page hasNext avoids the int overflow in framework PageImpl.

In-memory Page counts a sorted snapshot; Slice takes size+1 from a sorted snapshot. Both still sort the in-memory dataset. There are no SQL/count-query performance claims.

## Errors

JSON fields: timestamp, status, error, message, path, fieldErrors. Validation from MVC supplies fieldErrors keyed by field; other errors use an empty object. 406 cannot necessarily negotiate a JSON body when the client explicitly refuses JSON; assert the HTTP status for that case.

Concurrent duplicate creates are atomic in the synchronized repository; exactly one request can insert the same ID. No durable persistence, cross-process coordination, authentication or frontend is part of this core lab.
