# Knowledge Base — Many-to-Many, DAO, Repository và Service

Tài liệu này giải thích chuyên sâu hệ thống kiến thức của **SBA301 Slot 29 — Chapter 17 Part B**. Các ví dụ liên hệ trực tiếp với project `Student–Book` hiện tại, nhưng nguyên lý có thể áp dụng cho nhiều domain khác như User–Role, Product–Category, Doctor–Patient hoặc Course–Student.

## Cách sử dụng tài liệu

Mỗi chủ đề gồm bốn góc nhìn:

1. **Khái niệm:** thuật ngữ có nghĩa gì.
2. **Cơ chế:** Hibernate/JPA và database xử lý như thế nào.
3. **Áp dụng:** vị trí tương ứng trong project.
4. **Rủi ro:** lỗi thiết kế hoặc cách hiểu sai thường gặp.

Luồng kiến thức trung tâm:

```text
Object graph Student ↔ Book
              ↓ mapping
Relational tables + STUDENTS_BOOKS
              ↓ persistence operations
Session / Persistence Context / Unit of Work
              ↓ transaction boundary
DAO → Repository → Service → UI
```

---

## 2.1. Many-to-Many relational model

### Khái niệm

Quan hệ many-to-many tồn tại khi:

- Một instance của A có thể liên kết với nhiều instance của B.
- Một instance của B cũng có thể liên kết với nhiều instance của A.

Trong bài học:

```text
Một Student ── nhiều Book
Một Book    ── nhiều Student
```

Ví dụ dữ liệu logic:

```text
Student An  → Hibernate Basics, ORM Design
Student Bình → Hibernate Basics
```

### Vì sao không đặt một FK trực tiếp?

Nếu đặt `book_id` trong `STUDENTS`, mỗi Student chỉ chứa được một Book. Nếu đặt `student_id` trong `BOOKS`, mỗi Book chỉ liên kết được một Student. Cả hai đều biến quan hệ thành many-to-one hoặc one-to-many.

Relational database biểu diễn many-to-many bằng bảng trung gian:

```text
STUDENTS                 STUDENTS_BOOKS                BOOKS
---------                --------------                -----
id PK       ←──────────  student_id FK
                         book_id FK   ───────────────→ id PK
```

Ví dụ rows:

| STUDENTS_BOOKS.student_id | STUDENTS_BOOKS.book_id |
|---:|---:|
| 1 | 10 |
| 1 | 11 |
| 2 | 10 |

Ba rows này biểu diễn ba cạnh trong object graph.

### Áp dụng trong project

- Entity: `domain/Student.java`, `domain/Book.java`.
- Bảng nối: `STUDENTS_BOOKS`.
- Kiểm chứng: test T01, T02 và `sql/verify-sqlserver.sql`.

### Điều cần phân biệt

Cardinality của database không tự định nghĩa business rule. Database cho phép một Book liên kết nhiều Student, nhưng Service vẫn có thể áp dụng thêm quy tắc như giới hạn số sách hoặc không cho mượn trùng.

---

## 2.2. Join table

### Vai trò

Join table lưu identity của hai entity tham gia association:

```sql
STUDENTS_BOOKS(
    student_id BIGINT NOT NULL,
    book_id    BIGINT NOT NULL
)
```

Mỗi column là một foreign key:

```text
student_id → STUDENTS.id
book_id    → BOOKS.id
```

Foreign key bảo đảm không thể tạo association tới Student hoặc Book không tồn tại.

### Composite primary key và unique pair

Một cặp Student–Book chỉ nên xuất hiện một lần. Có hai cách phổ biến:

```sql
PRIMARY KEY (student_id, book_id)
```

hoặc:

```sql
UNIQUE (student_id, book_id)
```

Project khai báo unique constraint `UK_STUDENT_BOOK`. Hibernate có thể tạo thêm cấu trúc khóa tùy dialect và chiến lược sinh schema.

