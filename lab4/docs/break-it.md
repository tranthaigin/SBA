# Break-It lab - symptom, root cause, fix, retest

Các ca dưới đây được thực thi qua HTTP trên ứng dụng chạy bằng SQL Server. Không sửa model final để cố tình giữ lỗi. B8 dùng biến thể client gọi path sai để tái hiện route mismatch; chưa thực hiện biến thể thay annotation mapping. Các lỗi cấu hình SQL Server/JPA metadata B1-B4 chưa thực thi trên SQL Server.

| Case | Thao tác gây lỗi | Expected / Actual | Layer và root cause | Fix | Retest / Evidence |
|---|---|---|---|---|---|
| B5 | POST với `categoryId=999999` | 400 / 400, `Category not found: 999999` | Service: Category không tồn tại, relationship không hợp lệ | Lấy ID thật bằng GET categories rồi gửi lại | `T06-INVALID.json` trước; `B5-RETEST.json` 201 sau |
| B6 | GET `/api/orchids/999999` | 404 / 404 | API/resource: ID không tồn tại; database vẫn hoạt động | Dùng ID do POST trả về | `T04.json` trước; `B6-RETEST.json` 200 sau |
| B7 | Body `{` hoặc Content-Type `text/plain` | 400/415 / 400/415 | HTTP/Jackson: JSON không hợp lệ hoặc media type không được hỗ trợ | Body JSON hoàn chỉnh, `application/json` | `B7-MALFORMED.json`, `B7-MEDIA.json` trước; `B7-RETEST.json` 201 sau |
| B8 | GET `/api/orchid` thiếu `s` | 404 / 404 | Routing: URL không khớp `@RequestMapping` | GET `/api/orchids` đúng contract | `B8-ROUTE.json` trước; `B8-RETEST.json` 200 sau |

Evidence nằm trong `evidence/http`. Request sửa lỗi có ID mới được cleanup sau retest. Full CRUD regression đã chạy cùng bộ verification. Rejected PUT cũng được kiểm tra GET hậu điều kiện: `T07-INVALID.json` và `T07-ROLLBACK.json`; dữ liệu cũ không bị thay đổi.

## Bài học

- Phân biệt route 404 và resource 404 bằng URL và controller mapping.
- Phân biệt 400 JSON syntax, 400 business relationship và 415 media type; không đổi tất cả thành 500.
- Không tạo Category tùy ý từ nested request: resolve từ repository tại Service.
- Sau mỗi sửa lỗi phải kiểm tra state/hậu điều kiện, không chỉ response status.
- SQL Server startup failure là lỗi môi trường. H2 pass không thay thế T11/T12 SQL; các kiểm tra SQL hiện đã được thực thi và pass.
