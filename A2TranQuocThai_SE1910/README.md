# Assignment 02 — A2TranQuocThai_SE1910

Tran Quoc Thai — CE190243 — SE1910

Đề: `Tài Liệu/Assignment 02_Implement CRUD Application with RESTful Web Service and ReactJS.pdf`.

## Tên và cấu trúc

- Folder nộp bài: `A2TranQuocThai_SE1910`.
- Spring Initializr project trong `backend`: Maven artifactId/name và spring.application.name là `A2TranQuocThai_SE1910`.
- React project trong `frontend`: package name `a2tranquocthai_se1910` (npm yêu cầu chữ thường).
- Database SQL Server: `A2TranQuocThai_SE1910`.
- Backend theo Controller → Service → Repository; React gọi REST bằng Axios.

## Chức năng theo đề

- Public: đọc/tìm tin Active; Staff: CRUD/Search Category và News (bao gồm chọn tags), hồ sơ cá nhân, lịch sử tin của mình; Admin: CRUD/Search Account.
- Create/Update Category, News và Account dùng popup; Delete có xác nhận; chặn xóa Category/Account đang có bài báo.
- Đăng nhập dùng session; API kiểm tra quyền và lấy tác giả từ session.

## Chạy ứng dụng

Java 21, Node.js 20.19+ hoặc 22.12+, SQL Server có SQL authentication. Tạo database trống `A2TranQuocThai_SE1910` bằng SSMS; JPA tạo bảng, DataInitializer thêm dữ liệu mẫu khi database rỗng. Không chạy SQL mẫu của đề vào database khác hoặc thay database mẫu đang có.

Mở terminal tại `backend`:

```powershell
$env:DB_USERNAME = "sa"
$env:DB_PASSWORD = "<your SQL Server password>"
# Mặc định localhost:1433; nếu cần đổi server/port:
# $env:DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=A2TranQuocThai_SE1910;encrypt=true;trustServerCertificate=true"
.\mvnw.cmd spring-boot:run
```

Backend: `http://localhost:8082`. Mở terminal thứ hai tại `frontend`:

```powershell
npm ci
npm run dev
```

Frontend: `http://localhost:5182`. Vite proxy `/api` đến backend `8082`; không cần `.env`. Dữ liệu lưu SQL Server qua các lần chạy. Production build cần reverse proxy `/api` tương tự.

Tài khoản mẫu: admin@funews.com / @1 (Admin); staff@funews.com / @1 (Staff).

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
