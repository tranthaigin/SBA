# Chapter 16 – Securing REST Services with Spring Boot
## Part C: Security Consolidation – Authn/Authz, Roles, JWT, CORS và Negative Tests

**Dự án nền:** SBA301 Slot 25 – Spring Boot 3.4.5, Java 21, Spring Security, JJWT 0.12.6.  
**Workbook rule:** Mỗi bước có **Mục tiêu → thao tác → code/config → command → expected result → PASS/FAIL evidence**.

## Core và mục tiêu học tập

Có JWT không đồng nghĩa với an toàn. Một request chỉ hoàn chỉnh khi identity được xác thực, quyền được kiểm soát, CORS phù hợp với browser, token không hợp lệ/hết hạn/bị sửa bị từ chối, 401 và 403 phân biệt rõ, và negative tests chứng minh contract có thể lặp lại.

Sau bài học, người học có thể:

1. Hợp nhất authentication, authorization, JWT, role và CORS thành một request lifecycle.
2. Thiết kế endpoint security matrix public/authenticated/admin.
3. Phân biệt role/authority, giải thích `ROLE_ADMIN`, `hasRole` và `hasAuthority`.
4. Kiểm chứng JWT signature, expiration, subject và role claims.
5. Giải thích CORS preflight và thứ tự xử lý cùng Spring Security.
6. Thiết kế negative test matrix, phân biệt 401/403 với browser CORS failure.
7. Trace React/Postman → CORS → `SecurityFilterChain` → JWT → authorization → Controller.
8. Chẩn đoán `permitAll` quá rộng, role prefix sai, filter order sai, origin/header sai.
9. Chuyển lab JWT parser sang production Resource Server/JWT validation chuẩn hóa.

## Endpoint security matrix – baseline phải thống nhất trước khi test

| Endpoint | Method | Expected access | Role | No token | Wrong role |
|---|---|---|---|---|---|
| `/auth/login` | POST | Public | – | Credential đúng: 200; sai/unknown user: 401 | – |
| `/auth/signup` | POST | Public | – | Request hợp lệ: 201 | – |
| `/users/me` | GET | Authenticated | USER/ADMIN | 401 | 403 nếu policy endpoint bị siết và role không đủ |
| `/users` | GET | Authenticated | USER/ADMIN | 401 | Hiện không giới hạn role |
| `/admin/users` | GET | Protected | ADMIN | 401 | USER: 403 |

Source Slot 25 **không có route/controller `/orchids`**. Vì vậy bài học dùng public `/auth/login`, protected `/users/me` và admin `/admin/users`; không coi `/orchids` là endpoint 200 cho đến khi có domain/controller orchids. `/admin/**` được bảo vệ bằng `hasRole("ADMIN")`; authority thực là `ROLE_ADMIN`. `.roles("ADMIN")` tự thêm prefix `ROLE_`; `hasAuthority("ROLE_ADMIN")` yêu cầu viết prefix tường minh.

Signup luôn cấp `USER`; không nhận role từ request client. Để manual test ADMIN, dùng tài khoản lab riêng rồi cập nhật role trong database local có kiểm soát; không thêm API public để tự cấp ADMIN.

## Step 1 – Chụp baseline security contract

- **Mục tiêu:** Cả lớp đồng thuận về status/access trước khi chạy test.
- **Thao tác:** Đối chiếu matrix trên với `SecurityConfiguration`, `UserController`, `AdminUserController`.
- **Code/config:** `/auth/**` permitAll; `/admin/**` hasRole ADMIN; các route còn lại authenticated; session stateless.
- **Command:** `mvn -Dtest=AuthFlowIntegrationTest test`
- **Expected result:** Login public; `/users/me` anonymous 401; `/admin/users` anonymous 401, USER 403, ADMIN 200.
- **PASS/FAIL evidence:** PASS nếu status/body khớp; FAIL nếu protected route trả 200 khi thiếu token, hoặc nhầm 401 (chưa xác thực) với 403 (đã xác thực nhưng thiếu quyền). Ghi matrix trước khi chạy.

## Step 2 – Xác nhận JWT positive path

