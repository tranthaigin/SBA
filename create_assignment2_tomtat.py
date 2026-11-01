import os
import sys
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import parse_xml
from docx.oxml.ns import nsdecls

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

from generate_docs_helper import (
    set_cell_background, set_cell_margins, add_header_banner,
    add_callout, add_summary_step,
    PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, DARK_TEXT, MUTED_TEXT, SUCCESS_COLOR
)
from generate_combined_docs import a2_steps, add_a2_matrix_table

def build_assignment2_tomtat_doc():
    doc = Document()
    
    # Thiết lập lề trang chuẩn báo cáo (0.75 inch)
    for s in doc.sections:
        s.top_margin = Inches(0.75)
        s.bottom_margin = Inches(0.75)
        s.left_margin = Inches(0.75)
        s.right_margin = Inches(0.75)

    # =========================================================================
    # HEADER BANNER & THÔNG TIN SINH VIÊN
    # =========================================================================
    add_header_banner(
        doc,
        "TÀI LIỆU TÓM TẮT TRỌNG TÂM BÁO CÁO KỸ THUẬT ASSIGNMENT 02",
        "HỌC PHẦN SBA301 - XÂY DỰNG ỨNG DỤNG DOANH NGHIỆP | ĐẠI HỌC FPT"
    )

    # Bảng thông tin sinh viên & đề tài
    tbl_meta = doc.add_table(rows=5, cols=2)
    tbl_meta.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_meta.autofit = False
    meta_info = [
        ("Sinh viên thực hiện:", "Trần Quốc Thái (MSSV: CE190243 - Lớp: SE1910)"),
        ("Tên dự án Assignment 02:", "A2TranQuocThai_SE1910 — FUNewsManagementSystem"),
        ("Mô hình kiến trúc:", "Kiến trúc 3 lớp (3-Tier) RESTful Web Service kết hợp ReactJS SPA"),
        ("Công nghệ sử dụng:", "Spring Boot 3 (Java 21), Spring Data JPA, MS SQL Server, ReactJS, Axios, Bootstrap 5"),
        ("Mục đích tài liệu:", "Tóm tắt đầy đủ Yêu cầu đề bài, Giải pháp kỹ thuật, Ý nghĩa dự án và toàn bộ 33 ảnh minh chứng kiểm thử các chức năng"),
    ]
    col_w = [Inches(2.2), Inches(4.8)]
    for i, (k, v) in enumerate(meta_info):
        r = tbl_meta.rows[i]
        c0, c1 = r.cells[0], r.cells[1]
        c0.width, c1.width = col_w[0], col_w[1]
        set_cell_background(c0, "F1F5F9")
        set_cell_background(c1, "FFFFFF")
        set_cell_margins(c0, 45, 45, 90, 90)
        set_cell_margins(c1, 45, 45, 90, 90)
        p0 = c0.paragraphs[0]
        r0 = p0.add_run(k)
        r0.font.bold = True
        r0.font.size = Pt(9.5)
        r0.font.color.rgb = PRIMARY_COLOR
        p1 = c1.paragraphs[0]
        r1 = p1.add_run(v)
        r1.font.size = Pt(9.5)

    doc.add_paragraph().paragraph_format.space_after = Pt(10)

    # =========================================================================
    # PHẦN 1: YÊU CẦU ĐỀ BÀI (REQUIREMENTS)
    # =========================================================================
    h1 = doc.add_heading("1. Yêu cầu (Requirements)", level=1)
    for r in h1.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    p_req_intro = doc.add_paragraph()
    p_req_intro.paragraph_format.space_after = Pt(6)
    p_req_intro.add_run(
        "Dưới đây là bảng tổng hợp chi tiết và đầy đủ toàn bộ các yêu cầu chức năng, phi chức năng và ràng buộc nghiệp vụ "
        "được quy định trong file đề bài chính thức của Assignment 02 (Assignment 02 SBA301 - Implement CRUD Application with RESTful Web Service and ReactJS):"
    )

    # Bảng Requirements: STT | Yêu cầu | Mô tả
    tbl_req = doc.add_table(rows=1, cols=3)
    tbl_req.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_req.autofit = False
    headers_req = ["STT", "Yêu cầu (Requirements)", "Mô tả chi tiết (Description)"]
    col_w_req = [Inches(0.6), Inches(2.2), Inches(4.2)]

    for j, h_text in enumerate(headers_req):
        c = tbl_req.rows[0].cells[j]
        c.width = col_w_req[j]
        set_cell_background(c, "1E3A8A")
        set_cell_margins(c, 70, 70, 90, 90)
        p = c.paragraphs[0]
        r = p.add_run(h_text)
        r.font.bold = True
        r.font.size = Pt(9.5)
        r.font.color.rgb = RGBColor(255, 255, 255)

    requirements_data = [
        (
            "1",
            "Public News Reading & Search",
            "Độc giả vãng lai (Public) không cần đăng nhập vẫn xem được danh sách tin tức kích hoạt (newsStatus = active/1), "
            "tìm kiếm bài viết theo từ khóa và mở hộp thoại xem toàn văn chi tiết bài báo."
        ),
        (
            "2",
            "Authentication & Session",
            "Thành viên (Staff) và Quản trị viên (Admin) đăng nhập bằng Email và Password. "
            "Quản lý phiên làm việc tập trung bằng HttpSession; hỗ trợ đăng xuất hủy phiên an toàn."
        ),
        (
            "3",
            "Role-Based Access Control",
            "Phân quyền nghiêm ngặt 2 vai trò: Admin (Role = 1) và Staff (Role = 2). "
            "Backend dùng Interceptor chặn bắt request trái phép (401/403); Frontend ẩn/hiện menu điều hướng tương ứng."
        ),
        (
            "4",
            "Category Management (Staff)",
            "Nhân viên Staff thực hiện CRUD (Xem, Thêm, Sửa, Xóa) và Tìm kiếm danh mục tin tức. "
            "Hỗ trợ các trường Category ID, Name, Description, Parent Category ID và trạng thái Active/Inactive."
        ),
        (
            "5",
            "Category Deletion Rule (Ràng buộc)",
            "Chặn tuyệt đối việc xóa Thể loại nếu thể loại đó đang có bài viết tin tức liên kết. "
            "Chỉ cho phép xóa khi danh mục không chứa bất kỳ bài báo nào."
        ),
        (
            "6",
            "News Management (Staff)",
            "Nhân viên Staff thực hiện CRUD (Xem, Thêm, Sửa, Xóa) và Tìm kiếm bài viết tin tức. "
            "Mỗi bài viết gồm: Title, Headline, Content, Category, Nguồn tin (News Source), Trạng thái (Active/Inactive)."
        ),
        (
            "7",
            "Tags & Many-to-Many Relationship",
            "Một bài viết có thể gắn nhiều Thẻ Tags và một Thẻ có thể thuộc nhiều bài viết (quan hệ Many-to-Many). "
            "Form cho phép chọn linh hoạt nhiều thẻ Tags dạng nút bấm trực quan khi tạo hoặc chỉnh sửa."
        ),
        (
            "8",
            "Auto Metadata & Creator Audit",
            "Tự động lấy thông tin nhân viên đăng nhập từ Session gán làm Tác giả (createdBy); "
            "tự động ghi nhận ngày tạo (createdDate) và cập nhật thời gian chỉnh sửa (modifiedDate = LocalDateTime.now())."
        ),
        (
            "9",
            "Staff Profile Management",
            "Nhân viên Staff xem và cập nhật thông tin hồ sơ cá nhân của chính mình (Họ và tên, Email) "
            "và hỗ trợ đổi mật khẩu mới thuận tiện, an toàn."
        ),
        (
            "10",
            "Staff News History",
            "Màn hình riêng (My News History) giúp nhân viên Staff theo dõi toàn bộ danh sách các bài viết "
            "do chính mình xuất bản theo thứ tự thời gian giảm dần."
        ),
        (
            "11",
            "Account Management (Admin)",
            "Quản trị viên Admin thực hiện CRUD (Xem, Thêm, Sửa, Xóa) và Tìm kiếm tài khoản người dùng hệ thống. "
            "Cấp phát tài khoản mới, phân quyền vai trò Staff (Role 2) hoặc Admin (Role 1)."
        ),
        (
            "12",
            "Account Deletion Rule (Ràng buộc)",
            "Chặn tuyệt đối việc xóa Tài khoản người dùng nếu tài khoản đó đã từng tạo bất kỳ bài viết tin tức nào "
            "trong hệ thống, nhằm bảo vệ toàn vẹn lịch sử tác giả bài báo."
        ),
        (
            "13",
            "Email Uniqueness Constraint",
            "Bắt lỗi validation chống trùng lặp Email: Không cho phép tạo tài khoản có địa chỉ Email đã tồn tại "
            "trong cơ sở dữ liệu."
        ),
        (
            "14",
            "Dialogs, Modals & Confirmations",
            "Mọi thao tác Thêm mới (Create) và Chỉnh sửa (Update) bắt buộc hiển thị dưới dạng Popup Dialog/Modal. "
            "Mọi thao tác Xóa (Delete) bắt buộc phải có hộp thoại xác nhận (Confirm Dialog) trước khi thực thi."
        ),
        (
            "15",
            "3-Layer Architecture & REST API",
            "Backend triển khai kiến trúc 3 lớp chuẩn mực (Controller - Service - Repository) bằng Spring Boot 3, "
            "sử dụng Spring Data JPA kết nối cơ sở dữ liệu quan hệ Microsoft SQL Server."
        ),
    ]

    for req_row in requirements_data:
        row = tbl_req.add_row()
        for c_idx, val in enumerate(req_row):
            cell = row.cells[c_idx]
            cell.width = col_w_req[c_idx]
            set_cell_background(cell, "F8FAFC" if c_idx == 0 else "FFFFFF")
            set_cell_margins(cell, 55, 55, 80, 80)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.size = Pt(9)
            if c_idx == 0:
                p.alignment = WD_ALIGN_PARAGRAPH.CENTER
                r.font.bold = True
                r.font.color.rgb = PRIMARY_COLOR
            elif c_idx == 1:
                r.font.bold = True
                r.font.color.rgb = DARK_TEXT

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    # =========================================================================
    # PHẦN 2: GIẢI PHÁP ĐỀ XUẤT (PROPOSED SOLUTION) & Ý NGHĨA DỰ ÁN
    # =========================================================================
    h2 = doc.add_heading("2. Giải pháp (Proposed Solution) và Ý nghĩa của dự án", level=1)
    for r in h2.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    # 2.1 Bảng Giải pháp Kỹ thuật
    h2_1 = doc.add_heading("2.1 Bảng Giải pháp Kỹ thuật áp dụng (Technical Solutions)", level=2)
    for r in h2_1.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    tbl_sol = doc.add_table(rows=1, cols=2)
    tbl_sol.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_sol.autofit = False
    headers_sol = ["Thành phần (Component)", "Giải pháp kỹ thuật đã triển khai (Proposed Solution)"]
    col_w_sol = [Inches(2.2), Inches(4.8)]

    for j, h_text in enumerate(headers_sol):
        c = tbl_sol.rows[0].cells[j]
        c.width = col_w_sol[j]
        set_cell_background(c, "1E3A8A")
        set_cell_margins(c, 70, 70, 90, 90)
        p = c.paragraphs[0]
        r = p.add_run(h_text)
        r.font.bold = True
        r.font.size = Pt(9.5)
        r.font.color.rgb = RGBColor(255, 255, 255)

    solutions_data = [
        (
            "Backend Architecture",
            "Spring Boot 3 (Java 21) theo mô hình 3 lớp chuẩn mực (3-Layer Architecture): "
            "Controller tiếp nhận HTTP RESTful, Service xử lý nghiệp vụ & validation ràng buộc, Repository truy xuất dữ liệu."
        ),
        (
            "Database & Driver",
            "Microsoft SQL Server, kết nối bằng official JDBC Driver (com.microsoft.sqlserver.jdbc.SQLServerDriver). "
            "Cơ sở dữ liệu đặt tên: A2TranQuocThai_SE1910."
        ),
        (
            "ORM & Data Persistence",
            "Spring Data JPA & Hibernate ORM tự động ánh xạ Entity (Category, NewsArticle, SystemAccount, Tag) "
            "và tự động quản lý bảng liên kết nhiều-nhiều NewsTag; tự sinh khóa chính."
        ),
        (
            "Frontend Architecture",
            "Single Page Application (SPA) phát triển bằng ReactJS 19 kết hợp Vite 6 cho tốc độ build và Hot Reload tức thì."
        ),
        (
            "User Interface (UI)",
            "Thư viện React-Bootstrap kết hợp tùy biến Bootstrap 5, giao diện hiện đại, responsive đa kích thước màn hình."
        ),
        (
            "Authentication & Security",
            "Cơ chế Session-Based Authentication qua HttpSession; kết hợp HandlerInterceptor (AccessConfig.java) "
            "tự động kiểm tra đăng nhập và mã vai trò (Role 1/Role 2) cho toàn bộ route /api/**."
        ),
        (
            "Client-Server API Communication",
            "Axios Client (axiosClient.js) cấu hình withCredentials: true để đồng bộ Cookie Session; "
            "cấu hình Vite Reverse Proxy trong vite.config.js chuyển tiếp /api sang cổng 8082."
        ),
        (
            "State Management",
            "React Context API (AuthContext) quản lý phiên đăng nhập toàn cục; "
            "kết hợp React useState và useEffect quản lý trạng thái tải dữ liệu và form cục bộ."
        ),
        (
            "Routing & Navigation",
            "React Router DOM v6 điều hướng mượt mà không reload trang; "
            "bảo vệ các Private Routes theo vai trò Staff và Admin; Navbar hiển thị động theo phiên."
        ),
        (
            "Forms & Modals Interaction",
            "Modal Popup của React-Bootstrap cho tất cả thao tác Thêm mới và Chỉnh sửa; "
            "hộp thoại Window Confirm cảnh báo an toàn cho thao tác Xóa dữ liệu."
        ),
        (
            "Search Functionality",
            "Tìm kiếm đa trường ở Backend qua derived query Spring Data JPA (ContainingIgnoreCase) "
            "và bộ lọc Client-Side tức thì cho trang công khai."
        ),
        (
            "Exception Handling",
            "@RestControllerAdvice tập trung (ApiExceptionHandler) bắt BusinessRuleViolationException "
            "và ResourceNotFoundException, trả về mã lỗi HTTP 400/404 JSON chuẩn RFC."
        ),
        (
            "Data Integrity Validation",
            "Tầng Service kiểm tra ràng buộc trước khi xóa: existsByCategory chặn xóa Category đang có tin; "
            "existsByCreatedById chặn xóa Account đã từng tạo tin; findByAccountEmail chặn trùng lặp email."
        ),
    ]

    for sol_row in solutions_data:
        row = tbl_sol.add_row()
        for c_idx, val in enumerate(sol_row):
            cell = row.cells[c_idx]
            cell.width = col_w_sol[c_idx]
            set_cell_background(cell, "F8FAFC" if c_idx == 0 else "FFFFFF")
            set_cell_margins(cell, 55, 55, 80, 80)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.size = Pt(9)
            if c_idx == 0:
                r.font.bold = True
                r.font.color.rgb = PRIMARY_COLOR
            else:
                r.font.color.rgb = DARK_TEXT

    doc.add_paragraph().paragraph_format.space_after = Pt(10)

    # 2.2 Ý nghĩa của dự án
    h2_2 = doc.add_heading("2.2 Ý nghĩa của dự án (Project Significance)", level=2)
    for r in h2_2.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    # Callout box ý nghĩa tổng quan
    add_callout(
        doc,
        "Tầm nhìn và giá trị cốt lõi của FUNewsManagementSystem",
        "Dự án FUNewsManagementSystem không chỉ là một bài tập thực hành kỹ thuật mà là một giải pháp chuyển đổi số "
        "toàn diện cho công tác quản trị và truyền thông tin tức nội bộ tại môi trường đại học. "
        "Hệ thống giải quyết triệt để bài toán phân quyền, bảo mật thông tin và bảo toàn dữ liệu lịch sử xuất bản của nhà trường.",
        bg_hex="EFF6FF",
        title_color=PRIMARY_COLOR
    )

    p_sig1 = doc.add_paragraph()
    p_sig1.paragraph_format.space_after = Pt(4)
    r_sig1_t = p_sig1.add_run("a) Ý nghĩa về mặt nghiệp vụ & thực tiễn (Business & Real-World Value):\n")
    r_sig1_t.font.bold = True
    r_sig1_t.font.color.rgb = DARK_TEXT
    p_sig1.add_run(
        "• Số hóa quy trình xuất bản tin tức đại học: Cung cấp luồng công việc khép kín giúp tòa soạn FUNews của trường đại học "
        "quản lý, phân loại chuyên mục, biên tập và xuất bản tin tức một cách chuyên nghiệp, nhanh chóng đến sinh viên và giảng viên.\n"
        "• Phân định quyền hạn minh bạch, an toàn: Độc giả vãng lai chỉ tiếp cận các bài báo chính thống đã được kích hoạt (Active); "
        "biên tập viên (Staff) tự do sáng tạo nội dung, chọn thẻ chủ đề và theo dõi lịch sử bài viết của chính mình; "
        "quản trị viên (Admin) nắm toàn quyền kiểm soát nhân sự và cấp phát tài khoản.\n"
        "• Bảo toàn tuyệt đối lịch sử tác giả và ấn phẩm: Các quy tắc nghiệp vụ chặt chẽ (chặn xóa Category đang có tin tức, "
        "chặn xóa Account đã từng tạo bài báo) giúp lưu trữ trọn vẹn dấu vết tác giả, ngăn chặn hoàn toàn nguy cơ xuất hiện "
        "dữ liệu mồ côi (orphan records) hoặc đứt gãy liên kết trong cơ sở dữ liệu của nhà trường."
    )

    p_sig2 = doc.add_paragraph()
    p_sig2.paragraph_format.space_after = Pt(8)
    r_sig2_t = p_sig2.add_run("b) Ý nghĩa về mặt kỹ thuật & học thuật phần mềm (Technical & Educational Value):\n")
    r_sig2_t.font.bold = True
    r_sig2_t.font.color.rgb = DARK_TEXT
    p_sig2.add_run(
        "• Làm chủ kiến trúc doanh nghiệp 3 lớp (3-Tier Enterprise Architecture): Áp dụng chuẩn mực phân tách trách nhiệm (Separation of Concerns) "
        "giữa Controller, Service và Repository, giúp mã nguồn dễ bảo trì, dễ mở rộng và sẵn sàng cho các dự án thực tế quy mô lớn.\n"
        "• Tích hợp công nghệ Fullstack hiện đại: Kết nối hoàn hảo giữa Backend Spring Boot 3 RESTful API mạnh mẽ trên Java 21 "
        "với Frontend ReactJS SPA qua Axios Client và Vite Reverse Proxy, giải quyết triệt để vấn đề CORS và đồng bộ Session Cookie.\n"
        "• Ứng dụng thành thạo ORM và quan hệ dữ liệu phức tạp: Làm chủ Spring Data JPA/Hibernate để quản lý các quan hệ dữ liệu "
        "phức tạp trong thực tế như One-to-Many (Category/Author với NewsArticle) và Many-to-Many (NewsArticle với Tag qua bảng trung gian NewsTag).\n"
        "• Nâng cao tư duy thiết kế phần mềm chuẩn công nghiệp: Tiếp cận tư duy xử lý ngoại lệ tập trung (Global Exception Handling), "
        "xác thực dữ liệu chặt chẽ từ tầng gốc và tối ưu hóa trải nghiệm người dùng (UX) với hệ thống Popup Modal và Confirm Dialog."
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    # =========================================================================
    # PHẦN 3: KẾT QUẢ KIỂM THỬ GIAO DIỆN VÀ MINH CHỨNG CHỨC NĂNG (33 BƯỚC)
    # =========================================================================
    doc.add_page_break()
    h3 = doc.add_heading("3. Kết quả kiểm thử giao diện & Minh chứng chức năng (33 Bước)", level=1)
    for r in h3.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    add_callout(
        doc,
        "Ghi chú hướng dẫn đọc phần kiểm thử",
        "Dưới đây là toàn bộ 33 bước kiểm thử thực tế của Assignment 02 trên trình duyệt với hình ảnh thực tế sắc nét. "
        "Mỗi bước được kèm theo một khung nhận định kết quả nhìn như thế nào là đúng đúng 1 câu cốt lõi giúp người đọc nắm bắt luồng chức năng nhanh nhất.",
        bg_hex="F0FDF4",
        title_color=SUCCESS_COLOR
    )

    # Render 33 steps from a2_steps
    for item in a2_steps:
        # item[0]: image path, item[1]: step title, item[6]: short summary
        add_summary_step(doc, item[0], item[1], item[6])

    # =========================================================================
    # PHẦN 4: BẢNG TỔNG KẾT MA TRẬN YÊU CẦU VÀ KIỂM THỬ (TEST MATRIX)
    # =========================================================================
    doc.add_page_break()
    h4 = doc.add_heading("4. Bảng tổng kết Ma trận yêu cầu và kiểm thử Assignment 02 (Test Matrix)", level=1)
    for r in h4.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    p_mat_intro = doc.add_paragraph()
    p_mat_intro.paragraph_format.space_after = Pt(8)
    p_mat_intro.add_run(
        "Bảng ma trận đối chiếu 14 yêu cầu kỹ thuật trọng tâm của Assignment 02 với các thành phần mã nguồn và bằng chứng giao diện thực tế:"
    )

    add_a2_matrix_table(doc)

    # Lưu tài liệu ra cả 2 nơi: noteTT/assignment2-tomtat.docx và assignment2-tomtat.docx
    out_file1 = os.path.abspath("noteTT/assignment2-tomtat.docx")
    doc.save(out_file1)
    print(f"File 1 created successfully at: {out_file1}")

    out_file2 = os.path.abspath("assignment2-tomtat.docx")
    doc.save(out_file2)
    print(f"File 2 created successfully at: {out_file2}")

if __name__ == "__main__":
    build_assignment2_tomtat_doc()
