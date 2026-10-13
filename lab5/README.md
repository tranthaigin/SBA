# SBA301 Lab 05 — Orchid Management

Hai project ngang hàng: `chapter15api` (JDK 21, Spring Boot 3, Maven, JPA, SQL Server) và `chapter15reactjs` (React/Vite, Axios, React Router, Bootstrap). Phạm vi là CRUD và tích hợp SPA đúng hướng dẫn Lab 05.

## Chạy hệ thống

### 1. SQL Server

Khởi động SQL Server, bật kết nối TCP/IP tại port `1433`. Trong SSMS, chạy [database.sql](database.sql) để tạo `SBA301_OrchidDB`, bảng `Orchid` và 10 dòng dữ liệu mẫu. Tại máy hiện tại, có thể chạy từ thư mục `lab5`:

```powershell
sqlcmd -S '.\SQL2022' -E -i .\database.sql
```

Kiểm tra bằng `SELECT 1 AS health_check;` và `USE SBA301_OrchidDB; SELECT * FROM dbo.Orchid;`.

### 2. Backend — terminal thứ nhất

Cần JDK 21; Maven Wrapper đã có trong project.

```powershell
cd .\chapter15api
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
```

Sửa `.env` theo SQL Server của máy: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. File `.env` được Git bỏ qua. Nạp cấu hình vào terminal hiện tại rồi chạy backend:

```powershell
foreach ($line in [IO.File]::ReadAllLines((Join-Path $PWD '.env'))) {
    if ($line -match '^([A-Z_]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
    }
}
.\mvnw.cmd spring-boot:run
```

Backend chạy tại `http://localhost:8080`. Kiểm tra độc lập API bằng Postman trước khi chạy frontend.

### 3. Frontend — terminal thứ hai

Cần Node.js/npm phù hợp Vite. Chạy từ thư mục `lab5`:

```powershell
cd .\chapter15reactjs
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
npm.cmd install
npm.cmd run dev
```

Mở `http://localhost:5173`. `.env` chứa `VITE_API_URL=http://localhost:8080/orchids`; sau khi sửa phải khởi động lại Vite. Backend cho phép CORS đúng origin `http://localhost:5173`.

## API và kiểm chứng

Import [Postman collection](postman/Orchid-Lab05.postman_collection.json), giữ `baseUrl=http://localhost:8080/orchids`, chạy 9 request theo thứ tự. Collection tạo bản ghi kiểm tra, cập nhật, xóa rồi kiểm tra các trường hợp không tồn tại.

| Method | URL | Status kỳ vọng |
| --- | --- | --- |
| GET | `/orchids/` | 200 |
| POST | `/orchids/` | 201, có `orchidId` |
| GET | `/orchids/{id}` | 200 hoặc 404 |
| PUT | `/orchids/{id}` | 200 hoặc 404 |
| DELETE | `/orchids/{id}` | 204 hoặc 404 |

Từ UI, kiểm tra List → Create → Edit → Delete. Đối chiếu Browser DevTools → Network với dữ liệu SQL Server: GET có số dòng khớp UI/DB, POST tạo ID, PUT giữ ID, DELETE xóa dòng và GET lại trả 404.

Luồng GET đúng tên trong hướng dẫn:

```text
React ListOfOrchids.loadData()
  -> getOrchids() trong orchidApi.js
  -> Axios GET http://localhost:8080/orchids/
  -> Browser CORS check
  -> OrchidController.fetchAll()
  -> OrchidService.getAllOrchids()
  -> IOrchidRepository.findAll()
  -> Hibernate/JDBC SELECT
  -> SQL Server
  <- rows <- List<Orchid> <- JSON 200 <- Axios response.data
  -> setOrchids(...) -> React re-render table
```

Đã kiểm chứng build backend/frontend, UI/Network bằng Playwright với Chrome và CRUD trên SQL Server với 10 dòng mẫu. Matrix E01–E08, ảnh chụp, bằng chứng Network/SQL và Break-It → Diagnose → Fix nằm trong [verification.md](evidence/verification.md).

## Điểm không thống nhất trong PDF

SQL trong PDF tạo cả `SBA301_OrchidDB` và `OrchidManagementDB`, nhưng checkpoint/datasource dùng `SBA301_OrchidDB`; project thống nhất bảng và seed trong `SBA301_OrchidDB`. SQL mẫu khai báo description `NVARCHAR(500)`, entity khai báo length `1000`; giữ hai bước đó, `ddl-auto=update` cập nhật cột thành `NVARCHAR(1000)`. Mapping cột ghi rõ tên và `columnDefinition` để khớp schema SQL Server và giữ Unicode.