- **Mục tiêu:** Login đúng tạo token dùng được và principal/authority chính xác.
- **Thao tác:** Đăng ký user nếu chưa tồn tại; Postman login và lưu `accessToken` vào environment variable `access_token`.
- **Code/config:** `AuthService` authenticate qua `AuthenticationManager`; filter load principal/authority từ repository.
- **Command:** `POST {{base_url}}/auth/login`, body `{"email":"student24@example.com","password":"Sba301@2026"}`; sau đó `GET {{base_url}}/users/me` với `Authorization: Bearer {{access_token}}`.
- **Expected result:** Login 200; `/users/me` 200, username đúng email, authority `ROLE_USER`. Nếu dùng tài khoản mẫu chưa có, gọi `/auth/signup` trước với email riêng.
- **PASS/FAIL evidence:** PASS nếu identity/authority đúng; FAIL nếu identity lấy từ input client hoặc authority sai. Environment nộp bài không chứa token thật.

## Step 3 – Missing token negative test

- **Mục tiêu:** Không cho anonymous vào protected endpoint.
- **Thao tác:** Gửi `/users/me` và cố ý bỏ hoàn toàn Authorization header.
- **Code/config:** Authentication failure đi qua `RestAuthenticationEntryPoint`.
- **Command:** `curl.exe -i http://localhost:8080/users/me`
- **Expected result:** `401` và JSON `{"error":"UNAUTHORIZED"}`, không phải 200/500.
- **PASS/FAIL evidence:** PASS nếu `protectedEndpointWithoutToken_returnsJson401` pass; lưu screenshot status/headers/body đã che dữ liệu nhạy cảm. FAIL nếu controller trả 200 hoặc server lỗi 500.

## Step 4 – Tampered JWT test

- **Mục tiêu:** Bằng chứng JWT signature được verify chứ không chỉ decode.
- **Thao tác:** Copy token test, sửa một ký tự trong segment signature (segment thứ ba), không re-sign.
- **Code/config:** `JwtService.parseClaims` dùng JJWT `verifyWith(signingKey).parseSignedClaims(...)`; filter không tạo Authentication nếu token parse lỗi.
- **Command:** `curl.exe -i http://localhost:8080/users/me -H "Authorization: Bearer <tampered-token>"`
- **Expected result:** `401 UNAUTHORIZED`, không 200/500.
- **PASS/FAIL evidence:** PASS nếu `tamperedSignature_returnsJson401` pass; FAIL nếu token đã bị sửa được chấp nhận. Chỉ dùng token giả local.

## Step 5 – Expired token test

- **Mục tiêu:** Token hết hạn bị từ chối đúng.
- **Thao tác:** Integration test dùng expiration 100 ms, login lấy token, chờ 150 ms rồi gọi endpoint protected.
- **Code/config:** `JwtExpirationIntegrationTest` override `security.jwt.expiration-ms=100` trong test context riêng; cấu hình runtime vẫn 900000 ms.
- **Command:** `mvn -Dtest=JwtExpirationIntegrationTest test`
- **Expected result:** Login thành công; sau expiry request trả 401. Không đặt token vô hạn, không thay config môi trường thật để né lỗi.
- **PASS/FAIL evidence:** PASS nếu expired token bị 401 và test thường vẫn giữ expiration mặc định; FAIL nếu token hết hạn được chấp nhận.

## Step 6 – Role/authority test

- **Mục tiêu:** Kiểm chứng boundary giữa authenticated USER và ADMIN.
- **Thao tác:** USER token gọi `/admin/users`; ADMIN token gọi cùng endpoint. Signup chỉ tạo USER; test tự tạo ADMIN fixture. Manual lab chỉ nâng role cho tài khoản riêng trong database local có kiểm soát.
- **Code/config:** `.requestMatchers("/admin/**").hasRole("ADMIN")`; controller admin trả danh sách DTO không lộ password.
- **Command:** `mvn -Dtest=AuthFlowIntegrationTest#userRole_cannotAccessAdminEndpoint+adminRole_canAccessAdminEndpoint test`
- **Expected result:** USER 403 `FORBIDDEN`; ADMIN 200; anonymous 401.
- **PASS/FAIL evidence:** PASS nếu cả ba tình huống phân biệt đúng; FAIL nếu USER được phép hoặc token ADMIN nhận 401.

## Step 7 – Kiểm tra role prefix

- **Mục tiêu:** Khớp naming giữa role trong DB, GrantedAuthority và authorization policy.
- **Thao tác:** Debug local sau login, quan sát `SecurityContextHolder.getContext().getAuthentication().getAuthorities()`.
- **Code/config:** `.roles("USER")` tạo `ROLE_USER`; `hasRole("ADMIN")` so với `ROLE_ADMIN`; `hasAuthority` cần authority đầy đủ.
- **Command:** Gọi `/users/me` với USER token hoặc xem response authorities; breakpoint chỉ dùng local.
- **Expected result:** USER có `ROLE_USER`, ADMIN có `ROLE_ADMIN`; claim `roles` trong JWT lưu tên role không prefix.
- **PASS/FAIL evidence:** PASS nếu token role khớp authority hiện tại từ DB; FAIL nếu dùng `.roles("ROLE_USER")` hoặc mismatch prefix. Không ghi token/credential ra production log.

