# Human verification - câu trả lời ôn tập

Đây là tài liệu ôn tập; người học cần tự giải thích và thực hiện lại các thao tác trước khi nộp.

1. **JPA** là specification ánh xạ object/relational và persistence API; không tự là database driver.
2. **Hibernate** là JPA provider thực thi mapping, quản lý entity và sinh SQL.
3. **Spring Data JPA** tạo repository proxy/implementation và derived queries trên EntityManager/JPA.
4. `mappedBy="orchidCategory"` trỏ tới field Java ở Orchid đang sở hữu quan hệ.
5. FK `category_id` ở `orchids`: nhiều Orchid trỏ tới một Category.
6. `mappedBy="category_id"` sai vì dùng tên cột thay cho field Java.
7. Repository ID dùng `Long` vì field `@Id` trong model là Long.
8. Service resolve Category từ DB để xác minh tồn tại và gán managed relationship; không tin category name/object tùy ý từ client.
9. Service không trả ResponseEntity vì business layer không phụ thuộc HTTP.
10. Controller bind request/response; Repository và SQL thuộc data access boundary phía dưới Service.
11. `@RequestBody` bind JSON body thành Orchid.
12. `@PathVariable` lấy id từ `/orchids/{id}`; `@RequestParam` lấy name từ `?name=cat`.
13. POST tạo thành công dùng 201 Created và trả generated ID.
14. DELETE thành công dùng 204 No Content và không có body.
15. Resource thiếu trả 404 sau khi query DB; DB kết nối lỗi là lỗi hạ tầng, không phải missing resource.
16. Transaction tạo/sửa/xóa bắt đầu khi gọi Service qua Spring proxy; commit sau khi method trả thành công, rollback khi unchecked exception thoát ra.
17. GET lại sau POST chứng minh resource được đọc lại, không chỉ phản hồi object ở memory.
18. SELECT DB xác minh row, FK và đúng datasource; response JSON không đủ chứng minh SQL Server state.
19. Bidirectional graph có thể serialize lặp vô hạn; `@JsonIgnore` ở inverse collection ngăn vòng lặp trong bài này.
20. Kiểm chứng đề xuất AI bằng mapping/model, startup, tests, SQL/metadata và hậu điều kiện; không coi code được sinh là evidence chạy thật.

## AI verification log

| Quyết định | Cách xác minh | Kết luận |
|---|---|---|
| Giữ mappedBy theo field Java | Entity code + Spring context/JPA integration test | Chấp nhận `orchidCategory` |
| Resolve category tại Service | Invalid category HTTP + rejected PUT rollback | Chấp nhận |
| Validation tên rỗng | HTTP 400 + test count DB không đổi | Chấp nhận |
| ID do server sinh | POST ID 999999 nhưng response ID mới | Chấp nhận |
| H2 pass tương đương SQL Server pass | H2 và SQL Server được chạy riêng; có JDBC/metadata/row evidence | Bác bỏ tính tương đương; T11/T12 SQL đã kiểm tra riêng và pass |
| `@Nationalized` lưu Unicode SQL Server | SQL Server metadata nvarchar + T11/T12 description UTF-16 match | Chấp nhận, có runtime SQL evidence |
