# SBA301 Lab04 - Orchid REST API & JPA

Spring Boot 3.3.4, Java 21, Maven Wrapper 3.9.12, Spring Web, Spring Data JPA/Hibernate, Microsoft SQL Server. Bài thực hiện theo `Lab 04_Spring Boot RESTful Web Service and JPA.pdf` và `SBA301_Slot18_Lab04_Guide.pdf` trong [`../Tài Liệu`](../T%C3%A0i%20Li%E1%BB%87u/).

## Kết quả và phạm vi xác minh

- Orchid CRUD, tìm kiếm không phân biệt hoa/thường, quan hệ Category 1-N Orchid, validation và transaction đã triển khai.
- `mvnw.cmd clean package`: 26 test pass. Có cả MockMvc + JPA integration test, thay FK, rollback, Unicode, ID do server sinh và kiểm tra xóa.
- HTTP trên ứng dụng chạy thật với SQL Server: CRUD, lỗi, GET hậu điều kiện và restart pass. Kết quả nằm trong `docs/test-matrix.md` và `evidence/http`. Bằng chứng H2 trước đó được giữ tại `evidence/h2`.
- **SQL Server đã xác minh**: OrchidDB trên SQL Server 2022 Developer, container healthy tại `localhost:14334`; T11 kiểm tra row/FK/Unicode và T12 kiểm tra dữ liệu sau restart đều pass. Schema xác nhận PK identity, FK và các cột tên/mô tả là nvarchar. Newman chạy với SQL Server: 22 requests, 30 assertions, 0 failures.
- `docs/verification.md` ghi phạm vi bằng chứng; `docs/break-it.md` ghi lỗi, nguyên nhân và retest; `docs/oral-review.md` hỗ trợ ôn tập.

## Cấu trúc và luồng

```text
HTTP -> Controller -> Service / Transaction -> JpaRepository -> Hibernate -> SQL Server
          controllers/  services/               repositories/   pojos/
```

`Orchid.orchidCategory` là owning side, chứa `@ManyToOne` và `@JoinColumn(category_id)`. `OrchidCategory.orchids` là inverse side, dùng `mappedBy="orchidCategory"` và `@JsonIgnore` để tránh vòng lặp JSON. Tên/mô tả sử dụng `@Nationalized` để SQL Server lưu Unicode. Không cascade xóa từ Orchid sang Category.

## Chạy bằng Docker sau khi Docker Desktop hoạt động

Yêu cầu JDK 21, Docker Desktop Linux engine, PowerShell, kết nối Internet cho lần tải dependency/image đầu tiên. Không cần cài Maven riêng. Port API `8080`, SQL Server của lab `14334`.

```powershell
cd "C:\SBA301\bài tập\sba-301\lab4"
.\mvnw.cmd -B clean package
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\init-local.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run.ps1 -SkipBuild
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verify-api.ps1 -VerifyDatabase -Restart
```

`init-local.ps1` tạo `.env` với mật khẩu ngẫu nhiên, chạy SQL Server Developer và tạo `OrchidDB` nếu chưa có. File `.env` được Git bỏ qua. Image và volume riêng của lab; port chỉ bind localhost. Dữ liệu được giữ trong Docker volume `sba301-lab4_orchid-sqlserver-data`.

Trên database mới, DataSeeder tạo Cattleya, Dendrobium, Phalaenopsis và hai Orchid mẫu. Collection/script tự lấy category ID thực tế; không giả định ID luôn là 1/2. Seeder không ghi đè dữ liệu đã có. Nếu database có Category nhưng chưa có Orchid, dùng POST để tạo Orchid.

