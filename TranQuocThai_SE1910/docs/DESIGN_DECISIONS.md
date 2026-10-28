# Design Decisions

| Quyết định | Lựa chọn | Lý do | Ảnh hưởng nếu đổi yêu cầu |
|---|---|---|---|
| Navigation | State navigation trong `App.jsx` | Đủ cho 5 màn hình, không thêm dependency ngoài yêu cầu | Nếu cần URL/deep link sẽ chuyển sang React Router |
| State ownership | Auth và ba danh sách ở `App`; form/search/modal ở từng page | Shared data nằm ở parent, UI state ở component trực tiếp sử dụng | Nếu nhiều module dùng chung hơn có thể chuyển sang Context/service |
| Persistence | `localStorage` qua `useLocalStorage` | Dữ liệu CRUD và session không mất khi reload | Khi có API sẽ thay hook bằng service gọi backend |
| CRUD dialog | Một `Modal` dùng chung, mỗi page quản lý create/update mode | Tái sử dụng layout nhưng giữ validation riêng, dễ giải thích | Field mới chỉ sửa form của entity tương ứng |
| Search | Giữ source list, tạo displayed list bằng `filter` | Search không phá dữ liệu gốc; clear sẽ trả full list | Với API thật có thể chuyển sang query server-side |
| Folder structure | Tách `components`, `pages`, `data`, `hooks` | Trách nhiệm rõ và phù hợp quy mô Assignment 01 | Có thể thêm `services` khi kết nối Spring Boot |

## State flow chính

1. Login thành công ghi `currentUser` vào localStorage và `App` render admin layout.
2. Menu thay đổi `activePage`; `App` render page tương ứng trong cùng layout.
3. Create mở form rỗng; Update mở form được prefill từ selected item.
4. Submit validate dữ liệu rồi tạo array/object mới bằng spread hoặc `map`.
5. Delete mở confirmation; Confirm tạo danh sách mới bằng `filter`, Cancel không đổi state.
6. Search chuẩn hóa keyword bằng `trim().toLowerCase()` và chỉ lọc displayed list.
