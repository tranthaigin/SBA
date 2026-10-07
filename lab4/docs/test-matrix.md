# Lab04 test matrix

Run: 2026-10-07 09:06:41 +07:00
Database: SQL Server (confirm datasource in startup log)

HTTP responses below come from the running application. SQL Server checks run only with -VerifyDatabase. T12 runs only with -Restart. See verification.md for remaining manual checks.

| ID | Request / check | Expected | Actual | Result | Evidence |
|---|---|---|---|---|---|
| PRE-CATEGORIES | GET /api/categories | 200 | 200 | PASS | [JSON](../evidence/http/PRE-CATEGORIES.json) |
| T05 | POST /api/orchids | 201 | 201 | PASS | [JSON](../evidence/http/T05.json) |
| T01 | GET /api/orchids | 200 | 200 | PASS | [JSON](../evidence/http/T01.json) |
| T02 | GET /api/orchids?name=HOA%20LAN%20LAB4%2020261007090627 | 200 | 200 | PASS | [JSON](../evidence/http/T02.json) |
| T03 | GET /api/orchids/8 | 200 | 200 | PASS | [JSON](../evidence/http/T03.json) |
| T04 | GET /api/orchids/999999 | 404 | 404 | PASS | [JSON](../evidence/http/T04.json) |
| T06-MISSING | POST /api/orchids | 400 | 400 | PASS | [JSON](../evidence/http/T06-MISSING.json) |
| T06-INVALID | POST /api/orchids | 400 | 400 | PASS | [JSON](../evidence/http/T06-INVALID.json) |
| VALIDATION-BLANK | POST /api/orchids | 400 | 400 | PASS | [JSON](../evidence/http/VALIDATION-BLANK.json) |
| T07 | PUT /api/orchids/8 | 200 | 200 | PASS | [JSON](../evidence/http/T07.json) |
| T07-GET | GET /api/orchids/8 | 200 | 200 | PASS | [JSON](../evidence/http/T07-GET.json) |
| T08 | PUT /api/orchids/999999 | 404 | 404 | PASS | [JSON](../evidence/http/T08.json) |
| T07-INVALID | PUT /api/orchids/8 | 400 | 400 | PASS | [JSON](../evidence/http/T07-INVALID.json) |
| T07-ROLLBACK | GET /api/orchids/8 | 200 | 200 | PASS | [JSON](../evidence/http/T07-ROLLBACK.json) |
| T11 | SQL row/category_id, Unicode and schema | Updated row + FK + Unicode | SQL assertion passed | PASS | [row](../evidence/sqlserver/T11-row.txt), [schema](../evidence/sqlserver/schema-and-data.txt) |
| T12 | GET /api/orchids/8 | 200 | 200 | PASS | [JSON](../evidence/http/T12.json) |
| T09 | DELETE /api/orchids/8 | 204 | 204 | PASS | [JSON](../evidence/http/T09.json) |
| T09-GET | GET /api/orchids/8 | 404 | 404 | PASS | [JSON](../evidence/http/T09-GET.json) |
| T10 | DELETE /api/orchids/8 | 404 | 404 | PASS | [JSON](../evidence/http/T10.json) |
| B7-MALFORMED | POST /api/orchids | 400 | 400 | PASS | [JSON](../evidence/http/B7-MALFORMED.json) |
| B7-MEDIA | POST /api/orchids | 415 | 415 | PASS | [JSON](../evidence/http/B7-MEDIA.json) |
| B7-RETEST | POST /api/orchids | 201 | 201 | PASS | [JSON](../evidence/http/B7-RETEST.json) |
| B7-CLEANUP | DELETE /api/orchids/9 | 204 | 204 | PASS | [JSON](../evidence/http/B7-CLEANUP.json) |
| B5-RETEST | POST /api/orchids | 201 | 201 | PASS | [JSON](../evidence/http/B5-RETEST.json) |
| B6-RETEST | GET /api/orchids/10 | 200 | 200 | PASS | [JSON](../evidence/http/B6-RETEST.json) |
| B5-CLEANUP | DELETE /api/orchids/10 | 204 | 204 | PASS | [JSON](../evidence/http/B5-CLEANUP.json) |
| B8-ROUTE | GET /api/orchid | 404 | 404 | PASS | [JSON](../evidence/http/B8-ROUTE.json) |
| B8-RETEST | GET /api/orchids | 200 | 200 | PASS | [JSON](../evidence/http/B8-RETEST.json) |
| LEGACY-PATH | GET /orchids/ | 200 | 200 | PASS | [JSON](../evidence/http/LEGACY-PATH.json) |
