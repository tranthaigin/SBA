# SBA301 Slot 29 — Hibernate Many-to-Many, DAO, Repository, Service

Project thực hành cho **Chapter 17 – Hibernate Architecture, Configuration, Mapping – Part B**. Mục tiêu không chỉ là làm CRUD chạy, mà là quan sát đồng thời:

1. Object graph `Student ↔ Book` được ánh xạ thành `STUDENTS_BOOKS` như thế nào.
2. Persistence context, Session và transaction được quản lý ở đâu.
3. Vì sao DAO, Repository và Service có trách nhiệm khác nhau.

Project dùng Java 21, Spring Boot 3.5.7, Hibernate ORM 6.6, Maven, H2 và Microsoft SQL Server. H2 chạy ở chế độ tương thích SQL Server để mọi sinh viên chạy test ngay; profile `sqlserver` dùng cho kiểm chứng trên `LibraryDB` thật.

## 1. Kết quả đã triển khai

- Bidirectional `@ManyToMany`: `Student` là owner, `Book` là inverse side.
- Join table `STUDENTS_BOOKS(student_id, book_id)` có hai FK và unique constraint chống trùng.
- Helper `addBook`/`removeBook` đồng bộ hai phía.
- Cascade chỉ gồm `PERSIST`, `MERGE`; không dùng `REMOVE` cho shared many-to-many.
- DAO thao tác bằng Hibernate `Session` và HQL.
- Repository cung cấp contract theo ngôn ngữ nghiệp vụ, không làm lộ HQL.
- Service chứa use case và đặt transaction boundary bằng `@Transactional`.
- `borrowBook`, `returnBook`, `findStudentsByBook`, CRUD và rollback nguyên tử.
- Fetch plan `left join fetch` cho use case cần sách sau khi Session đóng.
- `ManualStudentDAO` để so sánh với cách mỗi DAO method tự mở Session/Transaction.
- Association entity `Borrowing` có `borrowedDate`, `dueDate`, `status`.
- 14 automated tests: T01–T12 và hai test cho `Borrowing`.
- SQL script kiểm tra bảng, row, FK và unique index trên SQL Server.

## 2. Kiến trúc

```text
DemoRunner / Controller
        |
        v
IStudentService -- use case, validation, @Transactional
        |
        v
IStudentRepository -- persistence contract
        |
        v
StudentRepository -- adapter/delegation
        |
        v
StudentDAO -- Session + HQL
        |
        v
Hibernate persistence context -> SQL -> H2 / SQL Server
```

Trách nhiệm từng layer:

| Layer | Được phép biết | Trách nhiệm |
|---|---|---|
| UI/Runner | Service interface | Nhận input, hiển thị output |
| Service | Repository contract, domain | Use case, business rule, transaction boundary |
| Repository | Domain và DAO | Contract truy xuất theo ngôn ngữ domain |
| DAO | Hibernate Session, HQL | Câu truy vấn và thao tác persistence cụ thể |
| Entity | Mapping và invariant cục bộ | Object graph, helper hai chiều, equality |

Trong project nhỏ, Repository forwarding 1:1 có vẻ dư thừa. Nó được giữ có chủ ý để sinh viên nhìn rõ abstraction boundary và có thể thay DAO/technology mà không đổi Service.

## 3. Cấu trúc project

```text
src/main/java/vn/edu/fpt/slot29
├── Slot29Application.java
├── bootstrap/DemoRunner.java
├── domain/
│   ├── Student.java
│   ├── Book.java
│   ├── Borrowing.java
│   └── BorrowingStatus.java
├── dao/
│   ├── StudentDAO.java
│   ├── BookDAO.java
│   ├── BorrowingDAO.java
│   └── manual/ManualStudentDAO.java
├── repository/                  # interfaces + implementations
└── service/                     # interfaces + @Transactional services

src/test/java
├── StudentPersistenceIntegrationTest.java  # T01–T07, T09–T12
├── MappingFailureTest.java                 # T08
└── BorrowingEntityIntegrationTest.java

src/main/resources
├── application.properties                 # H2 mặc định
└── application-sqlserver.properties       # LibraryDB

sql/verify-sqlserver.sql
```

## 4. Baseline và cách chạy nhanh

### 4.1. Yêu cầu

```powershell
java -version
mvn -version
git status
```

Baseline đã kiểm chứng khi tạo project:

- JDK `21.0.12.1`
- Maven `3.9.9`
- Spring Boot `3.5.7`
- H2 `2.3.232`
- Microsoft JDBC Driver `12.8.1.jre11` (chạy được trên Java 21)

### 4.2. Compile và test bằng H2

```powershell
mvn -q -DskipTests compile
mvn test
```

PASS khi Maven báo:

