# Execution trace - POST Category

Trace request `T04 - POST valid` tại `http://localhost:8082/api/categories` ngày 13/10/2026. [Request/response Postman](../evidence/newman-run.json), [row SQL tại T04](../evidence/api-sql.json), [SQL và handler log](../evidence/sql-trace.txt).

## Code của project

1. `controller/CategoryController.java:create(CategoryRequest request)`: `@PostMapping`, `@Valid`, `@RequestBody`; nhận name Technology News, description Technology and software engineering news, parent null, active true.
2. Constructor đã inject `CategoryService`; Controller gọi `service.create(request)`.
3. `service/CategoryServiceImpl.java:create(CategoryRequest request)`: `@Transactional`; trim tên và gọi `repository.existsByCategoryNameIgnoreCase(...)` để chặn duplicate.
4. Tạo `new Category()` chưa có ID; `apply(...)` gán bốn field theo contract.
5. `repository/CategoryRepository.java` kế thừa `JpaRepository<Category, Short>`; Service gọi `repository.save(category)`.
6. `toResponse(...)` tạo `CategoryResponse` với generated ID và bốn field. Controller trả 201 cùng DTO.

```text
CategoryController.create(request)
 -> CategoryServiceImpl.create(request)
 -> CategoryRepository.existsByCategoryNameIgnoreCase(name)
 -> new Category() -> apply(category, request)
 -> CategoryRepository.save(category)
 -> toResponse(savedCategory)
 -> ResponseEntity.status(CREATED).body(...)
```

## Phần framework và evidence quan sát được

DispatcherServlet/HandlerMapping, Jackson binding và Bean Validation là cơ chế Spring MVC. Repository proxy/SimpleJpaRepository, EntityManager, Hibernate, JDBC và transaction interceptor là cơ chế framework, không phải method do project tự viết.

Log thực tế ghi mapping tới `CategoryController#create`, query kiểm tra tên, SQL INSERT Category và HTTP 201. Postman response chứa ID tự sinh; snapshot SQL ngay sau response chứa đúng row và bốn field. Cuối collection, row test đã được xóa và seed còn nguyên. Không suy ra commit chỉ từ việc `save()` return: SQL được đọc từ connection độc lập sau khi HTTP response hoàn tất.

`@Transactional` ở Service bao trùm kiểm tra rule và persistence. Với UPDATE, `update(short id, CategoryRequest request)` dùng `findEntity(id)` trước; kiểm tra tên trùng, mutate entity đang được quản lý trong transaction, `save(...)`, trả DTO giữ ID. T07 chứng minh UPDATE đúng field; T08 không tạo row khi ID thiếu; PUT duplicate không sửa row.

## Breakpoint plan để người học kiểm chứng trong IDE

- `CategoryController.create()`: xem request DTO và dependency Service.
- `CategoryServiceImpl.create()`: xem kết quả kiểm tra duplicate.
- Dòng `new Category()` / trước `repository.save()`: ID còn null và bốn field đã gán.
- Sau `repository.save()`: xem entity trả về có ID.
- `toResponse()`: đối chiếu response với SQL sau request.

Lượt này dùng debug note + source + handler/SQL log + Postman response + SQL row, là lựa chọn evidence được trang 26 cho phép. Chưa chụp breakpoint trong IntelliJ; không khẳng định đã quan sát trực tiếp persistence context/flush bằng debugger.
