# Chapter 16 – Securing REST Services with Spring Boot
## Part B/C: Authentication, Authorization, JWT và Security Consolidation

**Workbook thực hành:** SBA301 – Slot 25  
**Phiên bản:** Spring Boot 3.4.5, Java 21, Maven, Spring Security, Spring Data JPA, Microsoft SQL Server, JJWT 0.12.6.

## 1. Mục tiêu và phạm vi dự án

Đây là REST API học tập để lần theo toàn bộ vòng đời xác thực bằng JWT, authorization theo role và CORS cho browser client. Hoàn thành bài tập, người học có thể:

1. Phân biệt Authentication (xác thực danh tính), Principal (danh tính), GrantedAuthority (quyền), Role (nhóm authority) và Authorization (quyết định cho phép truy cập).
2. Giải thích quan hệ giữa `SecurityContextHolder`, `SecurityContext` và `Authentication` trong vòng đời request.
3. Trace `AuthenticationManager` → `AuthenticationProvider` → `UserDetailsService` → `PasswordEncoder`.
4. Lưu password dạng BCrypt hash, không bao giờ lưu/trả password plaintext.
5. Thực hiện signup và login qua đúng Spring Security flow.
6. Tạo JWT có subject/email, issued-at, expiration và chữ ký; kiểm tra chữ ký và claims ở request kế tiếp.
7. Đặt Authentication vào SecurityContext bằng JWT filter trước bước authorization.
8. Cấu hình `/auth/**` public và bảo vệ các endpoint còn lại theo stateless policy.
9. Gọi `/users/me` bằng principal hiện tại và `/users` bằng token hợp lệ.
10. Kiểm chứng hành vi qua integration test, Postman/HTTP client, SQL Server và debugger.

**Trong phạm vi:** signup/login, BCrypt, JWT access token, role-based authorization, CORS, JSON lỗi 401/403 và negative integration tests. Đây là workbook cho lab, không phải bộ khung production hoàn chỉnh; refresh/revoke token, rate limit và production operations vẫn ngoài phạm vi.

## 2. Công nghệ và vai trò

| Công nghệ | Phiên bản/phạm vi | Vai trò |
|---|---|---|
| Java | 21 | Ngôn ngữ chạy ứng dụng |
| Spring Boot | 3.4.5 | Auto-configuration, application runtime |
| Spring Web | Boot-managed | REST controllers, JSON/HTTP |
| Spring Security | Boot-managed | Authentication, filter chain, authorization |
| Spring Data JPA / Hibernate | Boot-managed | Repository và ánh xạ entity |
| Microsoft SQL Server JDBC | Boot-managed | Database runtime mặc định |
| JJWT | 0.12.6 | Tạo và validate signed JWT |
| Jakarta Bean Validation | Boot-managed | Validate DTO qua `@Valid` |
| H2 | Test scope | Database tạm cho test; không dùng làm runtime database |
| Spring Boot Test / MockMvc | Test scope | Integration test qua HTTP/security/JPA |
| Maven | 3.9+ khuyến nghị | Dependency resolution, build, test, chạy ứng dụng |

### Security flow tổng quan

**Signup:** Controller → validation → AuthService → BCrypt encode → JPA repository → SQL Server.  
**Login:** Controller → AuthService → AuthenticationManager → DaoAuthenticationProvider → UserDetailsService → repository → BCrypt `matches` → JwtService ký JWT.  
**Protected request:** HTTP Bearer header → SecurityFilterChain → JwtAuthenticationFilter → validate chữ ký/expiration → load UserDetails → set Authentication vào SecurityContext → authorization → controller.

Role `USER` được chuyển thành authority `ROLE_USER`, `ADMIN` thành `ROLE_ADMIN`. `/users` và `/users/me` yêu cầu authenticated user; `/admin/users` yêu cầu ADMIN. JWT có claim `roles`, nhưng request xác thực lại user và authorities hiện tại từ database, từ chối token nếu role claim không khớp.

## 3. API và contract

| Method | Endpoint | Truy cập | Thành công | Lỗi chính |
|---|---|---|---|---|
| `POST` | `/auth/signup` | Public | `201`, id/email/role | `400 INVALID_REQUEST`, `409 EMAIL_EXISTS` |
| `POST` | `/auth/login` | Public | `200`, accessToken/tokenType/expiresIn | `400 INVALID_REQUEST`, `401 UNAUTHORIZED` |
| `GET` | `/admin/users` | Protected, ADMIN | `200`, danh sách id/email/role | `401 UNAUTHORIZED`, `403 FORBIDDEN` nếu thiếu role |
| `GET` | `/users/me` | Bearer token | `200`, username/authorities | `401 UNAUTHORIZED` |
| `GET` | `/users` | Bearer token | `200`, danh sách id/email/role | `401 UNAUTHORIZED` |

Workbook Part C với endpoint matrix, 18 bước thao tác, command, expected result, PASS/FAIL evidence và G1–G14: [`SECURITY_WORKBOOK.md`](SECURITY_WORKBOOK.md). Source Slot 25 không có controller `/orchids`; workbook dùng các endpoint có thật như `/auth/login`, `/users/me` và `/admin/users`.

Request signup/login:

```json
{
  "email": "slot25.student@example.com",
  "password": "Sba301@2026"
}
```

Ví dụ response signup:

```json
{
  "id": 1,
  "email": "slot25.student@example.com",
  "role": "USER"
}
```

Ví dụ response login:

