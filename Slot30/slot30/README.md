# SBA301 Slot 30 — Chapter 18 Part A: Hibernate CRUD Application, Layers & Configuration

Dự án hiện thực kiến trúc phân lớp chuẩn cho ứng dụng CRUD với Hibernate ORM:
- **Presentation / Client boundary**: `Main.java` (chuẩn bị cho JavaFX Controller ở Slot 31)
- **Service Layer**: `IStudentService`, `StudentService` (business validation, duplicate check, use case orchestration)
- **Repository Layer**: `IStudentRepository`, `StudentRepository` (che giấu kỹ thuật persistence)
- **DAO Layer**: `StudentDAO` (quản lý `Session`, `Transaction`, HQL query, dirty checking & merge)
- **Domain Layer**: `Student`, `Book`
- **Configuration**: `HibernateUtil`, `hibernate.cfg.xml`

## Kiểm thử

Chạy test với H2 in-memory:

```powershell
.\mvnw.cmd test
```

## Chạy demo

```powershell
.\mvnw.cmd compile exec:java -Dexec.mainClass="fu.edu.sba301.slot30.Main"
```
