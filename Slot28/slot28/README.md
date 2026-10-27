# SBA301 Slot 28 — Spring Boot + Hibernate Workbook

Project minh họa Spring Boot chạy dạng console và Hibernate native API (`SessionFactory`, `Session`, `Transaction`, HQL) với SQL Server. Mỗi lần chạy chọn **một** experiment để sinh viên dễ đọc SQL và đối chiếu trạng thái entity.

## Kiến thức nền tảng cần củng cố trước khi thực hành

### 1. Mục tiêu của Chapter 17 Part A

Sau bài học, sinh viên không chỉ viết được CRUD mà phải giải thích được Hibernate làm gì giữa Java object và database:

```text
Java object
    ↓ persist/get/merge/remove/query
Session + Persistence Context
    ↓ đọc mapping metadata và theo dõi entity state
Hibernate ORM Runtime
    ↓ sinh và dịch câu lệnh
SQL
    ↓ thực thi qua driver
JDBC
    ↓
SQL Server: table, row, PK, FK, constraint
```

Tư duy trung tâm: **Hibernate không chỉ là thư viện CRUD**. Hibernate là ORM runtime quản lý:

- Mapping giữa object model và relational model.
- Persistence context và identity của entity trong một Session.
- Entity lifecycle: transient, persistent/managed, detached, removed.
- Dirty checking và thời điểm flush SQL.
- Transaction boundary: commit hoặc rollback một unit of work.
- Dịch HQL/object query thành SQL phù hợp dialect SQL Server.

Evidence cuối bài không chỉ là “chương trình chạy được”. Sinh viên phải đối chiếu được ba lớp:

```text
Trạng thái Java object ↔ SQL Hibernate sinh ra ↔ row thực tế trong database
```

### 2. Phân biệt JDBC, ORM, JPA và Hibernate

| Khái niệm | Vai trò | API/đối tượng thường gặp | Ai chịu trách nhiệm chính? |
|---|---|---|---|
| JDBC | API Java mức thấp để gửi SQL đến database | `Connection`, `PreparedStatement`, `ResultSet` | Lập trình viên viết SQL, bind parameter và chuyển row thành object |
| ORM | Kỹ thuật ánh xạ object ↔ bảng/row/quan hệ | Entity, mapping metadata, persistence context | ORM tự chuyển đổi giữa object model và relational model |
| JPA/Jakarta Persistence | Đặc tả chuẩn cho ORM trong Java | `EntityManagerFactory`, `EntityManager`, JPQL | Định nghĩa contract/API, không tự thực thi ORM |
| Hibernate ORM | Một implementation của JPA và cũng có native API riêng | `SessionFactory`, `Session`, HQL | Thực thi mapping, lifecycle, dirty checking, SQL generation |
| Spring Data JPA | Lớp abstraction phía trên JPA | `JpaRepository`, derived query | Giảm code repository phổ biến; phía dưới vẫn dùng JPA provider như Hibernate |

Điểm dễ nhầm: **JPA không phải một ORM engine cạnh tranh với Hibernate**. JPA là tiêu chuẩn; Hibernate có thể là provider thực thi tiêu chuẩn đó. Do vậy, phần so sánh CRUD bên dưới thực chất so sánh hai cách sử dụng cùng Hibernate runtime:

1. Dùng API chuẩn JPA qua `EntityManager`.
2. Dùng native Hibernate API qua `Session` — cách project này đang minh họa.

### 3. SessionFactory và Session khác nhau thế nào?

| Tiêu chí | `SessionFactory` | `Session` |
|---|---|---|
| Trách nhiệm | Giữ mapping metadata, service và cấu hình dùng chung | Đại diện một persistence context/unit of work |
| Chi phí tạo | Đắt | Nhẹ hơn |
| Lifecycle | Thường một instance cho toàn application | Ngắn, mở và đóng theo use case/transaction |
| Thread-safe | Có, có thể dùng chung | Không, không chia sẻ giữa các thread |
| Project này | `HibernateUtil.getSessionFactory()` | `sessionFactory.openSession()` |

Mental model:

```text
Application lifetime:  SessionFactory ───────────────────────────── close
                                ├─ Session 1 ─ transaction ─ close
                                ├─ Session 2 ─ transaction ─ close
                                └─ Session 3 ─ query ─────── close
```

Không tạo `SessionFactory` cho từng request. Không giữ một `Session` dùng chung cho toàn ứng dụng.

### 4. Mapping Student và Book

| Object model | Mapping metadata | Relational model |
|---|---|---|
| `Student` | `@Entity`, `@Table(name="STUDENTS")` | Bảng `STUDENTS` |
| `Student.id` | `@Id`, `@GeneratedValue(IDENTITY)` | PK identity `id` |
| `Student.email` | `nullable=false`, `unique=true` | Column NOT NULL + UNIQUE |
| `Book` | `@Entity`, `@Table(name="BOOKS")` | Bảng `BOOKS` |
| `Student.books` | `@ManyToMany`, `@JoinTable` | Bảng nối `STUDENTS_BOOKS` |
| `student_id`, `book_id` | `@JoinColumn` | Hai FK đến bảng cha |

Annotation chỉ là mapping metadata. Hibernate đọc metadata này lúc bootstrap `SessionFactory`, sau đó dùng nó để sinh DDL/SQL và materialize row thành object.

### 5. So sánh triển khai CRUD: JPA và Hibernate native API

Hai cách dùng chung entity annotations (`@Entity`, `@Id`, `@ManyToMany`) và cùng cần transaction cho thao tác ghi. Khác biệt chính nằm ở bootstrap và API được gọi.

| Công việc | JPA chuẩn | Hibernate native trong project |
|---|---|---|
| Bootstrap | `EntityManagerFactory` | `SessionFactory` |
| Unit of work | `EntityManager` | `Session` |
| Transaction | `EntityTransaction` | `Transaction` |
| Create | `entityManager.persist(student)` | `session.persist(student)` |
| Read by ID | `entityManager.find(Student.class, id)` | `session.get(Student.class, id)` |
| Update managed | Sửa field, dirty checking tại flush/commit | Sửa field, dirty checking tại flush/commit |
| Update detached | `entityManager.merge(detached)` | `session.merge(detached)` |
| Delete | `entityManager.remove(managed)` | `session.remove(managed)` |
| Object query | JPQL | HQL; hỗ trợ JPQL và thêm Hibernate extensions |
| Portability | Cao hơn vì theo specification | Gắn trực tiếp với Hibernate API |
| Tính năng đặc thù | Chỉ dùng API chuẩn | Truy cập đầy đủ tính năng Hibernate |