```json
{
  "accessToken": "<JWT>",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

Dùng token trong header `Authorization: Bearer <JWT>` cho `/users/me` và `/users`. Không gửi user id để xác định endpoint `/users/me`; endpoint này lấy danh tính từ Authentication hiện tại.

## 4. Kiến trúc thư mục

```text
.
├── pom.xml
├── README.md
├── .gitignore
├── database
│   ├── 01-create-database.sql
│   └── 02-verify-app-user.sql
├── scripts
│   └── run-local.ps1
└── src
    ├── main
    │   ├── java/edu/slot25/security
    │   │   ├── SecurityJwtApplication.java
    │   │   ├── auth
    │   │   │   ├── AuthResponse.java
    │   │   │   ├── AuthService.java
    │   │   │   ├── AuthenticationController.java
    │   │   │   ├── LoginRequest.java
    │   │   │   └── SignupRequest.java
    │   │   ├── error
    │   │   │   ├── ApiError.java
    │   │   │   ├── EmailAlreadyExistsException.java
    │   │   │   └── RestExceptionHandler.java
    │   │   ├── jwt
    │   │   │   ├── JwtAuthenticationFilter.java
    │   │   │   └── JwtService.java
    │   │   ├── security
    │   │   │   ├── RestAuthenticationEntryPoint.java
    │   │   │   └── SecurityConfiguration.java
    │   │   └── user
    │   │       ├── AppUser.java
    │   │       ├── AppUserRepository.java
    │   │       ├── UserController.java
    │   │       └── UserResponse.java
    │   └── resources/application.properties
    └── test
        ├── java/edu/slot25/security/auth/AuthFlowIntegrationTest.java
        └── resources/application.properties
```

**Ranh giới trách nhiệm:** `auth` xử lý API và use case signup/login; `user` chứa entity, repository, response, `AdminUserController` cho `/admin/users`, và các protected controller; `jwt` ký/đọc token và gắn danh tính vào request; `security` khai báo provider/filter chain/401/403 handlers; `error` chuẩn hóa REST errors. `database` chứa script tạo database và script kiểm chứng; Hibernate tạo/migrate bảng theo entity trong môi trường lab.

## 5. Cài đặt và cấu hình môi trường

### 5.1. Cài prerequisites

1. **JDK 21:** Cài JDK 21 và thiết lập `JAVA_HOME`, thêm `%JAVA_HOME%\bin` vào `PATH`.
2. **Maven 3.9 trở lên:** Cài Maven và thêm thư mục `bin` vào `PATH`. Có thể dùng Maven Wrapper nếu được thêm vào project sau này; workspace hiện dùng Maven cài trên máy.
3. **SQL Server:** Cài SQL Server Developer/Express hoặc dùng SQL Server instance có thể truy cập. Bật TCP/IP, cấu hình port `1433`, bật SQL Server authentication (Mixed Mode), và khởi động service.
4. **SQL client:** Cài SSMS hoặc `sqlcmd` để chạy script và xem dữ liệu.
5. **IDE/API client (khuyến nghị):** IntelliJ IDEA hoặc VS Code; Postman để gửi REST request.

Kiểm tra Java và Maven trong PowerShell:

```powershell
java -version
mvn -version
```

**Kết quả mong đợi:** Java 21 và Maven 3.9+ được in ra; Maven sử dụng đúng JDK. Nếu lệnh không nhận diện, sửa `PATH`/`JAVA_HOME`, đóng và mở lại terminal.

### 5.2. Tạo database

Từ thư mục gốc project, chạy script bằng `sqlcmd`:

```powershell
sqlcmd -S localhost,1433 -U sa -C -i database\01-create-database.sql
```

`sqlcmd` sẽ hỏi password tương tác; không thêm password bằng `-P` để tránh lưu credential vào shell history/process arguments. `-C` chấp nhận certificate server trong môi trường local học tập. Nếu dùng SSMS, mở `database/01-create-database.sql`, kết nối đúng instance rồi Execute.

**Verification:** chạy truy vấn:

```sql
SELECT name FROM sys.databases WHERE name = N'OrchidManagementDB';
```

Phải có đúng một dòng `OrchidManagementDB`. Script chỉ tạo database khi chưa tồn tại, không xóa database hiện tại.

### 5.3. Đặt biến môi trường an toàn

Ứng dụng cần:

- `DB_PASSWORD`: password của SQL Server login `sa` (hoặc đổi username trong `application.properties` sang DB login phù hợp).
- `JWT_SECRET`: Base64 của tối thiểu 32 byte ngẫu nhiên cho HMAC signing key. Tạo một key riêng cho môi trường; không dùng key test bên dưới.

#### Cách khuyến nghị: chạy Maven bằng script local

```powershell
.\scripts\run-local.ps1
```

Script hỏi password SQL Server bằng secure prompt, sinh JWT key ngẫu nhiên trong bộ nhớ, đặt hai giá trị làm environment variables cho tiến trình Maven, rồi xóa chúng khi tiến trình dừng. Key được tạo mới mỗi lần chạy; vì vậy token từ lần chạy trước không còn hợp lệ sau khi restart. Script không ghi password/key vào file.

Nếu muốn chạy lệnh Maven trực tiếp thay vì script, đặt biến trong cùng cửa sổ PowerShell:

```powershell
$securePassword = Read-Host "SQL Server password" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
}
finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
}

$keyBytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($keyBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($keyBytes)
```

Sau đó chạy `mvn spring-boot:run` ngay trong cửa sổ đó. JWT secret mới được tạo mỗi lần thực hiện block; token cũ sẽ không xác thực sau khi khởi động lại với key khác. Khi cần giữ token qua restart, lưu key trong secret manager/OS environment được bảo vệ và tái sử dụng, không ghi key vào repository.

#### Cấu hình IntelliJ để chạy `main()`

1. Mở **Run → Edit Configurations…**, thêm cấu hình **Spring Boot** (hoặc **Application**) và chọn main class `edu.slot25.security.SecurityJwtApplication`.
2. Ở **Environment variables**, khai báo `DB_PASSWORD` với đúng password SQL Server `sa` và `JWT_SECRET` là Base64 của 32 byte ngẫu nhiên. Không nhập literal placeholder như `${DB_PASSWORD}` vào trường giá trị.
3. Để tạo signing key cho cấu hình local, mở PowerShell:

   ```powershell
   $keyBytes = New-Object byte[] 32
   $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
   try { $rng.GetBytes($keyBytes); [Convert]::ToBase64String($keyBytes) }
   finally { $rng.Dispose(); [Array]::Clear($keyBytes, 0, $keyBytes.Length) }
   ```

   Copy chuỗi Base64 được in ra vào `JWT_SECRET` trong Run Configuration. Cấu hình này nằm local trong IDE; không chia sẻ màn hình/log chứa secret và không commit `.idea`.
4. Chọn JDK 21 và module/classpath của project, Apply rồi Run/Debug.

Nếu app báo thiếu `DB_PASSWORD`/`JWT_SECRET`, kiểm tra **Run Configuration của chính process Java**; biến chỉ đặt trong một terminal không tự được truyền cho IntelliJ đã mở trước đó.

### 5.4. Cấu hình ứng dụng

`src/main/resources/application.properties` dùng SQL Server tại `localhost:1433`, database `OrchidManagementDB`, `ddl-auto=update`, SQL logging cho mục đích học tập, JWT expiration 900000 ms (15 phút). Password và signing secret được tham chiếu qua environment variables.

Nếu instance/database/user/port khác máy mẫu, chỉnh JDBC URL và `spring.datasource.username` cho phù hợp. Đảm bảo SQL Server cho phép TCP/IP và database login có quyền tạo/alter bảng cho Hibernate trong môi trường lab.

### 5.5. Cài dependency và chạy test ban đầu

```powershell
mvn dependency:tree | Select-String -Pattern "security|data-jpa|mssql|jjwt"
mvn test
```

**Kết quả mong đợi:** dependency tree gồm Spring Security, JPA, `mssql-jdbc`, `jjwt-api`, `jjwt-impl`, `jjwt-jackson`; test dùng database H2 trong bộ nhớ và không cần SQL Server. Build cuối có `BUILD SUCCESS`.

## 6. Workbook – step by step thực hiện

Mỗi bước gồm **Mục tiêu → thao tác → code/config → lệnh chạy → verification → PASS/FAIL**. Các lệnh PowerShell chạy ở thư mục gốc project. Với bước API, ứng dụng phải đang chạy, trừ khi bước ghi rõ chỉ chạy test.

### Step 1 – Kiểm tra project dependencies

- **Mục tiêu:** Project có đúng Web/Security/JPA/SQL Server/Test/JWT.
- **Thao tác:** Resolve và xem dependency graph.
- **Code/config:** `pom.xml` khai báo Spring Boot Web, Security, Data JPA, Validation, SQL Server JDBC, JJWT và test dependencies.
- **Lệnh chạy:** `mvn dependency:tree | Select-String -Pattern "spring-boot-starter-security|spring-boot-starter-data-jpa|mssql-jdbc|jjwt"`
- **Verification:** Output có `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `mssql-jdbc` và đủ ba JJWT module.
- **PASS/FAIL:** PASS nếu tất cả xuất hiện và Maven kết thúc thành công; FAIL nếu dependency resolve lỗi hoặc thiếu module.

### Step 2 – Cấu hình datasource và JWT settings

- **Mục tiêu:** Ứng dụng kết nối SQL Server và có HMAC key an toàn.
- **Thao tác:** Cài/start SQL Server, chạy `database/01-create-database.sql`, khai báo hai environment variables.
- **Code/config:** `application.properties` tham chiếu `${DB_PASSWORD}`/`${JWT_SECRET}`; `security.jwt.expiration-ms=900000`.
- **Lệnh chạy:** Khuyến nghị `.\scripts\run-local.ps1`; cách khác: đặt env theo mục 5.3 rồi chạy `mvn spring-boot:run` trong cùng PowerShell. Trong IntelliJ dùng Run Configuration theo mục 5.3.
- **Verification:** Log có Hikari pool kết nối thành công, Hibernate khởi động và Tomcat lắng nghe port 8080. `DB_PASSWORD` sai, database chưa tạo hoặc SQL Server chưa chạy sẽ khiến startup thất bại; sửa nguyên nhân trước khi tiếp tục.
- **PASS/FAIL:** PASS khi startup hoàn tất và datasource connected; FAIL khi unresolved property, login DB hoặc JDBC connection lỗi.

### Step 3 – Tạo User entity/schema

- **Mục tiêu:** Biểu diễn user với password hash, role, enabled.
- **Thao tác:** Khởi động app lần đầu trên database mới; Hibernate tạo bảng theo entity.
- **Code/config:** `AppUser` map `dbo.AppUser`; `email` unique/required, `password` required, `role` mặc định `USER`, `enabled` mặc định true. Entity không implement `UserDetails`; security mapping tách ở UserDetailsService.
- **Lệnh chạy:** Sau startup, chạy `sqlcmd -S localhost,1433 -U sa -P "<SQL_SERVER_PASSWORD>" -C -i database\02-verify-app-user.sql` hoặc mở script bằng SSMS.
- **Verification:** Script hiển thị định nghĩa cột `id`, `email`, `password`, `role`, `enabled`; bảng trống là bình thường trước signup.
- **PASS/FAIL:** PASS nếu bảng/cột được tạo và entity mapping không báo lỗi; FAIL nếu database context/schema sai hoặc Hibernate lỗi.

### Step 4 – Tạo UserRepository

- **Mục tiêu:** Tìm user theo email và kiểm tra trùng lặp bằng Spring Data.
- **Thao tác:** Chạy integration test; theo dõi `findByEmail` trong lúc login.
- **Code/config:** `AppUserRepository extends JpaRepository<AppUser, Long>` có `findByEmail` và `existsByEmail`.
- **Lệnh chạy:** `mvn -Dtest=AuthFlowIntegrationTest test`
- **Verification:** Spring context khởi động repository; signup/login test đi qua JPA repository.
- **PASS/FAIL:** PASS nếu test xanh; FAIL nếu repository query/wiring lỗi.

### Step 5 – Tạo DTO và response classes

- **Mục tiêu:** Validate request và tách API contract khỏi entity.
- **Thao tác:** Gửi payload signup/login hợp lệ và payload thiếu/sai email.
- **Code/config:** `SignupRequest`, `LoginRequest` là records có Jakarta validation; `AuthResponse` chỉ chứa access token/type/expiry; `UserResponse` không chứa password/hash.
- **Lệnh chạy:** Gửi request từ Postman hoặc PowerShell; xem ví dụ ở mục 7.1.
- **Verification:** Payload hợp lệ được xử lý; email sai hoặc password thiếu/ngắn trả `400 {"error":"INVALID_REQUEST"}`.
- **PASS/FAIL:** PASS nếu response không chứa password và invalid input bị reject; FAIL nếu validation không chạy hoặc credential bị expose.

### Step 6 – Cấu hình PasswordEncoder

- **Mục tiêu:** BCrypt hash password trước khi lưu.
- **Thao tác:** Signup một user và so sánh row với password gốc.
- **Code/config:** Bean `BCryptPasswordEncoder`; `AuthService.signup` gọi `encode`; test gọi `matches`.
- **Lệnh chạy:** Signup theo mục 7.1, sau đó chạy `database/02-verify-app-user.sql`/truy vấn kiểm tra ở mục 7.3.
- **Verification:** DB lưu BCrypt hash, không lưu raw password; Java `passwordEncoder.matches(raw, hash)` trả true (được kiểm tra trong integration test).
- **PASS/FAIL:** PASS nếu hash khác raw và matches true; FAIL nếu password plaintext được lưu hoặc không xác thực lại được.

### Step 7 – Cấu hình UserDetailsService

- **Mục tiêu:** Load user từ repository và chuyển thành principal/security authorities.
- **Thao tác:** Đặt breakpoint trong lambda `SecurityConfiguration.userDetailsService`; gọi login user đã signup.
- **Code/config:** Email là username; password field nhận hash DB; role `USER` được ánh xạ qua `.roles(...)` thành `ROLE_USER`; disabled theo `enabled`.
- **Lệnh chạy:** Chạy app Debug, gửi `POST /auth/login`.
- **Verification:** Breakpoint cho thấy `findByEmail(email)` và `UserDetails` có username=email, authority `ROLE_USER`, password là hash.
- **PASS/FAIL:** PASS nếu account được load/authority đúng; FAIL nếu email không lookup được hoặc authorities trống/sai.

### Step 8 – Cấu hình AuthenticationProvider và AuthenticationManager

- **Mục tiêu:** Để Spring Security chịu trách nhiệm xác minh username/password.
- **Thao tác:** Đặt breakpoint trên `authenticationManager.authenticate(...)`, trace call stack.
- **Code/config:** `DaoAuthenticationProvider` dùng `UserDetailsService` và `PasswordEncoder`; AuthenticationManager lấy từ `AuthenticationConfiguration`.
- **Lệnh chạy:** Chạy `POST /auth/login` với credential hợp lệ.
- **Verification:** Call path qua `AuthenticationManager` → `DaoAuthenticationProvider` → `UserDetailsService` → repository và BCrypt `matches`.
- **PASS/FAIL:** PASS nếu authentication thành công nhờ provider; FAIL nếu AuthService tự so sánh raw password hoặc bypass manager.

### Step 9 – Xây dựng signup service

- **Mục tiêu:** Tạo tài khoản hợp lệ, chống trùng email và chỉ lưu hash.
- **Thao tác:** Signup mới, signup lại cùng email.
- **Code/config:** `AuthService.signup` kiểm tra exists, encode password và persist; lỗi duplicate trả 409.
- **Lệnh chạy:** `POST /auth/signup` hai lần theo mục 7.1.
- **Verification:** Request đầu trả 201; lần sau trả `409 {"error":"EMAIL_EXISTS"}`; database chỉ có một row cho email đó.
- **PASS/FAIL:** PASS nếu response/DB đúng; FAIL nếu duplicate tạo thêm row hoặc có password plaintext.

### Step 10 – Xây JwtService

- **Mục tiêu:** Tạo signed JWT có subject, `iat`, `exp`; parse và validate chữ ký/expiration.
- **Thao tác:** Login, đọc token response, rồi dùng token gọi endpoint protected.
- **Code/config:** `JwtService` giải mã Base64 signing secret, ký HMAC, parse signed claims bằng JJWT 0.12.6 và so subject/expiration với user hiện tại.
- **Lệnh chạy:** Gọi login và `GET /users/me` theo mục 7.1/7.2.
- **Verification:** Login response có token; claim subject là email, `iat` và `exp` được tạo; token hợp lệ được chấp nhận. Test malformed token nhận 401.
- **PASS/FAIL:** PASS nếu token được verify và hết hạn bị reject; FAIL nếu token signature/expiry không được kiểm tra.

### Step 11 – Xây login service qua AuthenticationManager

- **Mục tiêu:** Chỉ phát hành JWT khi credentials được Spring Security xác thực.
- **Thao tác:** Gửi password đúng, sau đó password sai.
- **Code/config:** `AuthService.login` authenticate `UsernamePasswordAuthenticationToken`; principal authenticated được truyền sang JwtService.
- **Lệnh chạy:** Postman/PowerShell `POST /auth/login`.
- **Verification:** Đúng → 200 và token; sai → `401 {"error":"UNAUTHORIZED"}`, không có access token và không trả 500.
- **PASS/FAIL:** PASS nếu kết quả tương ứng; FAIL nếu password sai vẫn có JWT.

### Step 12 – Xây JwtAuthenticationFilter

- **Mục tiêu:** Chuyển Bearer JWT thành Authentication cho request hiện tại.
- **Thao tác:** So sánh protected request không token, token hợp lệ, token hỏng.
- **Code/config:** `OncePerRequestFilter` đọc Authorization; validate JWT; load user; tạo authenticated token với authorities; lưu vào `SecurityContextHolder`. JWT filter chỉ đăng ký trong SecurityFilterChain, không đăng ký thành servlet filter thứ hai.
- **Lệnh chạy:** Gọi `/users/me` ở mục 7.2 với từng header.
- **Verification:** Token hợp lệ cho request tiếp tục với principal; thiếu/sai token không được đặt Authentication và kết quả protected endpoint là 401 JSON; không log token.
- **PASS/FAIL:** PASS nếu hợp lệ được phép và invalid bị chặn; FAIL nếu token hỏng tạo 500 hoặc bypass bảo vệ.

### Step 13 – Cấu hình SecurityFilterChain

- **Mục tiêu:** Public auth endpoint, protect endpoint còn lại, sử dụng JWT stateless.
- **Thao tác:** Gửi request auth không token và protected request có/không token.
- **Code/config:** CSRF tắt cho REST lab; `/auth/**` permitAll; any other request authenticated; `SessionCreationPolicy.STATELESS`; JWT filter trước username/password filter.
- **Lệnh chạy:** Test no-token và valid-token ở mục 7.2.
- **Verification:** Signup/login public; `/users/me` và `/users` từ chối anonymous; request xác thực thành công.
- **PASS/FAIL:** PASS nếu behavior đúng; FAIL nếu protected endpoint public hoặc auth route cần token.

### Step 14 – Tạo AuthenticationController

- **Mục tiêu:** Expose signup/login qua HTTP API và validation.
- **Thao tác:** Gửi JSON request tới hai route.
- **Code/config:** `AuthenticationController` dùng `@Valid`; signup 201, login 200.
- **Lệnh chạy:** `POST /auth/signup`, rồi `POST /auth/login`.
- **Verification:** Signup/login hoạt động không có token; response signup không có password.
- **PASS/FAIL:** PASS nếu status/JSON đúng; FAIL nếu route không hoạt động hoặc lộ credential.

### Step 15 – Tạo protected `/users/me`

- **Mục tiêu:** Trả principal hiện tại, không nhận user id do client chọn.
- **Thao tác:** Gọi endpoint với token trả từ login.
- **Code/config:** `UserController.me(Authentication authentication)` trả username và authorities.
- **Lệnh chạy:** `GET /users/me` với `Authorization: Bearer <JWT>`.
- **Verification:** 200; username/email khớp user đăng nhập, authorities có `ROLE_USER`; no-token trả 401.
- **PASS/FAIL:** PASS nếu principal lấy từ Authentication; FAIL nếu user identity được tin từ client body/query.

### Step 16 – Tạo protected `/users`

- **Mục tiêu:** Chứng minh endpoint danh sách user cũng chịu authorization.
- **Thao tác:** Gọi GET `/users` không token và có token.
- **Code/config:** `UserController.findAll` map entity sang `UserResponse`, không serialize entity/password.
- **Lệnh chạy:** `GET /users` với `Authorization: Bearer <JWT>`.
- **Verification:** No-token 401; token hợp lệ 200 và user list không chứa password.
- **PASS/FAIL:** PASS nếu cả authorization và response shape đúng; FAIL nếu anonymous truy cập hoặc hash bị lộ.

### Step 17 – Chuẩn hóa lỗi authentication

- **Mục tiêu:** REST client luôn nhận JSON 401 thay vì trang lỗi HTML.
- **Thao tác:** Gọi protected endpoint không token và login với password sai.
- **Code/config:** `RestAuthenticationEntryPoint` trả `application/json`; `RestExceptionHandler` ánh xạ authentication failure, duplicate signup, request validation.
- **Lệnh chạy:** `GET /users/me` no token; `POST /auth/login` sai password.
- **Verification:** 401 `{"error":"UNAUTHORIZED"}`; request invalid 400; duplicate 409. Response không gồm stack trace.
- **PASS/FAIL:** PASS nếu status/content-type/body khớp; FAIL nếu trả HTML hoặc lỗi 500 cho trường hợp dự kiến.

### Step 18 – Trace login end-to-end

- **Mục tiêu:** Nhìn thấy Authentication, Principal, GrantedAuthority, provider và password matching.
- **Thao tác:** Đặt breakpoint ở `AuthenticationController.login`, `AuthService.login`, `authenticate`, UserDetailsService lambda, repository `findByEmail`, `JwtService.generateToken`; chạy app Debug và login.
- **Code/config:** Source chính ở `auth/AuthenticationController`, `auth/AuthService`, `security/SecurityConfiguration`, `user/AppUserRepository`, `jwt/JwtService`.
- **Lệnh chạy:** Debug application, rồi gửi login credentials đúng.
- **Verification:** Controller → AuthService → AuthenticationManager → DaoAuthenticationProvider → UserDetailsService → repository → PasswordEncoder.matches → authenticated UserDetails → generate JWT. CREDENTIALS sai không tới token generation.
- **PASS/FAIL:** PASS nếu call sequence/principal đúng; FAIL nếu auth bị bỏ qua hoặc token được sinh khi authentication thất bại.

### Step 19 – Trace protected request end-to-end

- **Mục tiêu:** Quan sát SecurityContextHolder/SecurityContext/Authentication trong request lifecycle.
- **Thao tác:** Đặt breakpoint ở filter, `JwtService.parseClaims`, UserDetailsService và `UserController.me`; gọi endpoint có Bearer token.
- **Code/config:** `SecurityContextHolder` giữ `SecurityContext`; filter đặt Authentication sau verify token và trước authorization; stateless policy không dựa session.
- **Lệnh chạy:** Login lấy JWT, sau đó `GET /users/me`.
- **Verification:** SecurityFilterChain → JwtAuthenticationFilter → parse/validate claims → load user → set Authentication → authorization → controller; `authentication.getName()` đúng email.
- **PASS/FAIL:** PASS nếu controller nhận principal đúng; FAIL nếu context không được set hoặc anonymous đi vào handler.

### Step 20 – Chạy verification matrix

- **Mục tiêu:** Kiểm tra các hành vi tối thiểu end-to-end.
- **Thao tác:** Chạy integration tests; sau đó lặp lại manual với SQL Server/Postman để kiểm tra môi trường local.
- **Code/config:** `AuthFlowIntegrationTest` dùng Spring context, MockMvc, JPA/H2 và real JWT generation/validation.
- **Lệnh chạy:** `mvn -Dtest=AuthFlowIntegrationTest test`
- **Verification matrix:**

| ID | Kịch bản | Kết quả mong đợi | Bằng chứng |
|---|---|---|---|
| T01 | Signup valid | 201 | User row mới; password BCrypt |
| T02 | Signup duplicate | 409 | Chỉ một row cho email |
| T03 | Login valid | 200 + JWT | Authentication provider thành công |
| T04 | Login sai password | 401 JSON | Không có token |
| T05 | `/users/me` no token | 401 JSON | Controller không được gọi |
| T06 | `/users/me` valid token | 200 | Principal/authority chính xác |
| T07 | `/users` valid token | 200 | Response không có password |
| T08 | `/users/me` malformed token | 401 JSON | Không có stack trace |

- **PASS/FAIL:** PASS nếu tất cả kịch bản có kết quả như bảng và test suite xanh; FAIL nếu bất kỳ status/body/DB evidence sai. Part C workbook hiện có trong `SECURITY_WORKBOOK.md`.

### Step 21 – Automated test bridge

- **Mục tiêu:** Có regression test chạy lặp lại mà không cần SQL Server.
- **Thao tác:** Chạy test trên H2 profile properties riêng trong `src/test/resources`.
- **Code/config:** `@SpringBootTest` + `@AutoConfigureMockMvc`; test signup → kiểm tra BCrypt trong DB → login → gọi protected endpoints; thêm duplicate, wrong-password, no-token và malformed-token cases.
- **Lệnh chạy:** `mvn test`
- **Verification:** Surefire báo 0 failures/errors và Maven `BUILD SUCCESS`; test config dùng H2 `create-drop`, runtime config vẫn SQL Server.
- **PASS/FAIL:** PASS nếu toàn bộ test xanh; FAIL nếu compile, context hoặc test thất bại.

## 6.22. Slot 25 – Yêu cầu tăng thêm cho Chapter 16 Part C

Phần này nâng cấp lab authentication/JWT của Slot 25 thành một security request lifecycle có thể kiểm chứng. Một ứng dụng phát hành JWT chưa đủ để kết luận an toàn: phải xác minh identity, enforce quyền, cấu hình CORS cho browser, từ chối token invalid/expired/tampered, phân biệt 401/403 và có negative tests tái lập được.

### Yêu cầu bổ sung so với baseline Slot 25

1. **Security contract:** `/auth/login` và `/auth/signup` public; `/users/me` và `/users` cần authenticated; `/admin/users` chỉ ADMIN. No token → 401; authenticated USER gọi admin → 403; ADMIN → 200.
2. **Role và authority:** DB lưu `USER`/`ADMIN`; Spring `.roles(...)` chuyển thành `ROLE_USER`/`ROLE_ADMIN`; policy dùng `.hasRole("ADMIN")`. Signup luôn cấp `USER`, không nhận role từ client.
3. **JWT validation:** Token có subject, issued-at, expiry, chữ ký và role claim; mỗi request verify chữ ký/expiry/subject, load user hiện tại, đối chiếu role claim với authority hiện tại và từ chối user disabled. Không tin payload chỉ vì decode được.
4. **CORS:** Chỉ allow origin đã cấu hình (`http://localhost:5173` mặc định), các methods/headers cần thiết; credentials tắt. Preflight được CORS filter xử lý trước authorization. Origin/header không nằm trong allowlist không được browser authorize.
5. **REST errors:** 401 là chưa có identity hợp lệ; 403 là đã authenticated nhưng thiếu quyền. CORS browser failure là policy browser, không đồng nghĩa server trả 401/403.
6. **Regression/evidence:** Automated tests cover no token, malformed/tampered/expired JWT, USER/ADMIN, login sai/unknown user, CORS allow/deny/header; manual evidence cover browser DevTools, preflight, trace và break-it regression.
7. **Production boundary:** Runtime yêu cầu `JWT_SECRET` từ environment; test key chỉ dùng test. Production cần cân nhắc Spring OAuth2 Resource Server/JWK, issuer/audience validation, key rotation, TLS và secret manager.

Source Slot 25 không có `/orchids`, nên contract/test dùng các endpoint thực tế `/auth/login`, `/users/me` và `/admin/users`; không mặc định `/orchids` trả thành công. Chi tiết đủ 18 bước, bảng endpoint, negative matrix N01–N15 và gates G1–G14 nằm trong [`SECURITY_WORKBOOK.md`](SECURITY_WORKBOOK.md).

### Quy trình thực hiện Slot 25 Part C

| Bước | Thao tác và code/config | Command/Expected result | PASS/FAIL evidence |
|---|---|---|---|
| 1. Baseline | Đọc matrix ở trên; rà `SecurityConfiguration` và các controller trước khi test. | `mvn -Dtest=AuthFlowIntegrationTest test`; public/authenticated/admin contracts khớp. | PASS khi status 200/401/403 đúng role; FAIL nếu route protected vào được khi anonymous. |
| 2. Chạy test suite | H2 test config, real JWT login, role/CORS regression và expiry test độc lập. | `mvn test`; 17 tests pass, không cần SQL Server. | Lưu Maven test report; phải có real-token test, không chỉ mock principal. |
| 3. Chạy ứng dụng | Khởi động SQL Server và dùng local script để đặt `DB_PASSWORD`/sinh `JWT_SECRET` trong process. | `.\scripts\run-local.ps1`; chờ `Started SecurityJwtApplication`. | PASS khi app kết nối DB và nghe port 8080; không ghi secret vào file/source. |
| 4. Positive path | Tạo user qua signup nếu cần; login rồi lưu access token vào Postman `access_token`; gọi `/users/me`. | Login 200; `/users/me` 200 với email và `ROLE_USER`. | Lưu response status/body đã che token. |
| 5. 401/403 boundary | Gọi `/users/me` không Authorization, rồi `/admin/users` lần lượt thiếu token, USER token, ADMIN token. | Expected: 401, 401, 403, 200. Test ADMIN dùng fixture; manual chỉ dùng tài khoản lab riêng và cấp role trong DB local có kiểm soát. | Evidence phải cho thấy 401 khác 403; signup không có khả năng tự cấp ADMIN. |
| 6. JWT negative | Sửa signature token local; thử token sai định dạng; expiry tự động kiểm tra trong `JwtExpirationIntegrationTest` bằng 100 ms test-only. | Invalid/tampered/expired → 401 JSON, không 500. | Lưu status/body/screenshot; không nộp JWT thật. |
| 7. CORS preflight | Gửi OPTIONS với origin React và `authorization,content-type`; lặp với origin lạ và `x-not-allowed`. | Lệnh CMD bên dưới. Origin allow có ACAO; origin lạ không có ACAO tương ứng; header lạ không được cấp trong ACAH. | Kiểm headers chứ không chỉ status (Spring có thể trả 200 cho preflight header sai nhưng browser sẽ chặn actual request). |
| 8. Browser/evidence | Mở React `http://localhost:5173`, DevTools → Network, quan sát OPTIONS rồi actual request; chạy break-it local để chứng minh no-token test fail khi permitAll mở rộng, sau đó khôi phục. | Browser actual request chỉ chạy sau preflight pass; `mvn test` pass với config đã sửa. | Lưu DevTools và test report đã che credentials; không merge cấu hình break-it. |

Preflight allow trong Windows CMD:

```cmd
curl.exe -i -X OPTIONS http://localhost:8080/users/me ^
  -H "Origin: http://localhost:5173" ^
  -H "Access-Control-Request-Method: GET" ^
  -H "Access-Control-Request-Headers: authorization,content-type"
```

Negative origin: chạy lại với `Origin: http://evil.example`; phải không có `Access-Control-Allow-Origin` tương ứng. Negative header: chạy lại với `Access-Control-Request-Headers: authorization,x-not-allowed`; có thể response HTTP là 200, nhưng `Access-Control-Allow-Headers` không được chứa `x-not-allowed`.

### Trace và tiêu chí hoàn tất

Trace một protected browser request theo thứ tự: React → CORS/preflight → Spring CORS filter → `SecurityFilterChain` → `JwtAuthenticationFilter` (verify signature, expiration, subject, role claim) → load current user/authority → authorization (`authenticated`/`hasRole`) → controller. 401 chỉ ra chưa xác thực; 403 chỉ ra authenticated nhưng thiếu quyền; CORS failure nghĩa là browser không cho JavaScript tiếp tục/đọc response theo policy.

**PASS Slot 25 Part C** khi matrix endpoint được thống nhất; login/JWT positive path đúng; tampered/expired/missing token bị 401; USER/ADMIN boundary đúng; CORS allow/deny/preflight được chứng minh ở test và browser; automated negative tests xanh; evidence không chứa secret/token; người học giải thích được CORS ≠ authentication ≠ authorization. **FAIL** nếu chỉ chứng minh login thành công, dùng Postman thay browser verification, hoặc không kiểm tra negative paths.

## 7. Chạy và kiểm chứng ứng dụng

### 7.1. Khởi chạy

Đảm bảo SQL Server/database đã sẵn sàng, sau đó chạy script khởi tạo local:

```powershell
.\scripts\run-local.ps1
```

Script sẽ hỏi password `sa`; nhập password SQL Server thật, không nhập chuỗi mẫu. Chờ log `Started SecurityJwtApplication`, sau đó mở PowerShell thứ hai để gửi API request. Nếu muốn dùng lệnh Maven nguyên bản, cần cấu hình cả `DB_PASSWORD` và `JWT_SECRET` trong cùng process theo mục 5.3. Để chạy nút `main()` trong IntelliJ, cấu hình environment variables ngay trong Run Configuration như mục 5.3.

### 7.2. Kiểm tra API bằng PowerShell

Đăng ký user (email có thể đổi; dùng email mới nếu đã tồn tại):

```powershell
$baseUrl = "http://localhost:8080"
$signupBody = @{
    email = "slot25.student@example.com"
    password = "Sba301@2026"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "$baseUrl/auth/signup" `
    -ContentType "application/json" -Body $signupBody
```

**Mong đợi:** response có `id`, email, role; status là `201 Created`. PowerShell có thể trình bày response khác Postman, nhưng trường password không được xuất hiện.

Login và giữ access token trong biến:

```powershell
$login = Invoke-RestMethod -Method Post -Uri "$baseUrl/auth/login" `
    -ContentType "application/json" -Body $signupBody
$token = $login.accessToken
$login | Format-List
```

**Mong đợi:** `tokenType` là `Bearer`, `expiresIn` là `900`, accessToken không rỗng. Không chia sẻ/ghi token vào source.

Gọi hai protected endpoint:

```powershell
$headers = @{ Authorization = "Bearer $token" }
Invoke-RestMethod -Method Get -Uri "$baseUrl/users/me" -Headers $headers
Invoke-RestMethod -Method Get -Uri "$baseUrl/users" -Headers $headers
```

**Mong đợi:** `/users/me` trả username/email và authorities `ROLE_USER`; `/users` trả danh sách response DTO không có password.

### 7.3. Kiểm tra lỗi và dữ liệu database

No-token (PowerShell 7):

```powershell
try {
    Invoke-RestMethod -Method Get -Uri "$baseUrl/users/me"
}
catch {
    $_.ErrorDetails.Message
    [int]$_.Exception.Response.StatusCode
}
```

Mong đợi: status 401 và body `{"error":"UNAUTHORIZED"}`. Thử login sai password: mong đợi cùng JSON 401. Signup lại cùng email: 409 `EMAIL_EXISTS`. Gửi email không hợp lệ/password ngắn: 400 `INVALID_REQUEST`.

Kiểm tra schema và user đã lưu:

```powershell
sqlcmd -S localhost,1433 -U sa -C -i database\02-verify-app-user.sql
```

Hoặc chạy truy vấn trong SSMS:

```sql
USE OrchidManagementDB;
SELECT TOP (100) id, email, password, role, enabled
FROM dbo.AppUser
ORDER BY id DESC;
```

**Mong đợi:** row cho email signup; `password` là BCrypt hash (thường bắt đầu `$2a$`/`$2b$`), không bằng raw password; role `USER`, enabled `1`. Không paste giá trị hash/password/token vào tài liệu công khai.

### 7.4. Kiểm tra automated

Dừng app local nếu muốn, sau đó chạy:

```powershell
mvn test
```

**Mong đợi:** Integration test suite pass. H2 được tạo/xóa riêng trong tiến trình test; dữ liệu SQL Server local không được dùng bởi test này.

## 8. Hướng dẫn debugger (IntelliJ IDEA)

1. Mở project bằng `pom.xml`, chờ Maven import hoàn tất, chọn Project SDK JDK 21.
2. Tạo Run Configuration kiểu Spring Boot/Application cho `edu.slot25.security.SecurityJwtApplication`; đặt `DB_PASSWORD` và `JWT_SECRET` trong Environment variables theo mục 5.3. Không nhập `${DB_PASSWORD}` làm giá trị literal.
3. Đặt breakpoint:
   - `AuthenticationController.signup` / `.login`
   - `AuthService.signup` / `.login`, dòng `authenticationManager.authenticate(...)`
   - lambda `SecurityConfiguration.userDetailsService`
   - `AppUserRepository.findByEmail` (có thể theo vào proxy/query)
   - `JwtService.generateToken`
   - `JwtAuthenticationFilter.doFilterInternal`
   - `JwtService.parseClaims`
   - `UserController.me`
5. Chạy Debug, tạo user và login bằng Postman. Quan sát call stack và kiểm tra:
   - password trong DB/hash khác raw password;
   - provider nhận raw password trong Authentication request và đối chiếu bằng PasswordEncoder;
   - principal sau authentication là `UserDetails`, authority có `ROLE_USER`;
   - token có subject email và expiry;
   - protected request tạo Authentication từ UserDetails và đưa vào SecurityContext trước khi controller chạy.
6. Không copy password, JWT signing secret, bearer token hoặc token đầy đủ vào screenshot/log/ticket.

## 9. Khái niệm cần giải thích sau bài thực hành

| Khái niệm | Vai trò trong project |
|---|---|
| Authentication | Vật thể Spring Security mô tả request xác thực; trước auth chứa credentials, sau auth chứa principal/authorities và authenticated=true |
| Principal | Danh tính hiện tại; ở đây `UserDetails` có email làm username |
| GrantedAuthority | Quyền được security kiểm tra, ở đây `ROLE_USER` |
| Role | Nhãn/nhóm quyền; `.roles("USER")` tự thêm tiền tố `ROLE_` |
| Authorization | Quyết định endpoint có cho Authentication hiện tại đi qua hay không |
| SecurityContext | Chứa Authentication cho request hiện tại |
| SecurityContextHolder | Cách truy cập context hiện hành trong quá trình xử lý request |
| AuthenticationManager | Điểm vào điều phối việc xác thực credential |
| AuthenticationProvider | Thực hiện một phương thức xác thực; `DaoAuthenticationProvider` tra user và dùng encoder |
| UserDetailsService | Load representation security từ email/user repository |
| PasswordEncoder | Hash password lúc signup và kiểm tra raw password với hash khi login |
| JwtAuthenticationFilter | Đọc Bearer token ở request tiếp theo và xây Authentication |
| SecurityFilterChain | Chọn routes public/protected và thứ tự security filters |

## 10. Troubleshooting

| Triệu chứng | Nguyên nhân thường gặp | Cách kiểm tra/sửa |
|---|---|---|
| Maven không chạy/không tải dependencies | JDK/PATH hoặc network/repository | `java -version`, `mvn -version`, thử `mvn -U test` |
| `Could not resolve placeholder DB_PASSWORD` hoặc `JWT_SECRET` | Env variable chưa được truyền vào process Java | Dùng `.\scripts\run-local.ps1`, hoặc đặt cả hai biến trong đúng terminal/IntelliJ Run Configuration rồi restart |
| `Login failed for user 'sa'` / SQL error 18456 | `DB_PASSWORD` trống/sai, hoặc SQL Server chưa cho phép SQL authentication | Đảm bảo nhập đúng password `sa`; thử đăng nhập bằng SSMS/sqlcmd; bật Mixed Mode và restart SQL Server nếu cần. Port mở không chứng minh credentials hợp lệ |
| Invalid JWT Base64 / key too short | Secret không Base64 hoặc dưới 32 bytes | Tạo key bằng PowerShell mục 5.3; không dùng chuỗi ngắn tự nghĩ |
| SQL Server connection refused/timeout | Service/TCP-IP/port sai | Kiểm tra SQL Server service, TCP/IP, firewall và port 1433 |
| Cannot open database | Database chưa được tạo hoặc tên sai | Chạy `01-create-database.sql`, đối chiếu JDBC URL |
| `401` khi gọi protected route | Token thiếu, sai, hết hạn hoặc server đã restart với key khác | Login lại và dùng token mới trong `Authorization: Bearer ...` |
| `409 EMAIL_EXISTS` | Email đã có trong bảng | Chọn email mới hoặc xóa đúng user lab theo quy trình DB do người quản trị phê duyệt |
| Test không kết nối SQL Server | Đang chạy test khác test config hoặc profile bị override | `mvn test` mặc định nạp `src/test/resources/application.properties` và dùng H2 |

## 11. Known limitations và hướng production

- `/users` hiện cho mọi authenticated user xem danh sách; `/admin/users` là route ADMIN-only để thực hành. Thu hẹp dữ liệu `/users` trước khi triển khai thật.
- JWT access token stateless; chưa có refresh token, revocation/denylist, logout phía server hoặc key rotation.
- CORS mặc định chỉ allow `http://localhost:5173`, một origin cấu hình, và không bật credentials. Cookie-based client cần threat model/CORS/CSRF policy riêng.
- `ddl-auto=update`, `show-sql=true`, login `sa` và `trustServerCertificate=true` dành cho lab local. Production nên dùng migration Flyway/Liquibase, DB account ít quyền, TLS certificate được tin cậy, secret manager, log policy an toàn và database backup.
- Cần bổ sung rate limiting, brute-force protection/lockout, audit, policy mật khẩu, key rotation, TLS bắt buộc và giám sát. Dùng Spring Security OAuth2 Resource Server/JWK validation cho production thay vì tự quản lý parser lab.
- Error body hiện tối giản để học status flow; production nên thiết kế error schema/correlation id và logging phù hợp, không tiết lộ thông tin nhạy cảm.
