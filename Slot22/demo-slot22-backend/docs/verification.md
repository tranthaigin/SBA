# Verification - Slot22

Ngày 13/10/2026. Đã đọc đủ 35 trang PDF; triển khai workbook 6.1, 6.2, 6.4, 6.5 và evidence bắt buộc. Research prompts/production radar là phần mở rộng nhận thức, không được biến thành tính năng ngoài scope.

## Pre-flight

Java 21.0.11, Maven Wrapper 3.9.12, SQL Server `.\SQL2022` kết nối được và `SELECT 1` trả 1. Project mới compile baseline trước khi thêm domain code. Database `FUNewsManagementSBA301` được tạo mới cho bài này; không sửa database/source Lab05. Runtime local chọn port 8082, mặc định source vẫn 8080 như PDF cho phép.

## Gate G1-G10

| Gate | Evidence thực tế | Kết quả |
| --- | --- | --- |
| G1 Build | `mvnw.cmd -B clean test package`, 15 Java tests, 0 failure/error/skipped; JAR tạo thành công | PASS |
| G2 DB | Đúng database; 3 Category seed và một NewsArticle liên kết Category 1; PK/FK/Unicode schema | PASS |
| G3 Entity | Category + NewsArticle mapping đúng; startup với `ddl-auto=validate` thành công | PASS |
| G4 Repository | JpaRepository, derived queries search và kiểm tra tham chiếu; query thật qua SQL | PASS |
| G5 Service | Transaction, duplicate, missing ID/FK và delete rule; unit tests + HTTP/SQL | PASS |
| G6 Controller | Category/NewsArticle GET/POST/PUT/DELETE/search; collection 33 request | PASS |
| G7 HTTP | 200/201/204/400/404/409 đạt; workshop 415 đạt | PASS |
| G8 Persistence | Snapshot SQL sau từng request; INSERT/UPDATE/DELETE đúng; các ca reject không mutation | PASS |
| G9 Trace | POST Category: source/method chain, handler/SQL log, response và row SQL | PASS |
| G10 Explain | README/API contract và execution trace giải thích Controller-Service-Repository-JPA-SQL; có breakpoint plan để sinh viên tự kiểm chứng | Tài liệu đã cung cấp |

T01-T12: 12/12 ca bắt buộc PASS. Phần mở rộng NewsArticle và các ca validation/paging/duplicate bổ sung đều PASS. Newman: 33 request, 49 assertion, 0 failure. SQL được đọc từ connection riêng ngay sau từng HTTP response; sau cleanup, snapshot 3 Category và một NewsArticle mẫu bằng snapshot trước test.

## Evidence và giới hạn

- [Build/test output](../evidence/build.txt), [test summary](../evidence/test-summary.json), [dependency tree](../evidence/dependencies.txt).
- [Startup validate](../evidence/startup.txt), [SQL schema/seed](../evidence/sqlserver.txt).
- [Newman run](../evidence/newman-run.json), [API/SQL snapshots](../evidence/api-sql.json), [handler/SQL trace](../evidence/sql-trace.txt).
- [Test matrix](test-matrix.md), [execution trace](execution-trace.md), [Break/Fix](break-it.md), [contract cho Part B](api-contract.md).

Controller/Service Java tests dùng MockMvc/Mockito; không chứng minh SQL persistence. Persistence được kiểm chứng riêng với SQL Server thật. Postman collection chạy bằng Newman; database được query bằng sqlcmd. Không có screenshot thao tác Postman/SSMS/IDE; query output và debug note được PDF chấp nhận. Chưa có kiểm chứng vấn đáp của giảng viên/sinh viên; G10 cung cấp giải thích và kế hoạch tự trace, không thay phần human verification.

NewsArticle dùng mapping tối thiểu được mục 6.2 cho phép, không phải toàn bộ schema Assignment 02. Không có React, security/JWT, migration framework hoặc các cơ chế production awareness như optimistic locking.