#### 5.1. CREATE

JPA:

```java
EntityTransaction tx = entityManager.getTransaction();
tx.begin();
entityManager.persist(student);
tx.commit();
```

Hibernate native:

```java
Transaction tx = session.beginTransaction();
session.persist(student);
tx.commit();
```

Trong cả hai cách, `student` chuyển từ transient sang managed. Với `GenerationType.IDENTITY`, ID thường được gán khi Hibernate thực hiện INSERT.

#### 5.2. READ

```java
// JPA
Student student = entityManager.find(Student.class, id);

// Hibernate native
Student student = session.get(Student.class, id);
```

Nếu load cùng entity ID nhiều lần trong cùng persistence context, ORM trả lại cùng managed identity và thường không SELECT lần thứ hai.

#### 5.3. UPDATE

```java
// Cả EntityManager và Session đều dirty-check entity managed
Student student = session.get(Student.class, id);
student.setMarks(9.0);
transaction.commit(); // Hibernate phát hiện thay đổi và sinh UPDATE
```

Không cần gọi `update()` cho entity đang managed. Với object detached, dùng `merge()` và tiếp tục làm việc với object được trả về:

```java
Student managed = session.merge(detached);
managed.setMarks(8.8);
```

#### 5.4. DELETE

```java
// JPA
Student managed = entityManager.find(Student.class, id);
entityManager.remove(managed);

// Hibernate native
Student managed = session.get(Student.class, id);
session.remove(managed);
```

Entity cần ở trạng thái managed. Với quan hệ, phải hiểu owning side và cascade trước khi xóa để tránh lỗi FK hoặc xóa ngoài ý muốn.

#### 5.5. QUERY

```java
// JPQL qua JPA
entityManager.createQuery(
    "from Student s where s.email = :email", Student.class)
    .setParameter("email", email)
    .getResultList();

// HQL qua Hibernate Session
session.createQuery(
    "from Student s where s.email = :email", Student.class)
    .setParameter("email", email)
    .getResultList();
```

Hai câu trên gần như giống nhau vì HQL là nền tảng ảnh hưởng đến JPQL và Hibernate hỗ trợ JPQL. Cả hai query **entity/property**, không query trực tiếp table/column. `Student` và `email` đúng; `STUDENTS` chỉ đúng khi viết SQL/native query.

### 6. Lợi ích và lựa chọn cách triển khai

**Dùng JPA `EntityManager` khi:**

- Muốn code phụ thuộc vào chuẩn Jakarta Persistence.
- Cần khả năng thay provider hoặc giữ kiến trúc ít phụ thuộc vendor hơn.
- Làm ứng dụng Spring Data JPA, nơi repository dựa trên JPA contract.

**Dùng Hibernate `Session` khi:**

- Bài học cần quan sát trực tiếp Hibernate architecture và lifecycle.
- Cần Hibernate-specific API/tính năng không có trong JPA.
- Cần hiểu chính xác runtime đang thực thi phía dưới Spring Data JPA.

**Dùng Spring Data JPA khi:**

- Xây dựng CRUD backend thông thường và muốn giảm boilerplate repository.
- Đã hiểu transaction, entity state, lazy loading và query semantics ở tầng dưới.

Ví dụ Spring Data:

```java
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
}
```

`JpaRepository` làm code CRUD ngắn hơn, nhưng không loại bỏ Hibernate concepts. Entity vẫn có lifecycle, transaction vẫn có boundary, lazy loading vẫn cần persistence context và query vẫn được Hibernate dịch thành SQL.

### 7. Learning workflow và evidence

| Pha | Sinh viên làm gì? | Evidence phải lưu |
|---|---|---|
| Prepare | Ôn JPA/SQL/transaction; kiểm JDK, Maven, SQL Server | Baseline checklist |
| Understand | Phân biệt ORM, JDBC, JPA, Hibernate, `SessionFactory`, `Session` | Concept map |
| Model | Map Student/Book từ class sang table, PK, FK | Mapping worksheet |
| Trace | Trace bootstrap và một persist/query transaction | Execution trace + SQL log |
| Implement | Tạo Hibernate micro-project và HQL | Source + SQL evidence |
| Break | Cố ý sai dialect, mapping, session, transaction hoặc HQL | Failure evidence |
| Diagnose | Đọc root cause, SQL log, entity state và session scope | Debug note |
| Verify | Đối chiếu object state, generated SQL và database row | Test matrix |
| Transfer | So sánh Hibernate native, JPA và Spring Data JPA | Architecture worksheet |

Checkpoint trước khi vào Step 1: sinh viên phải trả lời được “JPA là gì?”, “Hibernate là gì?”, “SessionFactory khác Session thế nào?”, và mô tả được chuỗi `Java object → Hibernate → JDBC → SQL Server`.

## Từ bài tập minh họa đến dự án doanh nghiệp

### 8. Lab đang đơn giản hóa điều gì?

Project này cố ý là một **console workbook**: mỗi lần chạy chọn một experiment, tự mở `Session`, tự bắt đầu transaction và in SQL để quan sát. Cách này phù hợp để học cơ chế Hibernate nhưng chưa phải kiến trúc backend production hoàn chỉnh.

| Trong workbook | Trong dự án doanh nghiệp thường gặp |
|---|---|
| `CommandLineRunner` chọn experiment | HTTP controller, message consumer hoặc scheduled job nhận use case |
| Gọi `HibernateUtil` trực tiếp | Spring quản lý `EntityManagerFactory`/`SessionFactory` và dependency injection |
| Tự `openSession()`/`beginTransaction()` | Service method dùng transaction declarative, thường là `@Transactional` |
| `hibernate.cfg.xml` | `application.yml`, environment variables, secret manager và profile |
| `hbm2ddl.auto=update` | Flyway/Liquibase migration có version, review và rollback plan |
| Built-in Hibernate connection pool | HikariCP hoặc datasource do platform quản lý |
| Trả/in trực tiếp entity | API dùng request/response DTO, validation và mapping |
| Một process chạy một bài | Nhiều request/thread đồng thời, connection pool và transaction độc lập |
| Đọc SQL bằng console | Structured log, metrics, tracing, slow-query dashboard và alert |
| H2 test nhanh | Unit test + integration test SQL Server/Testcontainers + API/contract test |