### Vì sao vẫn cần constraint khi Java dùng Set?

`Set` chỉ bảo vệ trong một object graph của một JVM. Database có thể nhận dữ liệu từ:

- Nhiều request đồng thời.
- Nhiều application instances.
- Script SQL.
- ETL/import tool.
- Một service khác.

Vì vậy cần hai lớp bảo vệ:

```text
Set/business validation ở application
                 +
UNIQUE constraint ở database
```

### Join table thuần không phải entity

Trong raw `@ManyToMany`, join row không có object identity riêng trong domain. Application thao tác association thông qua collection `Student.books` và `Book.students`, không thông qua một `StudentBook` object.

Khi join row có dữ liệu nghiệp vụ riêng, nó phải được nâng cấp thành association entity; xem mục 2.9.

---

## 2.3. Owning side

### Định nghĩa

Owning side là phía chịu trách nhiệm mô tả cách ghi association xuống database. Trong mapping bidirectional many-to-many, phía khai báo `@JoinTable` là owner.

Trong project:

```java
@ManyToMany(fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(
    name = "STUDENTS_BOOKS",
    joinColumns = @JoinColumn(name = "student_id"),
    inverseJoinColumns = @JoinColumn(name = "book_id")
)
private Set<Book> books = new HashSet<>();
```

`Student.books` là owning side.

### Owner quyết định association mutation

Khi managed collection phía owner thay đổi, Hibernate so sánh trạng thái collection và lập lịch SQL:

```java
student.getBooks().add(book);
```

Kết quả dự kiến khi flush:

```sql
INSERT INTO STUDENTS_BOOKS(student_id, book_id) VALUES (?, ?);
```

Khi remove:

```java
student.getBooks().remove(book);
```

Kết quả dự kiến:

```sql
DELETE FROM STUDENTS_BOOKS
WHERE student_id = ? AND book_id = ?;
```

### Owning side không đồng nghĩa business owner

Đây là điểm rất dễ nhầm. Mapping owner chỉ là khái niệm kỹ thuật quyết định phía nào ghi foreign key/join table. Nó không có nghĩa Student sở hữu vòng đời của Book.

Book vẫn là shared entity độc lập. Vì vậy không thể suy luận “Student là owner nên xóa Student phải xóa Book”.

---

## 2.4. Inverse side

### Định nghĩa

Inverse side là phía phản chiếu association đã được mapping ở owner:

```java
@ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
private Set<Student> students = new HashSet<>();
```

`Book.students` là inverse side.

### Ý nghĩa của `mappedBy`

```java
mappedBy = "books"
```

Chuỗi `books` là tên Java property trong `Student`, không phải:

- Tên bảng `BOOKS`.
- Tên join table `STUDENTS_BOOKS`.
- Tên column `book_id`.
- Tên class `Book`.

Nếu viết sai, ví dụ `mappedBy = "book"`, Hibernate không tìm thấy property tương ứng và metadata bootstrap thất bại. Test T08 tạo mapping sai cô lập để chứng minh điều này.

### Vì sao inverse-only mutation không đủ?

```java
book.getStudents().add(student);
```

Object graph trong memory thay đổi, nhưng owning collection `student.books` không thay đổi. Hibernate không xem inverse collection là nguồn quyết định cập nhật join table.

Test T09 kiểm tra sau commit vẫn không có join row.

---

## 2.5. Bidirectional helper methods

### Vấn đề nhất quán trong memory

Nếu chỉ cập nhật owner:

```java
student.getBooks().add(book);
```

thì:

```text
student.books chứa book
book.students không chứa student
```

Database có thể được cập nhật đúng sau flush, nhưng object graph đang dùng trong cùng transaction lại không nhất quán.

### Helper đúng

Project đóng gói thao tác bằng:

