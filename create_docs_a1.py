import os
import sys
import docx

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

PRIMARY_COLOR = RGBColor(30, 58, 138)    # #1E3A8A
SECONDARY_COLOR = RGBColor(2, 132, 199)  # #0284C7
DARK_TEXT = RGBColor(31, 41, 55)         # #1F2937
MUTED_TEXT = RGBColor(107, 114, 128)     # #6B7280
SUCCESS_COLOR = RGBColor(16, 149, 193)

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def add_header_banner(doc, title_text, subtitle_text):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell = tbl.cell(0, 0)
    set_cell_background(cell, "1E3A8A")
    set_cell_margins(cell, top=200, bottom=200, left=250, right=250)
    
    p = cell.paragraphs[0]
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run1 = p.add_run(title_text + "\n")
    run1.font.bold = True
    run1.font.size = Pt(16)
    run1.font.color.rgb = RGBColor(255, 255, 255)
    
    run2 = p.add_run(subtitle_text)
    run2.font.size = Pt(11)
    run2.font.color.rgb = RGBColor(224, 242, 254)
    
    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_after = Pt(12)

def add_callout(doc, title, text, bg_hex="EFF6FF", border_hex="3B82F6"):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell = tbl.cell(0, 0)
    set_cell_background(cell, bg_hex)
    set_cell_margins(cell, top=140, bottom=140, left=200, right=200)
    
    p = cell.paragraphs[0]
    p.paragraph_format.space_after = Pt(4)
    r_title = p.add_run(f"📌 {title}\n")
    r_title.font.bold = True
    r_title.font.size = Pt(10.5)
    r_title.font.color.rgb = PRIMARY_COLOR
    
    r_text = p.add_run(text)
    r_text.font.size = Pt(10)
    r_text.font.color.rgb = DARK_TEXT
    
    p_sp = doc.add_paragraph()
    p_sp.paragraph_format.space_after = Pt(8)

def add_screenshot_block(doc, image_path, step_title, how_it_works, expected_result, code_location, function_explanation):
    # Step Heading
    h = doc.add_heading(step_title, level=3)
    h.paragraph_format.space_before = Pt(14)
    h.paragraph_format.space_after = Pt(6)
    for r in h.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    # Image
    if os.path.exists(image_path):
        p_img = doc.add_paragraph()
        p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(4)
        p_img.paragraph_format.space_after = Pt(4)
        run_img = p_img.add_run()
        run_img.add_picture(image_path, width=Inches(6.0))
        
        # Caption
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_after = Pt(8)
        r_cap = p_cap.add_run(f"Hình: {step_title}")
        r_cap.font.italic = True
        r_cap.font.size = Pt(9.5)
        r_cap.font.color.rgb = MUTED_TEXT

    # Explanation Table / Structured Info
    tbl = doc.add_table(rows=4, cols=2)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl.autofit = False
    
    col_widths = [Inches(1.8), Inches(4.7)]
    rows_data = [
        ("Cơ chế hoạt động:", how_it_works),
        ("Kết quả nhìn thấy là đúng:", expected_result),
        ("Vị trí code tương ứng:", code_location),
        ("Phương thức / Hàm xử lý:", function_explanation),
    ]
    
    for i, (label, content) in enumerate(rows_data):
        row = tbl.rows[i]
        
        # Cell 0 (Header)
        cell0 = row.cells[0]
        cell0.width = col_widths[0]
        set_cell_background(cell0, "F8FAFC")
        set_cell_margins(cell0, top=80, bottom=80, left=120, right=120)
        p0 = cell0.paragraphs[0]
        p0.paragraph_format.space_after = Pt(0)
        r0 = p0.add_run(label)
        r0.font.bold = True
        r0.font.size = Pt(9.5)
        r0.font.color.rgb = PRIMARY_COLOR
        
        # Cell 1 (Content)
        cell1 = row.cells[1]
        cell1.width = col_widths[1]
        set_cell_background(cell1, "FFFFFF" if i % 2 == 0 else "F9FAFB")
        set_cell_margins(cell1, top=80, bottom=80, left=120, right=120)
        p1 = cell1.paragraphs[0]
        p1.paragraph_format.space_after = Pt(0)
        r1 = p1.add_run(content)
        r1.font.size = Pt(9.5)
        r1.font.color.rgb = DARK_TEXT

    p_sp = doc.add_paragraph()
    p_sp.paragraph_format.space_after = Pt(12)

