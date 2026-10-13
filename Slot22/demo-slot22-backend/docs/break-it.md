# Break-It -> Diagnose -> Fix

Workshop thực tế ngày 13/10/2026, dùng ca `Gửi text/plain` tại mục 6.3 của PDF.

| Pha | Thao tác | Kết quả quan sát |
| --- | --- | --- |
| Break - B01 | POST `/api/categories` với JSON text nhưng header `Content-Type: text/plain` | HTTP 415, không có row mới |
| Diagnose | Xem header request/response trong Postman collection và handler log | Body converter không nhận media type này cho `@RequestBody CategoryRequest` |
| Fix - B02 | Giữ payload hợp lệ, đổi header thành `application/json` | HTTP 201, ID tự sinh, SQL có Workshop Category |
| Cleanup - B03 | DELETE Category vừa tạo | HTTP 204, SQL không còn row test |

Payload:

```json
{
  "categoryName": "Workshop Category",
  "categoryDescription": "Technology and software engineering news",
  "parentCategoryId": null,
  "active": true
}
```

[Newman request/header/response](../evidence/newman-run.json), [SQL trước/sau từng ca](../evidence/api-sql.json), [log thực tế](../evidence/sql-trace.txt). Lỗi nằm ở HTTP media/binding boundary, trước Service/persistence; không sửa Repository hoặc schema để xử lý lỗi header.
