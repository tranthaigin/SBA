# SBA301 - Lab03: Employee Management REST API

Lab03 nằm ở tiết 45-46 trong syllabus. Trong bộ tài liệu trên máy, không có file đề Lab03 riêng: yêu cầu core được xác định từ **Slot15, trang 12** (kick-off) và **Slot16, trang 11-13, 18-19** (completion/testing). Xem [nguồn và phạm vi](docs/source-discovery.md).

## Chạy bài

JDK 21; Maven Wrapper đã đi kèm. Spring Boot 3.3.4 + Spring Web + Validation + Spring Data Commons; springdoc 2.6.0. Repository dùng ArrayList trong bộ nhớ; không cần Docker/SQL Server cho core Lab03.

```powershell
cd "C:\SBA301\bài tập\sba-301\lab3"
.\mvnw.cmd -B clean package
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run.ps1 -SkipBuild
```

Nếu app đang chạy, dùng `scripts/stop.ps1` trước `clean package` trên Windows (jar đang chạy bị khóa).

Mở [Swagger UI](http://127.0.0.1:8083/swagger-ui/index.html), [OpenAPI JSON](http://127.0.0.1:8083/v3/api-docs) hoặc `GET http://127.0.0.1:8083/api/employees`. Port 8083 tránh trùng Lab4 (8080). Có thể dùng `scripts/run.ps1 -SkipBuild -Port 8084`; app bind loopback.

Dừng ứng dụng:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\stop.ps1
```

Có thể chạy trực tiếp ở foreground bằng `java -jar target/lab03-employee-management-0.0.1-SNAPSHOT.jar` rồi Ctrl+C để dừng. Dùng jar path tương đối trên Windows để tránh lỗi đường dẫn tiếng Việt.

## Nội dung đã triển khai

- Controller → IEmployeeService/EmployeeService → IEmployeeRepository/EmployeeRepository.
- 30 nhân viên seed E001-E030; CRUD; Employee immutable; tạo trùng ID trả 409; validation trả 400; ID thiếu trả 404.
- URI versioning: `/api/employees` là alias V1; `/api/v1/employees` giữ bốn field; V2 GET tại `/api/v2/employees` thêm email/department. CRUD ghi dữ liệu qua base/V1.
- Paging zero-based, size mặc định 10 và giới hạn 1..100; sort whitelist, multi-sort và empId làm tie-breaker ổn định.
- Page có tổng số bản ghi/trang; Slice đọc thêm một phần tử để biết hasNext, không trả totals.
- Swagger, Postman collection, MVC/service/repository/paging/live HTTP tests, production mutation red → green.

[API contract](docs/api-contract.md) · [test matrix và kết quả](docs/verification.md) · [testing strategy](docs/testing-strategy.md) · [concept trace](docs/concept-trace.md) · [Break-It](docs/break-it.md) · [AI verification log](docs/ai-verification.md) · [Human Gate notes](docs/human-gate.md)

## Testing

```powershell
.\mvnw.cmd -B test
.\mvnw.cmd -B '-Dtest=EmployeeControllerTest' test
.\mvnw.cmd -B '-Dtest=EmployeeControllerTest#create_valid_returns201LocationAndBody' test
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\break-it.ps1
```

Quote đối số `-D...` trong PowerShell. `break-it.ps1` tạm đổi POST từ 201 sang 200, yêu cầu test fail vì đúng assertion 201/200, khôi phục byte source trong finally, chạy lại test và so SHA256. Không chạy đồng thời script này với build/edits khác.

- Controller: `@WebMvcTest` + MockMvc + `@MockBean` (đúng Spring Boot 3.3), service mock, kiểm tra mapping/binding/status/JSON/validation.
- Service: plain JUnit/Mockito, repository mock và Bean Validator thật; kiểm tra delegation và business rules.
- Repository: plain JUnit, repository thật trong bộ nhớ, fixture mới mỗi test; CRUD/paging/sorting/slice/concurrency.
- PagingPolicy: plain JUnit/parameterized tests; invalid size/sort, duplicate field và offset lớn.
- Integration: `@SpringBootTest(RANDOM_PORT)` + TestRestTemplate; live HTTP qua các bean thật, Unicode và Swagger/OpenAPI.

Không dùng `@DataJpaTest` với ArrayList. JPA/H2 là extension trong Slot16; lab này hoàn thành core, không thêm extension database.

### Postman/Newman

Khởi động Lab03 với 30 seeds trước khi chạy. Collection dùng E901 làm dữ liệu test; DELETE ở cuối. Nếu lần trước bị ngắt giữa chừng, dừng/chạy lại app để reset seeds trước khi chạy lại.

```powershell
cd postman
npm.cmd ci
npm.cmd test
```

Hoặc từ root Lab03: `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/verify-api.ps1`; script tự cài Newman theo lockfile và lưu console/report. Dùng `-BaseUrl http://127.0.0.1:8084` nếu chọn port khác.

Có thể import [collection](postman/Employee-Lab03.postman_collection.json) vào Postman và đổi variable `baseUrl`. Newman 6.2.1 được pin cùng lockfile; báo cáo JSON ở `evidence/postman/newman-run.json`.

## Giới hạn và phần người học bổ sung

Dữ liệu reset khi restart; đây là core in-memory theo Slot16. Sorting vẫn đọc/sort tập dữ liệu trong RAM. Slice không có total count nhưng không chứng minh hiệu năng SQL hoặc persistence thật. Không có UI quản trị/JWT trong phạm vi Lab03 này.

Human Gate yêu cầu người học tự giải thích AAA, test boundary và red → green. Notes là tài liệu ôn tập, không chứng minh bạn đã trả lời bằng lời. Nếu giảng viên yêu cầu thêm ảnh Postman/IDE hoặc phát đề Lab03 riêng khác các tài liệu hiện có, bổ sung theo yêu cầu đó. Evidence hiện có gồm log chạy thật, response/report và ảnh Swagger.
