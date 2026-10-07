# Concept Trace — Request to Database Architectural Flow

## 1. Kiến trúc tổng thể (3-Layer with Repository Pattern)

```
HTTP Client (Postman / React)
       │
       ▼
[ REST Controller ]  <-- HTTP Contract boundary (URL, HTTP verbs, status code, JSON deserialization)
       │
       ▼
   [ Service ]       <-- Business logic, Category resolution & validation, Transaction boundary
       │
       ▼
  [ Repository ]     <-- Spring Data JPA Abstraction (IOrchidRepository, IOrchidCategoryRepository)
       │
       ▼
 [ Hibernate/JPA ]   <-- ORM engine, Persistence Context, DDL/SQL generator
       │
       ▼
 [ SQL Server ]      <-- Physical storage (Tables: orchids, orchid_categories; PK & FK constraints)
```

---

## 2. Owning Side, Inverse Side và mappedBy trong JPA

- **Owning side (Bên sở hữu quan hệ):** `Orchid`
  - Khai báo: `@ManyToOne(optional = false)` và `@JoinColumn(name = "category_id", nullable = false)`
  - Lý do: Bảng `orchids` chứa cột vật lý khóa ngoại `category_id`. Mọi thay đổi về quan hệ giữa Orchid và Category đều được Hibernate theo dõi và cập nhật qua trường `orchidCategory` của entity `Orchid`.
- **Inverse side (Bên bị sở hữu):** `OrchidCategory`
  - Khai báo: `@OneToMany(mappedBy = "orchidCategory")`
  - Ý nghĩa: `mappedBy = "orchidCategory"` chỉ ra rằng thuộc tính Java `orchidCategory` trong class `Orchid` đang quản lý liên kết này.
  - *Lưu ý quan trọng:* `mappedBy` nhận **tên thuộc tính Java** ở entity phía bên kia, không phải tên cột CSDL (`category_id`).
- **Ngăn chặn vòng lặp tuần tự hoá JSON (Jackson Infinite Recursion):**
  - Đặt `@JsonIgnore` trên thuộc tính `private List<Orchid> orchids` trong `OrchidCategory` để khi serialize Orchid hoặc OrchidCategory ra JSON, Jackson không rơi vào vòng lặp vô tận `Orchid -> OrchidCategory -> orchids -> Orchid -> ...`.

---

## 3. End-to-End Request Trace 1: `POST /api/orchids` (Tạo mới hoa lan)

1. **Client / HTTP Request:**
   - Client gửi `POST http://localhost:8080/api/orchids` với header `Content-Type: application/json`.
   - Body:
     ```json
     {
       "orchidName": "Cattleya Queen",
       "isNatural": true,
       "orchidDescription": "Demo orchid for Slot 18",
       "orchidCategory": { "categoryId": 1 },
       "isAttractive": true,
       "orchidURL": "https://example.com/orchid.jpg"
     }
     ```
2. **REST Controller (`OrchidController.create`):**
   - Đón nhận HTTP request, Spring Boot Jackson tự động deserialize JSON body thành đối tượng Java `Orchid`.
   - Controller chuyển tiếp lời gọi sang `orchidService.create(orchid)`.
3. **Service Layer (`OrchidService.create`):**
   - `@Transactional` bắt đầu transaction nghiệp vụ.
   - Gọi phương thức `resolveCategory(orchid)`:
     - Trích xuất `categoryId` từ request (giá trị = 1).
     - Gọi `categoryRepository.findById(1L)`.
     - Nếu không tìm thấy: ném ngoại lệ `IllegalArgumentException("Category not found: 1")` -> Controller bắt lại và trả về `400 Bad Request`.
     - Nếu tìm thấy: gán managed entity `OrchidCategory` vào đối tượng `Orchid`.
   - Đặt `orchid.setOrchidID(null)` để đảm bảo JPA thực hiện INSERT thay vì UPDATE.
4. **Repository & Hibernate/JPA:**
   - Gọi `orchidRepository.save(orchid)`.
   - Hibernate kiểm tra entity state, đưa vào Persistence Context ở trạng thái *Managed*.
   - Hibernate sinh câu lệnh SQL `INSERT`:
     ```sql
     INSERT INTO orchids (orchid_name, is_natural, orchid_description, category_id, is_attractive, orchidurl)
     VALUES (?, ?, ?, ?, ?, ?);
     ```