Thông điệp chuyển giao: **khái niệm không thay đổi, cách quản lý runtime thay đổi**. Dù Spring mở Session và transaction giúp lập trình viên, entity vẫn có state, persistence context vẫn có phạm vi, flush vẫn khác commit và Hibernate vẫn phải sinh SQL.

### 9. Ánh xạ 9 experiment sang tình huống thực tế

| Experiment | Kiến thức trong lab | Ứng dụng/rủi ro trong doanh nghiệp |
|---|---|---|
| `lifecycle` | transient → managed → detached | Entity nhận từ request không tự trở thành managed; không giả định object ngoài transaction được theo dõi |
| `dirty-checking` | Sửa managed entity tự sinh UPDATE | Service chỉ load và sửa field trong transaction; cần kiểm soát field nào được phép thay đổi |
| `first-level-cache` | Cùng ID/Session trả cùng identity | Giảm query lặp trong một unit of work nhưng Session dài có thể giữ nhiều object và dữ liệu cũ |
| `detached-merge` | Detached change và `merge()` | Cẩn thận lost update, mass assignment và merge cả object graph từ client |
| `hql` | Query object model, parameter, paging | Repository query an toàn, DTO projection, deterministic pagination và tránh query thừa |
| `relationship` | Owning side và join table | Thiết kế aggregate boundary, cascade, orphan/delete semantics và tránh JSON recursion |
| `rollback` | Atomic transaction, flush ≠ commit | Nhiều thao tác DB trong cùng use case phải cùng commit hoặc cùng rollback |
| `break-hql` | Đọc semantic exception | Phân loại lỗi query/mapping sớm bằng test và log root cause |
| `batch` | `flush()` + `clear()` theo checkpoint | Import dữ liệu lớn mà không làm persistence context tăng không giới hạn |

Khi review code thực tế, hãy luôn hỏi: “Experiment nào giải thích hành vi này?” và “Evidence là object state, SQL hay database row?”.

### 10. Kiến trúc backend điển hình sử dụng Hibernate

```text
Client / Postman
       ↓ HTTP + JSON
REST Controller
       ↓ validate request, không chứa persistence logic
Application Service  ← transaction boundary (@Transactional)
       ↓ orchestration + business rules
Repository
       ↓ JPA EntityManager / Hibernate Session / Spring Data JPA
Hibernate ORM + Persistence Context
       ↓ generated SQL
JDBC Driver + HikariCP
       ↓
SQL Server
```

#### 10.1. Controller

Controller chịu trách nhiệm HTTP: parse path/query/body, validation, status code và response. Controller không nên tự mở Session hoặc chứa HQL.

```java
@PostMapping("/api/students")
ResponseEntity<StudentResponse> create(@Valid @RequestBody CreateStudentRequest request) {
    StudentResponse result = studentService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
}
```

#### 10.2. Service và transaction boundary

Service biểu diễn một use case nghiệp vụ. Đây thường là nơi đặt transaction boundary:

```java
@Transactional
public StudentResponse updateMarks(long id, double marks) {
    Student student = studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));
    student.setMarks(marks); // dirty checking
    return studentMapper.toResponse(student);
}
```

Không cần gọi `save()` chỉ để Hibernate nhận một thay đổi trên entity đang managed. Tuy nhiên, team có thể giữ `save()` vì convention; sinh viên phải hiểu SQL xuất hiện do dirty checking chứ không phải do tên method.

#### 10.3. Repository

Repository che giấu chi tiết truy cập dữ liệu và tập trung query. Không đặt business workflow dài trong repository.

```java
public interface StudentJpaRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    @Query("select s from Student s where s.marks >= :minimum order by s.id")
    Page<Student> findQualified(@Param("minimum") double minimum, Pageable pageable);
}
```

Native Hibernate API vẫn hữu ích cho use case đặc thù, nhưng không nên trộn `Session`, `EntityManager` và Spring Data tùy tiện trong cùng module nếu chưa có quy ước rõ ràng.

#### 10.4. DTO thay vì expose entity

Không nên dùng entity trực tiếp làm JSON request/response vì:

- Client có thể sửa field không được phép — mass assignment.
- Lazy association có thể phát sinh query ngoài dự kiến hoặc `LazyInitializationException`.
- Quan hệ hai chiều có thể gây JSON recursion.
- Thay đổi database model vô tình phá API contract.
- Entity chứa persistence concerns, còn DTO mô tả use case/API contract.

```java
public record CreateStudentRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @DecimalMin("0.0") @DecimalMax("10.0") double marks) {
}

public record StudentResponse(
        long id, String email, String firstName, String lastName, double marks) {
}
```

### 11. Transaction và persistence context trong thực tế

#### 11.1. Một transaction cho một use case nhất quán

Ví dụ “gán sách cho sinh viên” gồm kiểm tra Student, kiểm tra Book và tạo association. Các thay đổi phải nằm trong cùng transaction; lỗi ở bước cuối phải rollback toàn bộ.

```java
@Transactional
public void assignBook(long studentId, long bookId) {
    Student student = getStudent(studentId);
    Book book = getBook(bookId);
    student.addBook(book);
}
```

Không chia một use case atomic thành nhiều transaction repository rời rạc. Cũng không giữ transaction mở trong lúc gọi API bên ngoài chậm nếu có thể thiết kế workflow/outbox phù hợp.

#### 11.2. `flush()` không phải `commit()`

Hibernate có thể flush trước commit, trước một số query hoặc khi gọi `flush()` thủ công. SQL đã xuất hiện trong log không có nghĩa dữ liệu đã commit. Experiment `rollback` minh họa đúng điểm này.

#### 11.3. Đọc dữ liệu và read-only transaction

Với use case chỉ đọc, có thể dùng:

```java
@Transactional(readOnly = true)
public Page<StudentResponse> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(mapper::toResponse);
}
```

`readOnly=true` truyền ý định và có thể giúp framework/provider tối ưu, nhưng không thay thế database permission hay security control.

#### 11.4. Không dùng Open Session in View để che thiết kế fetch

