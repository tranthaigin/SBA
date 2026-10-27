# SBA301 - Slot 30: Chapter 18 Part A - Hibernate CRUD Application Layers & Configuration

Dự án triển khai bài tập thực hành theo tài liệu `SBA301_Slot30_Chapter18_PartA_Hibernate_CRUD_Application_Layers_Configuration.pdf`.

## 1. Kiến trúc phân lớp chuẩn Hibernate (5 Layers)

Hệ thống được thiết kế theo đúng mô hình 5 tầng độc lập, tuân thủ nguyên lý tách biệt trách nhiệm (Separation of Concerns):

1. **Presentation Layer (`fu.edu.sba301.slot30.Main`)**:
   - Giao tiếp với người dùng / client (hoặc JavaFX FXML Controller ở Slot 31).
   - Chỉ gọi trực tiếp `IStudentService`, tuyệt đối không tương tác với DAO hay `Session`.
2. **Service Layer (`IStudentService`, `StudentService`)**:
   - Chứa nghiệp vụ kinh doanh (Business Logic) và validation (kiểm tra email, id, tên không rỗng, v.v.).
   - Gọi `IStudentRepository`.
3. **Repository Layer (`IStudentRepository`, `StudentRepository`)**:
   - Abstraction cho tầng lưu trữ dữ liệu, đóng gói các thao tác CRUD và truy vấn nghiệp vụ.
   - Gọi `StudentDAO`.
4. **Data Access Object (DAO) Layer (`StudentDAO`)**:
   - Tương tác trực tiếp với Hibernate `SessionFactory` / `Session`.
   - Quản lý mở session, bắt đầu transaction, commit / rollback khi có ngoại lệ, và đóng session an toàn qua `try-with-resources` / `finally`.
5. **Domain Layer (`Student`, `Book`)**:
   - Các JPA / Hibernate Entity ánh xạ bảng cơ sở dữ liệu.
   - Quan hệ `@ManyToMany` giữa `Student` và `Book` qua bảng liên kết `STUDENT_BOOK`.

## 2. Cấu trúc thư mục

```
Slot30/
├── README.md
└── slot30/
    ├── pom.xml
    ├── mvnw.cmd
    └── src/
        ├── main/
        │   ├── java/fu/edu/sba301/slot30/
        │   │   ├── Main.java
        │   │   ├── dao/
        │   │   │   └── StudentDAO.java
        │   │   ├── domain/
        │   │   │   ├── Book.java
        │   │   │   └── Student.java
        │   │   ├── repository/
        │   │   │   ├── IStudentRepository.java
        │   │   │   └── StudentRepository.java
        │   │   ├── service/
        │   │   │   ├── IStudentService.java
        │   │   │   └── StudentService.java
        │   │   └── util/
        │   │       └── HibernateUtil.java
        │   └── resources/
        │       ├── application.properties
        │       └── hibernate.cfg.xml
        └── test/
            └── java/fu/edu/sba301/slot30/
                └── HibernateCrudIntegrationTest.java
```

## 3. Kiểm thử & Xác thực tự động (Integration Tests)

Bộ kiểm thử `HibernateCrudIntegrationTest` bao gồm 9 kịch bản kiểm thử toàn diện:
- `testSaveStudentAndFindById`: Lưu và đọc lại sinh viên qua đầy đủ các layer.
- `testUpdateStudent`: Cập nhật thông tin sinh viên và kiểm tra tính toàn vẹn dữ liệu.
- `testDeleteStudent`: Xóa sinh viên và kiểm tra kết quả null khi tìm lại.
- `testFindAllStudents`: Tìm kiếm danh sách toàn bộ sinh viên.
- `testSearchStudentsByName`: Tìm kiếm theo tên sinh viên (hỗ trợ phân biệt hoặc không phân biệt hoa thường).
- `testStudentBookManyToManyRelationship`: Thiết lập và kiểm tra quan hệ nhiều-nhiều giữa `Student` và `Book`.
- `testValidationRejectsBlankName`: Tầng Service chặn việc tạo sinh viên nếu tên bị để trống.
- `testValidationRejectsInvalidEmail`: Tầng Service kiểm tra định dạng email hợp lệ.
- `testTransactionRollbackOnError`: Kiểm tra cơ chế rollback giao dịch của DAO khi có lỗi xảy ra.

### Chạy kiểm thử:
```powershell
cd "Slot30\slot30"
.\mvnw.cmd test
```
Kết quả: **9/9 tests passed (100% SUCCESS)**.