```text
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 4.3. Chạy demo

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

`DemoRunner` tạo một Student, hai Book, mượn cả hai trong một transaction, load bằng fetch plan và chạy query tìm Student theo Book.

## 5. Chạy với SQL Server LibraryDB

Tạo database một lần trong SSMS:

```sql
IF DB_ID('LibraryDB') IS NULL CREATE DATABASE LibraryDB;
GO
```

Thiết lập credential trong terminal, không ghi password vào Git:

```powershell
$env:LIBRARY_DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=LibraryDB;encrypt=true;trustServerCertificate=true'
$env:LIBRARY_DB_USER = 'sa'
$env:LIBRARY_DB_PASSWORD = 'your-password'
mvn spring-boot:run "-Dspring-boot.run.profiles=sqlserver,demo"
```

Mở `sql/verify-sqlserver.sql` trong SSMS và chạy. PASS khi:

- Có `STUDENTS`, `BOOKS`, `STUDENTS_BOOKS`, `BORROWINGS`.
- `STUDENTS_BOOKS` có FK tới cả hai bảng cha.
- Cặp `(student_id, book_id)` là duy nhất.
- Demo tạo một Student, hai Book và hai join rows.

Profile SQL Server dùng `ddl-auto=update` cho lab. Production nên dùng Flyway/Liquibase và review migration thay vì để Hibernate tự thay schema.

## 6. Workbook step by step

Mỗi bước tuân thủ: **mục tiêu → thao tác → chạy → verification → PASS/FAIL**.

### Step 1 — Baseline

- Mục tiêu: xác nhận JDK/Maven và project compile.
- Thao tác: chạy các lệnh ở mục 4.1 và `mvn -q -DskipTests compile`.
- PASS: compile thành công, Git chỉ có thay đổi mong đợi.

### Step 2 — Student owning side

- Mở `domain/Student.java`.
- Xác định `@ManyToMany`, `@JoinTable`, `joinColumns`, `inverseJoinColumns`.
- Kết luận: `Student.books` là owner vì khai báo join table.
- PASS: sinh viên chỉ ra được owner là phía tạo `INSERT/DELETE` cho join table.

### Step 3 — Book inverse side

- Mở `domain/Book.java`.
- Kiểm tra `@ManyToMany(mappedBy = "books")`.
- PASS: chuỗi `books` khớp chính xác Java property của `Student`, không phải tên bảng/cột.

### Step 4 — Schema và constraint

- Chạy app bằng H2 hoặc SQL Server.
- Với SQL Server, chạy `sql/verify-sqlserver.sql`.
- PASS: đủ hai FK và unique constraint `(student_id, book_id)`.

### Step 5 — Persist graph

- Đọc test `T01` trong `StudentPersistenceIntegrationTest`.
- Chạy: `mvn -Dtest=StudentPersistenceIntegrationTest#persistGraph test`.
- PASS: 1 Student, 2 Books, 2 join rows.

### Step 6 — Break-It: inverse-only

- Đọc test `T09`: chỉ gọi `book.getStudents().add(student)` trong transaction.
- Chạy test `inverseOnlyMutation`.
- PASS: object ở RAM thay đổi nhưng join table vẫn có 0 row, chứng minh owner điều khiển association update.

### Step 7 — DAO save và rollback pattern

- So sánh `StudentDAO.save` với `ManualStudentDAO.save`.
- Manual DAO tự `openSession`, `beginTransaction`, `commit/rollback`.
- Spring DAO lấy Session từ `EntityManager`; transaction do Service quản lý.
- PASS: giải thích được vì sao hai cách không nên trộn trong cùng use case.

### Step 8 — DAO findAll

- Đọc HQL `from Student s order by s.id`.
- Chú ý `books` vẫn LAZY.
- PASS: không truy cập collection ngoài Session nếu query không có fetch plan.

### Step 9 — DAO findById

- `session.get` trả entity hoặc `null`, DAO chuyển thành `Optional`.
- PASS: contract không trả `null` ra Repository/Service.

### Step 10 — DAO update

- `session.merge(detached)` trả về managed instance.
- PASS: code dùng giá trị trả về; không giả định object detached ban đầu trở thành managed.

### Step 11 — DAO delete

- DAO load Student, tháo association bằng helper rồi remove Student.
- Chạy test `deleteStudentPreservesBooks`.
- PASS: Student và join rows bị xóa, shared Book vẫn tồn tại.

### Step 12 — Fetch plan

- Đọc `findByIdWithBooks`: `select distinct` + `left join fetch`.
- Chạy test `loadWithBooks`.
- PASS: collection vẫn đọc được sau khi Service trả về mà không dùng global EAGER.

### Step 13 — Repository contract

- Đọc `IStudentRepository`.
- PASS: interface có CRUD, fetch-plan query và `findStudentsByBook`, không chứa `Session` hay raw HQL.

### Step 14 — Repository implementation

- Theo trace `StudentRepository → StudentDAO`.
- PASS: Service chỉ phụ thuộc interface, không import Hibernate.

### Step 15 — Service contract

- Đọc `IStudentService`.
- Xác nhận có cả `borrowBook`, `returnBook`, `findStudentsByBook`, `borrowBooksAtomically`.
- PASS: tên method diễn tả use case, không diễn tả SQL/HQL.

### Step 16 — Service CRUD

- Đọc `StudentService` và các annotation `@Transactional`/`readOnly=true`.
- PASS: write method có transaction; not-found được chuyển thành business-facing exception.

### Step 17 — borrowBook trong một transaction

- Service load Student-with-books và Book qua hai Repository nhưng trong cùng persistence context.
- Helper cập nhật hai phía; dirty checking tạo join row khi commit.
- Chạy `addExistingBook` và `duplicateBorrow`.
- PASS: join row được thêm một lần; mượn trùng phát sinh `IllegalStateException`.