5. **SQL Server Database:**
   - Thực thi câu lệnh SQL INSERT. Cột `orchidid` tự tăng (IDENTITY) sinh giá trị khóa chính mới (ví dụ: `1`).
   - Ràng buộc khóa ngoại do Hibernate tạo được đảm bảo thỏa mãn vì `category_id = 1` đã tồn tại trong bảng `orchid_categories`. Xem tên constraint thực tế qua `scripts/inspect-db.sql`.
6. **HTTP Response:**
   - Transaction commit thành công.
   - Controller nhận thực thể đã lưu và trả về `201 Created` kèm body JSON chứa đầy đủ thông tin và `orchidID` vừa sinh.

---

## 4. End-to-End Request Trace 2: `GET /api/orchids?name=Cat` (Tìm kiếm theo tên)

1. **Client / HTTP Request:**
   - Client gửi `GET http://localhost:8080/api/orchids?name=Cat`.
2. **REST Controller (`OrchidController.getAll`):**
   - `@RequestParam(required = false) String name` trích xuất giá trị `"Cat"`.
   - Vì `name != null && !name.isBlank()`, Controller gọi `orchidService.searchByName("Cat")`.
3. **Service Layer (`OrchidService.searchByName`):**
   - Chạy trong ngữ cảnh `@Transactional(readOnly = true)` để tối ưu hiệu năng đọc.
   - Gọi `orchidRepository.findByOrchidNameContainingIgnoreCase("Cat")`.
4. **Repository & Hibernate/JPA:**
   - Spring Data JPA phân tích cú pháp derived query method:
     - `findBy`: câu truy vấn `SELECT`
     - `OrchidName`: thuộc tính `orchidName`
     - `Containing`: điều kiện `LIKE '%...%'`
     - `IgnoreCase`: hàm `UPPER(...)` hoặc collation không phân biệt chữ hoa/thường.
   - Câu SQL minh họa tương đương (Hibernate có thể SELECT Orchid và load Category bằng các SELECT riêng; xem log thực tế để kết luận):
     ```sql
     SELECT o.orchidid, o.orchid_name, o.is_natural, o.orchid_description, o.category_id, o.is_attractive, o.orchidurl,
            c.category_id, c.category_name
     FROM orchids o
     JOIN orchid_categories c ON o.category_id = c.category_id
     WHERE UPPER(o.orchid_name) LIKE UPPER('%Cat%');
     ```
5. **SQL Server Database:**
   - Quét dữ liệu và trả về tập kết quả các dòng khớp với tiêu chí tìm kiếm.
6. **HTTP Response:**
   - Hibernate ánh xạ các dòng dữ liệu thành danh sách `List<Orchid>`.
   - Jackson tuần tự hoá thành JSON mảng và Controller trả về `200 OK`.

## 5. Bằng chứng hiện tại

`OrchidPersistenceTest` dùng MockMvc + JPA/H2 thật để kiểm tra Controller→Service→Repository→Hibernate, category resolution, relationship replacement, rollback và validation. Build sạch: 26 test pass.

Ứng dụng sau đó chạy bằng SQL Server thật tại localhost:14334. `evidence/console/SQLServer-initial-startup.txt` ghi JDBC connection, DDL và seed SQL. `evidence/sqlserver/schema-and-data.txt` xác nhận OrchidDB, SQL Server 2022 Developer, identity PK, FK `orchids.category_id` và các cột nvarchar. `T11-row.txt` xác minh row/FK/flags và description UTF-16 chính xác; `T12-restart-row.txt` xác minh cùng trạng thái sau restart ứng dụng. `T09-delete.txt` xác minh row đã xóa và Category còn tồn tại.

`evidence/http` và `docs/test-matrix.md` là request/status/response từ SQL Server runtime. Newman có 22 requests và 30 assertions pass. `evidence/h2` giữ kết quả H2 trước đó, tách khỏi bằng chứng SQL hiện tại.
