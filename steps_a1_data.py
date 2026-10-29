# Assignment 01 Step Definitions
a1_steps = [
    # Nhóm 1: Xác thực & Đăng nhập
    (
        "noteTT/screenshots_a1/01_login_initial.png",
        "Chức năng 1.1: Giao diện màn hình Đăng nhập ban đầu (LoginPage)",
        "Khi khởi động ứng dụng, nếu chưa có thông tin đăng nhập trong localStorage, hệ thống tự động render component LoginPage. Màn hình chia bố cục 2 cột gồm panel form bên trái hiển thị logo thương hiệu AI, 2 ô nhập liệu Username, Password, nút Đăng nhập và gợi ý tài khoản demo. Bên phải là poster giới thiệu hệ thống.",
        "Form hiển thị đầy đủ tiêu đề 'Sign in to continue', logo FUNews, 2 ô input rỗng có placeholder rõ ràng và thẻ Demo account hiển thị tài khoản 'Admin / Admin'.",
        "src/pages/LoginPage.jsx (Dòng 7 - 110)",
        "Component LoginPage: Quản lý form nhập liệu đăng nhập, xử lý sự kiện submit và kích hoạt callback onLogin khi thông tin xác thực chính xác."
    ),
    (
        "noteTT/screenshots_a1/02_login_empty_error.png",
        "Chức năng 1.2: Bắt lỗi Validation khi để trống trường đăng nhập",
        "Người dùng bấm nút 'Sign in' khi chưa nhập Username hoặc Password. Hàm handleSubmit kiểm tra điều kiện !form.username.trim() || !form.password, nếu vi phạm sẽ gán chuỗi thông báo lỗi vào state error và dừng xử lý.",
        "Xuất hiện hộp thông báo màu đỏ có vai trò role='alert' với nội dung: 'Username and password are required.'. Ứng dụng không chuyển trang, bảo vệ tính hợp lệ dữ liệu.",
        "src/pages/LoginPage.jsx (Dòng 25 - 31)",
        "Hàm handleSubmit: Kiểm tra tính hợp lệ của dữ liệu trước khi gửi đi; ngăn chặn việc đăng nhập với dữ liệu rỗng."
    ),
    (
        "noteTT/screenshots_a1/03_login_wrong_credentials.png",
        "Chức năng 1.3: Bắt lỗi khi nhập sai thông tin tài khoản đăng nhập",
        "Người dùng nhập tài khoản không khớp (ví dụ: WrongUser / WrongPass) rồi bấm 'Sign in'. Hàm handleSubmit đối chiếu với thông tin Admin/Admin, phát hiện không hợp lệ nên gán thông báo lỗi vào state error.",
        "Xuất hiện thông báo cảnh báo lỗi màu đỏ: 'Incorrect username or password.'. Người dùng vẫn ở lại trang đăng nhập và có thể nhập lại.",
        "src/pages/LoginPage.jsx (Dòng 33 - 42)",
        "Hàm handleSubmit: So khớp thông tin đăng nhập với tài khoản mẫu của hệ thống và hiển thị phản hồi lỗi phù hợp."
    ),
    (
        "noteTT/screenshots_a1/04_login_success_dashboard.png",
        "Chức năng 1.4: Đăng nhập thành công và chuyển hướng vào Dashboard",
        "Người dùng nhập đúng tài khoản 'Admin' và mật khẩu 'Admin' rồi bấm 'Sign in'. Hàm handleSubmit gọi onLogin({ username: 'Admin', role: 1 }), cập nhật state currentUser trong App.jsx và lưu vào localStorage. App.jsx lập tức chuyển đổi render sang giao diện quản trị với trang Dashboard mặc định.",
        "Màn hình đăng nhập biến mất, hiển thị giao diện Dashboard với banner 'Welcome to FUNews Management System', các thẻ thống kê tổng quan và danh sách tin mới nhất.",
        "src/App.jsx (Dòng 26 - 37) & src/pages/LoginPage.jsx (Dòng 33 - 37)",
        "Hàm onLogin (setCurrentUser) và điều kiện render trong App.jsx: Nhận dữ liệu phiên làm việc, cập nhật state toàn cục và mở khóa truy cập vào admin layout."
    ),
    (
        "noteTT/screenshots_a1/05_layout_sidebar_header.png",
        "Chức năng 1.5: Bố cục Layout dùng chung (Header & Sidebar 5 menu)",
        "Hệ thống sử dụng cấu trúc layout cố định: Sidebar bên trái chứa logo và 5 nút điều hướng (Dashboard, Category, News, Users, Settings). Header phía trên hiển thị tiêu đề trang động, avatar chữ cái đầu của Admin, tên đăng nhập 'Admin', chức danh 'Administrator' và nút 'Log out'.",
        "Giao diện hiển thị đầy đủ và chuẩn xác cả thanh Sidebar và Topbar Header. Nút Dashboard đang ở trạng thái active (nền sáng xanh).",
        "src/components/Sidebar.jsx (Dòng 14 - 42) & src/components/Header.jsx (Dòng 5 - 27)",
        "Component Sidebar & Header: Tạo khung layout dùng chung cho toàn bộ ứng dụng, quản lý điều hướng trang và hiển thị phiên làm việc của người dùng."
    ),
    (
        "noteTT/screenshots_a1/06_dashboard_stats_and_news.png",
        "Chức năng 1.6: Trang Tổng quan Dashboard (Thống kê & Tin mới nhất)",
        "DashboardPage nhận props categories, news, users để tính toán động: tổng số danh mục (và số Active), tổng số bài viết (và số Active), tổng số tài khoản (và số Active). Bên dưới hiển thị top 3 bài viết tin tức mới nhất kèm danh mục và người tạo.",
        "Hiển thị 3 thẻ stat-card: Categories (3 / 2 active), News articles (3 / 2 active), User accounts (3 / 2 active). Bấm vào từng thẻ sẽ chuyển nhanh sang trang quản lý tương ứng.",
        "src/pages/DashboardPage.jsx (Dòng 7 - 62)",
        "Component DashboardPage: Tổng hợp số liệu thống kê từ các state và cung cấp giao diện tổng quan kèm lối tắt điều hướng nhanh."
    ),

    # Nhóm 2: Quản lý Thể loại (Category)
    (
        "noteTT/screenshots_a1/07_category_list.png",
        "Chức năng 2.1: Hiển thị danh sách Thể loại (Category Read)",
        "Khi bấm vào menu 'Category' trên Sidebar, App.jsx kích hoạt activePage='categories', render CategoryPage. Trang hiển thị bảng danh sách thể loại với các cột: ID, Name, Status (sử dụng component StatusBadge hiển thị màu xanh cho Active, màu xám cho Inactive), và cột Actions chứa 2 nút Edit, Delete.",
        "Bảng hiển thị chính xác 3 thể loại ban đầu: #1 Education (Active), #2 Technology (Active), #3 Campus Life (Inactive). Phía trên có thanh tìm kiếm và nút '+ Add category'.",
        "src/pages/CategoryPage.jsx (Dòng 127 - 159)",
        "Hàm render bảng trong CategoryPage: Duyệt mảng displayedCategories để xuất các dòng tr chứa thông tin thể loại và các nút thao tác."
    ),
    (
        "noteTT/screenshots_a1/08_category_create_modal.png",
        "Chức năng 2.2: Mở Popup Modal thêm mới Thể loại (Category Create Modal)",
        "Người dùng bấm nút '+ Add category', hàm openCreate được gọi: thiết lập modalMode='create', làm rỗng form, xóa các thông báo lỗi cũ và mở popup Modal tiêu đề 'Add category'. Form chứa ô nhập Category name và dropdown chọn Status (Active/Inactive).",
        "Xuất hiện modal popup nằm trên lớp nền mờ (backdrop), tiêu đề 'Add category', ô nhập Category name hiển thị placeholder 'Example: Technology' và tự động focus.",
        "src/pages/CategoryPage.jsx (Dòng 35 - 41 & Dòng 170 - 207)",
        "Hàm openCreate: Khởi tạo trạng thái form thêm mới và kích hoạt hiển thị Modal component."
    ),
    (
        "noteTT/screenshots_a1/09_category_create_success.png",
        "Chức năng 2.3: Thêm mới Thể loại thành công vào danh sách",
        "Người dùng nhập tên 'Sports', chọn Status 'Active' rồi bấm 'Create category'. Hàm handleSubmit validate thành công, tính toán ID tự tăng tiếp theo: nextId = max(ID) + 1 = 4, thêm bản ghi mới vào mảng categories thông qua setCategories, đóng modal.",
        "Bảng danh sách lập tức xuất hiện thêm dòng mới: ID #4, Name 'Sports', Status 'Active' màu xanh lá. Dữ liệu được tự động đồng bộ vào localStorage.",
        "src/pages/CategoryPage.jsx (Dòng 66 - 92)",
        "Hàm handleSubmit (nhánh create): Thêm phần tử mới vào danh sách categories bằng phương pháp immutable update mảng."
    ),
    (
        "noteTT/screenshots_a1/10_category_validation_duplicate.png",
        "Chức năng 2.4: Bắt lỗi Validation chống trùng lặp tên Thể loại",
        "Người dùng thử bấm '+ Add category' và nhập lại tên 'Sports' (đã tồn tại). Hàm handleSubmit sử dụng phương thức categories.some(...) để kiểm tra trùng lặp (không phân biệt chữ hoa/thường). Phát hiện trùng lặp, hàm gán 'Category name must be unique.' vào formError.",
        "Trong Modal xuất hiện thông báo lỗi màu đỏ 'Category name must be unique.'. Dữ liệu trùng lặp không được thêm vào bảng.",
        "src/pages/CategoryPage.jsx (Dòng 74 - 79)",
        "Hàm handleSubmit (kiểm tra trùng lặp): Đảm bảo tính duy nhất của tên thể loại trong toàn hệ thống."
    ),
    (
        "noteTT/screenshots_a1/11_category_update_modal.png",
        "Chức năng 2.5: Mở Popup Modal chỉnh sửa Thể loại (Category Edit Modal)",
        "Người dùng bấm nút 'Edit' tại dòng thể loại 'Sports'. Hàm openUpdate được gọi với tham số là category hiện tại, nạp ID vào selectedId, điền sẵn thông tin tên 'Sports' và trạng thái hiện tại vào form, mở modal với tiêu đề 'Update category'.",
        "Modal popup xuất hiện với tiêu đề 'Update category', trường Category name được điền sẵn giá trị 'Sports', nút lưu hiển thị 'Save changes'. Người dùng chỉnh sửa thành 'Sports & Recreation' và đổi Status thành 'Inactive'.",
        "src/pages/CategoryPage.jsx (Dòng 46 - 52)",
        "Hàm openUpdate: Nạp thông tin thể loại được chọn vào state form và chuyển modalMode sang chế độ 'update'."
    ),
    (
        "noteTT/screenshots_a1/12_category_update_success.png",
        "Chức năng 2.6: Cập nhật Thể loại thành công",
        "Người dùng bấm nút 'Save changes'. Hàm handleSubmit cập nhật mảng categories bằng map: thay thế bản ghi có category.id === selectedId với dữ liệu mới, sau đó đóng modal.",
        "Tại dòng ID #4, tên thể loại đã được đổi thành 'Sports & Recreation' và badge trạng thái chuyển sang 'Inactive' màu xám. Dữ liệu được cập nhật tức thì.",
        "src/pages/CategoryPage.jsx (Dòng 82 - 92)",
        "Hàm handleSubmit (nhánh update): Cập nhật thông tin bản ghi theo ID bằng phương pháp immutable map."
    ),
    (
        "noteTT/screenshots_a1/13_category_delete_prevent_in_use.png",
        "Chức năng 2.7: Chặn xóa Thể loại đang có bài viết liên kết (Ràng buộc nghiệp vụ)",
        "Người dùng bấm 'Delete' tại thể loại 'Technology' (đang được bài viết tin tức #2 sử dụng). Khi bấm xác nhận trong dialog, hàm handleDelete kiểm tra news.some(article => article.categoryId === deleteTarget.id). Vì có liên kết, hàm hủy thao tác và gán lỗi vào pageError.",
        "Phía trên bảng xuất hiện thanh thông báo cảnh báo lỗi màu đỏ: 'Cannot delete “Technology” because it is used by a news article.'. Thể loại 'Technology' không bị xóa, bảo toàn tính toàn vẹn dữ liệu.",
        "src/pages/CategoryPage.jsx (Dòng 104 - 113)",
        "Hàm handleDelete: Thực thi quy tắc nghiệp vụ bảo vệ toàn vẹn tham chiếu giữa Thể loại và Bài viết tin tức."
    ),
    (
        "noteTT/screenshots_a1/14_category_delete_confirm_dialog.png",
        "Chức năng 2.8: Hộp thoại xác nhận trước khi xóa Thể loại (Confirm Dialog)",
        "Người dùng bấm 'Delete' tại thể loại 'Sports & Recreation' (không có bài viết nào liên kết). State deleteTarget được gán đối tượng này, kích hoạt hiển thị component ConfirmDialog.",
        "Xuất hiện hộp thoại popup tiêu đề 'Confirm deletion', thông điệp cảnh báo rõ ràng: 'Are you sure you want to delete Sports & Recreation? This action cannot be undone.' kèm 2 nút Cancel và Delete.",
        "src/components/ConfirmDialog.jsx (Dòng 6 - 25)",
        "Component ConfirmDialog: Hiển thị giao diện cảnh báo và nhận phản hồi xác nhận từ người dùng trước khi tiến hành xóa vĩnh viễn."
    ),
    (
        "noteTT/screenshots_a1/15_category_delete_success.png",
        "Chức năng 2.9: Xóa Thể loại thành công khỏi hệ thống",
        "Người dùng bấm nút 'Delete' màu đỏ trong hộp thoại xác nhận. Hàm handleDelete gọi setCategories với filter loại bỏ phần tử có ID trùng khớp, đồng thời đóng dialog.",
        "Hộp thoại đóng lại, dòng thể loại 'Sports & Recreation' đã biến mất hoàn toàn khỏi bảng danh sách. Bảng trở về 3 danh mục ban đầu.",
        "src/pages/CategoryPage.jsx (Dòng 114 - 117)",
        "Hàm handleDelete (thực hiện xóa): Loại bỏ đối tượng khỏi mảng state categories bằng phương thức filter."
    ),
    (
        "noteTT/screenshots_a1/16_category_search_success.png",
        "Chức năng 2.10: Tìm kiếm Thể loại theo từ khóa (Search Category)",
        "Người dùng nhập từ khóa 'Edu' vào ô tìm kiếm. State keyword được cập nhật, biến displayedCategories lọc mảng categories theo điều kiện category.name.toLowerCase().includes(normalizedKeyword).",
        "Bảng danh sách lập tức lọc chỉ hiển thị đúng 1 dòng là #1 'Education'. Xuất hiện nút 'Clear search' bên cạnh ô tìm kiếm.",
        "src/pages/CategoryPage.jsx (Dòng 27 - 30)",
        "Bộ lọc displayedCategories: Lọc dữ liệu thời gian thực theo từ khóa nhập vào, không phân biệt chữ hoa và chữ thường."
    ),
    (
        "noteTT/screenshots_a1/17_category_search_empty.png",
        "Chức năng 2.11: Trạng thái không tìm thấy kết quả tìm kiếm Thể loại (Empty Search)",
        "Người dùng nhập từ khóa không tồn tại 'NonExistentCategory999'. Mảng displayedCategories trả về rỗng ([]). Điều kiện displayedCategories.length === 0 hiển thị khối giao diện empty-state.",
        "Thay vì bảng trống trơn gây hiểu lầm, hệ thống hiển thị thông báo trực quan: 'No matching categories / Try another search keyword.'. Bấm 'Clear search' sẽ khôi phục lại toàn bộ danh sách.",
        "src/pages/CategoryPage.jsx (Dòng 161 - 167)",
        "Khối render Empty State: Đảm bảo trải nghiệm người dùng thân thiện khi tìm kiếm không có dữ liệu phù hợp."
    ),

    # Nhóm 3: Quản lý Tin tức (News Articles)
    (
        "noteTT/screenshots_a1/18_news_list.png",
        "Chức năng 3.1: Hiển thị danh sách Tin tức (News Read)",
        "Bấm vào menu 'News' trên Sidebar, component NewsPage được render. Trang hiển thị danh sách bài báo dạng bảng với các cột: Article (tiêu đề in đậm và nội dung tóm tắt), Category (tên thể loại liên kết được tìm theo categoryId), Created by (tác giả), Status badge và cột Actions.",
        "Bảng hiển thị 3 bài viết mẫu ban đầu (#1 New semester orientation schedule..., #2 Technology club opens..., #3 Campus volunteer day) với đầy đủ thông tin phân loại danh mục chính xác.",
        "src/pages/NewsPage.jsx (Dòng 149 - 186)",
        "Hàm render bảng NewsPage: Duyệt mảng displayedNews và kết nối dữ liệu danh mục tương ứng thông qua categories.find(c => c.id === article.categoryId)."
    ),
    (
        "noteTT/screenshots_a1/19_news_create_modal.png",
        "Chức năng 3.2: Mở Popup Modal thêm mới bài viết Tin tức",
        "Người dùng bấm nút '+ Add news'. Hàm openCreate khởi tạo form rỗng với categoryId mặc định từ danh mục đầu tiên và tác giả là username hiện tại (Admin). Modal 'Add news article' xuất hiện.",
        "Modal popup mở ra gồm các trường: Title (tiêu đề), Content (nội dung chi tiết nhiều dòng - textarea), Category (dropdown chọn danh mục từ danh sách thể loại có sẵn), Status (Active/Inactive), và Created by (Admin).",
        "src/pages/NewsPage.jsx (Dòng 44 - 49 & Dòng 195 - 238)",
        "Hàm openCreate & makeEmptyForm: Thiết lập form thêm mới tin tức với các giá trị mặc định hợp lệ."
    ),
    (
        "noteTT/screenshots_a1/20_news_create_success.png",
        "Chức năng 3.3: Thêm mới bài viết Tin tức thành công",
        "Người dùng nhập tiêu đề 'AI Innovation Summit 2026', nội dung giới thiệu, chọn thể loại 'Technology', trạng thái 'Active' và bấm 'Create article'. Hàm handleSubmit validate hợp lệ, sinh ID mới #4 và thêm vào mảng news.",
        "Bảng tin tức xuất hiện bài viết mới với ID #4, tiêu đề 'AI Innovation Summit 2026', danh mục 'Technology', tác giả 'Admin', badge 'Active' màu xanh lá.",
        "src/pages/NewsPage.jsx (Dòng 90 - 121)",
        "Hàm handleSubmit (nhánh create): Thêm bài viết mới vào state news và đồng bộ vào localStorage."
    ),
    (
        "noteTT/screenshots_a1/21_news_validation_empty.png",
        "Chức năng 3.4: Bắt lỗi Validation khi thiếu Title hoặc Content tin tức",
        "Người dùng bấm '+ Add news' nhưng để trống ô Title hoặc Content rồi bấm 'Create article'. Hàm handleSubmit kiểm tra !title || !content và kích hoạt thông báo lỗi.",
        "Trong Modal xuất hiện thông báo lỗi màu đỏ 'Title and content are required.'. Form không được submit, ngăn ngừa dữ liệu bài viết bị rỗng.",
        "src/pages/NewsPage.jsx (Dòng 96 - 100)",
        "Hàm handleSubmit (validation): Kiểm tra tính bắt buộc của các trường thông tin tin tức."
    ),
    (
        "noteTT/screenshots_a1/22_news_update_modal.png",
        "Chức năng 3.5: Mở Popup Modal chỉnh sửa bài viết Tin tức",
        "Người dùng bấm 'Edit' tại bài viết vừa tạo. Hàm openUpdate nạp toàn bộ thông tin bài viết vào form, mở modal với tiêu đề 'Update news article'. Người dùng đổi tiêu đề thành 'AI & Robotics Summit 2026 - Extended' và chuyển Category sang 'Education'.",
        "Modal 'Update news article' hiển thị với toàn bộ dữ liệu cũ được điền sẵn, các trường thông tin sẵn sàng cho người dùng chỉnh sửa.",
        "src/pages/NewsPage.jsx (Dòng 54 - 63)",
        "Hàm openUpdate: Nạp dữ liệu bài viết vào form chỉnh sửa và kích hoạt modal cập nhật."
    ),
    (
        "noteTT/screenshots_a1/23_news_update_success.png",
        "Chức năng 3.6: Cập nhật bài viết Tin tức thành công",
        "Người dùng bấm nút 'Save changes'. Hàm handleSubmit cập nhật bài viết tương ứng trong mảng news và đóng modal.",
        "Bài viết tại dòng tương ứng hiển thị tiêu đề mới 'AI & Robotics Summit 2026 - Extended' và cột Category đã chuyển thành công sang 'Education'.",
        "src/pages/NewsPage.jsx (Dòng 114 - 124)",
        "Hàm handleSubmit (nhánh update): Ghi nhận thay đổi của bài viết và cập nhật state news."
    ),
    (
        "noteTT/screenshots_a1/24_news_delete_confirm_dialog.png",
        "Chức năng 3.7: Hộp thoại xác nhận trước khi xóa bài viết Tin tức",
        "Người dùng bấm nút 'Delete' tại bài viết vừa tạo. State deleteTarget được gán đối tượng bài viết, hiển thị ConfirmDialog.",
        "Hộp thoại xác nhận xuất hiện với nội dung: 'Are you sure you want to delete AI & Robotics Summit 2026 - Extended? This action cannot be undone.'. Có nút Cancel và Delete.",
        "src/pages/NewsPage.jsx (Dòng 240 - 246)",
        "Component ConfirmDialog trong NewsPage: Xác nhận an toàn trước khi xóa bài viết tin tức."
    ),
    (
        "noteTT/screenshots_a1/25_news_delete_success.png",
        "Chức năng 3.8: Xóa bài viết Tin tức thành công",
        "Người dùng bấm nút 'Delete' trong dialog. Hàm handleDelete lọc bỏ bài viết có ID tương ứng ra khỏi mảng news.",
        "Bài viết đã bị xóa khỏi danh sách, bảng tin tức quay trở lại 3 bài viết ban đầu, giao diện cập nhật ngay lập tức.",
        "src/pages/NewsPage.jsx (Dòng 133 - 136)",
        "Hàm handleDelete: Loại bỏ bài viết khỏi danh sách dữ liệu bằng phương thức filter."
    ),
    (
        "noteTT/screenshots_a1/26_news_search_success.png",
        "Chức năng 3.9: Tìm kiếm bài viết Tin tức theo từ khóa",
        "Người dùng nhập từ khóa 'orientation' vào ô tìm kiếm. displayedNews lọc các bài viết mà tiêu đề hoặc nội dung có chứa từ khóa.",
        "Bảng lọc ra đúng 1 bài viết phù hợp: 'New semester orientation schedule announced'.",
        "src/pages/NewsPage.jsx (Dòng 36 - 39)",
        "Bộ lọc displayedNews: Tìm kiếm đa trường (cả tiêu đề lẫn nội dung) không phân biệt hoa thường."
    ),
    (
        "noteTT/screenshots_a1/27_news_search_empty.png",
        "Chức năng 3.10: Trạng thái không có kết quả tìm kiếm Tin tức",
        "Người dùng nhập từ khóa không khớp 'UnknownArticleKeyword123'.",
        "Hiển thị thông báo trạng thái rỗng thân thiện: 'No matching news articles / Try another search keyword.'.",
        "src/pages/NewsPage.jsx (Dòng 188 - 194)",
        "Khối render Empty State: Phản hồi rõ ràng cho người dùng khi không tìm thấy tin tức tương ứng."
    ),

    # Nhóm 4: Quản lý Người dùng (Users)
    (
        "noteTT/screenshots_a1/28_users_list.png",
        "Chức năng 4.1: Hiển thị danh sách Tài khoản Người dùng (Users Read)",
        "Bấm vào menu 'Users' trên Sidebar, component UsersPage được render. Trang hiển thị danh sách tài khoản gồm các cột: ID, Username, Role (Admin hoặc Staff thông qua hàm roleLabel), Status badge và cột Actions.",
        "Bảng hiển thị 3 tài khoản mẫu ban đầu: #1 Admin (Role Admin, Active), #2 staff01 (Role Staff, Active), #3 staff02 (Role Staff, Inactive).",
        "src/pages/UsersPage.jsx (Dòng 137 - 163)",
        "Hàm render bảng UsersPage & hàm roleLabel: Hiển thị danh sách tài khoản và chuyển đổi role số (1, 2) sang tên vai trò trực quan."
    ),
    (
        "noteTT/screenshots_a1/29_users_create_modal.png",
        "Chức năng 4.2: Mở Popup Modal thêm mới Tài khoản Người dùng",
        "Người dùng bấm nút '+ Add user'. Hàm openCreate mở modal 'Add user account' với form rỗng.",
        "Modal popup hiển thị gồm các trường: Username, Mock password, Role (dropdown Admin/Staff), Status (Active/Inactive) và các nút Cancel, Create user.",
        "src/pages/UsersPage.jsx (Dòng 37 - 42 & Dòng 173 - 210)",
        "Hàm openCreate: Khởi tạo modal thêm mới người dùng với trạng thái sạch."
    ),
    (
        "noteTT/screenshots_a1/30_users_create_success.png",
        "Chức năng 4.3: Thêm mới Tài khoản Người dùng thành công",
        "Người dùng nhập Username 'editor_thai', Password, chọn Role 'Staff', Status 'Active' và bấm 'Create user'. Hàm handleSubmit kiểm tra hợp lệ, sinh ID #4 và thêm vào mảng users.",
        "Bảng danh sách xuất hiện thêm tài khoản #4: Username 'editor_thai', Role 'Staff', Status 'Active' màu xanh lá.",
        "src/pages/UsersPage.jsx (Dòng 82 - 110)",
        "Hàm handleSubmit (nhánh create): Thêm người dùng mới vào hệ thống sau khi đã kiểm tra tính hợp lệ."
    ),
    (
        "noteTT/screenshots_a1/31_users_validation_duplicate.png",
        "Chức năng 4.4: Bắt lỗi Validation chống trùng lặp Username",
        "Người dùng bấm '+ Add user' và nhập lại Username 'editor_thai' rồi bấm submit. Hàm handleSubmit kiểm tra trùng lặp thông qua users.some(...).",
        "Xuất hiện thông báo lỗi màu đỏ 'Username must be unique.' bên trong modal. Không cho phép tạo 2 tài khoản trùng tên.",
        "src/pages/UsersPage.jsx (Dòng 90 - 95)",
        "Hàm handleSubmit (kiểm tra trùng lặp): Bảo vệ tính toàn vẹn định danh của tài khoản người dùng."
    ),
    (
        "noteTT/screenshots_a1/32_users_update_modal.png",
        "Chức năng 4.5: Mở Popup Modal chỉnh sửa Tài khoản Người dùng",
        "Người dùng bấm nút 'Edit' tại tài khoản 'editor_thai'. Modal 'Update user account' mở ra với thông tin cũ được nạp sẵn. Người dùng đổi Role sang 'Admin' và Status sang 'Inactive'.",
        "Modal hiển thị tiêu đề 'Update user account', các trường thông tin sẵn sàng cho việc cập nhật vai trò và trạng thái.",
        "src/pages/UsersPage.jsx (Dòng 47 - 56)",
        "Hàm openUpdate: Nạp thông tin tài khoản được chọn vào form chỉnh sửa."
    ),
    (
        "noteTT/screenshots_a1/33_users_update_success.png",
        "Chức năng 4.6: Cập nhật Tài khoản Người dùng thành công",
        "Người dùng bấm 'Save changes'. Hàm handleSubmit cập nhật thông tin trong mảng users và đóng modal.",
        "Tại dòng tài khoản 'editor_thai', vai trò đã đổi thành 'Admin' và trạng thái chuyển sang 'Inactive' màu xám.",
        "src/pages/UsersPage.jsx (Dòng 107 - 114)",
        "Hàm handleSubmit (nhánh update): Lưu lại các thay đổi về vai trò và trạng thái của người dùng."
    ),
    (
        "noteTT/screenshots_a1/34_users_delete_confirm_dialog.png",
        "Chức năng 4.7: Hộp thoại xác nhận trước khi xóa Tài khoản Người dùng",
        "Người dùng bấm nút 'Delete' tại tài khoản 'editor_thai'. State deleteTarget được thiết lập, hiển thị ConfirmDialog.",
        "Xuất hiện hộp thoại cảnh báo: 'Are you sure you want to delete editor_thai? This action cannot be undone.' kèm nút xác nhận màu đỏ.",
        "src/pages/UsersPage.jsx (Dòng 212 - 218)",
        "Component ConfirmDialog trong UsersPage: Bảo vệ thao tác xóa tài khoản an toàn."
    ),
    (
        "noteTT/screenshots_a1/35_users_delete_success.png",
        "Chức năng 4.8: Xóa Tài khoản Người dùng thành công",
        "Người dùng bấm 'Delete' trong hộp thoại. Hàm handleDelete loại bỏ tài khoản khỏi mảng users.",
        "Tài khoản 'editor_thai' biến mất khỏi danh sách, bảng người dùng trở về 3 tài khoản ban đầu.",
        "src/pages/UsersPage.jsx (Dòng 122 - 125)",
        "Hàm handleDelete: Thực hiện xóa tài khoản khỏi state users."
    ),
    (
        "noteTT/screenshots_a1/36_users_search_success.png",
        "Chức năng 4.9: Tìm kiếm Tài khoản Người dùng theo Username",
        "Người dùng nhập từ khóa 'staff' vào ô tìm kiếm. displayedUsers lọc các tài khoản có username chứa từ khóa.",
        "Bảng lọc ra chính xác 2 tài khoản: 'staff01' và 'staff02'.",
        "src/pages/UsersPage.jsx (Dòng 29 - 32)",
        "Bộ lọc displayedUsers: Tìm kiếm người dùng nhanh chóng theo username không phân biệt chữ hoa/thường."
    ),
    (
        "noteTT/screenshots_a1/37_users_search_empty.png",
        "Chức năng 4.10: Trạng thái không có kết quả tìm kiếm Người dùng",
        "Người dùng nhập từ khóa không tồn tại 'ghostuser999'.",
        "Giao diện hiển thị empty state: 'No matching users / Try another search keyword.'.",
        "src/pages/UsersPage.jsx (Dòng 165 - 171)",
        "Khối render Empty State: Phản hồi thông báo thân thiện khi không có tài khoản phù hợp."
    ),

    # Nhóm 5: Cài đặt & Đăng xuất
    (
        "noteTT/screenshots_a1/38_settings_profile.png",
        "Chức năng 5.1: Trang Cài đặt & Thông tin Hồ sơ tài khoản (SettingsPage)",
        "Bấm vào menu 'Settings' trên Sidebar, SettingsPage hiển thị thông tin hồ sơ của tài khoản đang đăng nhập (Username: Admin, Role: Admin, Data storage: Browser localStorage, Application: FUNewsManagementSystem) cùng thẻ ghi chú về môi trường dữ liệu mock.",
        "Hiển thị rõ ràng 2 thẻ: Profile information với các cặp thuộc tính-giá trị chuẩn xác và thẻ Mock environment giải thích cơ chế lưu dữ liệu bền vững qua localStorage.",
        "src/pages/SettingsPage.jsx (Dòng 5 - 43)",
        "Component SettingsPage: Cung cấp thông tin chi tiết về tài khoản đang truy cập và cơ chế vận hành dữ liệu của ứng dụng."
    ),
    (
        "noteTT/screenshots_a1/39_logout_action.png",
        "Chức năng 5.2: Chức năng Đăng xuất tài khoản (Logout)",
        "Người dùng bấm nút 'Log out' trên Header. Hàm handleLogout trong App.jsx được kích hoạt: gọi setCurrentUser(null) (xóa khóa phiên khỏi localStorage) và reset activePage về 'dashboard'.",
        "Toàn bộ layout quản trị biến mất, người dùng được đưa trở lại màn hình Đăng nhập (LoginPage). Nếu thử truy cập lại khi chưa đăng nhập, hệ thống sẽ chặn hoàn toàn.",
        "src/App.jsx (Dòng 42 - 45) & src/components/Header.jsx (Dòng 21 - 23)",
        "Hàm handleLogout trong App.jsx: Hủy phiên làm việc hiện tại, xóa dữ liệu xác thực và bảo vệ toàn bộ các trang quản trị."
    ),
]