### Step 18 — Main wiring

- Spring container constructor-inject Service/Repository/DAO.
- `DemoRunner` chỉ gọi Service, không import Session.
- PASS: chạy profile `demo` và thấy Student có 2 Books.

### Step 19 — Layer trace

Đặt breakpoint theo thứ tự:

```text
DemoRunner.run
→ StudentService.getStudent
→ StudentRepository.findByIdWithBooks
→ StudentDAO.findByIdWithBooks
→ Hibernate Session.createQuery
→ JDBC SQL
```

- PASS: ghi được transaction còn active ở Service và SQL phát sinh tại DAO.

### Step 20 — returnBook

- Chạy test `removeAssociationOnly`.
- PASS: `DELETE` chỉ xảy ra trên `STUDENTS_BOOKS`; row `BOOKS` còn nguyên.

### Step 21 — Semantic HQL query

- Đọc `StudentDAO.findStudentsByBook`.
- Chạy test `findStudentsByBook`.
- PASS: trả đúng tập Student, Repository không expose HQL.

### Step 22 — Break-It: CascadeType.REMOVE

Chỉ thực hiện trên branch thử nghiệm:

```powershell
git switch -c breakit/cascade-remove
```

- Tạm thêm `CascadeType.REMOVE` vào `Student.books`.
- Tạo hai Student dùng chung một Book rồi xóa một Student.
- Quan sát Book bị yêu cầu xóa hoặc transaction lỗi FK.
- Khôi phục mapping an toàn trước khi quay lại bài chính.
- PASS: giải thích được many-to-many shared entity không có parent-child ownership.

### Step 23 — Break-It: lazy collection

- Chạy test `lazyOutsideSession`.
- `ManualStudentDAO.findById` đóng Session trước khi test gọi `getBooks().size()`.
- PASS: nhận `LazyInitializationException`; sửa bằng query/fetch plan chứ không chuyển toàn bộ sang EAGER.

### Step 24 — Association entity Borrowing

- Đọc `Borrowing`: hai `@ManyToOne`, ngày mượn, hạn trả và enum status.
- Chạy `BorrowingEntityIntegrationTest`.
- PASS: lưu được thuộc tính nghiệp vụ, return đổi status và active borrowing trùng bị từ chối.

> Raw many-to-many và `Borrowing` cùng tồn tại trong project để so sánh học thuật. Production thường chọn một mô hình làm source of truth, không duy trì hai quan hệ song song.

### Step 25 — Test matrix

| ID | Test tự động | Expected |
|---|---|---|
| T01 | `persistGraph` | 2 join rows |
| T02 | `addExistingBook` | 1 join row mới |
| T03 | `setPreventsDuplicate` | Không trùng semantic association |
| T04 | `loadWithBooks` | Collection dùng được sau Service return |
| T05 | `removeAssociationOnly` | Chỉ xóa join row |
| T06 | `deleteStudentPreservesBooks` | Books còn nguyên |
| T07 | `findStudentsByBook` | Đúng tập Student |
| T08 | `wrongMappedByFailsDuringMetadataBootstrap` | Metadata bootstrap fail |
| T09 | `inverseOnlyMutation` | Không tạo join row |
| T10 | `lazyOutsideSession` | `LazyInitializationException` |
| T11 | `duplicateBorrow` | Business exception |
| T12 | `rollbackIsAtomic` | Không có partial join row |

Chạy riêng một test:

```powershell
mvn "-Dtest=StudentPersistenceIntegrationTest#rollbackIsAtomic" test
```

### Step 26 — Integration gate G1–G14

| Gate | Evidence trong project |
|---|---|
| G1 Build | `mvn test` xanh |
| G2 Mapping | `Student` owner, `Book` inverse |
| G3 Join table | SQL verification + unique constraint |
| G4 Helper | `addBook`/`removeBook` cập nhật hai collection |
| G5 DAO | CRUD, HQL, manual rollback pattern |
| G6 Repository | Interface không lộ Hibernate |
| G7 Service | Use case và `@Transactional` |
| G8 Direction | Runner → Service → Repository → DAO |
| G9 Add | T01/T02 kiểm join INSERT |
| G10 Remove | T05 kiểm join DELETE only |
| G11 Shared safety | T06 bảo toàn Book |
| G12 Query | T07 semantic HQL |
| G13 Break/Fix | T08/T09/T10 + cascade experiment |
| G14 Trade-off | Raw join table so với `Borrowing` |

## 7. Mười hai phần bổ sung đã xử lý

1. `returnBook` đã có trong `IStudentService`.
2. `findStudentsByBook` đã có xuyên suốt Repository contract và implementation.
3. Có `BookDAO`, `IBookRepository`, `BookRepository`, `BookService`.
4. `borrowBook` chạy trong một Spring transaction; không còn load-detach-merge qua nhiều transaction.
5. Equality dùng natural key ổn định (`Student.email`, `Book.isbn`), collection là `Set`, DB có unique join constraint.
6. SQL script xác minh bảng, FK và unique index.
7. Test matrix đã thành integration tests thực thi.
8. `borrowBooksAtomically` tạo tình huống lỗi thật để chứng minh rollback không commit một phần.
9. T08 tạo mapping sai cô lập và xác minh Hibernate bootstrap fail.
10. `Borrowing` có mapping hai phía, repository/service và test lifecycle.
11. Layer responsibility được mô tả và thể hiện trong package/code.
12. Có cả manual Hibernate DAO và hướng production bằng Spring-managed transaction.