## Step 8 – CORS consolidation

- **Mục tiêu:** Cho phép đúng React origin, không mở wildcard credentials.
- **Thao tác:** Kiểm tra `CorsConfigurationSource` và security chain.
- **Code/config:** `security.cors.allowed-origin` mặc định `http://localhost:5173`; allow methods GET/POST/PUT/DELETE/OPTIONS, headers Authorization/Content-Type, expose Location; `allowCredentials=false`; chain bật `.cors(cors -> {})`.
- **Command:** `mvn -Dtest=AuthFlowIntegrationTest test`
- **Expected result:** Origin allowlist được CORS; origin khác không có ACAO, header ngoài allowlist không được cấp trong ACAH. Có thể cấu hình `CORS_ALLOWED_ORIGIN`.
- **PASS/FAIL evidence:** PASS nếu CORS test xanh; FAIL nếu origin lạ nhận ACAO hoặc preflight hợp lệ bị security 401. Cookie credentials cần threat model và CORS/CSRF policy riêng, không copy `false`/`true` máy móc.

## Step 9 – Manual preflight bằng Windows CMD

- **Mục tiêu:** Đảm bảo preflight được xử lý trước authorization.
- **Thao tác:** Gửi OPTIONS cho `/users/me` với origin React, method GET và headers sẽ dùng ở actual request.
- **Code/config:** Spring CORS filter kết thúc preflight hợp lệ trước khi yêu cầu authentication.
- **Command:**

```cmd
curl.exe -i -X OPTIONS http://localhost:8080/users/me ^
  -H "Origin: http://localhost:5173" ^
  -H "Access-Control-Request-Method: GET" ^
  -H "Access-Control-Request-Headers: authorization,content-type"
```

- **Expected result:** Có `Access-Control-Allow-Origin: http://localhost:5173`, method GET và headers Authorization/Content-Type; không bị 401.
- **PASS/FAIL evidence:** PASS nếu headers phù hợp; FAIL nếu thiếu CORS headers hoặc OPTIONS bị entry point chặn. Trong PowerShell dùng `curl.exe` để tránh alias.

## Step 10 – Disallowed origin negative test

- **Mục tiêu:** Browser origin không tin cậy không được CORS authorize.
- **Thao tác:** Lặp preflight Step 9, đổi Origin sang `http://evil.example`.
- **Code/config:** Chỉ `security.cors.allowed-origin` được allow.
- **Command:** Cùng curl preflight nhưng dùng `-H "Origin: http://evil.example"`.
- **Expected result:** Không có `Access-Control-Allow-Origin: http://evil.example`; status tùy CORS processor.
- **PASS/FAIL evidence:** PASS nếu không cấp ACAO cho origin đó; FAIL nếu origin lạ được echo/allow. Header quan trọng hơn status.

## Step 11 – Browser DevTools verification

- **Mục tiêu:** Xác minh browser thật, không chỉ Postman/curl.
- **Thao tác:** Mở React tại `http://localhost:5173`; DevTools → Network; thực hiện thao tác gọi endpoint protected.
- **Code/config:** Browser gửi Origin; request có Authorization thường cần OPTIONS trước actual request.
- **Command:** Thực hiện UI action tạo `GET /users/me`; inspect OPTIONS, actual request và console.
- **Expected result:** Request có Origin/Access-Control-Request-*; response có allow-origin/method/header phù hợp; actual request chỉ chạy sau preflight pass.
- **PASS/FAIL evidence:** PASS nếu browser đọc được response auth đúng; FAIL nếu CORS console error hoặc actual request không được gửi. Screenshot phải che Bearer token.

## Step 12 – Negative test: wrong allowed header

- **Mục tiêu:** Chứng minh allowlist header có hiệu lực.
- **Thao tác:** Giữ origin hợp lệ nhưng yêu cầu header `x-not-allowed`; không cần sửa config.
- **Code/config:** Allowed headers chỉ `Authorization`, `Content-Type`.
- **Command:** Step 9 với `-H "Access-Control-Request-Headers: authorization,x-not-allowed"`.
- **Expected result:** Spring có thể trả HTTP 200 nhưng chỉ cấp ACAH cho header hợp lệ; vì `x-not-allowed` không được cấp, browser từ chối actual request và báo CORS failure.
- **PASS/FAIL evidence:** PASS nếu `preflightDisallowedHeader_doesNotAllowRequestedHeader` chứng minh header đó vắng mặt; FAIL nếu ACAH cấp `x-not-allowed`. Không dùng status 200 một mình làm tiêu chí pass.

