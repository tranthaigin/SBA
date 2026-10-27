# Debug Log

## D01 - Login automation không kích hoạt bằng pointer click

- Triệu chứng: nút Sign in được tìm thấy nhưng thao tác pointer click của công cụ kiểm thử không submit form.
- Giả thuyết: lỗi handler React hoặc browser automation không phát đúng pointer event.
- Kiểm tra: console không có lỗi; submit bằng phím Enter trên password field chạy đúng và hiển thị validation.
- Root cause: giới hạn của pointer action trong môi trường browser automation, không phải lỗi application handler.
- Fix: không thay đổi code; dùng keyboard submit để kiểm tra form theo hành vi accessibility chuẩn.
- Retest: empty, wrong credential và Admin/Admin đều cho kết quả đúng.

## D02 - Nguy cơ News bị orphan khi xóa Category

- Triệu chứng dự kiến: nếu Category đang được News tham chiếu bị xóa, `categoryId` trở thành không hợp lệ.
- Giả thuyết: Delete Category chỉ dùng `filter` mà không kiểm tra relation.
- Kiểm tra: đối chiếu data model và test relation trong Assignment Guide.
- Root cause: policy xóa relation chưa được xác định.
- Fix: `CategoryPage` kiểm tra News đang sử dụng Category; nếu có thì từ chối xóa và hiển thị lỗi.
- Retest: source build/lint đạt; relation guard dùng cùng source `news` mà table đang render.

## D03 - Dữ liệu có thể mất sau reload

- Triệu chứng dự kiến: state React thông thường bị reset khi reload.
- Giả thuyết: dữ liệu chỉ tồn tại trong memory.
- Kiểm tra: tạo thêm Category, News và User rồi reload trình duyệt.
- Root cause: cần persistence strategy rõ ràng.
- Fix: dùng `useLocalStorage` để hydrate lần đầu và lưu sau mỗi state change.
- Retest: sau reload Dashboard vẫn hiển thị 4 Category, 4 News, 4 Users và session Admin vẫn còn.
