# AI Usage Log

| Mục đích | Nội dung AI hỗ trợ | Phần áp dụng | Cách kiểm chứng | Điều chỉnh |
|---|---|---|---|---|
| Phân tích đề | Đọc PDF và lập danh sách 12 requirement | Scope, RTM và checklist | Đối chiếu từng dòng với Student Guide | Loại bỏ backend/database/JWT vì không bắt buộc |
| Cấu trúc React | Gợi ý component tree và state ownership | `App`, `components`, `pages`, `hooks`, `data` | Lint, build và smoke test trên trình duyệt | Chọn state navigation thay vì Router để giữ đúng scope cơ bản |
| CRUD và validation | Hỗ trợ tạo handlers immutable cho 3 entity | Category, News, Users pages | Test create invalid/valid, update, search, delete cancel | Thêm unique validation và Category relation guard |
| Logo | Tạo logo news + broadcast cho FUNews | `public/assets/funews-logo.png` | Kiểm tra hiển thị tại Login, Sidebar và kích thước nhỏ | Dùng ảnh nền trong suốt, không chứa chữ để tránh lỗi text |
| Kiểm thử | Gợi ý và chạy smoke test theo test matrix | `docs/TEST_MATRIX.md` | Browser test, console log check, lint và production build | Ghi rõ giới hạn của các case chỉ kiểm tra bằng code review |

## Prompt logo

Tạo logo dạng flat vector-like cho FUNewsManagementSystem, kết hợp tờ báo gấp và biểu tượng broadcast, màu navy/coral, không có chữ, nền trong suốt, đọc rõ ở kích thước 40px, không watermark.

## Cam kết kiểm chứng

Output AI không được xem là đúng mặc định. Source đã được kiểm tra bằng `npm run lint`, `npm run build`, chạy ứng dụng thật trên trình duyệt và thực hiện smoke test cho các flow chính.
