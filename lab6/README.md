# SBA301 Lab 06 - Spring Security + JWT

Dự án hiện thực Core Lab trong tài liệu: đăng ký, đăng nhập, phát JWT và bảo vệ các endpoint người dùng.

## Yêu cầu

- JDK 21
- Maven 3.9+
- Microsoft SQL Server chạy tại `localhost:1433`

## Chạy dự án

1. Chạy script `database/create-database.sql` trong SQL Server Management Studio.
2. Tạo các biến môi trường. `JWT_SECRET` phải là Base64 của ít nhất 32 byte ngẫu nhiên.

PowerShell:
- lệnh trong cmd:
for /f "delims=" %i in ('powershell -NoProfile -Command "$bytes = New-Object byte[] 32; $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create(); $rng.GetBytes($bytes); [Convert]::ToBase64String($bytes); $rng.Dispose()"') do set "JWT_SECRET=%i"

- lệnh trong powershell:
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
[Convert]::ToBase64String($bytes)
$rng.Dispose()

```powershell
$env:DB_USER = "sa"
$env:DB_PASSWORD = "your-password"
$env:JWT_SECRET = "your-base64-encoded-256-bit-secret"
mvn spring-boot:run
```

Ứng dụng chạy tại `http://localhost:8005`.

## API

| Method | Endpoint | Xác thực | Mô tả |
| --- | --- | --- | --- |
| POST | `/auth/signup` | Public | Đăng ký người dùng; trả 201 |
| POST | `/auth/login` | Public | Đăng nhập; trả JWT và TTL |
| GET | `/users/me` | Bearer JWT | Thông tin người dùng hiện tại |
| GET | `/users` | Bearer JWT | Danh sách người dùng, không lộ password |

Ví dụ đăng ký:

```json
{
  "fullName": "SBA301 Student",
  "email": "student01@fpt.edu.vn",
  "password": "P@ssword123"
}
```

## Kiểm thử

```powershell
mvn test
```

Test dùng H2 in-memory và không cần SQL Server. Bộ test kiểm tra: no-token/wrong-scheme/malformed/tampered token 401, sai mật khẩu 401, validation 400, signup/login/JWT happy path, BCrypt hash và duplicate email 409.

Có thể import `postman/Lab06-JWT.postman_collection.json`; collection tự lưu token sau request Login. Hợp đồng API đầy đủ nằm ở `openapi.yaml`.

## Ghi chú bảo mật

- Không commit `.env`, mật khẩu SQL Server hoặc JWT secret thật.
- JWT payload được ký, không được mã hóa; không đưa dữ liệu nhạy cảm vào token.
- `ddl-auto=update` chỉ phù hợp lab/dev.
- Cấu hình hiện tại là Core Lab: chưa có role, nên mọi user đã xác thực đều gọi được `/users`.