```java
public void addBook(Book book) {
    Objects.requireNonNull(book);
    if (books.add(book)) {
        book.addStudentInternal(this);
    }
}

public void removeBook(Book book) {
    Objects.requireNonNull(book);
    if (books.remove(book)) {
        book.removeStudentInternal(this);
    }
}
```

### Vì sao method phía Book là internal?

Nếu cả `Student.addBook` và `Book.addStudent` công khai rồi gọi lẫn nhau, code có thể đệ quy vô hạn. Project dùng một public aggregate operation ở Student và internal methods ở Book.

### Trách nhiệm của helper

Helper chịu trách nhiệm:

- Đồng bộ hai collection.
- Chặn `null`.
- Tránh thao tác lặp nhờ `Set.add()` trả `false`.
- Bảo vệ invariant cục bộ của object graph.

Helper không thay thế Service. Quy tắc nghiệp vụ cần repository lookup, transaction hoặc permission vẫn thuộc Service.

---

## 2.6. Set vs List

### Set

`Set` mô tả một tập phần tử không trùng theo `equals/hashCode`:

```java
private Set<Book> books = new HashSet<>();
```

Phù hợp khi domain nói rằng một Student chỉ có một association tới cùng một Book.

Ưu điểm:

- Diễn đạt uniqueness ở object model.
- `add()` cho biết phần tử có thực sự được thêm hay không.
- Helper dễ tránh cập nhật inverse side lặp.

### List

`List` cho phép phần tử trùng và có thể mang ý nghĩa thứ tự. Nếu cần lưu thứ tự, JPA có thể dùng `@OrderColumn`, nhưng database phải có thêm column vị trí.

`List` chỉ nên dùng khi:

- Duplicate có ý nghĩa; hoặc
- Thứ tự là một phần của domain.

Không nên chọn List chỉ vì UI muốn hiển thị có thứ tự; query `ORDER BY` hoặc projection thường phù hợp hơn.

### `equals/hashCode` của entity

Hash-based collection yêu cầu hash code ổn định trong thời gian object nằm trong Set. Generated ID chuyển từ `null` sang một giá trị sau persist, nên dùng ID trực tiếp trong `hashCode` có thể làm object bị “thất lạc” trong bucket.

Project dùng natural key:

- `Student.email`.
- `Book.isbn`.

Các giá trị này phải duy nhất và ổn định. Nếu nghiệp vụ cho phép thay đổi chúng, cần chọn equality strategy khác.

### Set không thay database constraint

Set xử lý identity trong memory; unique constraint xử lý toàn hệ thống và concurrency. Cần cả hai.

---

## 2.7. Cascade

### Cascade truyền operation, không truyền ownership

Cascade nói với JPA rằng khi một operation áp dụng lên entity nguồn, operation tương ứng có thể truyền sang entity liên quan.

| Cascade | Tác động khái quát |
|---|---|
| `PERSIST` | Persist entity mới liên quan |
| `MERGE` | Merge trạng thái entity liên quan |
| `REMOVE` | Remove entity liên quan |
| `REFRESH` | Refresh entity liên quan |
| `DETACH` | Detach entity liên quan |
| `ALL` | Tất cả operation trên |

### PERSIST/MERGE

Project dùng:

```java
cascade = {CascadeType.PERSIST, CascadeType.MERGE}
```

Điều này cho phép demo persist graph có Student và Book mới. Tuy nhiên cascade choice phải dựa trên lifecycle semantics thật của domain, không phải để “sửa lỗi transient object” một cách máy móc.

### Vì sao REMOVE nguy hiểm?

```text
Student A ─┐
           ├─ Book X
Student B ─┘
```

Nếu xóa Student A và cascade REMOVE tới Book X:

- Book dùng chung có thể bị xóa sai.
- Student B mất dữ liệu liên quan.
- Hoặc transaction thất bại vì foreign key còn tham chiếu Book X.

Xóa Student chỉ nên xóa join rows của Student đó và row Student; Books phải còn.