def build_full_documentation():
    doc = Document()
    
    # Page setup A4
    for section in doc.sections:
        section.top_margin = Inches(0.8)
        section.bottom_margin = Inches(0.8)
        section.left_margin = Inches(0.8)
        section.right_margin = Inches(0.8)

    # 1. Title Banner
    add_header_banner(
        doc,
        "BÁO CÁO KẾT QUẢ TRIỂN KHAI VÀ KIỂM THỬ DỰ ÁN ASSIGNMENT 01",
        "MÔN HỌC: SBA301 - XÂY DỰNG ỨNG DỤNG WEB DOANH NGHIỆP | ĐẠI HỌC FPT"
    )

    # Student metadata table
    tbl_meta = doc.add_table(rows=5, cols=2)
    tbl_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
    meta_info = [
        ("Họ và tên sinh viên:", "Trần Quốc Thái"),
        ("Mã số sinh viên (MSSV):", "CE190243"),
        ("Lớp học phần:", "SE1910"),
        ("Tên dự án nộp:", "TranQuocThai_SE1910"),
        ("Hệ thống triển khai:", "FUNewsManagementSystem (Single Page Application - ReactJS & Vite)"),
    ]
    for i, (k, v) in enumerate(meta_info):
        r = tbl_meta.rows[i]
        c0, c1 = r.cells[0], r.cells[1]
        c0.width, c1.width = Inches(2.3), Inches(4.2)
        set_cell_background(c0, "F1F5F9")
        set_cell_background(c1, "FFFFFF")
        set_cell_margins(c0, 60, 60, 100, 100)
        set_cell_margins(c1, 60, 60, 100, 100)
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(k)
        r0.font.bold = True
        r0.font.size = Pt(10)
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(v)
        r1.font.size = Pt(10)

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    # --- PHẦN I ---
    h1 = doc.add_heading("PHẦN I: TỔNG QUAN ĐỀ TÀI, YÊU CẦU VÀ GIẢI PHÁP KỸ THUẬT", level=1)
    for r in h1.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(6)
    p.add_run("1. Đề tài làm về gì?\n").font.bold = True
    p.add_run(
        "Dự án Assignment 01 là hệ thống quản lý tin tức nội bộ mang tên FUNewsManagementSystem dành cho môi trường trường đại học. "
        "Mục tiêu là xây dựng một ứng dụng Single Page Application (SPA) hoàn chỉnh phía người dùng (Client-side) cho phép người quản trị "
        "(Administrator) tổ chức, quản lý phân loại tin tức (Category), đăng tải và biên tập các bài báo (News Articles), "
        "quản lý danh sách các tài khoản người dùng nội bộ (Users - Admin & Staff), xem dashboard tổng quan số liệu và tùy chỉnh hồ sơ."
    )

    p2 = doc.add_paragraph()
    p2.paragraph_format.space_after = Pt(6)
    p2.add_run("2. Các yêu cầu chi tiết của đề bài:\n").font.bold = True
    reqs = [
        ("R01 - Nền tảng công nghệ:", "Phát triển bằng ReactJS 19 kết hợp công cụ đóng gói Vite 8 hiện đại, đảm bảo tốc độ phản hồi nhanh và chuẩn SPA."),
        ("R02 - Xác thực & Đăng nhập:", "Giao diện đăng nhập kiểm tra tính hợp lệ dữ liệu (không để trống, báo sai tài khoản), xác thực đúng tài khoản Admin/Admin."),
        ("R03 - Bố cục Layout & Điều hướng:", "Layout dùng chung gồm Header (logo AI, tiêu đề trang, tên user, nút Logout) và Sidebar menu với 5 mục chức năng."),
        ("R04 - Quản lý Thể loại (Category):", "Thực hiện đầy đủ chức năng CRUD (Create, Read, Update, Delete) và Search thể loại. Bắt buộc kiểm tra trùng tên và chặn xóa khi đang có bài viết liên kết."),
        ("R05 - Quản lý Tin tức (News):", "CRUD và Search bài viết tin tức. Mỗi bài viết bắt buộc chọn danh mục (Category) đang có và tự động gắn tác giả/ngày tạo."),
        ("R06 - Quản lý Tài khoản (Users):", "CRUD và Search tài khoản người dùng. Hiển thị vai trò Admin/Staff và trạng thái Active/Inactive, không cho phép trùng username."),
        ("R07 - Giao diện Hộp thoại tương tác:", "Toàn bộ form Thêm mới và Chỉnh sửa phải mở dạng Popup Modal. Thao tác Xóa phải qua Dialog xác nhận (Confirm Dialog)."),
        ("R08 - Lưu trữ dữ liệu & Trạng thái:", "Dữ liệu được duy trì bền vững trong trình duyệt thông qua localStorage, tự động nạp dữ liệu mẫu ban đầu khi khởi chạy."),
    ]
    for r_title, r_desc in reqs:
        p_item = doc.add_paragraph(style='List Bullet')
        p_item.paragraph_format.space_after = Pt(3)
        r_t = p_item.add_run(r_title + " ")
        r_t.font.bold = True
        p_item.add_run(r_desc)

    p3 = doc.add_paragraph()
    p3.paragraph_format.space_after = Pt(6)
    p3.add_run("3. Các giải pháp kỹ thuật đã áp dụng trong dự án:\n").font.bold = True
    solutions = [
        ("Kiến trúc Component hướng module:", "Tách bạch giao diện thành các thành phần tái sử dụng cao: Header, Sidebar, Modal, ConfirmDialog, StatusBadge. Giúp code gọn gàng, dễ bảo trì và mở rộng."),
        ("Quản lý trạng thái & Lưu trữ bền vững (State & localStorage):", "Sử dụng Custom Hook useLocalStorage kết hợp useState và useEffect để đồng bộ hóa 2 chiều tự động giữa React State và window.localStorage của trình duyệt."),
        ("Kiểm soát tính toàn vẹn dữ liệu (Referential Integrity):", "Trước khi xóa bất kỳ thể loại nào, hệ thống duyệt mảng news để kiểm tra liên kết categoryId. Nếu tồn tại bài viết, hệ thống chặn ngay và hiển thị thông báo lỗi thân thiện."),
        ("Giao diện UI/UX trực quan & CSS thuần:", "Ứng dụng hệ thống Design Tokens và CSS Variables nhất quán, giao diện dạng thẻ (Cards) hiện đại, bố cục Flexbox/Grid chuẩn mực, các huy hiệu màu sắc trực quan cho trạng thái."),
    ]
    for s_title, s_desc in solutions:
        p_item = doc.add_paragraph(style='List Bullet')
        p_item.paragraph_format.space_after = Pt(3)
        s_t = p_item.add_run(s_title + " ")
        s_t.font.bold = True
        p_item.add_run(s_desc)

    add_callout(
        doc,
        "Đặc điểm kiến trúc của Assignment 01",
        "Dự án Assignment 01 được thiết kế tuân thủ 100% yêu cầu đề bài: chạy độc lập ở Client-side bằng ReactJS và Mock Data, "
        "không yêu cầu Spring Boot, REST API backend hay SQL Server thật (phần backend thật và API được thực hiện tại Assignment 02)."
    )

    # --- PHẦN II ---
    doc.add_page_break()
    h2 = doc.add_heading("PHẦN II: BÁO CÁO KẾT QUẢ KIỂM THỬ TỪNG CHỨC NĂNG VÀ MÃ NGUỒN", level=1)
    for r in h2.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    p_intro = doc.add_paragraph()
    p_intro.paragraph_format.space_after = Pt(8)
    p_intro.add_run(
        "Mỗi chức năng dưới đây được kiểm thử toàn diện trên trình duyệt với hình ảnh thực tế, mô tả cơ chế hoạt động, "
        "dấu hiệu nhận biết kết quả đúng, cùng vị trí mã nguồn và giải thích chi tiết chức năng của các hàm xử lý."
    )

    steps = [
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

    for img, title, works, result, loc, fn in steps:
        add_screenshot_block(doc, img, title, works, result, loc, fn)

    # --- PHẦN III ---
    doc.add_page_break()
    h3 = doc.add_heading("PHẦN III: BẢNG TỔNG KẾT MA TRẬN YÊU CẦU VÀ KIỂM THỬ (TEST MATRIX)", level=1)
    for r in h3.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    p_tbl = doc.add_paragraph()
    p_tbl.paragraph_format.space_after = Pt(8)
    p_tbl.add_run("Bảng đối chiếu toàn bộ các yêu cầu của đề bài (Traceability Matrix) và kết quả kiểm thử thực tế:")

    tbl_matrix = doc.add_table(rows=1, cols=4)
    tbl_matrix.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_matrix.autofit = False
    
    headers = ["Mã Yêu Cầu", "Nội Dung Yêu Cầu Đề Bài", "Thành Phần Code & Bằng Chứng UI", "Kết Quả Kiểm Thử"]
    col_w = [Inches(1.1), Inches(2.2), Inches(2.2), Inches(1.1)]
    
    hdr_cells = tbl_matrix.rows[0].cells
    for j, h_text in enumerate(headers):
        hdr_cells[j].width = col_w[j]
        set_cell_background(hdr_cells[j], "1E3A8A")
        set_cell_margins(hdr_cells[j], 100, 100, 100, 100)
        p = hdr_cells[j].paragraphs[0]
        r = p.add_run(h_text)
        r.font.bold = True
        r.font.size = Pt(9.5)
        r.font.color.rgb = RGBColor(255, 255, 255)

    matrix_data = [
        ("R01", "Single Page Application với ReactJS và Vite", "package.json, vite.config.js, App.jsx", "ĐẠT (PASS 100%)"),
        ("R02", "Trang đăng nhập có validation rỗng và sai credential", "src/pages/LoginPage.jsx", "ĐẠT (PASS 100%)"),
        ("R03", "Đăng nhập tài khoản Admin/Admin vào Dashboard", "Auth state trong App.jsx, LoginPage.jsx", "ĐẠT (PASS 100%)"),
        ("R04", "Logo thương hiệu tạo bằng công cụ AI", "public/assets/funews-logo.png, Header, Sidebar", "ĐẠT (PASS 100%)"),
        ("R05", "Bố cục Header và Sidebar dùng chung", "src/components/Header.jsx, Sidebar.jsx", "ĐẠT (PASS 100%)"),
        ("R06", "Menu thanh điều hướng đủ 5 mục chức năng", "Sidebar.jsx (Dashboard, Category, News, Users, Settings)", "ĐẠT (PASS 100%)"),
        ("R07", "CRUD & Search cho Thể loại (Category)", "src/pages/CategoryPage.jsx, StatusBadge.jsx", "ĐẠT (PASS 100%)"),
        ("R08", "CRUD & Search cho Tin tức (News)", "src/pages/NewsPage.jsx (liên kết Category)", "ĐẠT (PASS 100%)"),
        ("R09", "CRUD & Search cho Tài khoản Người dùng (Users)", "src/pages/UsersPage.jsx (Role Admin/Staff)", "ĐẠT (PASS 100%)"),
        ("R10", "Giao diện Thêm mới & Cập nhật dùng Popup Modal", "src/components/Modal.jsx", "ĐẠT (PASS 100%)"),
        ("R11", "Giao diện Xóa có hộp thoại xác nhận (Confirm Dialog)", "src/components/ConfirmDialog.jsx", "ĐẠT (PASS 100%)"),
        ("R12", "Ràng buộc toàn vẹn dữ liệu (Chặn xóa Category có News)", "handleDelete trong CategoryPage.jsx", "ĐẠT (PASS 100%)"),
        ("R13", "Lưu trữ dữ liệu bền vững trong trình duyệt", "src/hooks/useLocalStorage.js", "ĐẠT (PASS 100%)"),
    ]

    for m_row in matrix_data:
        row = tbl_matrix.add_row()
        for c_idx, val in enumerate(m_row):
            cell = row.cells[c_idx]
            cell.width = col_w[c_idx]
            set_cell_background(cell, "F8FAFC" if c_idx == 0 else "FFFFFF")
            set_cell_margins(cell, 70, 70, 100, 100)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.size = Pt(9)
            if c_idx == 3:
                r.font.bold = True
                r.font.color.rgb = RGBColor(16, 120, 60)
            elif c_idx == 0:
                r.font.bold = True
                r.font.color.rgb = PRIMARY_COLOR

    out_path = os.path.abspath("noteTT/BAO_CAO_CHI_TIET_ASSIGNMENT01_TranQuocThai_SE1910.docx")
    doc.save(out_path)
    print(f"File 1 created successfully at: {out_path}")

def build_visual_summary_documentation():
    doc = Document()
    
    # Page setup A4
    for section in doc.sections:
        section.top_margin = Inches(0.8)
        section.bottom_margin = Inches(0.8)
        section.left_margin = Inches(0.8)
        section.right_margin = Inches(0.8)

    # 1. Header Banner
    add_header_banner(
        doc,
        "TÀI LIỆU MINH CHỨNG HÌNH ẢNH & LUỒNG THỰC THI CHỨC NĂNG",
        "DỰ ÁN: TranQuocThai_SE1910 (ASSIGNMENT 01 - FUNewsManagementSystem)"
    )

    p_intro = doc.add_paragraph()
    p_intro.paragraph_format.space_after = Pt(10)
    p_intro.add_run(
        "Tài liệu này tổng hợp toàn bộ các ảnh chụp màn hình kiểm thử thực tế của từng bước chức năng, kèm theo "
        "mô tả ngắn gọn về luồng thực hiện và dấu hiệu nhận biết kết quả đạt chuẩn."
    )

    visual_items = [
        # Xác thực & Đăng nhập
        ("01_login_initial.png", "Bước 1.1: Giao diện Đăng nhập ban đầu", 
         "Kết quả đúng: Giao diện hiển thị đầy đủ logo thương hiệu, form đăng nhập rõ ràng và tài khoản demo Admin/Admin."),
        ("02_login_empty_error.png", "Bước 1.2: Bắt lỗi khi bỏ trống Username hoặc Password", 
         "Kết quả đúng: Xuất hiện thanh thông báo đỏ 'Username and password are required.', ngăn không cho đăng nhập rỗng."),
        ("03_login_wrong_credentials.png", "Bước 1.3: Bắt lỗi khi nhập sai thông tin tài khoản", 
         "Kết quả đúng: Xuất hiện thông báo đỏ 'Incorrect username or password.', người dùng được giữ lại trang để nhập lại."),
        ("04_login_success_dashboard.png", "Bước 1.4: Đăng nhập thành công với tài khoản Admin", 
         "Kết quả đúng: Hệ thống xác thực thành công và tự động chuyển hướng người dùng vào màn hình Tổng quan Dashboard."),
        ("05_layout_sidebar_header.png", "Bước 1.5: Bố cục Header và Sidebar dùng chung", 
         "Kết quả đúng: Header hiển thị thông tin Admin kèm nút Logout, Sidebar hiển thị đủ 5 menu với Dashboard đang kích hoạt."),
        ("06_dashboard_stats_and_news.png", "Bước 1.6: Thống kê số liệu và danh sách tin tức mới nhất trên Dashboard", 
         "Kết quả đúng: 3 thẻ thống kê hiển thị chính xác tổng số bản ghi và số lượng Active, bên dưới là danh sách 3 bài viết mới nhất."),

        # Quản lý Thể loại
        ("07_category_list.png", "Bước 2.1: Hiển thị danh sách Thể loại (Read)", 
         "Kết quả đúng: Bảng hiển thị đầy đủ 3 thể loại ban đầu với ID, Tên, Badge trạng thái màu xanh/xám và nút Edit, Delete."),
        ("08_category_create_modal.png", "Bước 2.2: Mở Popup Modal thêm mới Thể loại", 
         "Kết quả đúng: Modal popup 'Add category' mở lên trên nền mờ, có ô nhập tên và dropdown chọn trạng thái Active/Inactive."),
        ("09_category_create_success.png", "Bước 2.3: Thêm mới Thể loại thành công", 
         "Kết quả đúng: Thể loại mới 'Sports' xuất hiện trong bảng với ID #4 tự tăng và badge trạng thái 'Active' màu xanh lá."),
        ("10_category_validation_duplicate.png", "Bước 2.4: Bắt lỗi chống trùng lặp tên Thể loại", 
         "Kết quả đúng: Modal hiển thị cảnh báo đỏ 'Category name must be unique.', ngăn chặn việc tạo thể loại trùng tên."),
        ("11_category_update_modal.png", "Bước 2.5: Mở Popup Modal chỉnh sửa Thể loại", 
         "Kết quả đúng: Modal 'Update category' mở ra với thông tin cũ được điền sẵn, cho phép người dùng thay đổi tên và trạng thái."),
        ("12_category_update_success.png", "Bước 2.6: Cập nhật Thể loại thành công", 
         "Kết quả đúng: Dòng ID #4 được cập nhật thành tên 'Sports & Recreation' và badge trạng thái chuyển sang 'Inactive' màu xám."),
        ("13_category_delete_prevent_in_use.png", "Bước 2.7: Chặn xóa Thể loại đang có bài viết liên kết", 
         "Kết quả đúng: Xuất hiện thông báo đỏ 'Cannot delete “Technology” because it is used by a news article.', bảo vệ dữ liệu."),
        ("14_category_delete_confirm_dialog.png", "Bước 2.8: Hộp thoại xác nhận trước khi xóa Thể loại", 
         "Kết quả đúng: Hộp thoại Confirm Dialog cảnh báo hỏi người dùng có chắc chắn muốn xóa thể loại hay không."),
        ("15_category_delete_success.png", "Bước 2.9: Xóa Thể loại thành công", 
         "Kết quả đúng: Thể loại 'Sports & Recreation' đã được xóa hoàn toàn khỏi bảng, danh sách trở về 3 danh mục gốc."),
        ("16_category_search_success.png", "Bước 2.10: Tìm kiếm Thể loại theo từ khóa", 
         "Kết quả đúng: Bảng lọc thời gian thực ra đúng thể loại 'Education' khi nhập từ khóa 'Edu'."),
        ("17_category_search_empty.png", "Bước 2.11: Trạng thái không tìm thấy kết quả Thể loại", 
         "Kết quả đúng: Hiển thị thông báo trạng thái rỗng 'No matching categories' thân thiện khi tìm từ khóa không tồn tại."),

        # Quản lý Tin tức
        ("18_news_list.png", "Bước 3.1: Hiển thị danh sách Tin tức (Read)", 
         "Kết quả đúng: Bảng hiển thị 3 bài viết tin tức mẫu với tiêu đề, nội dung tóm tắt, tên danh mục liên kết và tác giả."),
        ("19_news_create_modal.png", "Bước 3.2: Mở Popup Modal thêm mới Tin tức", 
         "Kết quả đúng: Modal 'Add news article' mở ra với các trường tiêu đề, nội dung, chọn thể loại, trạng thái và tác giả."),
        ("20_news_create_success.png", "Bước 3.3: Thêm mới bài viết Tin tức thành công", 
         "Kết quả đúng: Bài viết mới 'AI Innovation Summit 2026' xuất hiện trong bảng với ID #4 và danh mục 'Technology'."),
        ("21_news_validation_empty.png", "Bước 3.4: Bắt lỗi khi bỏ trống tiêu đề hoặc nội dung tin tức", 
         "Kết quả đúng: Modal hiển thị cảnh báo đỏ 'Title and content are required.', không cho phép submit form rỗng."),
        ("22_news_update_modal.png", "Bước 3.5: Mở Popup Modal chỉnh sửa bài viết Tin tức", 
         "Kết quả đúng: Modal 'Update news article' mở ra cho phép đổi tiêu đề bài viết và chuyển đổi danh mục liên kết."),
        ("23_news_update_success.png", "Bước 3.6: Cập nhật bài viết Tin tức thành công", 
         "Kết quả đúng: Bài viết cập nhật tiêu đề mới 'AI & Robotics Summit 2026 - Extended' và danh mục chuyển sang 'Education'."),
        ("24_news_delete_confirm_dialog.png", "Bước 3.7: Hộp thoại xác nhận trước khi xóa bài viết Tin tức", 
         "Kết quả đúng: Hộp thoại Confirm Dialog hiển thị đúng tiêu đề bài viết cần xóa kèm nút Cancel và Delete."),
        ("25_news_delete_success.png", "Bước 3.8: Xóa bài viết Tin tức thành công", 
         "Kết quả đúng: Bài viết vừa tạo đã bị xóa vĩnh viễn khỏi danh sách, bảng tin tức quay về 3 bài viết gốc."),
        ("26_news_search_success.png", "Bước 3.9: Tìm kiếm bài viết Tin tức theo từ khóa", 
         "Kết quả đúng: Bảng lọc ra chính xác bài viết chứa từ khóa 'orientation' trong tiêu đề."),
        ("27_news_search_empty.png", "Bước 3.10: Trạng thái không có kết quả tìm kiếm Tin tức", 
         "Kết quả đúng: Hiển thị thông báo rỗng 'No matching news articles' khi không tìm thấy bài viết phù hợp."),

        # Quản lý Người dùng
        ("28_users_list.png", "Bước 4.1: Hiển thị danh sách Tài khoản Người dùng (Read)", 
         "Kết quả đúng: Bảng hiển thị 3 tài khoản ban đầu với nhãn vai trò Admin/Staff và badge trạng thái Active/Inactive."),
        ("29_users_create_modal.png", "Bước 4.2: Mở Popup Modal thêm mới Tài khoản", 
         "Kết quả đúng: Modal 'Add user account' mở ra với các trường nhập Username, Mật khẩu, chọn Role và chọn Status."),
        ("30_users_create_success.png", "Bước 4.3: Thêm mới Tài khoản Người dùng thành công", 
         "Kết quả đúng: Tài khoản mới 'editor_thai' xuất hiện trong bảng với ID #4, vai trò Staff và trạng thái Active."),
        ("31_users_validation_duplicate.png", "Bước 4.4: Bắt lỗi chống trùng lặp Username", 
         "Kết quả đúng: Modal hiển thị cảnh báo đỏ 'Username must be unique.', ngăn tạo tài khoản trùng tên đã có."),
        ("32_users_update_modal.png", "Bước 4.5: Mở Popup Modal chỉnh sửa Tài khoản Người dùng", 
         "Kết quả đúng: Modal 'Update user account' hiển thị cho phép điều chỉnh vai trò và trạng thái tài khoản."),
        ("33_users_update_success.png", "Bước 4.6: Cập nhật Tài khoản Người dùng thành công", 
         "Kết quả đúng: Tài khoản 'editor_thai' được cập nhật vai trò mới thành 'Admin' và trạng thái chuyển sang 'Inactive'."),
        ("34_users_delete_confirm_dialog.png", "Bước 4.7: Hộp thoại xác nhận trước khi xóa Tài khoản", 
         "Kết quả đúng: Hộp thoại Confirm Dialog cảnh báo hỏi người dùng có chắc chắn muốn xóa tài khoản 'editor_thai'."),
        ("35_users_delete_success.png", "Bước 4.8: Xóa Tài khoản Người dùng thành công", 
         "Kết quả đúng: Tài khoản 'editor_thai' đã bị xóa khỏi hệ thống, danh sách người dùng trở về 3 tài khoản ban đầu."),
        ("36_users_search_success.png", "Bước 4.9: Tìm kiếm Tài khoản Người dùng theo Username", 
         "Kết quả đúng: Bảng lọc ra chính xác 2 tài khoản 'staff01' và 'staff02' khi nhập từ khóa 'staff'."),
        ("37_users_search_empty.png", "Bước 4.10: Trạng thái không có kết quả tìm kiếm Người dùng", 
         "Kết quả đúng: Hiển thị thông báo rỗng 'No matching users' khi nhập từ khóa tìm kiếm không tồn tại."),

        # Cài đặt & Đăng xuất
        ("38_settings_profile.png", "Bước 5.1: Trang Cài đặt & Thông tin Hồ sơ tài khoản", 
         "Kết quả đúng: Hiển thị đúng thông tin hồ sơ của Admin (Username: Admin, Role: Admin, Storage: Browser localStorage)."),
        ("39_logout_action.png", "Bước 5.2: Đăng xuất khỏi hệ thống an toàn", 
         "Kết quả đúng: Phiên làm việc bị xóa, giao diện quản trị đóng lại và người dùng được đưa về trang Đăng nhập."),
    ]

    for img_file, step_title, result_desc in visual_items:
        img_path = os.path.join("noteTT/screenshots_a1", img_file)
        
        h = doc.add_heading(step_title, level=2)
        h.paragraph_format.space_before = Pt(14)
        h.paragraph_format.space_after = Pt(4)
        for r in h.runs:
            r.font.color.rgb = PRIMARY_COLOR
            r.font.size = Pt(11.5)
            r.font.bold = True

        if os.path.exists(img_path):
            p_img = doc.add_paragraph()
            p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
            p_img.paragraph_format.space_before = Pt(2)
            p_img.paragraph_format.space_after = Pt(4)
            p_img.add_run().add_picture(img_path, width=Inches(6.0))

        # 1-sentence description box
        tbl = doc.add_table(rows=1, cols=1)
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        cell = tbl.cell(0, 0)
        set_cell_background(cell, "F0FDF4") # Light green tint
        set_cell_margins(cell, 80, 80, 140, 140)
        p_box = cell.paragraphs[0]
        p_box.paragraph_format.space_after = Pt(0)
        r_box_title = p_box.add_run("✔️ ")
        r_box_desc = p_box.add_run(result_desc)
        r_box_desc.font.size = Pt(9.5)
        r_box_desc.font.bold = True
        r_box_desc.font.color.rgb = RGBColor(22, 101, 52) # Dark green

        doc.add_paragraph().paragraph_format.space_after = Pt(8)

    out_path = os.path.abspath("noteTT/HINH_ANH_LUONG_CHUC_NANG_ASSIGNMENT01_TranQuocThai_SE1910.docx")
    doc.save(out_path)
    print(f"File 2 created successfully at: {out_path}")

if __name__ == "__main__":
    build_full_documentation()
    build_visual_summary_documentation()
