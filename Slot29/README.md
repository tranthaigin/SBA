# SBA301 - Slot 29

**Chapter 17 Part B: Many-to-Many Association, DAO, Repository, Service Architecture và Transaction Boundary**

Tài liệu tham chiếu chính thức: [SBA301_Slot29_Chapter17_PartB_ManyToMany_DAO_Repository_Service.pdf](../Tài%20Liệu/SBA301_Slot29_Chapter17_PartB_ManyToMany_DAO_Repository_Service.pdf)

---

## 1. Giới thiệu tổng quan

Dự án Slot 29 triển khai chuẩn hóa theo hướng dẫn 26 bước của giáo trình SBA301 Slot 29:
- **Object Graph & Relational Mapping**: Ánh xạ quan hệ Nhiều - Nhiều (`@ManyToMany`) giữa `Student` và `Book` qua bảng liên kết `STUDENTS_BOOKS(student_id, book_id)`.
- **Owning vs Inverse Side**: `Student` là owning side giữ `@JoinTable`, `Book` là inverse side sử dụng `mappedBy = "books"`.
- **Bidirectional Helper Methods**: Đồng bộ bộ nhớ Java 2 chiều qua `student.addBook(book)` và `student.removeBook(book)`.
- **Cascade Rules**: Áp dụng `PERSIST`, `MERGE`; **không** dùng `REMOVE` để bảo vệ shared entity `Book`.
- **Kiến trúc phân tầng (Separation of Concerns)**:
  - `Entity`: Mapping Hibernate và helper methods.
  - `DAO`: Thao tác trực tiếp với Hibernate `Session` / `SessionFactory` và câu lệnh HQL.
  - `Repository`: Interface trừu tượng hóa nghiệp vụ lưu trữ (`IStudentRepository`, `IBookRepository`).
  - `Service`: Quản lý nghiệp vụ (`borrowBook`, `returnBook`) và ranh giới giao dịch nguyên tử (`@Transactional`).
  - `UI/Bootstrap`: Tầng giao diện / `DemoRunner` chỉ tương tác với Service.
- **Association Entity**: Mở rộng thực thể liên kết `Borrowing` khi quan hệ có thêm siêu dữ liệu (`borrowedDate`, `dueDate`, `status`).

---

## 2. Cấu trúc thư mục

- `slot29/`
  - `pom.xml`: Cấu hình Maven (Spring Boot, Hibernate, Spring Data JPA, H2 In-Memory, SQL Server Driver).
  - `mvnw`, `mvnw.cmd`, `.mvn/`: Maven Wrapper đi kèm.
  - `src/main/java/vn/edu/fpt/slot29/`:
    - `domain/`: `Student`, `Book`, `Borrowing`, `BorrowingStatus`
    - `dao/`: `StudentDAO`, `BookDAO`, `BorrowingDAO`, `manual/ManualStudentDAO`
    - `repository/`: `IStudentRepository`, `StudentRepository`, `IBookRepository`, `BookRepository`, `IBorrowingRepository`, `BorrowingRepository`
    - `service/`: `IStudentService`, `StudentService`, `IBookService`, `BookService`, `IBorrowingService`, `BorrowingService`
    - `bootstrap/DemoRunner.java`: Demo toàn bộ luồng use-case khi khởi động ứng dụng
    - `Slot29Application.java`: Main class
  - `src/test/java/vn/edu/fpt/slot29/`:
    - `StudentPersistenceIntegrationTest.java`: Bộ 12 test cases (T01 - T12) kiểm tra tính toàn vẹn quan hệ, cascade, lazy loading, rollback.
    - `BorrowingEntityIntegrationTest.java`: Kiểm thử thực thể liên kết `Borrowing`.
    - `MappingFailureTest.java`: Kiểm tra phát hiện lỗi mapping.
  - `sql/verify-sqlserver.sql`: Kịch bản SQL kiểm tra trực tiếp bảng và khóa ngoại trên Microsoft SQL Server.

---

## 3. Cách chạy và kiểm thử

### A. Chạy toàn bộ kiểm thử tự động (14 Tests T01 - T12)
```powershell
cd "C:\SBA301\bài tập\sba-301\Slot29\slot29"
.\mvnw.cmd test
```

### B. Đóng gói và chạy ứng dụng demo (DemoRunner)
```powershell
cd "C:\SBA301\bài tập\sba-301\Slot29\slot29"
.\mvnw.cmd package -DskipTests
java "-Dfile.encoding=UTF-8" -jar target\slot29-hibernate-1.0.0.jar --spring.profiles.active=demo
```
Ứng dụng sẽ tự động kích hoạt `DemoRunner` hiển thị toàn bộ luồng tạo sách, sinh viên, mượn sách hai chiều, kiểm tra join table và truy vấn HQL ngữ nghĩa.