### Cascade khác `ON DELETE CASCADE`

- JPA cascade là propagation của entity operation trong ORM.
- Database cascade là hành vi constraint khi row cha bị xóa.

Hai cơ chế ở hai tầng khác nhau và không nên bị đánh đồng.

---

## 2.8. Fetching

### LAZY collection

Many-to-many collection thường nên để LAZY:

```java
@ManyToMany(fetch = FetchType.LAZY)
```

Hibernate ban đầu có thể gắn một persistent collection/proxy thay vì load ngay toàn bộ Books.

### Khi nào query thực sự chạy?

```java
Student student = session.get(Student.class, id); // load Student
student.getBooks().size();                        // có thể query Books tại đây
```

Lazy loading cần persistence context còn hoạt động.

### `LazyInitializationException`

Nếu DAO đóng Session rồi caller mới truy cập collection chưa khởi tạo:

```java
Student detached = manualDao.findById(id);
detached.getBooks().size();
```

Hibernate không còn Session để chạy query và ném `LazyInitializationException`. T10 minh họa chính xác trường hợp này.

### Fetch plan theo use case

DAO cung cấp query:

```java
select distinct s
from Student s
left join fetch s.books
where s.id = :id
```

Collection được khởi tạo trong transaction. Sau khi Service trả kết quả, caller có thể đọc Books mà không cần Session mở.

### Không chữa bằng global EAGER

EAGER toàn cục có thể tạo graph lớn, N+1 query hoặc SQL join phức tạp ở những use case không cần Books. Fetch plan phải được thiết kế theo từng operation.

---

## 2.9. Association entity

### Khi raw many-to-many không còn đủ

Raw join table chỉ biểu diễn “có liên kết”. Nếu association có dữ liệu riêng:

- `borrowedDate`.
- `dueDate`.
- `returnedAt`.
- `status`.
- `quantity`.
- `createdBy`.

thì chính association đã trở thành một domain object.

### Mô hình mới

```text
Student 1 ── * Borrowing * ── 1 Book
```

`Borrowing` có identity và lifecycle riêng:

```java
@ManyToOne(optional = false)
private Student student;

@ManyToOne(optional = false)
private Book book;

private LocalDate borrowedDate;
private LocalDate dueDate;

@Enumerated(EnumType.STRING)
private BorrowingStatus status;
```

### Lợi ích

- Lưu nhiều lần mượn theo lịch sử.
- Phân biệt active và returned borrowing.
- Có thể thêm business methods như `markReturned()`.
- Query và constraint phản ánh đúng nghiệp vụ.
- Không phải nhồi thuộc tính vào một join table mà ORM không expose như entity.

### Lưu ý thiết kế

Project giữ raw many-to-many và `Borrowing` song song để so sánh học thuật. Production thường chọn một source of truth. Nếu Borrowing là nghiệp vụ thật, raw `Student.books` có thể trở thành dữ liệu suy ra thay vì mapping độc lập.

---

## 2.10. DAO responsibility

### DAO biết gì?

DAO là lớp gần persistence technology. Nó có thể biết:

- Hibernate `Session`.
- HQL/JPQL.
- `persist`, `merge`, `remove`.
- Fetch join.
- Pagination và query parameters.
- Cách chuyển result thấp cấp thành `Optional` hoặc collection.

Ví dụ:

```java
public Optional<Student> findByIdWithBooks(Long id) {
    return session().createQuery("""
        select distinct s from Student s
        left join fetch s.books
        where s.id = :id
        """, Student.class)
        .setParameter("id", id)
        .uniqueResultOptional();
}
```

### DAO không nên biết gì?

DAO không nên quyết định:

- Student có được mượn Book này không.
- Một người dùng có quyền thực hiện use case không.
- UI phải hiện thông báo gì.
- Một workflow gồm bao nhiêu bước nghiệp vụ.

### Transaction trong DAO

