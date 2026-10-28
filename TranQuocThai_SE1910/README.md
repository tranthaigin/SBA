# FUNewsManagementSystem - Assignment 01

Frontend quản trị tin tức được xây dựng bằng ReactJS và Vite theo yêu cầu của SBA301 Assignment 01.

> Project được đặt tên `TranQuocThai_SE1910` theo quy ước `StudentName_ClassCode` của đề.

## Công nghệ

- ReactJS 19
- Vite 8
- CSS thuần
- Mock data và `localStorage`
- Không sử dụng backend hoặc database thật

## Cài đặt và chạy

Yêu cầu Node.js phiên bản 20.19+ hoặc 22.12+.

```bash
npm install
npm run dev
```

Vite sẽ hiển thị địa chỉ local, thông thường là `http://localhost:5173`.

Kiểm tra source và tạo production build:

```bash
npm run lint
npm run build
```

## Tài khoản kiểm thử

| Username | Password | Kết quả |
|---|---|---|
| `Admin` | `Admin` | Đăng nhập vào trang quản trị |

Thông tin đăng nhập phân biệt chữ hoa và chữ thường.

## Chức năng

- Login với validation dữ liệu rỗng và sai tài khoản.
- Logout và chặn admin layout khi chưa đăng nhập.
- Header, logo AI và admin layout dùng chung.
- Menu Dashboard, Category, News, Users và Settings.
- Category: Read, Create, Update, Delete, Search.
- News: Read, Create, Update, Delete, Search và liên kết Category bằng `categoryId`.
- Users: Read, Create, Update, Delete, Search và hiển thị role Admin/Staff.
- Create/Update sử dụng popup dialog.
- Delete sử dụng confirmation dialog; Cancel không làm thay đổi dữ liệu.
- Validation field bắt buộc và kiểm tra tên Category/Username không trùng.
- Empty state và no-result state.
- Dữ liệu và phiên đăng nhập được lưu trong `localStorage`.
- Responsive cơ bản cho desktop, tablet và mobile.

## Quy tắc dữ liệu

- Role: `1 = Admin`, `2 = Staff`.
- Status: `1 = Active`, `0 = Inactive`.
- Mỗi entity có ID duy nhất.
- `News.categoryId` phải tham chiếu Category tồn tại.
- Không cho xóa Category đang được News sử dụng để tránh quan hệ không hợp lệ.
- Username và Category name không được trùng, không phân biệt hoa/thường.

## Cấu trúc source

```text
src/
|- components/   Header, Sidebar, Modal, ConfirmDialog, StatusBadge
|- data/         Mock data ban đầu
|- hooks/        Hook đồng bộ state với localStorage
|- pages/        Login, Dashboard, Category, News, Users, Settings
|- App.jsx       Auth state, shared data và page navigation
|- App.css       Toàn bộ style của ứng dụng
`- index.css     Global reset và design tokens
```

## Hồ sơ đánh giá

- [Requirement Traceability Matrix](docs/REQUIREMENT_TRACEABILITY.md)
- [Design Decisions](docs/DESIGN_DECISIONS.md)
- [Test Matrix](docs/TEST_MATRIX.md)
- [Debug Log](docs/DEBUG_LOG.md)
- [AI Usage Log](docs/AI_USAGE_LOG.md)

## Giới hạn

- Authentication chỉ là mock authentication, không phải cơ chế bảo mật production.
- Dữ liệu chỉ tồn tại trong trình duyệt hiện tại; xóa localStorage sẽ khôi phục dữ liệu ban đầu ở lần tải tiếp theo.
- Không có Spring Boot, API, JWT hoặc database thật vì Assignment 01 không bắt buộc.

## Logo và AI usage

Logo tại `public/assets/funews-logo.png` được tạo bằng công cụ AI image generation cho FUNewsManagementSystem. Chi tiết prompt, phần code sử dụng AI và cách kiểm chứng được ghi trong `docs/AI_USAGE_LOG.md`.

## Student

- Name: Tran Quoc Thai
- Student ID: CE190243
- Class: SE1910
- Project: TranQuocThai_SE1910