## 8. Transaction boundary: điểm kiến thức quan trọng nhất

`ManualStudentDAO` phù hợp để ôn Session/Transaction, nhưng mỗi method tự mở transaction dẫn tới chuỗi sau:

```text
find Student (tx 1, rồi detached)
find Book    (tx 2, rồi detached)
merge graph  (tx 3)
```

Hậu quả có thể gồm lazy-loading failure, partial commit và merge graph khó đoán. Bản chính đặt `@Transactional` tại Service:

```text
borrowBook (một transaction)
  ├─ repository tìm Student
  ├─ repository tìm Book
  ├─ kiểm tra rule
  ├─ helper thay đổi object graph
  └─ commit/rollback toàn bộ
```

DAO vẫn dùng Hibernate Session, nhưng Session được Spring gắn vào transaction hiện hành. Đây là migration hợp lý từ bài Hibernate thủ công sang Spring Boot production.

## 9. Equality, Set và duplicate association

- `Set` không tự hiểu “cùng database row”; nó dựa trên `equals/hashCode`.
- ID sinh tự động chưa tồn tại trước persist, nên dùng ID trực tiếp cho equality dễ làm hash thay đổi.
- Project dùng `email` và `isbn` làm natural key bất biến trong vòng đời object.
- DB vẫn phải có unique constraint vì application check không thay thế database integrity, đặc biệt khi có concurrent requests.

## 10. Hướng phát triển production

- Dùng Flyway/Liquibase thay `ddl-auto=update`.
- Dùng DTO ở boundary thay vì trả entity trực tiếp cho REST/UI.
- Dùng exception types và global exception handler rõ ràng.
- Thêm optimistic locking (`@Version`) cho use case cạnh tranh.
- Chọn một source of truth: raw many-to-many hoặc association entity.
- Khi nghiệp vụ mượn sách có trạng thái/lịch sử/phạt, chuyển hẳn sang `Borrowing`.
- Có thể thay adapter Repository bằng Spring Data JPA; Service contract và transaction boundary vẫn giữ.
- Dùng Testcontainers SQL Server trong CI để kiểm tra khác biệt dialect thật.

## 11. Câu hỏi tự kiểm tra

1. Vì sao chỉ sửa `Book.students` không tạo join row?
2. `mappedBy = "books"` trỏ tới tên gì?
3. Vì sao `CascadeType.REMOVE` nguy hiểm với Book dùng chung?
4. Vì sao `join fetch` tốt hơn đổi toàn bộ collection sang EAGER?
5. Vì sao phải dùng object trả về từ `merge()`?
6. Điều gì bảo đảm `borrowBooksAtomically` không commit Book thứ nhất khi Book thứ hai không tồn tại?
7. DAO khác Repository ở technology knowledge và contract như thế nào?
8. Khi nào join table phải trở thành entity `Borrowing`?

Hoàn thành bài khi sinh viên không chỉ chạy được test mà còn trace được một use case từ Service tới SQL, dự đoán được join-table mutation và giải thích được transaction boundary.

---

# PHỤ LỤC — KIẾN THỨC NỀN TẢNG LÝ THUYẾT

Phụ lục này cung cấp nền tảng để giải thích *vì sao* project hoạt động như hiện tại. Sinh viên nên học theo quan hệ nguyên nhân–kết quả, không chỉ ghi nhớ annotation.

## A.1. Persistence design là gì?

Persistence design là quá trình quyết định:

- Object nào cần được lưu bền vững.
- Object liên hệ với nhau như thế nào.
- Quan hệ đó được biểu diễn bằng bảng, khóa chính và khóa ngoại ra sao.
- Dữ liệu được load ở thời điểm nào.
- Ai chịu trách nhiệm mở transaction, commit và rollback.
- Business rule nằm ở đâu.
- Thiết kế có thể test, thay đổi và mở rộng hay không.

Relationship mapping chỉ là một phần của persistence design. Một mapping đúng annotation vẫn có thể tạo ra hệ thống khó bảo trì nếu transaction boundary hoặc layer responsibility sai.

## A.2. Object graph và relational model

Trong Java, quan hệ được nhìn như một đồ thị object:

```text
Student
  └── Set<Book>

Book
  └── Set<Student>
```

Trong relational database, một column không thể chứa trực tiếp một tập row của bảng khác. Quan hệ many-to-many phải được chuẩn hóa thành bảng nối:

```text
STUDENTS
  id PK

BOOKS
  id PK

STUDENTS_BOOKS
  student_id FK → STUDENTS.id
  book_id    FK → BOOKS.id
  UNIQUE(student_id, book_id)
```

Mỗi row trong `STUDENTS_BOOKS` biểu diễn một cạnh trong object graph.

| Thay đổi object graph | Thay đổi relational dự kiến |
|---|---|
| `student.addBook(book)` | `INSERT` vào `STUDENTS_BOOKS` |
| `student.removeBook(book)` | `DELETE` khỏi `STUDENTS_BOOKS` |
| Xóa Student | Xóa Student và các join rows liên quan |
| Trả Book | Xóa join row, không xóa Book |