## Step 13 – Negative test: permitAll regression

- **Mục tiêu:** Chứng minh regression test bắt cấu hình authorization mở quá rộng.
- **Thao tác:** Trong branch local riêng, thêm tạm `.requestMatchers("/**").permitAll()` trước rule protected, chạy test, rồi khôi phục; tuyệt đối không merge break-it config.
- **Code/config:** `protectedEndpointWithoutToken_returnsJson401` bảo vệ contract.
- **Command:** `mvn -Dtest=AuthFlowIntegrationTest#protectedEndpointWithoutToken_returnsJson401 test`
- **Expected result:** Khi break-it, test FAIL do anonymous không còn 401; sau khôi phục rule, test PASS.
- **PASS/FAIL evidence:** Lưu cả kết quả fail lúc break-it và pass sau fix; FAIL nếu regression không bị phát hiện.

## Step 14 – MockMvc authorization regression tests

- **Mục tiêu:** Lặp lại các boundary trong CI và phân biệt mock-authz với JWT thật.
- **Thao tác:** Chạy integration tests bằng H2; `@WithMockUser` có thể cô lập authorization nhưng không chứng minh JWT parser.
- **Code/config:** `AuthFlowIntegrationTest` chạy Spring context, login JWT thật, thiếu/malformed/tampered token, USER/ADMIN policy, login sai/unknown user và stale role.
- **Command:** `mvn -Dtest=AuthFlowIntegrationTest test`
- **Expected result:** USER → admin 403; ADMIN → 200; missing/invalid → 401; public login không cần token.
- **PASS/FAIL evidence:** PASS nếu test JWT thật và authorization boundary đều xanh; FAIL nếu chỉ dùng `@WithMockUser`.

## Step 15 – CORS MockMvc tests

- **Mục tiêu:** Khóa CORS allow/deny contract tự động.
- **Thao tác:** Chạy preflight allow/deny tests và actual-request test với allowed origin.
- **Code/config:** Assert ACAO cho origin allowlist; assert không có ACAO khi origin/header không hợp lệ.
- **Command:** `mvn -Dtest=AuthFlowIntegrationTest#preflightAllowedOrigin_hasCorsHeaders+allowedOriginActualRequest_hasCorsHeader+preflightDisallowedOrigin_hasNoAllowOriginHeader+preflightDisallowedHeader_doesNotAllowRequestedHeader test`
- **Expected result:** Allowed preflight 200 và allowed actual request có ACAO; disallowed origin không có ACAO; disallowed header không được cấp trong ACAH nên browser không authorize request.
- **PASS/FAIL evidence:** PASS nếu cả status và header assertions xanh; FAIL nếu chỉ dựa vào status mà không kiểm response headers.

## Step 16 – JWT real-path integration test

- **Mục tiêu:** Test chữ ký/expiry/subject/role claim qua đúng request lifecycle.
- **Thao tác:** Test tạo user → POST login → lấy token từ JSON → gọi `/users/me` với Bearer token.
- **Code/config:** `JwtService` verify signed claims; filter load user hiện tại; role claim phải khớp authority database.
- **Command:** `mvn -Dtest=AuthFlowIntegrationTest#signupLoginAndProtectedRequests_workEndToEnd test`
- **Expected result:** 200, username và `ROLE_USER` đúng; đổi role hiện tại sau login làm token role cũ bị từ chối 401.
- **PASS/FAIL evidence:** PASS nếu token thật qua filter và principal đúng; FAIL nếu chỉ mock Authentication/controller.

## Step 17 – Complete negative test matrix

| ID | Scenario | Expected |
|---|---|---|
| N01 | Protected/no token | 401 |
| N02 | Malformed token | 401 |
| N03 | Tampered signature | 401 |
| N04 | Expired token | 401 |
| N05 | Valid USER → ADMIN endpoint | 403 |
| N06 | Valid ADMIN → ADMIN endpoint | 200 |
| N07 | Public endpoint/no token | Login hợp lệ 200 |
| N08 | Allowed origin preflight | CORS headers present |
| N09 | Disallowed origin preflight | Không ACAO tương ứng |
| N10 | Disallowed requested header | Header không được allow; browser không gửi actual request |
| N11 | Wrong role prefix/config | Role regression test fail |
| N12 | JWT parser exception | 401, không 500 |
| N13 | Login bad password | 401 |
| N14 | Unknown user | 401 theo auth contract |
| N15 | Business conflict sau auth hợp lệ | Không đổi thành 403; source hiện chỉ có duplicate signup public trả 409, chưa có authenticated business-conflict endpoint |

