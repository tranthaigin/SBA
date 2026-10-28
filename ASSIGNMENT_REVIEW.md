# Đối chiếu Assignment 01, 02, 03

Sinh viên: **Tran Quoc Thai — CE190243 — SE1910**. Ngày kiểm tra: 28/10/2026.

## Nguồn và quy ước tên

- A1: `Tài Liệu/SBA301_Assignment01_Student_Guide.pdf`, mục 0 và R01: `StudentName_ClassCode` → `TranQuocThai_SE1910`.
- A2: `Tài Liệu/Assignment 02_Implement CRUD Application with RESTful Web Service and ReactJS.pdf`, mục 5: `A2StudentName_ClassCode` → `A2TranQuocThai_SE1910`.
- A3: `Tài Liệu/Assignment 03_Securing REST Services with Spring Boot.pdf`, mục 5: `A3StudentName_ClassCode` → `A3TranQuocThai_SE1910`.
- MSSV ghi trong README, không tự chèn vào mẫu tên mà đề không yêu cầu. A2/A3 giữ backend/frontend trong thư mục bài; Maven project name và application name đúng tên đề; npm name dùng chữ thường theo quy tắc npm. Database đúng tên A2/A3.

## Kết quả đối chiếu source và sửa lỗi

| Bài | Yêu cầu | Đối chiếu / thay đổi |
|---|---|---|
| A1 | React/Vite, mock Admin/Admin, logo, Header/layout, Dashboard/Category/News/Users/Settings | Đã có; giữ chức năng hiện có. Sửa folder, package/lock và thông tin sinh viên, bỏ hướng dẫn đặt tên cũ sai trong README. |
| A1 | Category/News/User CRUD + Search, popup Create/Update, confirmation Delete, mock data | Đã có trong các Page, Modal và ConfirmDialog; không thêm backend hay phân quyền ngoài core scope A1. |
| A2 | Spring Boot 3, SQL Server/JPA, 3 lớp, React/Axios | Đổi runtime H2 thành SQL Server đúng tên; H2 chỉ còn cho tests. Tags đi qua Service. Sửa proxy frontend/backend và hợp đồng API Category. |
| A2 | Public chỉ xem tin Active; Admin quản lý Account; Staff quản lý Category/News | API kiểm tra session/role. Public search chỉ lọc tin Active. Tác giả và lịch sử lấy theo tài khoản đăng nhập. |
| A2 | Category/News/Account CRUD + Search, popup Create/Update, confirmation Delete | Sửa Category list đang đòi Page trong khi API trả array, sai tên description/status/search; thay trang edit bằng popup. News/Account đã có popup/search/confirmation. |
| A2 | Tags, hồ sơ Staff, lịch sử tin, ràng buộc xóa | Thêm chọn tags và hiển thị tags, trang hồ sơ; giữ history. Test chặn xóa Category có tin và Account đã tạo tin. Sửa update Account để trống password giữ mật khẩu cũ. |
| A3 | SQL Server, JPA/3 lớp, JWT, Staff từ properties, public xem phòng | Đổi default runtime sang SQL Server đúng tên. Giữ JWT và tài khoản Staff từ application.properties. |
| A3 | Staff quản lý khách hàng/phòng/booking + details, CRUD/Search | Customer/Room đã có CRUD/Search. Bổ sung Create/Update/Delete/Search booking và form nhiều phòng, giữ cập nhật trạng thái. |
| A3 | Customer đăng ký, hồ sơ, đặt một/nhiều phòng, lịch sử | Đăng ký/đăng nhập/history đã có; bổ sung trang hồ sơ và chọn thêm nhiều phòng trong cùng booking. |
| A3 | Xóa phòng có booking detail chỉ đổi status; validate dữ liệu | Giữ hard/soft delete đúng điều kiện; thêm nested validation cho booking items, kiểm tra ngày/giá/phòng/trùng phòng, tính tổng từ chi tiết; online dùng giá phòng từ database. |

## Kiểm tra thực hiện trong lần sửa này

- A1: `npm run lint`, `npm run build` thành công. Chrome: đăng nhập Admin/Admin và Dashboard có đủ menu. Các case CRUD trong test matrix cũ không được coi là đã chạy lại toàn bộ lần này.
- A2: `mvnw.cmd clean package` thành công, **7 tests / 0 failure / 0 error**. Tests dùng H2 riêng, bao gồm đăng nhập, CRUD/ràng buộc xóa, tìm kiếm/lịch sử, anonymous/wrong role bị chặn, response không lộ password.
- A3: `mvnw.cmd package` thành công, **9 tests / 0 failure / 0 error**. Bao gồm public/JWT/role, đặt phòng/history, hard/soft room delete, Staff booking CRUD và nested validation.
- A2 và A3: `npm run build` thành công sau thay đổi cuối.
- Runtime: cả hai backend khởi động thành công với Microsoft SQL Server trên máy này; JPA tạo bảng trong hai database tên mới. Không đổi database Lab cũ.
- Chrome + SQL Server A2: Public đọc tin; Staff đăng nhập, đọc/sửa Category bằng popup và lưu hồ sơ.
- Chrome + SQL Server A3: Staff đăng nhập, tạo/sửa/xóa booking; Customer đăng nhập, xem/lưu hồ sơ, đặt hai phòng trong cùng booking, history hiển thị đủ hai phòng và tổng tiền.
- Collection Postman `A3TranQuocThai_SE1910/backend/postman/A3TranQuocThai_SE1910.postman_collection.json`: chạy bằng Newman trên runtime SQL Server, **11 requests / 14 assertions / 0 failure** (public, JWT, role, booking CRUD, total price, profile/history).
- `git diff --check` được kiểm tra trước commit.

## Phạm vi xác nhận

Build, source review, H2 integration tests và các luồng browser/SQL Server trên là các loại bằng chứng riêng. Chưa chạy lại toàn bộ tổ hợp CRUD/edge case bằng browser; chưa kiểm tra trên máy khác hoặc môi trường triển khai. Không tự thêm chức năng kinh doanh ngoài yêu cầu đề.

Thông tin kết nối SQL Server local nằm trong `backend/.env` bị Git ignore. Máy khác phải tự cấu hình DB_USERNAME/DB_PASSWORD theo README. Không commit mật khẩu database.
