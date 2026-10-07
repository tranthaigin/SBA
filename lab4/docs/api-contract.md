# API Contract — SBA301 Lab 04 Orchid REST API

## 1. Overview
- **Base URL**: `http://localhost:8080`
- **Supported Base Paths**: `/api/orchids` and `/orchids`
- **Content-Type**: `application/json`

---

## 2. Endpoints Summary

| HTTP Verb | Path | Description | Success Status | Error Status |
|:---|:---|:---|:---|:---|
| **GET** | `/api/orchids` | Lấy danh sách tất cả hoa lan | `200 OK` | — |
| **GET** | `/api/orchids?name={keyword}` | Tìm kiếm hoa lan theo tên (case-insensitive) | `200 OK` | — |
| **GET** | `/api/orchids/{id}` | Lấy chi tiết một hoa lan theo ID | `200 OK` | `404 Not Found` |
| **POST** | `/api/orchids` | Tạo mới một hoa lan | `201 Created` | `400 Bad Request` |
| **PUT** | `/api/orchids/{id}` | Cập nhật thông tin toàn bộ hoa lan | `200 OK` | `400 Bad Request`, `404 Not Found` |
| **DELETE** | `/api/orchids/{id}` | Xóa một hoa lan theo ID | `204 No Content` | `404 Not Found` |
| **GET** | `/api/categories` | Lấy danh sách tất cả thể loại hoa lan | `200 OK` | — |
| **GET** | `/api/categories/{id}` | Lấy chi tiết thể loại hoa lan | `200 OK` | `404 Not Found` |
| **POST** | `/api/categories` | Tạo mới thể loại hoa lan | `201 Created` | `400 Bad Request` |

---

## 3. Data Schemas

### Orchid Entity
```json
{
  "orchidID": 1,
  "orchidName": "Cattleya Queen",
  "isNatural": true,
  "orchidDescription": "Demo orchid for Slot 18",
  "orchidCategory": {
    "categoryId": 1,
    "categoryName": "Cattleya"
  },
  "isAttractive": true,
  "orchidURL": "https://example.com/orchid.jpg"
}
```

### OrchidCategory Entity
```json
{
  "categoryId": 1,
  "categoryName": "Cattleya"
}
```

---

## 4. Detailed Specification

### 4.1. GET `/api/orchids`
- **Description**: Trả về danh sách hoa lan.
- **Query Params**:
  - `name` (optional): Từ khóa tìm kiếm trong `orchidName`.
- **Response `200 OK`**:
```json
[
  {
    "orchidID": 1,
    "orchidName": "Cattleya Queen",
    "isNatural": true,
    "orchidDescription": "Demo orchid for Slot 18",
    "orchidCategory": {
      "categoryId": 1,
      "categoryName": "Cattleya"
    },
    "isAttractive": true,
    "orchidURL": "https://example.com/orchid.jpg"
  }
]
```

### 4.2. GET `/api/orchids/{id}`
- **Description**: Trả về thông tin chi tiết một hoa lan theo `id`.
- **Response `200 OK`**: JSON object hoa lan.
- **Response `404 Not Found`**: Khi không tìm thấy `id`.

### 4.3. POST `/api/orchids`
- **Description**: Thêm mới một hoa lan.
- **Request Body**:
```json
{
  "orchidName": "Cattleya Queen",
  "isNatural": true,
  "orchidDescription": "Demo orchid for Slot 18",
  "orchidCategory": {
    "categoryId": 1
  },
  "isAttractive": true,
  "orchidURL": "https://example.com/orchid.jpg"
}
```
- **Response `201 Created`**: Đối tượng hoa lan vừa tạo kèm `orchidID` được sinh tự động.
- **Response `400 Bad Request`**: Khi `categoryId` thiếu hoặc không tồn tại trong hệ thống.
- Tên Orchid bắt buộc, không chỉ whitespace; tên/mô tả/URL tối đa 150/1000/255 ký tự. Lỗi Bean Validation/JSON syntax trả 400; Content-Type không được hỗ trợ trả 415.
- Client gửi `orchidID` sẽ bị bỏ qua; server luôn sinh ID mới. `orchidCategory.categoryName` do server đọc từ Category đã tồn tại.

### 4.4. PUT `/api/orchids/{id}`
- **Description**: Cập nhật toàn bộ thuộc tính của hoa lan.
- **Request Body**: Tương tự như POST.
- **Response `200 OK`**: Đối tượng hoa lan sau khi cập nhật.
- **Response `404 Not Found`**: Khi `id` không tồn tại.
- **Response `400 Bad Request`**: Khi `categoryId` không hợp lệ.
- PUT thay toàn bộ field; optional field thiếu sẽ thành null. Category được resolve trước khi thay dữ liệu. Request không hợp lệ rollback, GET lại giữ nguyên trạng thái cũ.

### 4.5. DELETE `/api/orchids/{id}`
- **Description**: Xóa hoa lan theo `id`.
- **Response `204 No Content`**: Xóa thành công (không có body).
- **Response `404 Not Found`**: Khi không tìm thấy `id`.

### 4.6. Category endpoints

- GET `/api/categories` trả array gồm `categoryId`, `categoryName`; GET `/{id}` trả object hoặc 404.
- POST `{ "categoryName": "Vanda" }` trả 201. Tên bắt buộc, tối đa 100 ký tự, trim trước khi lưu; tên đã tồn tại (ignore case) trả 400.
- ID và collection `orchids` gửi từ client bị bỏ qua. POST Category chỉ tạo Category mới; không ghi đè Category có sẵn.
- Service Category xử lý business/data access; Controller không gọi repository trực tiếp.

### 4.7. Error bodies và alias

Lỗi business Category theo mẫu course trả text với status 400. Lỗi Bean Validation/Jackson dùng body error mặc định của Spring Boot; không cam kết format ProblemDetail riêng. Missing resource trả 404 không có body từ Controller. `/orchids` và `/orchids/` hỗ trợ list/create như đề gốc; `/api/orchids` là base path chính.