- **Mục tiêu:** Có một danh sách negative scenarios mà cả lớp có thể chạy lại.
- **Thao tác:** Chạy từng N01–N15; ghi test/manual evidence, không đưa token/secret thật vào report.
- **Code/config:** Automated evidence trong hai integration test class; CORS/browser evidence thu theo Step 9–12.
- **Command:** `mvn test`
- **Expected result:** Automated cases pass; N15 được ghi nhận là chưa áp dụng với endpoint hiện có, không giả lập 403.
- **PASS/FAIL evidence:** PASS nếu từng case có expected và evidence; FAIL nếu status 401/403/CORS nhập nhằng, lỗi parser thành 500 hoặc thiếu real-token test.

## Step 18 – Evidence package và integration gates G1–G14

- **Mục tiêu:** Nộp bằng chứng có thể lặp lại, giải thích được security lifecycle và giới hạn production.
- **Thao tác:** Thu endpoint matrix; Postman collection/environment đã xóa secret/token; screenshot/export 401, 403, tampered, expired; browser DevTools allowed/disallowed CORS; automated report; trace login/protected request; ghi chú Break-It → Diagnose → Fix.
- **Code/config:** README mô tả giới hạn; test dùng H2 và test signing key; runtime yêu cầu `JWT_SECRET` từ environment.
- **Command:** `mvn test`; kiểm tra browser/curl theo các bước trước.
- **Expected result:** Evidence không chứa password/hash/token/secret; có giải thích CORS ≠ authentication ≠ authorization.
- **PASS/FAIL evidence:** PASS khi đủ gates:

| Gate | Evidence |
|---|---|
| G1 Baseline | Contract, build/test/start |
| G2 Authn | Login đúng/sai |
| G3 JWT | Tampered/expired bị reject |
| G4 Principal | Identity/authorities đúng |
| G5 Authz | USER/ADMIN boundary |
| G6 401 | Missing/invalid credential |
| G7 403 | Authenticated thiếu role |
| G8 CORS allow | Origin React hoạt động |
| G9 CORS deny | Origin lạ không được allow |
| G10 Preflight | OPTIONS xử lý đúng |
| G11 Public contract | Login/signup vẫn public |
| G12 Tests | Negative matrix tự động/manual |
| G13 Trace | Filter/authz/controller chain |
| G14 Explain | Phân biệt CORS, authn, authz |

FAIL nếu chỉ có screenshot Postman positive hoặc thiếu bằng chứng negative.

## Request trace và chẩn đoán

**Trace:** React/Postman → browser CORS/preflight (nếu cross-origin) → Spring CORS filter → `SecurityFilterChain` → `JwtAuthenticationFilter` verify signature, expiration, subject, role claims → load current user/authorities → authorization (`permitAll`/`authenticated`/`hasRole`) → Controller.

- **401:** chưa có identity hợp lệ (thiếu/sai/hết hạn token hoặc credential login sai).
- **403:** identity hợp lệ nhưng thiếu quyền; nếu USER nhận 401 thay vì 403, kiểm tra token/authentication/filter trước khi kết luận policy.
- **Browser CORS failure:** browser không cho JavaScript đọc response vì origin/method/header policy; không đồng nghĩa server đã xác thực hay authorization trả 401. Postman thường không enforce CORS.
- **`permitAll` quá rộng:** thứ tự matcher sai có thể mở protected endpoint; regression no-token test cần fail.
- **Role prefix sai:** xem authority thực (`ROLE_USER`/`ROLE_ADMIN`) và đối chiếu `hasRole` với `hasAuthority`.
- **Filter order sai:** JWT filter phải chạy trước authorization; filter đăng ký một lần trong security chain.
- **Origin/header sai:** kiểm tra Origin, `Access-Control-Request-Method`, `Access-Control-Request-Headers` và response `Access-Control-Allow-*`.

## Production Resource Server migration

Lab tự parse JWT để học filter lifecycle; đó không phải khuyến nghị triển khai production. Production nên cân nhắc Spring Security OAuth2 Resource Server (`spring-boot-starter-oauth2-resource-server`) và issuer/JWK-based validation, key rotation, issuer/audience validation, TLS, secret manager, audit/monitoring và threat model đã review. Không tái sử dụng secret test/local cho production. Cookie credentials cần policy CORS/CSRF riêng.