Nguồn cách chạy: [Microsoft SQL Server Docker quickstart](https://learn.microsoft.com/en-us/sql/linux/quickstart-install-connect-docker?pivots=cs1-bash&tabs=cli&view=sql-server-ver16), [Apache Maven Wrapper](https://maven.apache.org/tools/wrapper/index.html).

## Dùng SQL Server đã có

Tạo `OrchidDB`, chép `.env.example` thành `.env`, điền `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` theo server của bạn. Chạy `run.ps1`; script nạp các biến vào tiến trình Java. Nếu chạy bằng IntelliJ, import `pom.xml`, chọn JDK 21 và cấu hình các biến này trong Run Configuration của `OrchidApplication`.

`verify-api.ps1 -VerifyDatabase` dành cho SQL Server của Compose. Với server bên ngoài, chạy HTTP verification không có switch đó, rồi chạy `scripts/inspect-db.sql` và kiểm tra row/FK bằng SSMS.

## API

Base URL `http://localhost:8080`. Chi tiết JSON và status nằm trong `docs/api-contract.md`.

| Method | Endpoint | Kết quả |
|---|---|---|
| GET | `/api/orchids` | 200 + array |
| GET | `/api/orchids?name=cat` | 200 + kết quả tìm kiếm |
| GET | `/api/orchids/{id}` | 200 / 404 |
| POST | `/api/orchids` | 201 / 400 |
| PUT | `/api/orchids/{id}` | 200 / 400 / 404 |
| DELETE | `/api/orchids/{id}` | 204 / 404 |
| GET | `/api/categories` và `/api/categories/{id}` | 200 / 404 |
| POST | `/api/categories` | 201 / 400 |

Đường dẫn `/orchids`, `/orchids/` tương thích đề Lab04 gốc. PUT thay toàn bộ các field của Orchid; field tùy chọn thiếu sẽ được đặt thành null. `orchidName` không được rỗng; độ dài tên/mô tả/URL lần lượt tối đa 150/1000/255. Category phải tồn tại. ID của POST do server sinh, ID do client gửi không cập nhật resource cũ.

## Postman và bằng chứng

Import `postman/Orchid-Lab04.postman_collection.json`, chọn Collection Runner và chạy theo thứ tự. Có 22 request với assertions cho status, JSON, ID, FK ở response, thay đổi dữ liệu và hậu điều kiện xóa. `baseUrl` mặc định là `http://localhost:8080`.

Có thể chạy cùng collection bằng Newman:

```powershell
npm.cmd exec --yes --package=newman -- newman run postman/Orchid-Lab04.postman_collection.json --reporters cli,json --reporter-json-export evidence/postman/newman-run.json
```

Khi nộp theo yêu cầu ảnh của giảng viên, chụp Postman gồm URL, method, status, body; chụp SQL Server schema PK/FK và SELECT hậu điều kiện. Report Newman/HTTP JSON là bằng chứng tự động; không phải ảnh Postman/SSMS.

Sau khi chạy SQL verification, chép startup/SQL log vào evidence (không chép `.env`). `scripts/inspect-db.sql` cung cấp truy vấn metadata/row. `verify-api.ps1 -VerifyDatabase -Restart` lưu T11, schema, DELETE hậu điều kiện và T12. GET sau restart phải giữ đúng ID, tên và category.

## Kiểm tra dự phòng khi SQL Server không chạy

H2 là test dependency, không nằm trong runtime jar SQL Server. Script diagnostic dùng Spring Boot PropertiesLauncher để nạp H2 từ Maven cache, tạo database file trong `.local`, với `WRITE_DELAY=0` để flush đồng bộ trước thao tác dừng tiến trình:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run.ps1 -SkipBuild -UseH2Verification
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\verify-api.ps1 -Restart -UseH2Verification
```

Đây là kiểm tra HTTP/JPA và restart trên H2. Nó không thay thế kiểm tra SQL Server, JDBC driver hoặc SQL Server DDL/FK. Dừng ứng dụng H2 trước khi chạy lại bằng SQL Server.

## Dừng và xử lý lỗi

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\stop.ps1
docker compose stop
```

- Port 8080 bị chiếm: dừng ứng dụng đang dùng port trước; script không tự dừng tiến trình khác.
- Connection refused/login failed: kiểm tra SQL Server, port, `.env`, Docker health và log; không đổi business code để sửa lỗi kết nối.
- Docker error 1920 trước đó: log lịch sử được giữ trong `evidence/console/docker-startup-failure.txt`. Docker hiện đã chạy và Lab4 được xác minh bằng SQL Server.
- SQL Server phải có database `OrchidDB`; Hibernate `ddl-auto=update` tạo/cập nhật bảng, không tạo database.
- Lỗi business Category trả text với 400 theo mẫu course; lỗi Bean Validation/Jackson dùng error response mặc định của Spring Boot. `application.log`/`.env` ở `.local` và root không được commit.

## Repository và lịch sử

Lab4 nằm tại `lab4/` trong repository [tranthaigin/SBA](https://github.com/tranthaigin/SBA). Repository cục bộ của cả môn học là `C:/SBA301/bài tập/sba-301`; không có repository Git lồng trong Lab4.

Các bằng chứng kiểm tra gồm unit/integration tests H2 và live HTTP/SQL Server riêng. Trước khi nộp, bổ sung ảnh Postman/SSMS theo yêu cầu giảng viên và tự giải thích Human Verification Gate.