`ManualStudentDAO` minh họa kiểu mỗi write method tự mở Session/Transaction. Cách này có giá trị học Hibernate cơ bản nhưng không phù hợp cho use case nhiều DAO calls.

DAO chính trong project sử dụng Session gắn với Spring transaction do Service quản lý.

---

## 2.11. Repository responsibility

### Repository là domain-facing persistence contract

Repository trả lời câu hỏi “domain cần truy xuất gì?” thay vì “Hibernate phải chạy HQL nào?”.

Ví dụ contract:

```java
Optional<Student> findByIdWithBooks(Long id);
List<Student> findStudentsByBook(Long bookId);
```

Tên operation có semantic rõ ràng. Service không biết query join nào được sử dụng.

### Collection-like abstraction

Repository thường tạo cảm giác giống một collection bền vững:

```text
save(entity)
findById(id)
findAll()
delete(id)
```

Nhưng Repository không phải `List` trong RAM. Mỗi operation có thể phát sinh network I/O, SQL, locking hoặc exception.

### Che giấu implementation detail

Service phụ thuộc `IStudentRepository`, không phụ thuộc trực tiếp vào:

- Hibernate Session.
- EntityManager.
- HQL.
- SQL Server driver.

Adapter `StudentRepository` hiện forwarding tới `StudentDAO`. Trong project nhỏ nó trông mỏng, nhưng boundary này minh họa cách thay persistence implementation mà không đổi Service.

---

## 2.12. Service responsibility

### Service tổ chức use case

`StudentService.borrowBook` thực hiện một chuỗi có ý nghĩa nghiệp vụ:

```text
1. Load Student cùng Books.
2. Load Book.
3. Kiểm tra Student/Book tồn tại.
4. Kiểm tra association chưa tồn tại.
5. Cập nhật object graph bằng helper.
6. Commit hoặc rollback toàn use case.
```

### Service chứa business validation

Ví dụ:

```java
if (duplicate) {
    throw new IllegalStateException("Book already associated");
}
```

Đây không phải data-access concern; nó diễn tả business rule.

### Service định nghĩa transaction boundary

```java
@Transactional
public void borrowBook(Long studentId, Long bookId) { ... }
```

Mọi repository call trong method cùng tham gia một transaction/persistence context.

### Service không nên chứa

- SQL hoặc HQL string.
- `SessionFactory.openSession()`.
- Logic render UI.
- Chi tiết HTTP request/response.

---

## 2.13. Layer dependency direction

Dependency direction chuẩn:

```text
UI/Main
   ↓
Service
   ↓
Repository
   ↓
DAO
   ↓
ORM/JDBC/Database
```

### Ý nghĩa

- UI gọi use case thay vì điều khiển transaction.
- Service gọi persistence contract thay vì viết HQL.
- Repository/DAO không gọi ngược UI.
- Entity không phụ thuộc Controller hoặc database connection.

### Vi phạm thường gặp

```java
// Controller/UI không nên làm như sau
Session session = sessionFactory.openSession();
session.persist(student);
```

Hậu quả:

- Business validation bị phân tán.
- Transaction boundary nằm ở UI.
- Khó tái sử dụng use case cho CLI, REST, JavaFX hoặc test.
- UI bị khóa vào Hibernate.

### Dependency inversion

Service phụ thuộc repository interface; implementation phụ thuộc DAO. Constructor injection giúp Spring nối graph tại runtime.

---

## 2.14. Transaction boundary

### Transaction phải bao quanh use case atomic

Một use case có nhiều persistence steps nhưng chỉ có một kết quả nghiệp vụ: thành công toàn bộ hoặc thất bại toàn bộ.

Ví dụ `borrowBooksAtomically(studentId, [bookA, bookB])`:

```text
Add association Book A
Book B không tồn tại → exception
Rollback association Book A
```

T12 chứng minh không có partial join row.

### Transaction bị chia vụn

Nếu mỗi DAO method tự commit:

