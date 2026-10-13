# Kiểm chứng Lab05

Ngày kiểm tra: 13/10/2026. Đã hoàn tất E01–E08 trong PDF Lab05; không bổ sung tính năng ngoài bài.

## Môi trường và kết quả

- Backend JDK 21: `mvnw.cmd -q -DskipTests package` thành công; không bổ sung automated Java tests.
- Frontend: `npm.cmd install`, kiểm tra dependency và `npm.cmd run build` thành công.
- SQL Server thật: instance `.\SQL2022`, database `SBA301_OrchidDB`, bảng `dbo.Orchid`; đối chiếu bằng `sqlcmd` với Windows Authentication. Có đúng 10 dòng seed của PDF.
- Postman collection chạy bằng Newman: **9 request, 15 assertion, 0 lỗi**; đã kiểm tra backend độc lập trước khi chạy Vite.
- UI được thao tác bằng Playwright với Chrome đã cài (`channel: chrome`), chế độ headless, viewport **1280 × 900**. Có ảnh chụp trang, JSON Network/CDP và HAR lưu request API thực tế từ browser; đã xem ảnh của các ca CRUD, lỗi và sau khôi phục.
- Sau kiểm tra đã khởi động lại backend, khôi phục `.env` và Vite tại `http://localhost:5173`; GET `http://localhost:8080/orchids/` trả 200 và UI có 10 dòng. Vite tạm tại port 5174 đã dừng. Lượt QA này không sửa mã ứng dụng.

## Test matrix theo trang 17

| ID | Kịch bản | Kết quả thực tế |
| --- | --- | --- |
| E01 | Mở list | **PASS** — browser GET 200; UI 10 dòng = JSON 10 dòng = SQL 10 dòng; CORS allow-origin đúng `http://localhost:5173`. |
| E02 | Create valid | **PASS** — nhập đủ 6 trường, POST 201 tạo ID 13; UI tăng lên 11 dòng; cả 6 giá trị được lưu đúng trong SQL. |
| E03 | Edit existing | **PASS** — GET detail 200 nạp đúng 6 trường; sửa cả 6, PUT 200 giữ ID 13; UI và SQL cập nhật đúng, hai checkbox từ true thành false. |
| E04 | Delete existing | **PASS** — chấp nhận `Delete this orchid?` cho dòng kiểm thử; DELETE 204, dòng biến mất trên UI và SQL; GET lại 404. Còn nguyên 10 seed ban đầu. |
| E05 | GET missing | **PASS** — mở `/orchids/9999999/edit`, GET 404; UI hiển thị `Request failed with status code 404`; không crash, DB không đổi. |
| E06 | Backend stop | **PASS** — dừng backend thật; browser GET 8080 lỗi `net::ERR_CONNECTION_REFUSED`, UI báo lỗi; SQL vẫn 10 dòng. Sau đó khởi động lại backend. |
| E07 | Sai API URL | **PASS** — đổi `.env` sang API port 9999 và restart Vite; browser gọi đúng URL sai, lỗi `net::ERR_CONNECTION_REFUSED`, UI báo lỗi; SQL vẫn 10 dòng. Khôi phục và kiểm tra lại thành công. |
| E08 | CORS bị chặn | **PASS** — mở Vite tạm ở origin `http://localhost:5174`; Chrome chặn request do `MissingAllowOriginHeader`, UI không tải được list. Backend 8080 vẫn GET 200; SQL vẫn 10 dòng. |

List → Create → Edit → List hoạt động theo SPA: đã kiểm tra `performance.timeOrigin` giữ nguyên từ trang ban đầu sang Create và sau toàn bộ CRUD, trước bước mở trực tiếp URL ID thiếu. Các HAR chỉ giữ request API. Chuyển từ Edit đã tải xong sang Create đặt lại form trống. Không có JavaScript `pageerror` trong các lượt kiểm tra; console ghi nhận các lỗi mạng/404 của ca âm và favicon 404.

## Đối chiếu SQL sau CRUD

Browser tạo `Taichung Beauty` với Name, Category, Description, Image URL, Natural và Attractive. Khi sửa, cả 6 trường đổi và vẫn giữ ID 13. [Bằng chứng SQL](browser/ui-positive-sql.json) lưu giá trị sau INSERT/UPDATE, kết quả DELETE bằng 0 dòng cho ID 13 và snapshot trước/sau: **10 dòng seed có giá trị không đổi**.

## Break-It → Diagnose → Fix

1. **Break:** đổi `VITE_API_URL` trong `.env` sang `http://localhost:9999/orchids`, restart Vite và mở list; UI báo lỗi.
2. **Diagnose:** [Network JSON](browser/e07.json) và [HAR](browser/e07.har) cho thấy GET đi tới `http://localhost:9999/orchids/`, lỗi `net::ERR_CONNECTION_REFUSED`.
3. **Fix:** khôi phục URL `http://localhost:8080/orchids`, restart Vite; [Network sau sửa](browser/fixed.json) ghi GET 200, đúng CORS allow-origin 5173; [ảnh sau sửa](browser/fixed.png) có đủ 10 dòng, SQL vẫn 10 dòng.

Ảnh [trước sửa](browser/e07.png) và [sau sửa](browser/fixed.png) chụp UI; bằng chứng Network là JSON/CDP và HAR thu từ Chrome. Các HAR giữ request API và loại tài nguyên Vite; không có ảnh giao diện DevTools. Ca Axios từ terminal trước đó cũng đã xác nhận [POST detail 405 → GET detail 200](break-fix.json).

## Bằng chứng

- CRUD UI/API: [report](browser/ui-positive.json), [HAR](browser/ui-positive.har), [SQL](browser/ui-positive-sql.json).
- Ảnh CRUD: [list](browser/E01-list.png), [create](browser/E02-create-form.png), [dòng vừa tạo](browser/E02-created-list.png), [edit nạp dữ liệu](browser/E03-edit-loaded.png), [edit đã sửa](browser/E03-edit-form.png), [list sau sửa](browser/E03-updated-list.png), [list sau xóa](browser/E04-deleted-list.png), [ID thiếu](browser/E05-missing-id.png).
- E06: [report/Network](browser/e06.json), [HAR](browser/e06.har), [ảnh](browser/e06.png).
- E07: [report/Network](browser/e07.json), [HAR](browser/e07.har), [ảnh](browser/e07.png).
- E08: [report/Network](browser/e08.json), [HAR](browser/e08.har), [ảnh](browser/e08.png).
- Sau khôi phục: [report/Network](browser/fixed.json), [HAR](browser/fixed.har), [ảnh](browser/fixed.png).
- Kiểm tra trước browser: [Postman/Newman](postman-run.json), [API và SQL](api-sql.json), [schema và seed](sqlserver.txt), [CORS HTTP](cors-http.json), [build/startup](build-startup.txt).

Phạm vi bằng chứng: runtime local với Chrome headless và SQL Server thật; Postman được chạy bằng Newman, SQL được kiểm tra qua CLI, không có ảnh thao tác Postman hoặc SSMS. Hướng dẫn chạy và luồng React → API → DB → React nằm trong [README](../README.md).