Nếu persistence context kéo dài đến lúc serialize response, lazy query có thể âm thầm chạy trong web layer và tạo N+1. Thông lệ an toàn là fetch đúng dữ liệu trong service transaction, map sang DTO, rồi trả DTO.

### 12. Mapping và thiết kế domain trong thực tế

#### 12.1. Quan hệ không phải lúc nào cũng cần hai chiều

Chỉ tạo bidirectional association khi hai hướng đều có use case. Quan hệ hai chiều tăng chi phí đồng bộ object graph; helper như `student.addBook(book)` phải cập nhật cả hai phía.

#### 12.2. Cẩn thận với cascade

- `PERSIST`: lưu entity con mới cùng entity cha khi ownership thật sự phù hợp.
- `MERGE`: lan truyền merge; có thể cập nhật graph lớn ngoài ý muốn.
- `REMOVE`: nguy hiểm với many-to-many vì Book có thể được nhiều Student dùng chung.
- `ALL`: không phải giá trị mặc định nên chọn cho mọi association.

Project cố ý persist `Student` và `Book` riêng rồi mới attach, thay vì dùng `CascadeType.ALL`, để sinh viên nhìn rõ từng thao tác.

#### 12.3. `equals()`/`hashCode()` và collection

Entity nằm trong `Set` cần equality ổn định. Không nên tạo `hashCode()` phụ thuộc vào generated ID rồi thay đổi sau persist. Trong dự án thật, team cần thống nhất chiến lược business key hoặc implementation an toàn cho entity lifecycle.

#### 12.4. Validation ở nhiều lớp

- DTO validation kiểm tra input format và required fields.
- Business rule kiểm tra điều kiện nghiệp vụ trong service.
- Database constraint bảo vệ tính toàn vẹn cuối cùng: NOT NULL, UNIQUE, FK, CHECK.

Không chỉ dựa vào Java validation vì có thể có nhiều ứng dụng hoặc script cùng ghi database.

### 13. Hiệu năng và khả năng mở rộng

#### 13.1. N+1 query

Nếu load 100 Student rồi lazy-load books của từng Student, hệ thống có thể chạy 1 query Student + 100 query Book. Phát hiện bằng SQL log/metrics và xử lý theo use case bằng:

- `join fetch` có kiểm soát.
- Entity graph.
- DTO projection.
- Batch fetching.

Không đổi mọi association thành `EAGER`; cách đó thường chuyển vấn đề thành query lớn, Cartesian product hoặc over-fetching.

#### 13.2. Pagination

Luôn có `ORDER BY` ổn định. Với dataset lớn hoặc trang sâu, đánh giá keyset pagination thay vì offset rất lớn. Không fetch-join collection một cách ngây thơ cùng pagination nếu chưa kiểm tra SQL/kết quả.

#### 13.3. Batch processing

Khi import dữ liệu:

```java
if (index % batchSize == 0) {
    entityManager.flush();
    entityManager.clear();
}
```

`clear()` làm các entity thành detached; code phía sau không được tiếp tục giả định chúng đang managed. IDENTITY generation cũng có thể hạn chế JDBC insert batching, nên phải đo bằng SQL/metrics thay vì chỉ dựa vào config.

#### 13.4. Connection pool

Session không phải JDBC connection dùng độc quyền cho toàn application. Trong production, datasource thường dùng HikariCP. Pool phải được cấu hình theo sức chịu tải của database, timeout và concurrency thực tế; tăng pool vô hạn không làm database nhanh hơn.

### 14. Concurrency và lost update

Hai transaction có thể cùng đọc marks `8.0`, sau đó lần lượt ghi `8.5` và `9.0`; update sau có thể ghi đè update trước. Với aggregate cần bảo vệ, cân nhắc optimistic locking:

```java
@Version
private long version;
```

Hibernate thêm version vào điều kiện UPDATE. Nếu row đã được transaction khác sửa, update count bằng 0 và ORM ném optimistic-lock exception. Application cần trả lỗi phù hợp, ví dụ HTTP `409 Conflict`, hoặc retry khi use case cho phép.

Pessimistic lock chỉ dùng khi thật sự cần vì có thể tăng blocking/deadlock. Luôn xác định consistency requirement trước khi chọn lock.

### 15. Quản lý schema, cấu hình và bí mật

#### 15.1. Schema migration

`hibernate.hbm2ddl.auto=update` thuận tiện cho lab nhưng không phải quy trình migration production vì:

- Không tạo change history có version.
- Khó review SQL trước khi deploy.
- Không quản lý data migration phức tạp.
- Không cung cấp deployment/rollback plan rõ ràng.

Thông lệ doanh nghiệp là Flyway/Liquibase:

```text
V1__create_students.sql
V2__create_books.sql
V3__create_students_books.sql
V4__add_student_version.sql
```

Production thường đặt Hibernate ở `validate` để kiểm tra mapping khớp schema sau migration.

#### 15.2. Cấu hình theo environment

Không hard-code URL, username hoặc password trong source. Dùng environment variables, deployment secret hoặc secret manager. Tách profile local/test/staging/production và không commit credential thật.

#### 15.3. Quyền database tối thiểu

Runtime account chỉ nên có quyền cần thiết. Account chạy migration có thể tách khỏi account ứng dụng. Không mặc định dùng `sa` ngoài local lab.

### 16. Error handling, logging và quan sát hệ thống

Backend nên chuyển exception kỹ thuật thành response ổn định:

| Tình huống | Ví dụ exception | HTTP thường dùng |
|---|---|---:|
| Không tìm thấy entity | custom `StudentNotFoundException` | 404 |
| Input không hợp lệ | validation exception | 400 |
| Email trùng/optimistic conflict | constraint/locking exception | 409 |
| Lỗi ngoài dự kiến | database/system exception | 500 |

Không trả stack trace, SQL hay credential cho client. Log nội bộ cần correlation ID, operation, duration và root cause; tránh log password hoặc dữ liệu cá nhân nhạy cảm.

Trong production cần quan sát ít nhất:

- Connection pool active/pending/timeout.
- Transaction/query latency và error rate.
- Slow queries và query count trên một request.
- Deadlock, lock timeout và optimistic-lock conflict.
- JVM heap/GC khi xử lý batch.

### 17. Chiến lược kiểm thử

