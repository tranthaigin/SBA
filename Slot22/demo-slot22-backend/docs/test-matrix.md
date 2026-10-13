# Test matrix Slot22

Kiểm tra ngày 13/10/2026 tại `http://localhost:8082` với SQL Server thật, `FUNewsManagementSBA301`. Mỗi request được đối chiếu snapshot Category + NewsArticle; [API/SQL evidence](../evidence/api-sql.json), [Newman report](../evidence/newman-run.json).

| ID | Request | HTTP mong đợi/thực tế | SQL hậu điều kiện | Kết quả |
| --- | --- | --- | --- | --- |
| T01 | GET categories | 200 | 3 seed, không mutation | PASS |
| T02 | GET Category 1 | 200 | Cùng row Academic news | PASS |
| T03 | GET Category 32767 | 404 | Không đổi | PASS |
| T04 | POST valid | 201 | INSERT Technology News, ID tự sinh | PASS |
| T05 | POST invalid | 400 | Không INSERT | PASS |
| T06 | POST duplicate | 409 | Không tạo duplicate | PASS |
| T07 | PUT existing | 200 | Giữ ID; cập nhật name/description/parent/active | PASS |
| T08 | PUT missing | 404 | Không UPDATE/INSERT | PASS |
| T09 | DELETE free Category | 204, GET lại 404 | Row test bị xóa | PASS |
| T10 | DELETE Category 1 đang được dùng | 409 | Category 1 và article mẫu giữ nguyên | PASS |
| T11 | Search keyword ACAdemic | 200 | Chỉ Academic news; DB không đổi | PASS |
| T12 | page=1, size=1, sort=categoryName,asc | 200 | Page có 1 row Campus Safety; DB không đổi | PASS |

T07 dùng Category vừa tạo tại T04 thay vì sửa seed, để giữ dữ liệu gốc cho các bước sau. Test thêm: tên trùng sau trim/ignore-case trả 409; PUT trùng trả 409 và giữ giá trị cũ; size=101 bị giới hạn 100; sort field sai trả 400.

## NewsArticle extension

| ID | Kịch bản | HTTP | SQL hậu điều kiện | Kết quả |
| --- | --- | --- | --- | --- |
| N01 | List seed | 200 | Có S22-SEED-001, Category 1 | PASS |
| N02 | Create article, Category 2 | 201 | INSERT đúng ID/FK | PASS |
| N03 | Read article | 200 | Cùng row | PASS |
| N04 | Create ID trùng | 409 | Không ghi đè article | PASS |
| N05 | Input invalid | 400 | Không INSERT | PASS |
| N06 | Create với Category thiếu | 404 | Không INSERT/FK sai | PASS |
| N07 | Update quan hệ sang Category 3 | 200 | Giữ ID, đổi FK | PASS |
| N08 | Update sang Category thiếu | 404 | Giữ nguyên FK Category 3 | PASS |
| N09 | Update article thiếu | 404 | Không INSERT | PASS |
| N10 | Search keyword temp, size=1 | 200 | Chỉ article test; DB không đổi | PASS |
| N11 | Delete article | 204 | Row test bị xóa | PASS |
| N12 | Read article đã xóa | 404 | Không đổi | PASS |

Workshop B01/B02/B03: `text/plain` trả 415 và DB không đổi; sửa JSON trả 201 và có row; cleanup trả 204. Tổng 33 HTTP request, 49 Postman assertion, 0 lỗi; tất cả snapshot SQL khớp state mong đợi. Sau test, 3 Category seed và một NewsArticle seed có giá trị đúng như trước test.

Java tests: 5 Controller MockMvc, 6 Category Service Mockito, 4 NewsArticle Service Mockito; tổng 15, không failure/error/skipped. Đây là test HTTP boundary/business logic với mocks; bằng chứng persistence là bộ HTTP + SQL riêng, không phải các mocks.
