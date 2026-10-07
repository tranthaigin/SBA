# Lab03 nằm ở đâu?

Đã rà nội dung 64 PDF/DOCX/PPTX trong các thư mục tài liệu, tài liệu của repository, slide và PDF ở root C:/SBA301. Không tìm thấy file đề riêng tên Lab03. Các bản PDF trùng nhau được rà nhưng không xem là nguồn yêu cầu độc lập.

| Nguồn | Vị trí | Yêu cầu dùng cho bài |
|---|---|---|
| [Syllabus DetailsSBA301.docx](../../Tài%20Liệu/Syllabus%20DetailsSBA301.docx) | Bảng lịch học, tiết 45-46 | Lab03: RESTful Web Services with Spring Boot, CLO3; trước phần JPA tiết 48 |
| [Slot15](../../Tài%20Liệu/SBA301_Slot15_Versioning_Paging_Sorting_Page_Slice.pdf) | Trang 12, mục D | CRUD baseline, v1/v2, paging page 0/1, sort asc/desc, Page vs Slice, 20-30 records, Postman và README |
| [Slot16](../../Tài%20Liệu/SBA301_Slot16_Testing_MockMvc.pdf) | Trang 2, 5-13, 18-19 | Employee fields empId/empName/designation/salary; core ArrayList repository; MVC/service/repository tests; error/paging/sorting; red-green, strategy, AI log, Human Gate |
| [Hướng dẫn học tập](../../Tài%20Liệu/Huong_dan_hoc_tap_va_phoi_hop_nhom_SBA301.pdf) | Trang 8, M3 | REST API chạy độc lập, documentation và error/test evidence |

## Quyết định phạm vi

Chọn Employee vì Slot16 xác định Employee Management làm ngữ liệu core Lab03. FUNews/JPA/H2 ở bài tổng hợp Slot15 là demo/transfer, không thay thế core in-memory của Slot16. Chỉ dùng Spring Data Commons để có Pageable/Page/Slice, không kéo starter JPA và datasource vào core.

Triển khai URI versioning (một strategy được chấp nhận trong checklist); các bài chủ đề query/header/media versioning là bài luyện thêm, không triển khai đồng thời bốn strategy. V2 đọc cùng dữ liệu nhưng mở rộng representation với email/department; base/V1 có CRUD.

Mở rộng hợp lý: Bean Validation, salary BigDecimal, duplicate 409, path/body ID guard, stable sort, synchronized repository operations và overflow regression. Không giả định đây là bản sao của đề Lab03 riêng chưa có trên máy.

## Tính tương thích đã kiểm tra

JDK 21 / Spring Boot 3.3.4 giữ cùng course stack với Lab4. Chọn springdoc 2.6.0 theo [compatibility matrix chính thức](https://springdoc.org/v2/#what-is-the-compatibility-matrix-of-springdoc-openapi-with-spring-boot): Boot 3.3.x ↔ springdoc 2.6.x. Runtime OpenAPI/Swagger và tests được chạy thật; không coi dependency resolve là đủ.