## A.3. Owning side và inverse side

Trong bidirectional association, hai collection cùng mô tả một quan hệ logic nhưng Hibernate cần một phía làm nguồn quyết định cho việc cập nhật join table.

### Owning side

`Student.books` là owning side vì khai báo `@JoinTable`:

```java
@ManyToMany
@JoinTable(
    name = "STUDENTS_BOOKS",
    joinColumns = @JoinColumn(name = "student_id"),
    inverseJoinColumns = @JoinColumn(name = "book_id")
)
private Set<Book> books;
```

Thay đổi collection này được Hibernate dùng để tính toán `INSERT` hoặc `DELETE` trên join table.

### Inverse side

`Book.students` là inverse side:

```java
@ManyToMany(mappedBy = "books")
private Set<Student> students;
```

`mappedBy = "books"` có nghĩa: “mapping của quan hệ này được quản lý bởi Java property `books` trong entity `Student`”. Nó không phải tên table, column hoặc SQL constraint.

### Hệ quả quan trọng

Nếu chỉ thực hiện:

```java
book.getStudents().add(student);
```

thì object graph trong bộ nhớ có vẻ đã thay đổi, nhưng owner không thay đổi nên Hibernate không có căn cứ ghi join row như mong đợi. T09 chứng minh hành vi này.

## A.4. Vì sao cần helper method hai chiều?

Code bên ngoài rất dễ chỉ cập nhật một phía:

```java
student.getBooks().add(book);
```

Sau câu lệnh trên, `student` biết `book` nhưng `book` chưa chắc biết `student`. Object graph trở nên không nhất quán trong cùng một JVM.

Helper method đóng gói invariant:

```java
public void addBook(Book book) {
    if (books.add(book)) {
        book.addStudentInternal(this);
    }
}
```

Lợi ích:

- Hai phía luôn đồng bộ trong bộ nhớ.
- Không lặp logic association ở UI, Service hoặc DAO.
- Dễ thêm validation sau này.
- Giảm lỗi khi entity tiếp tục được sử dụng trước lúc transaction commit.

Helper method không thay đổi quy tắc ownership: Hibernate vẫn theo dõi owning side để ghi join table.

## A.5. Entity lifecycle

Một entity thường đi qua bốn trạng thái:

| Trạng thái | Ý nghĩa |
|---|---|
| Transient | Object mới tạo, chưa thuộc persistence context, chưa có row tương ứng |
| Managed/Persistent | Đang được Session theo dõi; thay đổi có thể được dirty checking ghi xuống DB |
| Detached | Từng được quản lý nhưng Session đã đóng hoặc entity đã bị detach |
| Removed | Đã được đánh dấu xóa trong persistence context |

Ví dụ:

```java
Student s = new Student(...); // transient
session.persist(s);           // managed
tx.commit();
session.close();              // detached
```

Điểm cần nhớ:

- Setter/helper trên transient object chỉ thay đổi RAM.
- Thay đổi managed entity trong transaction có thể tự được lưu nhờ dirty checking.
- Thay đổi detached entity không tự được lưu.
- Lazy proxy trên detached entity có thể không còn Session để load dữ liệu.

## A.6. Session và persistence context

Hibernate `Session` đại diện cho một persistence context. Nó thực hiện các vai trò chính:

- Theo dõi entity managed.
- Bảo đảm identity trong phạm vi context: cùng một row thường tương ứng cùng một managed instance.
- Lưu snapshot để phát hiện thay đổi.
- Quản lý first-level cache.
- Lập lịch SQL cho insert, update và delete.
- Khởi tạo lazy association khi Session còn mở.

Trong project:

```java
private Session session() {
    return entityManager.unwrap(Session.class);
}
```

DAO vẫn sử dụng Hibernate Session, nhưng Session này gắn với transaction hiện hành do Spring quản lý.

### SessionFactory

`SessionFactory` là object nặng, thread-safe và thường chỉ được tạo một lần cho một database configuration. Nó chứa mapping metadata và tạo ra các Session.

Không tạo `SessionFactory` cho mỗi request hoặc mỗi DAO method.

## A.7. Transaction và tính nguyên tử

Transaction là ranh giới “tất cả hoặc không có gì” của một use case.

ACID gồm:

- **Atomicity:** tất cả thao tác thành công hoặc rollback toàn bộ.
- **Consistency:** dữ liệu chuyển từ trạng thái hợp lệ này sang trạng thái hợp lệ khác.
- **Isolation:** transaction đồng thời không nhìn thấy trạng thái trung gian không phù hợp.
- **Durability:** sau commit, dữ liệu được lưu bền vững.

Use case mượn nhiều sách:

```text
load Student
add Book A
load Book B → lỗi
```

Nếu toàn bộ use case nằm trong một transaction, association với Book A cũng rollback. T12 kiểm chứng tính chất này.

### Vì sao transaction nên đặt tại Service?

Service biết ranh giới nghiệp vụ đầy đủ. DAO chỉ biết một thao tác persistence riêng lẻ.

```java
@Transactional
public void borrowBook(Long studentId, Long bookId) {
    // nhiều repository calls cùng một transaction
}
```