```text
DAO find Student → tx 1 kết thúc
DAO find Book    → tx 2 kết thúc
DAO update       → tx 3 kết thúc
```

Hậu quả:

- Entities bị detached giữa các bước.
- Lazy loading có thể thất bại.
- Một số bước có thể commit trước khi bước sau lỗi.
- Merge graph phức tạp hơn dirty checking trên managed entity.

### Transaction không nên quá rộng

Transaction không nên bao quanh thời gian người dùng nhập liệu, gọi API chậm hoặc xử lý không liên quan. Transaction dài giữ connection/lock lâu và tăng contention.

Ranh giới phù hợp thường là một synchronous application use case ngắn.

---

## 2.15. Unit of Work

### Khái niệm

Unit of Work theo dõi các object đã được đọc/thay đổi trong một đơn vị công việc, sau đó đồng bộ thay đổi xuống database như một nhóm.

Hibernate Session/persistence context đã thực hiện pattern này:

- Identity map.
- Theo dõi managed entities.
- Dirty checking.
- Lập lịch insert/update/delete.
- Flush trước commit.

### Ví dụ

```java
Student student = students.findByIdWithBooks(id).orElseThrow();
Book book = books.findById(bookId).orElseThrow();
student.addBook(book);
```

Không cần gọi SQL trực tiếp. Unit of Work nhận thấy owning collection đã thay đổi và ghi join row khi flush.

### Tôn trọng lifecycle

DAO/Repository không nên mở một Session cho từng dòng thao tác. Làm vậy phá vỡ identity map và unit-of-work boundary.

Trong Spring Boot:

```text
@Transactional Service method
        ↕
Spring-managed persistence context
        ↕
nhiều DAO/Repository operations
```

---

## 2.16. HQL in DAO

### HQL thao tác trên entity model

HQL dùng entity/class property thay vì trực tiếp dùng table/column:

```java
select distinct s
from Student s join s.books b
where b.id = :bookId
order by s.id
```

Ở đây:

- `Student` là entity name.
- `s.books` là Java association property.
- `b.id` là entity property.

Hibernate chuyển HQL thành SQL phù hợp dialect.

### Vì sao HQL thuộc DAO?

HQL phụ thuộc ORM mapping và fetch strategy. Nếu để HQL trong Service:

- Service bị khóa vào Hibernate.
- Business code trộn với data access.
- Query khó tái sử dụng/kiểm thử độc lập.

Service chỉ gọi semantic operation:

```java
students.findStudentsByBook(bookId);
```

### Parameter binding

Luôn bind parameter:

```java
.setParameter("bookId", bookId)
```

Không nối input thành query string. Parameter binding hỗ trợ type handling và ngăn injection ở query layer.

---

## 2.17. Exception translation awareness

### Vấn đề framework exception leakage

DAO có thể phát sinh exception cụ thể của Hibernate/JPA/JDBC:

- Constraint violation.
- Lock timeout.
- Optimistic lock failure.
- Query syntax error.
- Connection failure.

Nếu exception này đi thẳng tới UI:

- UI bị phụ thuộc framework.
- Thông báo khó hiểu và có thể lộ chi tiết hệ thống.
- Đổi persistence technology làm ảnh hưởng upper layers.

### Translation theo tầng

Luồng hợp lý:

```text
SQLException / HibernateException
             ↓
Spring DataAccessException hoặc persistence exception của ứng dụng
             ↓
Domain/use-case exception khi có ý nghĩa nghiệp vụ
             ↓
UI-safe error response/message
```

`@Repository` của Spring hỗ trợ exception translation sang hierarchy `DataAccessException` cho các technology được tích hợp.

### Không dịch mọi lỗi thành cùng một message

Cần phân biệt:

- “Student không tồn tại” — business lookup outcome.
- “Association đã tồn tại” — business/constraint conflict.
- “Database không kết nối được” — infrastructure failure.
- “HQL sai” — programming defect.

