# Test Matrix

Ngày kiểm tra: 2026-09-29

Môi trường: Vite development server, Codex in-app browser, desktop viewport. `npm run lint` và `npm run build` đều hoàn tất với exit code 0; browser console không có error/warning.

| # | Nhóm | Case | Kết quả mong đợi | Actual / Evidence | Kết quả |
|---:|---|---|---|---|---|
| 1 | Login | Username/password rỗng | Không login; có validation | Hiện `Username and password are required.` | Pass |
| 2 | Login | Sai credential | Không vào admin | Hiện `Incorrect username or password.` | Pass |
| 3 | Login | `Admin/Admin` | Vào admin page | Dashboard hiển thị | Pass |
| 4 | Auth | Logout | Về Login; admin layout ẩn | Login page hiển thị; đăng nhập lại thành công | Pass |
| 5 | Layout | Menu completeness | Đủ 5 mục | Dashboard, Category, News, Users, Settings đều hiển thị | Pass |
| 6 | Navigation | Mở từng menu | Đúng nội dung, không full reload | Header page title và content đổi theo state navigation | Pass |
| 7 | Category | Read | Danh sách đúng | 3 mock categories hiển thị với ID/status | Pass |
| 8 | Category | Create valid | Record mới xuất hiện | Tạo `Events` thành công | Pass |
| 9 | Category | Create invalid | Không thêm; có feedback | Form rỗng bị chặn | Pass |
| 10 | Category | Update | Đúng record cập nhật | Đổi thành `Events Updated`, status Inactive | Pass |
| 11 | Category | Delete cancel | Record vẫn còn | Mở confirmation rồi Cancel; record còn | Pass |
| 12 | Category | Delete confirm | Đúng record bị xóa | Confirm handler dùng immutable `filter`; dialog wiring kiểm tra trong source | Pass (code path) |
| 13 | Category | Search hit | Lọc đúng | Keyword có khoảng trắng/khác hoa thường tìm được record | Pass |
| 14 | Category | Search miss | No-result state | Hiện `No matching categories` | Pass |
| 15 | News | Read | Danh sách đúng | 3 mock articles hiển thị đúng Category/Author/Status | Pass |
| 16 | News | Create valid | Record mới xuất hiện | Tạo `Test bulletin` thành công | Pass |
| 17 | News | Create invalid | Không thêm; có feedback | Thiếu Title/Content bị chặn | Pass |
| 18 | News | Update | Đúng record cập nhật | Đổi title, Category sang Technology, status Inactive | Pass |
| 19 | News | Delete cancel | Record vẫn còn | Mở confirmation rồi Cancel; record còn | Pass |
| 20 | News | Delete confirm | Đúng record bị xóa | Confirm handler dùng immutable `filter`; dialog wiring kiểm tra trong source | Pass (code path) |
| 21 | News | Search hit | Lọc đúng | Search title không phân biệt hoa/thường và trim keyword | Pass |
| 22 | News | Search miss | No-result state | Hiện `No matching news articles` | Pass |
| 23 | Users | Read | Danh sách đúng | Admin/staff01/staff02 hiển thị | Pass |
| 24 | Users | Create valid | Record mới xuất hiện | Tạo `reviewer` thành công | Pass |
| 25 | Users | Create invalid | Không thêm; có feedback | Thiếu Username/Password bị chặn | Pass |
| 26 | Users | Update | Đúng record cập nhật | Đổi reviewer thành Admin + Inactive | Pass |
| 27 | Users | Delete cancel | Record vẫn còn | Mở confirmation rồi Cancel; record còn | Pass |
| 28 | Users | Delete confirm | Đúng record bị xóa | Confirm handler dùng immutable `filter`; dialog wiring kiểm tra trong source | Pass (code path) |
| 29 | Users | Search hit | Lọc đúng | Keyword khác hoa thường tìm được reviewer | Pass |
| 30 | Users | Search miss | No-result state | Hiện `No matching users` | Pass |
| 31 | News | Category relation | Category phải tồn tại | Select chỉ lấy từ categories; submit kiểm tra category ID tồn tại | Pass |
| 32 | User | Role mapping | 1/2 thành Admin/Staff | Mock list và update hiển thị đúng nhãn | Pass |
| 33 | Status | Status mapping | 0/1 thành Inactive/Active | Category, News, Users dùng `StatusBadge` | Pass |
| 34 | Data | Empty/no-result state | UI không crash, có thông báo | No-result đã browser-test; source-empty branch có message riêng | Pass |
| 35 | Persistence | Reload sau CRUD | Session/data theo design | Reload vẫn còn session và tổng 4/4/4 | Pass |

## Ghi chú phạm vi evidence

- Các case ghi `Pass (code path)` được kiểm tra qua dialog trên browser, Cancel flow, handler Confirm trong source và production build; thao tác xóa cuối không được thực hiện trong smoke test để giữ nguyên bộ dữ liệu evidence vừa tạo.
- Dữ liệu kiểm thử chỉ nằm trong localStorage của browser test, không thay đổi mock data khởi tạo trong source.