Nếu mỗi DAO method tự commit, một use case nhiều bước có thể bị chia nhỏ thành nhiều transaction và để lại partial state.

### `readOnly = true`

`@Transactional(readOnly = true)` diễn đạt ý định chỉ đọc và có thể giúp framework/provider tối ưu. Nó không phải cơ chế bảo mật tuyệt đối và không thay thế quyền read-only tại database.

## A.8. Dirty checking và flush

Khi entity ở trạng thái managed, Hibernate giữ snapshot ban đầu. Trước commit hoặc khi cần đồng bộ, Hibernate so sánh trạng thái hiện tại với snapshot.

```java
Student student = repository.findByIdWithBooks(id).orElseThrow();
student.addBook(book);
// Không cần gọi update nếu student vẫn managed.
```

Tại `flush`, Hibernate có thể phát sinh SQL cho join table. `flush` không đồng nghĩa với `commit`:

- `flush`: đồng bộ thay đổi từ persistence context tới database trong transaction hiện tại.
- `commit`: kết thúc transaction và làm thay đổi trở nên bền vững.
- Sau flush vẫn có thể rollback.

## A.9. `persist()` và `merge()`

### `persist()`

Dùng chủ yếu cho entity mới:

```java
session.persist(student);
```

Instance truyền vào trở thành managed trong persistence context hiện tại.

### `merge()`

Dùng để chép trạng thái của một entity detached vào một managed instance:

```java
Student managed = session.merge(detached);
```

Điểm dễ nhầm:

- `detached` không tự biến thành managed.
- `merge()` trả về một object managed khác hoặc object managed hiện có.
- Code sau `merge()` phải sử dụng giá trị trả về.

Trong Service transaction hiện đại, nên ưu tiên load managed entity rồi thay đổi nó. Việc merge một object graph lớn có thể làm luồng cập nhật khó dự đoán.

## A.10. LAZY, EAGER và fetch plan

### LAZY

Association được load khi truy cập lần đầu:

```java
@ManyToMany(fetch = FetchType.LAZY)
```

Ưu điểm là tránh load dữ liệu không cần thiết. Nhược điểm là việc truy cập cần Session còn mở.

### `LazyInitializationException`

```java
Student student = manualDao.findById(id); // Session đã đóng
student.getBooks().size();                // có thể lỗi
```

Collection là lazy proxy chưa được khởi tạo nhưng entity đã detached. Proxy không còn Session để thực hiện query.

### Vì sao không đổi toàn bộ thành EAGER?

Global EAGER có thể gây:

- Load graph lớn ngoài ý muốn.
- Nhiều join phức tạp.
- Duplicate rows ở kết quả SQL.
- Hiệu năng khó dự đoán.
- N+1 query ở các đường truy xuất khác.

Giải pháp đúng là thiết kế fetch plan theo use case:

```java
select distinct s
from Student s
left join fetch s.books
where s.id = :id
```

Use case không cần books dùng query nhẹ; use case cần books dùng `join fetch`.

## A.11. N+1 query problem

Giả sử load 100 Student bằng một query, sau đó lặp và truy cập `student.getBooks()`:

```text
1 query load Students
+ 100 query load books của từng Student
= 101 queries
```

Đây là N+1 problem. Các hướng xử lý tùy use case:

- Fetch join.
- Entity graph.
- Batch fetching.
- DTO projection.
- Query chuyên biệt ở Repository/DAO.

Không nên chữa N+1 bằng cách chuyển mọi association thành EAGER.

## A.12. Cascade là gì?

Cascade truyền một entity operation từ entity nguồn sang entity liên quan.

| Cascade | Ý nghĩa khái quát |
|---|---|
| `PERSIST` | Persist nguồn thì persist entity mới liên quan |
| `MERGE` | Merge trạng thái graph liên quan |
| `REMOVE` | Remove nguồn thì remove entity liên quan |
| `REFRESH` | Refresh nguồn và entity liên quan |
| `DETACH` | Detach nguồn và entity liên quan |
| `ALL` | Gồm tất cả các operation trên |

Cascade không có nghĩa là “tự động lưu mọi thứ” và không giống database `ON DELETE CASCADE`.

### Vì sao tránh `CascadeType.REMOVE`?

Book có thể được chia sẻ bởi nhiều Student:

```text
Student A ─┐
           ├─ Book X
Student B ─┘
```

Xóa Student A chỉ nên xóa cạnh `A–X`. Nếu cascade REMOVE tới Book X, Hibernate có thể xóa một shared entity mà Student B vẫn cần, hoặc gặp lỗi FK.

Cascade REMOVE phù hợp hơn với quan hệ parent-child có ownership vòng đời rõ ràng, ví dụ Order và OrderLine.

## A.13. `Set`, `List` và duplicate association

`Set` phù hợp khi một Student không được liên kết cùng một Book nhiều lần về mặt ngữ nghĩa.

Tuy nhiên `Set` chỉ chống trùng theo `equals/hashCode`. Database vẫn cần unique constraint vì:

- Có thể có nhiều JVM/application instances.
- Hai request đồng thời có thể cùng vượt qua application check.
- Dữ liệu có thể được ghi từ script hoặc hệ thống khác.

Quy tắc phòng thủ hai lớp:

```text
Java Set + business validation
                AND
database UNIQUE(student_id, book_id)
```

