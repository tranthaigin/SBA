# API contract - Slot22 Part A

Base URL mặc định `http://localhost:8080`; máy kiểm chứng dùng `http://localhost:8082`. JSON request dùng `Content-Type: application/json`.

## Category

`GET /api/categories`: Page JSON, có `content`, `number`, `size`, `totalElements`, `totalPages`. Query parameters: `keyword` tìm trong tên, không phân biệt hoa/thường; `page` tính từ 0; `size` mặc định 10, tối đa 100; `sort` mặc định `categoryName,asc`. Field sort cho phép: `categoryId`, `categoryName`, `categoryDescription`, `parentCategoryId`, `active`. Field sort khác trả 400.

`GET /api/categories/{id}`: 200 hoặc 404. ID kiểu Short/SQL SMALLINT.

`POST /api/categories`, `PUT /api/categories/{id}` nhận:

```json
{
  "categoryName": "Technology News",
  "categoryDescription": "Technology and software engineering news",
  "parentCategoryId": null,
  "active": true
}
```

Name/description không blank, tối đa 100/250 ký tự. Service trim hai giá trị; name không trùng theo ignore-case ở cả create và update. `active` thiếu/null được mặc định true. `parentCategoryId` là scalar nullable theo schema trong PDF. Request không có field ID; POST để DB sinh ID, PUT giữ ID từ URL và cập nhật bốn field được phép.

Response thêm `categoryId` vào bốn field trên. POST 201; PUT 200. PUT ID thiếu trả 404, không tạo row mới. PUT đổi tên thành tên Category khác trả 409, không sửa row.

`DELETE /api/categories/{id}`: 204 khi xóa thành công; 404 nếu thiếu; 409 nếu NewsArticle đang tham chiếu. Category/NewsArticle giữ nguyên khi bị chặn. Hard delete, không cascade article.

## NewsArticle tối thiểu theo mục 6.2

`GET /api/news-articles`: Page JSON; `keyword` tìm trong `newsArticleId`, ignore-case; paging cùng quy tắc 0/10/100; sort chỉ `newsArticleId`, mặc định asc.

`GET /api/news-articles/{id}`: 200 hoặc 404.

`POST /api/news-articles`:

```json
{
  "newsArticleId": "NEWS-001",
  "categoryId": 2
}
```

ID không blank, tối đa 20 ký tự; `categoryId` bắt buộc và phải tồn tại. Trả 201; ID đã có trả 409; Category thiếu trả 404; input invalid trả 400. ID được trim và không tự sinh.

`PUT /api/news-articles/{id}`:

```json
{
  "categoryId": 3
}
```

Chỉ cập nhật quan hệ Category, giữ nguyên article ID. Article hoặc Category không tồn tại trả 404 và DB không đổi. Response của create/read/update chỉ gồm `newsArticleId`, `categoryId`, không serialize Entity/LAZY relationship.

`DELETE /api/news-articles/{id}`: 204; thiếu trả 404.

## Error contract

404/409 trả `ApiError` theo workbook:

```json
{
  "code": "NOT_FOUND",
  "message": "Category 32767 not found",
  "timestamp": "2026-10-13T00:00:00Z"
}
```

Conflict dùng code `CONFLICT`; sort không hợp lệ dùng `INVALID_INPUT`. Timestamp trên là ví dụ định dạng; dữ liệu thật nằm trong evidence. Bean Validation/binding/media errors dùng response mặc định Spring Boot theo lựa chọn course baseline tại trang 25; status 400/415. Không có JWT/role checks hoặc field-level error contract production.