Không nên bắt `Exception` rồi trả “Not found” cho mọi trường hợp.

---

## 2.18. Repository interface

### Khi interface có giá trị

Interface hữu ích khi:

- Service cần phụ thuộc abstraction.
- Có nhiều implementation.
- Cần fake/stub repository cho unit test.
- Persistence adapter có khả năng thay đổi.
- Contract được chia sẻ giữa module.

Ví dụ:

```java
public interface IStudentRepository {
    Optional<Student> findByIdWithBooks(Long id);
    List<Student> findStudentsByBook(Long bookId);
}
```

### Khi interface trở thành ceremony

Nếu project rất nhỏ, chỉ có một implementation, không có nhu cầu substitution và interface sao chép nguyên public methods của class, nó có thể chỉ tăng số file.

Không nên áp dụng quy tắc máy móc “mọi class đều phải có interface”. Câu hỏi đúng là:

> Interface này đang bảo vệ abstraction boundary hoặc hỗ trợ substitution nào?

### Trong project học tập

Repository interface được giữ để minh họa contract, constructor injection, dependency inversion và testing boundary. Đây là mục tiêu sư phạm có chủ ý.

---

## 2.19. Service interface

### Lợi ích

Service interface có thể hữu ích cho:

- Xác định application use-case contract.
- Tách caller khỏi implementation.
- Có nhiều implementation theo môi trường/tenant.
- Proxy/decorator hoặc remote facade.
- Mock service ở UI/controller test.

Ví dụ:

```java
public interface IStudentService {
    void borrowBook(Long studentId, Long bookId);
    void returnBook(Long studentId, Long bookId);
}
```

### Trade-off

Nếu interface và implementation luôn thay đổi cùng nhau, chỉ có một implementation và không có consumer boundary rõ ràng, interface có thể không tạo thêm giá trị trong production project nhỏ.

### Nguyên tắc

- Thiết kế interface theo use case, không sao chép máy móc CRUD.
- Đặt tên method theo ngôn ngữ domain.
- Không đưa `Session`, `EntityManager`, HQL hoặc framework type vào contract.
- Đánh giá abstraction value thay vì đếm số layer/file.

Trong bài học, Service interface giúp UI/Main chỉ biết use-case contract.

---

## 2.20. Delete semantics

Từ “delete” có thể chỉ nhiều hành động khác nhau. Phải xác định rõ object nào bị xóa.

### Xóa association

```java
student.removeBook(book);
```

Ý nghĩa relational:

```sql
DELETE FROM STUDENTS_BOOKS
WHERE student_id = ? AND book_id = ?;
```

Rows trong `STUDENTS` và `BOOKS` vẫn tồn tại.

### Xóa Student

Kết quả mong muốn:

```text
Xóa join rows của Student
Xóa STUDENTS row
Giữ BOOKS rows
```

T06 kiểm chứng behavior này với một Book được hai Student chia sẻ.

### Xóa Book

Nếu nghiệp vụ cho phép xóa Book, trước tiên cần quyết định:

- Xóa/đóng các association nào?
- Có borrowing lịch sử không?
- Nên hard delete hay đánh dấu inactive/archived?
- Foreign key có chặn xóa không?

Với dữ liệu lịch sử, soft delete hoặc status thường phù hợp hơn hard delete.

### Orphan removal

`orphanRemoval` thường áp dụng cho quan hệ parent-child one-to-many, không phải giải pháp chung để xóa shared many-to-many entity.

---

## 2.21. DAO vs Repository vs Service

### Bảng so sánh

| Layer | Biết gì? | Không nên biết gì? | Ví dụ |
|---|---|---|---|
| DAO | Session, HQL, Hibernate/JPA API, fetch query | UI workflow, thông báo hiển thị | `findByIdWithBooks`, `save`, `delete` |
| Repository | Domain persistence contract, entity/query intent | HQL, Session, JDBC details | `findStudentsByBook` |
| Service | Use case, business rule, transaction boundary | SQL, HQL, Session details | `borrowBook(studentId, bookId)` |
| UI/Main | Input/output và service contract | Database connection, persistence context | Gọi `service.borrowBook(...)` |