## A.14. `equals()` và `hashCode()` của entity

Entity dùng trong `HashSet` cần equality ổn định. Nếu `hashCode` thay đổi sau khi thêm object vào Set, Set có thể không tìm hoặc remove được object đó.

ID tự sinh thường là `null` trước persist rồi có giá trị sau persist. Vì vậy, dùng generated ID trực tiếp trong `hashCode` cần rất thận trọng.

Project dùng natural key:

- `Student.email`
- `Book.isbn`

Các giá trị này phải được xem là ổn định trong vòng đời object. Nếu nghiệp vụ cho phép thay đổi email/ISBN, nên cân nhắc immutable surrogate business key khác hoặc một equality strategy phù hợp hơn.

Database unique constraint vẫn là nguồn bảo vệ cuối cùng cho tính duy nhất.

## A.15. DAO, Repository và Service khác nhau thế nào?

### DAO

DAO gần persistence technology:

- Biết Hibernate Session.
- Viết HQL.
- Thực hiện `persist`, `merge`, `remove`.
- Chuyển kết quả thấp cấp thành object/collection phù hợp.

### Repository

Repository là abstraction theo domain:

```java
findStudentsByBook(bookId)
findByIdWithBooks(id)
```

Caller biết mình cần dữ liệu gì nhưng không biết HQL được viết ra sao.

### Service

Service tổ chức use case:

- Load các entity cần thiết.
- Kiểm tra business rule.
- Gọi nhiều Repository.
- Xác định transaction boundary.
- Chuyển persistence outcome thành kết quả nghiệp vụ.

Ví dụ phân loại:

| Logic | Layer phù hợp |
|---|---|
| `select distinct s from Student...` | DAO |
| Contract `findStudentsByBook` | Repository |
| “Không được mượn cùng Book hai lần” | Service |
| Hiển thị lỗi cho người dùng | UI/Controller |

## A.16. Dependency direction và Dependency Inversion

Luồng gọi runtime đi từ ngoài vào trong:

```text
UI → Service → Repository → DAO → Database
```

Service phụ thuộc `IStudentRepository`, không phụ thuộc trực tiếp `StudentDAO`. Điều này cho phép:

- Thay implementation khi test.
- Thay Hibernate DAO bằng technology khác.
- Giữ business use case ổn định khi persistence thay đổi.
- Mock/fake repository trong unit test của Service.

Spring constructor injection cung cấp implementation tại runtime. Entity và Service không nên tự `new StudentRepository()` hoặc truy cập global SessionFactory.

## A.17. Exception và rollback trong Spring

Mặc định, Spring rollback transaction khi method `@Transactional` ném `RuntimeException` hoặc `Error` ra ngoài.

Trong project:

```java
throw new IllegalArgumentException("Book not found");
throw new IllegalStateException("Book already associated");
```

đều là runtime exceptions nên transaction rollback.

Nếu exception bị catch và nuốt trong Service, Spring có thể không biết transaction cần rollback:

```java
try {
    // write operations
} catch (RuntimeException ex) {
    // không rethrow: transaction có thể commit ngoài ý muốn
}
```

Nếu phải catch, cần rethrow, chuyển sang exception thích hợp hoặc đánh dấu transaction rollback-only.

Checked exception không mặc định gây rollback; có thể cấu hình `rollbackFor` khi nghiệp vụ yêu cầu.

## A.18. Vì sao `@Transactional` cần đi qua Spring proxy?

Spring thường áp dụng transaction bằng proxy quanh bean. Khi code bên ngoài gọi public method `@Transactional`, proxy mở transaction trước khi đi vào method và commit/rollback khi method kết thúc.

Self-invocation có thể bỏ qua proxy:

```java
public void outer() {
    inner(); // gọi trong cùng object
}

@Transactional
public void inner() { }
```

Vì `outer()` gọi trực tiếp `this.inner()`, transaction annotation ở `inner()` có thể không được áp dụng như kỳ vọng. Nên đặt transaction trên public use-case method được bean khác gọi, hoặc tách responsibility sang bean khác.

## A.19. Join fetch và từ khóa `distinct`

SQL join có thể tạo nhiều dòng cho cùng một Student:

```text
Student 1 | Book A
Student 1 | Book B
```

Ở cấp object, kết quả mong muốn là một Student chứa hai Book. `select distinct s` yêu cầu Hibernate loại trùng root entity trong kết quả:

```java
select distinct s
from Student s
left join fetch s.books
where s.id = :id
```

`distinct` ở JPQL/HQL có semantic liên quan đến entity result; SQL cụ thể có thể khác tùy provider và phiên bản.

## A.20. Database constraints là một phần của domain safety

Application validation tạo thông báo thân thiện, nhưng database constraints bảo vệ dữ liệu trước mọi writer.

Project sử dụng:

- Primary key cho identity.
- Foreign key cho referential integrity.
- Unique email và ISBN.
- Unique `(student_id, book_id)` cho association.
- `NOT NULL` cho field bắt buộc.
- Unique borrowing key minh họa cho association entity.

Trong production, constraint violation cần được chuyển thành lỗi nghiệp vụ phù hợp. Không nên trả raw SQL exception cho UI.

## A.21. Concurrent requests và race condition

Hai request có thể đồng thời kiểm tra:

