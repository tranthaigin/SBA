# SBA301 - Assignment 03: Securing REST Services with Spring Boot & JWT

Dự án triển khai bài tập lớn Assignment 03 theo tài liệu `Assignment 03_Securing REST Services with Spring Boot.pdf` và script cơ sở dữ liệu `Assignment 02_FUMiniHotelManagement.sql`.

## 1. Kiến trúc hệ thống

Dự án bao gồm 2 phần độc lập kết nối qua REST API bảo mật JWT:
- **Backend (`Assignment03/backend`)**:
  - Spring Boot 3.5, Java 21, Spring Data JPA, Hibernate, Spring Security 6, JWT (`jjwt` 0.12.6).
  - Kiến trúc 3 tầng chuẩn (Controller -> Service -> Repository) kết hợp Domain Entities (`Customer`, `RoomType`, `RoomInformation`, `BookingReservation`, `BookingDetail`).
  - Hỗ trợ cả H2 in-memory (chạy kiểm thử và phát triển tức thì) và Microsoft SQL Server.
  - Phân quyền nghiêm ngặt theo JWT:
    - Public: Xem thông tin phòng (`/api/rooms/public/**`) không cần đăng nhập.
    - Customer (`ROLE_CUSTOMER`): Đăng ký tài khoản, đăng nhập JWT, quản lý hồ sơ, đặt phòng trực tuyến (Booking Reservation), xem lịch sử đặt phòng.
    - Staff (`ROLE_STAFF`): Tài khoản được cấu hình trong `application.properties`, quản lý khách hàng, quản lý đơn đặt phòng và chi tiết đặt phòng, quản lý thông tin phòng.
    - Ràng buộc nghiệp vụ xóa phòng: Nếu phòng đã có lịch sử đặt phòng trong `BookingDetail`, hệ thống tự động chuyển trạng thái phòng sang không hoạt động (`roomStatus = 2`, soft delete); nếu chưa từng được thuê thì xóa vĩnh viễn khỏi CSDL.
- **Frontend (`Assignment03/frontend`)**:
  - React 19 SPA xây dựng trên Vite, Axios, React-Bootstrap.
  - Quản lý JWT tự động: Lưu token trong localStorage, tự động gán header `Authorization: Bearer <token>` qua Axios interceptor/defaults.
  - Giao diện đặt phòng trực tuyến, tính tổng tiền tự động, bảng quản lý dành riêng cho Staff và Customer.

## 2. Tài khoản thử nghiệm (Pre-seeded Accounts)

| Email | Password | Role | Nguồn cấu hình | Mô tả |
|---|---|---|---|---|
| `staff@hotel.com` | `staff123` | `ROLE_STAFF` | `application.properties` | Quản trị viên khách sạn (Room, Customer & Booking Management) |
| `customer@hotel.com` | `123456` | `ROLE_CUSTOMER` | CSDL / DataInitializer | Khách hàng (Online Room Booking & History) |

## 3. Kiểm thử tự động (Verification & Test Results)

### Backend:
```powershell
cd "Assignment03\backend"
.\mvnw.cmd test
```
Kết quả: **8/8 tests passed (100% SUCCESS)**:
1. `testPublicCanViewRooms`: Khách vãng lai xem thông tin phòng active mà không cần đăng nhập (200 OK).
2. `testStaffLoginProperties`: Đăng nhập Staff từ `application.properties` trả về JWT với `ROLE_STAFF`.
3. `testCustomerLogin`: Đăng nhập Customer trả về JWT với `ROLE_CUSTOMER`.
4. `testUnauthenticatedAccessRejected`: Chặn truy cập trái phép vào endpoint bảo mật (403 Forbidden).
5. `testRoleAccessDenied`: Customer bị từ chối khi cố truy cập endpoint của Staff (403 Forbidden).
6. `testStaffAccessAllowed`: Staff truy cập thành công endpoint quản trị với JWT hợp lệ.
7. `testCustomerOnlineBookingAndHistory`: Customer tạo đơn đặt phòng trực tuyến và tra cứu lịch sử đặt phòng thành công.
8. `testRoomDeleteLogic`: Kiểm tra logic xóa phòng: phòng chưa có giao dịch bị xóa hẳn (`hardDeleted = true`), phòng đã có lịch sử thuê chuyển trạng thái sang inactive (`hardDeleted = false`, `status = 2`).

### Frontend:
```powershell
cd "Assignment03\frontend"
npm run build
```
Kết quả: **419 modules transformed, built in ~3.3s, zero errors**.

## 4. Hướng dẫn chạy

1. **Khởi động Backend**:
   ```powershell
   cd "Assignment03\backend"
   .\mvnw.cmd spring-boot:run
   ```
   API chạy tại: `http://localhost:8083`

2. **Khởi động Frontend**:
   ```powershell
   cd "Assignment03\frontend"
   npm install
   npm run dev
   ```
   Giao diện chạy tại: `http://localhost:5173`
