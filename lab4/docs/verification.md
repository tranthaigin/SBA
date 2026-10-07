# Verification report - 2026-10-07 (Asia/Saigon)

## Kết quả hiện tại

| Phần | Kết quả | Phạm vi / bằng chứng |
|---|---|---|
| Clean build | PASS: 26 tests, 0 failures/errors/skips | `mvnw.cmd -B clean package`; `evidence/console/build-summary.txt` |
| Controller tests | 10 PASS | Mock service / HTTP contract |
| Service tests | 10 PASS | Repository mocks / business rules |
| Application context | 1 PASS | JPA/H2 test datasource |
| Persistence integration | 5 PASS | MockMvc + JPA/H2, CRUD/FK/rollback/validation/category identity |
| SQL Server container | Healthy | `127.0.0.1:14334`, volume `sba301-lab4_orchid-sqlserver-data` |
| SQL Server version | 2022 Developer RTM-CU27, 16.0.4295.3 | `evidence/sqlserver/schema-and-data.txt` |
| Datasource | OrchidDB / JDBC SQL Server | `evidence/console/SQLServer-initial-startup.txt` |
| CRUD/search/negative tests | PASS | `docs/test-matrix.md`, `evidence/http` |
| T11 PK/FK/row/Unicode | PASS | `T11-row.txt`, `schema-and-data.txt` |
| T12 application restart | PASS | HTTP GET giữ đúng dữ liệu; `T12-restart-row.txt` xác nhận row/Unicode trực tiếp trong SQL Server |
| DELETE postcondition | PASS | GET 404; `T09-delete.txt` có count=0, hai Category vẫn tồn tại |
| Postman collection / Newman | 22 requests, 30 assertions, 0 failures | `evidence/postman/newman-run.json`, `summary.txt` |

Schema có identity PK ở cả hai bảng; FK `orchids.category_id` tham chiếu `orchid_categories.category_id`. Tên Orchid/Category và mô tả dùng nvarchar. T11 so sánh description dưới dạng UTF-16 binary với payload tiếng Việt, tránh sai encoding qua terminal. T12 chạy lại assertion này sau khi khởi động lại ứng dụng.

## Cách tái lập

```powershell
cd "C:/SBA301/bài tập/sba-301/lab4"
.\mvnw.cmd -B clean package
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\init-local.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run.ps1 -SkipBuild
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verify-api.ps1 -VerifyDatabase -Restart
npm.cmd exec --yes --package=newman -- newman run postman/Orchid-Lab04.postman_collection.json --reporters cli,json --reporter-json-export evidence/postman/newman-run.json
```

Nếu API đang chạy, dừng bằng `scripts/stop.ps1` trước khi gọi `run.ps1`. Verification tạo/xóa các Orchid test và giữ Category; không xóa database/volume. Ứng dụng hiện chạy tại `http://localhost:8080/api/orchids`.

Trong lần SQL verification đầu, dấu phân cách `|` của sqlcmd bị Windows PowerShell làm mất quote rồi trở thành shell pipe. Đã đổi sang separator dấu phẩy; truy vấn SQL, full HTTP regression và restart sau đó đều pass. Record test từ lần lỗi đã được cleanup, có `evidence/http/failed-run-cleanup.json`.

## Lịch sử H2 và Docker

Khi Docker chưa chạy, unit/integration tests và live HTTP đã được kiểm tra bằng H2. Bằng chứng cũ nằm trong `evidence/h2`; live H2 dùng file database và `WRITE_DELAY=0`. Nó không được tính là bằng chứng SQL Server.

Một lần restart H2 tức thì bằng Stop-Process làm mất phần PUT chưa flush do write delay. Diagnostic launcher đã sửa sang flush đồng bộ và full H2 regression pass; xem `evidence/console/H2-restart-diagnosis.txt`.

Docker Desktop trước đó gặp lỗi Windows socket error 1920 với `dockerInference` và `engine.sock`. Log lịch sử nằm trong `evidence/console/docker-startup-failure.txt`. Hai thư mục runtime được giữ tại `%LOCALAPPDATA%/Docker/run.lab4-backup-20261007` và `%LOCALAPPDATA%/docker-secrets-engine.lab4-backup-20261007`. Sau khi người dùng mở Docker thành công, Lab4 đã tạo container riêng và hoàn thành SQL verification. Không reset Docker hoặc thay đổi database của Nexora.

## Phần người học bổ sung trước khi nộp

- Nếu giảng viên yêu cầu ảnh GUI: chụp Postman có URL/method/status/body và SSMS có schema PK/FK/SELECT. JSON HTTP, Newman và sqlcmd là bằng chứng tự động, không phải ảnh GUI.
- Tự thực hành và giải thích Human Verification Gate trong `oral-review.md`; tài liệu trả lời không chứng minh người học đã giải thích bằng lời.
- B8 Break-It đã kiểm tra biến thể client dùng route sai; chưa thực hiện biến thể sửa annotation mapping. B1-B4 chưa được cố tình gây lỗi.
- Code và bằng chứng được lưu trong `lab4/` của repository SBA. Bản trước khi đưa vào repository môn học có checkpoint `s18-lab04-sqlserver-verified` tại thư mục Lab4 ban đầu.

## Kiểm tra tại repository SBA

Bài đã được đưa vào `C:/SBA301/bài tập/sba-301/lab4`. Tại đường dẫn mới, clean package pass 26 Java tests; live SQL Server verification pass 28 HTTP requests, T11 row/FK/Unicode và T12 restart. Newman pass 22 requests / 30 assertions. Script launcher dùng jar path tương đối để tránh lỗi Unicode của Java launcher trên Windows; working directory vẫn là thư mục Lab4 này. Khi tiến trình thoát trước khi API ready, script báo lỗi ngay thay vì chờ hết timeout.