```text
Request A: association chưa tồn tại
Request B: association chưa tồn tại
Request A: INSERT
Request B: INSERT
```

Business check trong Service không loại bỏ hoàn toàn race condition. Unique constraint khiến một trong hai transaction thất bại an toàn.

Các công cụ bổ sung khi nghiệp vụ yêu cầu:

- Optimistic locking bằng `@Version`.
- Pessimistic locking.
- Isolation level phù hợp.
- Idempotency key.
- Retry có kiểm soát.

Không chọn locking strategy trước khi xác định rõ contention và invariant cần bảo vệ.

## A.22. Khi nào many-to-many phải trở thành association entity?

Raw many-to-many phù hợp khi quan hệ chỉ cần biết “có liên kết hay không”.

Khi cạnh quan hệ có thuộc tính hoặc vòng đời riêng, nó đã trở thành domain concept:

```text
Student ── Borrowing ── Book
             ├── borrowedDate
             ├── dueDate
             └── status
```

Lợi ích của `Borrowing`:

- Lưu lịch sử nhiều lần mượn cùng một Book.
- Có ngày mượn và hạn trả.
- Có trạng thái `BORROWED`, `RETURNED`, `OVERDUE`.
- Có thể thêm tiền phạt, người xử lý, thời điểm trả.
- Business rule rõ ràng hơn raw join table.

Khi dùng association entity, quan hệ thường được mô hình thành:

```text
Student 1 ── * Borrowing * ── 1 Book
```

Tức là hai quan hệ one-to-many/many-to-one thay cho một raw many-to-many.

## A.23. Open Session in View và lý do project tắt nó

Project cấu hình:

```properties
spring.jpa.open-in-view=false
```

Nếu Open Session in View bật, Session có thể còn mở đến hết web request. Điều này làm lazy loading trong Controller/View có vẻ tiện lợi nhưng có thể:

- Che giấu fetch plan chưa được thiết kế.
- Phát sinh query ngoài Service transaction.
- Tạo N+1 query trong quá trình render.
- Làm ranh giới persistence khó quan sát.

Tắt OSIV buộc use case load đúng dữ liệu trong Service/Repository và giúp bài học thể hiện rõ transaction boundary.

## A.24. First-level cache không phải second-level cache

### First-level cache

- Gắn với một Session/persistence context.
- Luôn tồn tại trong Hibernate.
- Kết thúc khi Session đóng.
- Bảo đảm identity cho entity trong cùng context.

### Second-level cache

- Có thể dùng chung giữa nhiều Session.
- Cần cấu hình cache provider và cache policy.
- Không được bật mặc định chỉ vì dùng Hibernate.
- Đòi hỏi chiến lược invalidation và consistency phù hợp.

Bài học này chỉ cần hiểu first-level cache; không cần dùng second-level cache để giải quyết fetch hoặc transaction design.

## A.25. H2 và SQL Server khác nhau thế nào trong bài tập?

H2 giúp test nhanh, cô lập và không cần cài database server. Chế độ `MODE=MSSQLServer` tăng mức tương thích cú pháp nhưng H2 không trở thành SQL Server thật.

Các khác biệt có thể xuất hiện:

- Kiểu dữ liệu và độ chính xác.
- Identity generation.
- Reserved words.
- Locking và isolation behavior.
- Constraint/index behavior.
- SQL dialect và query plan.

Vì vậy chiến lược kiểm thử nên gồm:

```text
Unit/integration nhanh với H2
             +
Integration gate với SQL Server thật hoặc Testcontainers
```

Test H2 xanh là cần thiết nhưng chưa đủ để khẳng định mọi behavior production trên SQL Server.

## A.26. Checklist lý thuyết trước khi hoàn thành Slot 29

Sinh viên đạt yêu cầu khi có thể tự trả lời và chứng minh bằng code/SQL:

- [ ] Object graph `Student ↔ Book` biến thành join rows như thế nào?
- [ ] Phía nào là owner và annotation nào cho biết điều đó?
- [ ] `mappedBy` trỏ tới Java property nào?
- [ ] Vì sao helper phải sửa cả hai collection?
- [ ] Entity đang transient, managed hay detached tại từng dòng code?
- [ ] Session nào đang quản lý entity?
- [ ] Transaction bắt đầu và kết thúc ở đâu?
- [ ] SQL được phát sinh tại flush hay commit?
- [ ] Khi nào cần `persist`, khi nào cần `merge`?
- [ ] Vì sao phải dùng object mà `merge()` trả về?
- [ ] Query nào bảo đảm books dùng được sau khi Session đóng?
- [ ] Vì sao không chuyển association sang EAGER toàn cục?
- [ ] Vì sao không cascade REMOVE từ Student sang Book?
- [ ] `Set`, `equals/hashCode` và unique constraint phối hợp thế nào?
- [ ] Business rule thuộc Service, HQL thuộc DAO vì sao?
- [ ] Tại sao một use case nhiều bước cần một transaction?
- [ ] Khi nào raw many-to-many không còn đủ và phải dùng `Borrowing`?

Nắm vững các câu hỏi này quan trọng hơn việc nhớ chính xác từng dòng annotation, vì chúng có thể áp dụng cho nhiều domain và nhiều persistence technology khác nhau.
