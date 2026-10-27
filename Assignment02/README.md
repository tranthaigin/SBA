# SBA301 - Assignment 02: Implement CRUD Application with RESTful Web Service and ReactJS

Dự án triển khai bài tập lớn Assignment 02 theo tài liệu `Assignment 02_Implement CRUD Application with RESTful Web Service and ReactJS.pdf` và script cơ sở dữ liệu `Assignment 02_FUNewsManagementSBA301.sql`.

## 1. Kiến trúc hệ thống

Dự án bao gồm 2 phần độc lập kết nối qua REST API:
- **Backend (`Assignment02/backend`)**:
  - Spring Boot 3.5, Java 21, Spring Data JPA, Hibernate, Bean Validation.
  - Kiến trúc 3 tầng chuẩn (Controller -> Service -> Repository) kết hợp Domain Entities (`Category`, `SystemAccount`, `NewsArticle`, `Tag`).
  - Hỗ trợ cả H2 in-memory (chạy kiểm thử và phát triển tức thì) và Microsoft SQL Server.
  - Quản lý ràng buộc nghiệp vụ: Không cho phép xóa Account nếu đã tạo bài báo; Không cho phép xóa Category nếu đã gắn bài báo.
- **Frontend (`Assignment02/frontend`)**:
  - React 19 SPA xây dựng trên Vite, Axios, React-Bootstrap.
  - Phân quyền người dùng theo Role:
    - **Public (khách vãng lai)**: Xem danh sách bài báo Active, tìm kiếm bài viết, xem chi tiết bài báo.
    - **Staff (Role 2)**: Quản lý danh mục (Categories CRUD, modal, tìm kiếm), Quản lý bài báo (News CRUD, modal popup, tags), Xem lịch sử bài viết cá nhân đã tạo (Publishing History).
    - **Admin (Role 1)**: Quản lý tài khoản hệ thống (System Accounts CRUD, modal popup, tìm kiếm, kiểm tra ràng buộc xóa).

## 2. Tài khoản thử nghiệm (Pre-seeded Accounts)

| Email | Password | Role | Mô tả |
|---|---|---|---|
| `admin@funews.com` | `@1` | 1 | Quản trị viên hệ thống (Account Management) |
| `staff@funews.com` | `@1` | 2 | Nhân viên biên tập (News & Category Management) |

## 3. Kiểm thử tự động (Verification & Test Results)

### Backend:
```powershell
cd "Assignment02\backend"
.\mvnw.cmd test
```
Kết quả: **6/6 tests passed (100% SUCCESS)**:
1. `testPublicCanViewActiveNews`: Khách vãng lai xem danh sách bài viết active mà không cần đăng nhập.
2. `testAuthSuccess`: Đăng nhập thành công với tài khoản Admin và Staff.
3. `testAuthFailure`: Từ chối đăng nhập sai mật khẩu (409 Conflict).
4. `testCategoryCrudAndConstraint`: CRUD Category và chặn xóa danh mục khi có bài báo liên kết.
5. `testAccountCrudAndConstraint`: CRUD Account và chặn xóa tài khoản khi là người tạo bài báo.
6. `testNewsSearchAndCreatedByFilter`: Tìm kiếm bài báo theo từ khóa và lọc bài theo người tạo.

### Frontend:
```powershell
cd "Assignment02\frontend"
npm run build
```
Kết quả: **424 modules transformed, built in ~3.3s, zero errors**.

## 4. Hướng dẫn chạy

1. **Khởi động Backend**:
   ```powershell
   cd "Assignment02\backend"
   .\mvnw.cmd spring-boot:run
   ```
   API chạy tại: `http://localhost:8082`

2. **Khởi động Frontend**:
   ```powershell
   cd "Assignment02\frontend"
   npm install
   npm run dev
   ```
   Giao diện chạy tại: `http://localhost:5173`
