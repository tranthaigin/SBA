# Slot22 - Chapter 15 Part A: CRUD Backend

Bài làm theo `SBA301_Slot22_Chapter15_PartA_CRUD_Backend.pdf`, phần VI và các gate G1-G10. Java 21, Spring Boot 3.x, Maven, Spring Web, Spring Data JPA, Validation, SQL Server. Package gốc: `com.fpt.sba301.slot22`.

## Phạm vi

- Category: CRUD, DTO, validation, tên không trùng, search, paging/sorting, transaction và rule không xóa khi NewsArticle tham chiếu.
- NewsArticle: CRUD/search/paging trên mapping tối thiểu `NewsArticleID` và `CategoryID` được mục 6.2 cho phép; kiểm tra Category tồn tại, không ghi đè ID đã có, giữ ID khi cập nhật quan hệ.
- `ddl-auto=validate`, giữ đúng cột `CategoryDesciption` trong SQL và `NVARCHAR` cho dữ liệu Unicode.
- Không có React, JWT, Account/Tag CRUD, optimistic locking hoặc các tính năng production radar.

File SQL Assignment 02 đầy đủ không có trong checkout. `database.sql` dùng Category schema/3 seed tại trang 17 và NewsArticle hai cột của mục 6.2, có FK thật và một article mẫu tham chiếu Category 1 để kiểm tra 409. Đây là schema tối thiểu cho Slot22, không đại diện toàn bộ Assignment 02.

## Chạy database

Khởi động SQL Server tại port 1433. Mở SSMS và chạy [database.sql](database.sql), hoặc tại thư mục project trên máy hiện tại:

```powershell
sqlcmd -S '.\SQL2022' -E -C -b -i .\database.sql
```

Database: `FUNewsManagementSBA301`; tables: `dbo.Category`, `dbo.NewsArticle`. Script giữ dữ liệu đã có, chỉ seed Category khi bảng trống và seed article khi chưa có ID mẫu.

## Chạy backend

Cần JDK 21. Maven Wrapper đi kèm project.

```powershell
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
```

Sửa `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SERVER_PORT` trong `.env` theo máy. Mật khẩu thật chỉ ở file local được Git bỏ qua. Spring không tự đọc file `.env`; nạp biến vào terminal trước khi chạy:

```powershell
foreach ($line in [IO.File]::ReadAllLines((Join-Path $PWD '.env'))) {
    if ($line -match '^([A-Z_]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
    }
}
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Source mặc định port `8080` theo PDF. Lượt kiểm chứng tại máy này dùng `.env` local `SERVER_PORT=8082` để chạy Slot22 riêng; pre-flight của PDF cho phép đổi port. Đổi port thì cập nhật `baseUrl` của Postman tương ứng.

Smoke test tại cấu hình local hiện tại:

```text
GET http://localhost:8082/api/categories
GET http://localhost:8082/api/news-articles
```

## API và source chính

| Thành phần | Vị trí |
| --- | --- |
| Application | `src/main/java/com/fpt/sba301/slot22/Slot22Application.java` |
| HTTP handlers | `controller/CategoryController.java`, `controller/NewsArticleController.java` |
| Request/response model | `dto/` |
| Business rule/transaction | `service/CategoryServiceImpl.java`, `service/NewsArticleServiceImpl.java` |
| CRUD/derived query | `repository/CategoryRepository.java`, `repository/NewsArticleRepository.java` |
| SQL mapping | `entity/Category.java`, `entity/NewsArticle.java` |
| 404/409 error handling | `exception/ApiExceptionHandler.java` |

Các đường dẫn package trong bảng nằm dưới `src/main/java/com/fpt/sba301/slot22/`.

| Method | Endpoint | Success |
| --- | --- | --- |
| GET | `/api/categories`, `/api/news-articles` | 200, Page JSON |
| GET | `/api/categories/{id}`, `/api/news-articles/{id}` | 200 |
| POST | `/api/categories`, `/api/news-articles` | 201 |
| PUT | `/api/categories/{id}`, `/api/news-articles/{id}` | 200 |
| DELETE | `/api/categories/{id}`, `/api/news-articles/{id}` | 204 |

Validation: 400; ID/FK không tồn tại: 404; duplicate hoặc Category đang được article tham chiếu: 409. Category ID tự sinh `SMALLINT`; NewsArticle ID là chuỗi client cung cấp, tối đa 20 ký tự.

[API contract cho Part B](docs/api-contract.md) ghi payload, error shape, page/size/sort và filter. Import [Postman collection](postman/Slot22-CRUD.postman_collection.json), đặt `baseUrl` đúng port và chạy theo thứ tự. Collection tạo/xóa dữ liệu test; seed giữ nguyên.

## Kiểm chứng và giải thích

- [Báo cáo G1-G10](docs/verification.md): 15 Java tests, 33 request/49 assertion Postman-Newman, SQL đối chiếu sau từng request.
- [Test matrix T01-T12 và NewsArticle](docs/test-matrix.md).
- [Execution trace CREATE, source và SQL thực tế](docs/execution-trace.md).
- [Break-It -> Diagnose -> Fix](docs/break-it.md): `text/plain` 415 -> `application/json` 201.

Frontend chỉ gửi HTTP theo contract; dữ liệu lưu trong SQL Server. Khởi động lại backend không tạo lại/xóa seed vì dùng `validate`; script SQL chuẩn bị DB được chạy riêng.