### Cùng một use case qua bốn layer

```text
UI
  nhận studentId, bookId
  ↓
StudentService.borrowBook
  kiểm tra use case và quản lý transaction
  ↓
IStudentRepository / IBookRepository
  diễn đạt nhu cầu truy xuất domain
  ↓
StudentRepository / BookRepository
  chuyển contract sang DAO
  ↓
StudentDAO / BookDAO
  dùng Session và HQL
  ↓
Hibernate
  dirty checking owning collection
  ↓
SQL Server
  INSERT STUDENTS_BOOKS
```

### Dấu hiệu layer đang sai trách nhiệm

- Controller import `Session`.
- Service chứa chuỗi HQL.
- DAO kiểm tra “mỗi Student chỉ được mượn tối đa 5 Book”.
- Repository trả `Query` để Service tự chạy.
- Entity tự mở database connection.
- Mỗi DAO method commit độc lập dù use case cần atomicity.

### Nguyên tắc kết luận

Không phải số lượng layer càng nhiều càng tốt. Kiến trúc tốt là kiến trúc mà:

- Mỗi quyết định chỉ có một nơi chịu trách nhiệm rõ ràng.
- Dependency đi theo một hướng.
- Business rule có thể test mà không cần UI.
- Persistence implementation có thể thay đổi với ảnh hưởng giới hạn.
- Transaction bao quanh đúng use case.
- Mapping và database constraints cùng bảo vệ invariant.

---

## Bản đồ kiểm chứng trong project

| Kiến thức | Evidence |
|---|---|
| Many-to-many và join table | T01, T02 |
| Set/equality/duplicate | T03 |
| Fetch plan | T04 |
| Delete association | T05 |
| Shared entity safety | T06 |
| HQL semantic query | T07 |
| Sai `mappedBy` | T08 |
| Inverse-only mutation | T09 |
| Lazy ngoài Session | T10 |
| Service business rule | T11 |
| Transaction atomicity | T12 |
| Association entity | `BorrowingEntityIntegrationTest` |
| SQL Server constraints | `sql/verify-sqlserver.sql` |
| Manual vs managed transaction | `ManualStudentDAO` và `StudentService` |

## Câu hỏi ôn tập tổng hợp

1. Vì sao relational database cần join table cho many-to-many?
2. Unique constraint giải quyết vấn đề gì mà Set không thể giải quyết hoàn toàn?
3. Owning side có phải entity sở hữu vòng đời entity còn lại không?
4. `mappedBy = "books"` trỏ tới table, column hay Java property?
5. Tại sao phải cập nhật cả hai phía của bidirectional association?
6. Generated ID gây khó khăn gì cho `equals/hashCode` trong HashSet?
7. Cascade REMOVE khác xóa join row như thế nào?
8. Vì sao LAZY hợp lý nhưng có thể gây `LazyInitializationException`?
9. Khi nào `STUDENTS_BOOKS` phải được thay bằng `Borrowing` entity?
10. HQL nên nằm ở DAO hay Service? Vì sao?
11. Repository forwarding 1:1 có giá trị gì trong bài học, và khi nào nó là dư thừa?
12. Vì sao transaction boundary nên theo use case thay vì theo từng DAO method?
13. Hibernate Session thực hiện Unit of Work bằng những cơ chế nào?
14. Vì sao exception của Hibernate không nên đi thẳng tới UI?
15. Xóa association, xóa Student và xóa Book khác nhau ở SQL như thế nào?

Sinh viên nên trả lời từng câu bằng cả giải thích lý thuyết lẫn evidence từ test hoặc SQL trong project.
