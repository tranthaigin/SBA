# Assignment 03 — A3TranQuocThai_SE1910

Tran Quoc Thai — CE190243 — SE1910

Đề: `Tài Liệu/Assignment 03_Securing REST Services with Spring Boot.pdf`.

## Tên và cấu trúc

- Folder nộp bài: `A3TranQuocThai_SE1910`.
- Spring Initializr project trong `backend`: Maven artifactId/name và spring.application.name là `A3TranQuocThai_SE1910`.
- React project trong `frontend`: package name `a3tranquocthai_se1910` (npm yêu cầu chữ thường).
- Database SQL Server: `A3TranQuocThai_SE1910`.
- Backend theo Controller → Service → Repository; React gọi REST bằng Axios.

## Chức năng theo đề

- Public: xem phòng; Customer: đăng ký/đăng nhập JWT, cập nhật hồ sơ, đặt một hoặc nhiều phòng, xem lịch sử.
- Staff: tài khoản từ application.properties; CRUD/Search khách hàng, phòng, đặt phòng kèm chi tiết; cập nhật trạng thái đặt phòng.
- Xóa phòng chưa có booking detail: xóa thật; đã có booking detail: chuyển inactive.

## Chạy ứng dụng

Java 21, Node.js 20.19+ hoặc 22.12+, SQL Server có SQL authentication. Tạo database trống `A3TranQuocThai_SE1910` bằng SSMS; JPA tạo bảng, DataInitializer thêm dữ liệu mẫu khi database rỗng. Không chạy SQL mẫu của đề vào database khác hoặc thay database mẫu đang có.

Mở terminal tại `backend`:

```powershell
$env:DB_USERNAME = "sa"
$env:DB_PASSWORD = "<your SQL Server password>"
# Mặc định localhost:1433; nếu cần đổi server/port:
# $env:DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=A3TranQuocThai_SE1910;encrypt=true;trustServerCertificate=true"
.\mvnw.cmd spring-boot:run
```

Backend: `http://localhost:8083`. Mở terminal thứ hai tại `frontend`:

```powershell
npm ci
npm run dev
```

Frontend: `http://localhost:5183`. Vite proxy `/api` đến backend `8083`; không cần `.env`. Dữ liệu lưu SQL Server qua các lần chạy. Production build cần reverse proxy `/api` tương tự.

Tài khoản mẫu: staff@hotel.com / staff123 (Staff); customer@hotel.com / 123456 (Customer).

## Kiểm tra

```powershell
# backend
.\mvnw.cmd clean package
# frontend
npm run build
```

Backend tests dùng H2 riêng trong `src/test/resources/application.properties`, không ghi vào SQL Server thật. Xem `../ASSIGNMENT_REVIEW.md` để phân biệt kết quả test tự động và kiểm tra runtime.

Trên máy đã cấu hình local, có thể nạp file `.env` bị Git ignore từ thư mục `backend` trước khi chạy:

```powershell
foreach ($line in Get-Content -LiteralPath .env) {
  if ($line -match "^(DB_USERNAME|DB_PASSWORD)=(.*)$") {
    [Environment]::SetEnvironmentVariable($matches[1], $matches[2], "Process")
  }
}
```