| Tầng test | Mục tiêu | Database |
|---|---|---|
| Unit test | Business rule thuần Java | Không cần DB/mock boundary |
| Repository integration test | Mapping, HQL, constraint và transaction | Ưu tiên SQL Server thật hoặc container |
| Service integration test | Use case + rollback + concurrency | Database gần production |
| API test | HTTP contract, validation, status code | Test environment/container |
| Migration test | Migration chạy từ baseline đến version mới | Cùng engine/version production |

H2 nhanh và phù hợp feedback sớm, nhưng dialect, identity, type, locking và SQL behavior có thể khác SQL Server. Vì vậy test H2 trong workbook không phải bằng chứng duy nhất trước production.

Một test tốt cần xác nhận outcome, không chỉ xác nhận method đã được gọi:

```text
Request/use case → object state → generated SQL/transaction → committed DB state → response
```

### 18. Anti-pattern checklist khi review code

- Tạo `SessionFactory` cho mỗi request.
- Dùng chung một `Session` giữa nhiều thread.
- Mở transaction nhưng không rollback trong nhánh lỗi.
- Trả entity trực tiếp qua REST API.
- Ghép input người dùng vào HQL/SQL string.
- Dùng `CascadeType.ALL` và `FetchType.EAGER` cho mọi association.
- Giữ Session/transaction quá dài hoặc chứa network call chậm.
- Chạy batch lớn mà không `flush()`/`clear()`.
- Dùng `hbm2ddl.auto=update` làm chiến lược migration production.
- Bật SQL/parameter log chứa dữ liệu nhạy cảm ở production.
- Chỉ test bằng H2 nhưng deploy lên SQL Server.
- Thấy `BUILD SUCCESS` và kết luận mapping/transaction production đã đúng.

### 19. Lộ trình chuyển project này thành CRUD backend

Sau khi hoàn thành workbook, có thể mở rộng theo thứ tự sau mà không bỏ qua kiến thức nền:

1. Thêm `spring-boot-starter-web` và bỏ `spring.main.web-application-type=none`.
2. Chuyển cấu hình persistence sang Spring-managed JPA/Hibernate và HikariCP.
3. Tạo Flyway migrations thay cho `hbm2ddl.auto=update`.
4. Tạo DTO, Bean Validation và mapper.
5. Tạo repository, service `@Transactional` và REST controller.
6. Thêm exception handler và response contract.
7. Viết API tests/Postman collection cho CRUD và validation cases.
8. Thêm integration test với SQL Server container/test instance.
9. Kiểm tra N+1, pagination, constraint, rollback và concurrent update.
10. Thêm health check, metrics, structured logging và security trước deployment.

Kết quả mong đợi không phải “thay native Hibernate bằng Spring rồi quên lý thuyết”. Sinh viên phải chỉ ra được trong kiến trúc mới: persistence context nằm ở đâu, transaction bắt đầu/kết thúc ở đâu, entity trở thành detached lúc nào và câu SQL nào được tạo cho mỗi use case.

## 0. Chuẩn bị và quyết định version

| Thành phần | Version | Lý do |
|---|---:|---|
| Java | 21 | Đúng JDK đang cài trên máy |
| Maven | 3.9.9 | Đúng Maven đang cài trên máy |
| Spring Boot | 3.5.16 | Hỗ trợ Java 17–25 và Maven 3.6.3+ |
| Hibernate ORM | 6.6.x | Do Spring Boot 3.5 quản lý; tương thích Jakarta Persistence 3.1 và workbook |
| SQL Server JDBC | do Spring Boot quản lý | Tránh ghép version driver thủ công |
| JUnit | do Spring Boot quản lý | Đồng bộ bộ test của Spring Boot |

Không nâng riêng `hibernate-core` lên 7.x/8.x: Spring Boot 3.5 được kiểm thử với Hibernate 6.6. Tham khảo [Spring Boot 3.5 system requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html) và [Hibernate ORM 6.6 compatibility](https://hibernate.org/orm/releases/6.6/).

### Cách chạy chung

PowerShell:

```powershell
$env:DB_PASSWORD = 'YourStrongPassword'
# Hai biến dưới đây chỉ cần khi khác giá trị mặc định
$env:DB_USERNAME = 'sa'
$env:DB_URL = 'jdbc:sqlserver://localhost:1433;databaseName=LibraryDB;encrypt=true;trustServerCertificate=true'

mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=lifecycle'
```

CMD:

```bat
set DB_PASSWORD=YourStrongPassword
set DB_USERNAME=sa
mvn spring-boot:run -Dspring-boot.run.arguments="--lab.experiment=lifecycle"
```

Không tạo file chứa password thật và không commit `.env`. Khi gặp lỗi kết nối, đọc `Caused by` cuối stack trace trước.

---

## 6.1. Step 1 — Tạo database baseline

**Mục tiêu:** Có database rỗng `LibraryDB`; Hibernate sẽ tạo/cập nhật schema trong lab.

**Thao tác:** Mở SSMS hoặc Azure Data Studio, kết nối SQL Server bằng tài khoản có quyền tạo database, chạy [database/create-library-db.sql](database/create-library-db.sql).

**Code/config:**

```sql
IF DB_ID(N'LibraryDB') IS NULL
BEGIN
    CREATE DATABASE LibraryDB;
END;
GO
USE LibraryDB;
GO
SELECT DB_NAME() AS CurrentDatabase;
```

**Lệnh chạy:** Execute script trong query editor. Nếu dùng `sqlcmd`:

```powershell
sqlcmd -S localhost,1433 -U sa -P $env:DB_PASSWORD -i database/create-library-db.sql -C
```

**Verification:** Result grid có một dòng `LibraryDB`.

**PASS/FAIL:** PASS khi `CurrentDatabase = LibraryDB`. FAIL: kiểm tra tên instance, TCP/IP, port 1433, quyền `CREATE DATABASE`, SQL Authentication và firewall.

## 6.2. Step 2 — Tạo Maven project

**Mục tiêu:** Có cấu trúc Maven chuẩn, package gốc `fu.edu.sba301.slot28`.

**Thao tác:** Với project mới có thể dùng:

```powershell
mkdir sba301-slot28-hibernate
cd sba301-slot28-hibernate
idea .
```

Project mẫu này đã có cấu trúc:

```text
src/main/java/fu/edu/sba301/slot28/
├── Sba301Slot28Application.java
├── app/WorkbookRunner.java
├── entity/{Student,Book}.java
├── repository/StudentRepository.java
└── util/HibernateUtil.java
src/main/resources/{application.properties,hibernate.cfg.xml}
src/test/java/fu/edu/sba301/slot28/HibernateWorkbookTest.java
database/{create-library-db.sql,verify-workbook.sql}
```

**Code/config:** `pom.xml` ở root, source ở `src/main/java`, resource ở `src/main/resources`.

**Lệnh chạy:** `mvn -v` và `mvn help:effective-pom`.

**Verification:** Maven nhận đúng project và Java 21.

**PASS/FAIL:** PASS khi effective POM được tạo. FAIL: mở terminal đúng thư mục chứa `pom.xml`, kiểm tra `JAVA_HOME`.

## 6.3. Step 3 — Maven dependencies

**Mục tiêu:** Spring Boot, Hibernate, SQL Server JDBC và JUnit có version tương thích.

**Thao tác:** Dùng Spring Boot parent 3.5.16; khai báo `hibernate-core` và `mssql-jdbc` nhưng để BOM của Boot quản lý version.

**Code/config:** Xem [pom.xml](pom.xml). Dependencies chính:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
</dependency>
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Lệnh chạy:**

```powershell
mvn -q -DskipTests compile
mvn dependency:tree
```

**Verification:** Tree chỉ có một dòng version Hibernate core; không có xung đột Jakarta/`javax.persistence`.

**PASS/FAIL:** PASS khi `BUILD SUCCESS`. FAIL: xóa version override tùy ý, kiểm tra Internet/proxy/Maven settings.

## 6.4. Step 4 — `hibernate.cfg.xml`

**Mục tiêu:** Khai báo SQL Server connection, schema mode và entity mappings.

**Thao tác:** Mở [src/main/resources/hibernate.cfg.xml](src/main/resources/hibernate.cfg.xml). URL mặc định dùng `LibraryDB`, encryption bật và tin certificate local.

**Code/config:** XML không chứa password thật. [HibernateUtil.java](src/main/java/fu/edu/sba301/slot28/util/HibernateUtil.java) đọc `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `HIBERNATE_DDL_AUTO` rồi override cấu hình trước khi build.

**Lệnh chạy:**

```powershell
$env:DB_PASSWORD = 'YourStrongPassword'
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=lifecycle'
```

**Verification:** Log có connection pool khởi tạo, DDL/SQL và không có `Login failed`.

**PASS/FAIL:** PASS khi SessionFactory build và INSERT chạy. FAIL: `Login failed` → credentials; `TCP/IP`/timeout → service/port; `Unknown entity` → mapping class; certificate → kiểm tra URL.

## 6.5. Step 5 — Tạo Student entity

**Mục tiêu:** Map object `Student` vào `STUDENTS` với identity ID và email unique.

**Thao tác:** Đọc [Student.java](src/main/java/fu/edu/sba301/slot28/entity/Student.java).

**Code/config:** Class có `@Entity`, `@Table`, `@Id`, `@GeneratedValue(IDENTITY)`, email `nullable=false, unique=true`, constructor không tham số `protected` và constructor nghiệp vụ.

**Lệnh chạy:** `mvn -q -DskipTests compile`.

**Verification:** Code compile; IDE nhận Jakarta imports (`jakarta.persistence.*`).

**PASS/FAIL:** PASS khi entity có no-arg constructor và đúng một `@Id`. FAIL thường do dùng nhầm `javax.persistence` hoặc thiếu constructor.

## 6.6. Step 6 — Tạo Book entity

**Mục tiêu:** Map `Book` vào `BOOKS` với ISBN unique.

**Thao tác:** Đọc [Book.java](src/main/java/fu/edu/sba301/slot28/entity/Book.java).

**Code/config:** `isbn` bắt buộc/unique; `title` bắt buộc; `author` optional; phía inverse dùng `@ManyToMany(mappedBy = "books")`.

**Lệnh chạy:** `mvn -q -DskipTests compile`.

**Verification:** Compile và khi bootstrap không có `AnnotationException`.

**PASS/FAIL:** PASS khi Hibernate nhận cả hai entity. FAIL: kiểm tra mapping XML và tên field `books`.

## 6.7. Step 7 — SessionFactory utility

**Mục tiêu:** Chỉ tạo một `SessionFactory` cho application lifetime; tạo `Session` ngắn cho từng unit of work.

**Thao tác:** Đặt breakpoint ở `buildSessionFactory()` trong [HibernateUtil.java](src/main/java/fu/edu/sba301/slot28/util/HibernateUtil.java), chạy một experiment.

**Code/config:** Static final field giữ singleton; `shutdown()` đóng factory sau experiment.

**Lệnh chạy:** `mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=lifecycle'`.

**Verification:** Breakpoint `buildSessionFactory()` dừng đúng một lần; nhiều lần `openSession()` không tạo factory mới.

**PASS/FAIL:** PASS khi factory build thành công một lần. FAIL: đọc root cause cuối cùng về JDBC, dialect hay mapping. `SessionFactory` thread-safe/đắt; `Session` không thread-safe/rẻ.

## 6.8. Step 8 — Persist và trace lifecycle

**Mục tiêu:** Quan sát transient → persistent → detached.

**Thao tác:** Chạy experiment `lifecycle`; email có UUID nên có thể chạy lặp lại.

**Code/config:** Method `WorkbookRunner.lifecycle()` in trạng thái, gọi `persist()` trong transaction.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=lifecycle'
```

**Verification:** Ghi lại ID console, ví dụ `Reuse --lab.student-id=7`; chạy:

```sql
SELECT TOP (20) * FROM dbo.STUDENTS ORDER BY id DESC;
```

**PASS/FAIL:** PASS khi có INSERT và row mới. FAIL nếu không commit hoặc transaction rollback.

## 6.9. Step 9 — Dirty checking

**Mục tiêu:** Chứng minh entity managed tự sinh UPDATE dù không gọi `update()`.

**Thao tác:** Thay `7` bằng ID Step 8.

**Code/config:** `WorkbookRunner.dirtyChecking()` load entity, gọi `setMarks(9.0)`, rồi commit.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=dirty-checking --lab.student-id=7'
```

**Verification:** Console có một UPDATE; DB `marks = 9.0`.

**PASS/FAIL:** PASS khi UPDATE xuất hiện tại flush/commit. FAIL `not found` → dùng đúng ID; không UPDATE → entity có thể detached hoặc value không đổi.

## 6.10. Step 10 — First-level cache

**Mục tiêu:** Cùng identity trong cùng Session trả về cùng Java object.

**Thao tác:** Load cùng ID hai lần trong một Session.

**Code/config:** `WorkbookRunner.firstLevelCache()` kiểm `first == second`.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=first-level-cache --lab.student-id=7'
```

**Verification:** `a == b: true`, SQL SELECT cho Student chỉ xuất hiện một lần.

**PASS/FAIL:** PASS khi hai điều trên đúng. FAIL nếu code vô tình mở hai Session khác nhau.

## 6.11. Step 11 — Detached experiment

**Mục tiêu:** Sửa object sau khi Session đóng không tự sửa DB.

**Thao tác:** Experiment kết hợp `detached-merge` in giá trị object và DB trước merge.

**Code/config:** Sau `try (Session ...)`, code gọi `detached.setMarks(7.0)` rồi query bằng Session mới.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=detached-merge --lab.student-id=7'
```

**Verification:** Dòng `DETACHED` cho thấy `object=7.0`, còn database vẫn là giá trị cũ.

**PASS/FAIL:** PASS khi trước merge không có UPDATE. FAIL nếu Session cũ chưa đóng.

## 6.12. Step 12 — Merge detached entity

**Mục tiêu:** Merge state vào một managed copy và chỉ sửa reference được trả về.

**Thao tác:** Tiếp tục quan sát experiment Step 11.

**Code/config:** `Student managed = session.merge(detached); managed.setMarks(8.8);`.

**Lệnh chạy:** Giống Step 11.

**Verification:** Console: original managed `false`, merge result managed `true`; DB marks `8.8`.

**PASS/FAIL:** PASS khi commit đổi DB. FAIL nếu tiếp tục giả định object detached gốc đã được reattach.

## 6.13. Step 13 — HQL SELECT

**Mục tiêu:** Query object model bằng entity/property name.

**Thao tác:** Chạy nhóm HQL.

**Code/config:** Repository dùng `from Student s order by s.lastName, s.id`.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=hql --lab.student-id=7'
```

**Verification:** Danh sách `Student{...}` được in.

**PASS/FAIL:** PASS khi HQL dùng `Student`/`lastName`. FAIL nếu dùng `STUDENTS`/tên column DB.

## 6.14. Step 14 — HQL WHERE + named parameter

**Mục tiêu:** Bind input an toàn, không nối chuỗi HQL.

**Thao tác:** Xem `StudentRepository.findByEmail()`.

**Code/config:** `where s.email = :email` và `.setParameter("email", email)`.

**Lệnh chạy:** Giống Step 13.

**Verification:** SQL log có parameter `?`; kết quả đúng email.

**PASS/FAIL:** PASS khi input đi qua parameter binding. FAIL/security risk nếu ghép user input vào query string.

## 6.15. Step 15 — HQL paging

**Mục tiêu:** Lấy page ổn định, tối đa 10 rows.

**Thao tác:** Repository gọi `setFirstResult(0)`, `setMaxResults(10)` và luôn `order by s.id`.

**Code/config:** `StudentRepository.findPage(0, 10)`.

**Lệnh chạy:** Giống Step 13.

**Verification:** `HQL paging (max 10): count=N`, với `N <= 10`; xem SQL Server OFFSET/FETCH translation.

**PASS/FAIL:** PASS khi tối đa 10 và có ORDER BY. FAIL nếu pagination không deterministic vì thiếu order.

## 6.16. Step 16 — HQL projection

**Mục tiêu:** Chỉ lấy dữ liệu cần dùng thay vì full entity.

**Thao tác:** Repository query `select s.email` với result type `String.class`.

**Code/config:** `StudentRepository.findAllEmails()`.

**Lệnh chạy:** Giống Step 13.

**Verification:** Console in list email; SQL chỉ select column email.

**PASS/FAIL:** PASS khi kết quả là `List<String>`. FAIL khi ép result projection về `Student`.

## 6.17. Step 17 — HQL bulk UPDATE

**Mục tiêu:** Hiểu mutation query và số row bị tác động.

**Thao tác:** `StudentRepository.updateMarks()` dùng API Hibernate 6 `createMutationQuery()`.

**Code/config:** `update Student s set s.marks = :marks where s.id = :id`.

**Lệnh chạy:** Giống Step 13 với ID tồn tại.

**Verification:** `updated=1`, DB marks `9.5`.

**PASS/FAIL:** PASS khi count 1. FAIL count 0 → ID không tồn tại. Bulk HQL bỏ qua state đang giữ trong persistence context; clear/refresh nếu trộn bulk update với managed entities.

## 6.18. Step 18 — Many-to-Many bridge

**Mục tiêu:** Persist Student–Book association qua `STUDENTS_BOOKS`.

**Thao tác:** `Student` là owning side có `@JoinTable`; helper `addBook()` cập nhật cả hai phía trong memory.

**Code/config:** `WorkbookRunner.relationship()` persist cả Student và Book rồi attach.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=relationship'
```

**Verification:**

```sql
SELECT TOP (20) * FROM dbo.STUDENTS_BOOKS ORDER BY student_id DESC;
```

**PASS/FAIL:** PASS khi có row chứa hai FK. FAIL: kiểm tra owning side, transaction và thứ tự persist.

## 6.19. Step 19 — Transaction rollback

**Mục tiêu:** Chứng minh `flush()` không phải `commit()` và transaction là atomic.

**Thao tác:** Code INSERT + flush, cố ý ném exception, rollback, rồi query lại theo email.

**Code/config:** `WorkbookRunner.rollback()`.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=rollback'
```

**Verification:** Có thể thấy INSERT SQL đã gửi nhưng console kết thúc `rows after rollback=0`.

**PASS/FAIL:** PASS khi row count 0. FAIL nếu catch exception nhưng quên `rollback()` hoặc transaction đã commit.

## 6.20. Step 20 — Break-It: sai HQL entity/table name

**Mục tiêu:** Phân biệt HQL object model và SQL relational model.

**Thao tác:** Chạy query sai `from STUDENTS`, catch lỗi, đọc fix.

**Code/config:** `WorkbookRunner.breakHql()`.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=break-hql'
```

**Verification:** Có semantic/query exception dự kiến và gợi ý `from Student`.

**PASS/FAIL:** PASS khi lỗi được tái hiện và chẩn đoán. FAIL nếu “fix” bằng cách đổi table nhưng vẫn dùng HQL; native SQL là API khác.

## 6.21. Step 21 — Break-It: Session quá dài

**Mục tiêu:** Thấy persistence context giữ managed entities và học checkpoint `flush()` + `clear()`.

**Thao tác:** Runner insert N entity trong một transaction, mỗi 50 row flush + clear. Hãy đặt breakpoint trước/sau `clear()` hoặc dùng profiler.

**Code/config:** `hibernate.jdbc.batch_size=50`; lưu ý IDENTITY có thể làm giảm hiệu quả JDBC insert batching, nhưng `clear()` vẫn giới hạn persistence-context growth.

**Lệnh chạy:**

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--lab.experiment=batch --lab.batch-size=1000'
```

**Verification:** Có checkpoint 50, 100, … và cuối cùng `inserted=1000`.

**PASS/FAIL:** PASS khi hoàn thành, dữ liệu đủ và Session được clear định kỳ. FAIL/OOM khi giữ Session quá lâu; giảm batch size, giữ transaction boundary hợp lý. Đây là lifecycle exercise, không phải benchmark.

## 6.22. Step 22 — SQL verification matrix

**Mục tiêu:** Có checklist chứng cứ thống nhất cho mọi experiment.

**Thao tác:** Sau từng run, lưu console SQL và chạy [database/verify-workbook.sql](database/verify-workbook.sql).

**Code/config:**

| Test | Operation | Expected SQL/DB | Cách chạy |
|---|---|---|---|
| T01 | persist Student | INSERT + row | `lifecycle` |
| T02 | get same ID twice | một SELECT, sau đó cache | `first-level-cache` |
| T03 | managed field change | UPDATE at commit | `dirty-checking` |
| T04 | detached field change | không UPDATE | phần đầu `detached-merge` |
| T05 | merge detached | SELECT/UPDATE + DB changed | phần sau `detached-merge` |
| T06 | parameter query | SELECT với bound parameter | `hql` |
| T07 | paging | OFFSET/FETCH translation | `hql` |
| T08 | attach many-to-many | join-table INSERT | `relationship` |
| T09 | rollback | không có committed row | `rollback` |

**Lệnh chạy:** `mvn test` để chạy automated matrix trên H2; chạy từng command trên SQL Server để thu SQL Server evidence.

**Verification:** Test report ở `target/surefire-reports`; DB result đúng matrix.

**PASS/FAIL:** PASS khi automated tests xanh và T01–T09 đúng. FAIL: ghi rõ test, expected, actual SQL/DB rồi quay lại step tương ứng.

## 6.23. Step 23 — Integration Gate G1–G13

**Mục tiêu:** Chỉ kết thúc lab khi có bằng chứng kỹ thuật và giải thích được mental model.

**Thao tác/code/config/lệnh chạy:** Chạy gate tự động trước:

```powershell
mvn clean test
mvn -q -DskipTests package
java -jar target/sba301-slot28-hibernate-1.0.0-SNAPSHOT.jar
```

Lệnh cuối phải in help mà không đòi kết nối DB. Sau đó chạy các experiment theo bảng:

| Gate | Evidence để nộp | PASS |
|---|---|---|
| G1 Build | Maven test/package log | `BUILD SUCCESS`, tests 0 failures |
| G2 Bootstrap | breakpoint/log | SessionFactory build đúng một lần |
| G3 DB | Step 1 result | `CurrentDatabase=LibraryDB` |
| G4 Mapping | schema query | `STUDENTS`, `BOOKS`, `STUDENTS_BOOKS` đúng |
| G5 Lifecycle | `lifecycle` output | transient/persistent/detached rõ |
| G6 Transaction | commit + `rollback` output | row commit tồn tại, rollback không còn row |
| G7 Dirty checking | SQL log | managed change sinh UPDATE |
| G8 Cache | `first-level-cache` | `a == b`, một SELECT |
| G9 HQL | `hql` | select/filter/parameter đúng |
| G10 Paging | `hql` | ordered page <= 10 |
| G11 Relationship | join-table query | FK pair tồn tại |
| G12 Break/Fix | `break-hql` + `batch` | tái hiện, giải thích, sửa đúng |
| G13 Explain | vấn đáp ngắn | trả lời đúng bên dưới |

**Verification G13:**

- `SessionFactory` là thread-safe, đắt, dùng chung lâu dài; `Session` không thread-safe, ngắn, đại diện persistence context/unit of work.
- `flush` đồng bộ thay đổi trong persistence context xuống SQL nhưng transaction vẫn có thể rollback; `commit` hoàn tất transaction và làm thay đổi bền vững.
- Entity managed được dirty checking; detached entity không được tracking; `merge()` trả về managed copy.
- HQL dùng entity/property; SQL dùng table/column.

**PASS/FAIL:** PASS toàn bài khi G1–G13 đều có evidence. FAIL nếu chỉ “chạy không lỗi” nhưng thiếu DB/SQL/lifecycle evidence; sửa gate thất bại rồi chạy lại đúng gate.

## Troubleshooting nhanh

- `Login failed for user 'sa'`: kiểm tra mixed mode, user/password và biến môi trường trong **đúng terminal** đang chạy Maven.
- `The TCP/IP connection ... failed`: SQL Server service chưa chạy, TCP/IP chưa enable, sai instance/port hoặc firewall.
- `Cannot open database LibraryDB`: chạy Step 1 bằng account có quyền.
- `Student id=... not found`: chạy `lifecycle`, lấy đúng ID mới in ra và truyền `--lab.student-id`.
- `Table ... not found`: để `HIBERNATE_DDL_AUTO=update` cho lab hoặc kiểm tra account có DDL permission.
- Production không nên dùng `hbm2ddl.auto=update`; dùng migration tool như Flyway/Liquibase và secret manager.

## Automated tests

Test dùng H2 in-memory ở chế độ SQL Server để kiểm tra nhanh mapping/lifecycle, không thay thế integration test với SQL Server thật.

```powershell
mvn clean test
Get-Content target/surefire-reports/*.txt
```

Các test bao phủ persist/lifecycle, dirty checking, first-level cache, detached/merge, HQL, many-to-many và rollback.
