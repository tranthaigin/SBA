import os
import sys
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

from generate_docs_helper import (
    set_cell_background, set_cell_margins,
    add_callout, add_summary_step,
    PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, DARK_TEXT, MUTED_TEXT, SUCCESS_COLOR
)
from generate_combined_docs import a2_steps, add_a2_matrix_table

def add_bullet_item(doc, title, content, level=0):
    """Thêm một mục gạch đầu dòng có định dạng rõ ràng, chữ to thoáng để dễ thuyết trình."""
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(3)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.line_spacing = 1.15
    if level == 1:
        p.paragraph_format.left_indent = Inches(0.3)
        bullet_symbol = "– "
    else:
        p.paragraph_format.left_indent = Inches(0.15)
        bullet_symbol = "• "

    r_bullet = p.add_run(bullet_symbol)
    r_bullet.font.bold = True
    r_bullet.font.size = Pt(11)
    r_bullet.font.color.rgb = PRIMARY_COLOR if level == 0 else SECONDARY_COLOR

    if title:
        r_title = p.add_run(title + ": ")
        r_title.font.bold = True
        r_title.font.size = Pt(10.5)
        r_title.font.color.rgb = DARK_TEXT

    r_content = p.add_run(content)
    r_content.font.size = Pt(10.5)
    r_content.font.color.rgb = DARK_TEXT

def build_assignment2_tomtat_doc():
    doc = Document()
    
    # Thiết lập lề trang chuẩn báo cáo (0.75 inch)
    for s in doc.sections:
        s.top_margin = Inches(0.75)
        s.bottom_margin = Inches(0.75)
        s.left_margin = Inches(0.75)
        s.right_margin = Inches(0.75)

    # =========================================================================
    # TIÊU ĐỀ LỚN DUY NHẤT: Assignment 2
    # =========================================================================
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_title.paragraph_format.space_before = Pt(10)
    p_title.paragraph_format.space_after = Pt(16)
    
    r_title = p_title.add_run("Assignment 2")
    r_title.font.bold = True
    r_title.font.size = Pt(26)
    r_title.font.color.rgb = PRIMARY_COLOR

    # =========================================================================
    # PHẦN 1: YÊU CẦU CỦA ĐỀ BÀI (REQUIREMENTS)
    # =========================================================================
    h1 = doc.add_heading("1. Yêu cầu của đề bài (Requirements)", level=1)
    h1.paragraph_format.space_before = Pt(8)
    h1.paragraph_format.space_after = Pt(8)
    for r in h1.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(15)
        r.font.bold = True

    p_intro1 = doc.add_paragraph()
    p_intro1.paragraph_format.space_after = Pt(6)
    r_intro1 = p_intro1.add_run(
        "Các yêu cầu chính được quy định nguyên văn theo file đề bài chính thức của Assignment 02 (FUNewsManagementSystem):"
    )
    r_intro1.font.italic = True
    r_intro1.font.size = Pt(10.5)
    r_intro1.font.color.rgb = MUTED_TEXT

    # 1. Public
    add_bullet_item(
        doc,
        "Độc giả công khai (Public News Access)",
        "Độc giả vãng lai không cần đăng nhập vẫn được phép truy cập danh sách tin tức ở trạng thái kích hoạt (News status = Active/1), tìm kiếm bài viết theo từ khóa và mở xem toàn văn nội dung chi tiết bài báo."
    )

    # 2. Authentication
    add_bullet_item(
        doc,
        "Xác thực & Phân quyền (Authentication & Authorization)",
        "Thành viên (Staff) và Quản trị viên (Admin) đăng nhập hệ thống bằng Email và Password. Phân quyền theo 2 vai trò: Admin (Role = 1) và Staff (Role = 2). Phiên làm việc được quản lý bằng HttpSession, hỗ trợ đăng xuất an toàn."
    )

    # 3. Category
    add_bullet_item(
        doc,
        "Quản lý Thể loại (Category Management - Dành cho Staff)",
        "Nhân viên Staff thực hiện đầy đủ các thao tác CRUD (Xem danh sách, Thêm mới, Chỉnh sửa, Xóa) và Tìm kiếm thể loại theo tên. Hỗ trợ các thông tin: ID, Name, Description, Parent Category ID, Status (Active/Inactive)."
    )
    add_bullet_item(
        doc,
        "Ràng buộc nghiệp vụ Thể loại (Category Rule)",
        "Chặn tuyệt đối việc xóa Thể loại nếu thể loại đó đang có bất kỳ bài viết tin tức nào liên kết (cannot delete if associated with existing news articles). Chỉ được xóa khi thể loại rỗng chưa có bài báo.",
        level=1
    )

    # 4. News
    add_bullet_item(
        doc,
        "Quản lý Tin tức (News Article Management - Dành cho Staff)",
        "Nhân viên Staff thực hiện CRUD (Đọc, Thêm mới, Chỉnh sửa, Xóa) và Tìm kiếm bài viết tin tức. Mỗi bài viết gồm: Title, Headline, Content, Category, Nguồn tin (News Source), Trạng thái (Active/Inactive)."
    )
    add_bullet_item(
        doc,
        "Quản lý Thẻ phân loại (Tags & Many-to-Many)",
        "Một bài viết có thể gắn nhiều Thẻ Tags và một Thẻ có thể thuộc về nhiều bài viết (quan hệ Nhiều - Nhiều). Giao diện cho phép chọn linh hoạt nhiều Tags dạng nút bấm trực quan khi tạo hoặc chỉnh sửa bài viết.",
        level=1
    )
    add_bullet_item(
        doc,
        "Tự động lưu vết tác giả & thời gian (Audit Metadata)",
        "Tự động trích xuất thông tin nhân viên đăng nhập từ Session gán làm Tác giả (createdBy); tự động ghi nhận thời gian tạo (createdDate) và cập nhật thời gian sửa đổi (modifiedDate = LocalDateTime.now()).",
        level=1
    )

    # 5. Profile & History
    add_bullet_item(
        doc,
        "Hồ sơ cá nhân & Lịch sử bài viết (Staff Profile & History)",
        "Nhân viên Staff được cung cấp 2 màn hình chuyên trách:"
    )
    add_bullet_item(
        doc,
        "My Profile",
        "Xem và chỉnh sửa thông tin hồ sơ của chính mình (Họ và tên, Email) và cho phép đổi mật khẩu mới thuận tiện, an toàn.",
        level=1
    )
    add_bullet_item(
        doc,
        "My News History",
        "Xem lại toàn bộ danh sách các bài viết do chính tài khoản của mình tạo ra theo thứ tự thời gian xuất bản giảm dần.",
        level=1
    )

    # 6. Account
    add_bullet_item(
        doc,
        "Quản lý Tài khoản người dùng (Account Management - Dành cho Admin)",
        "Quản trị viên Admin thực hiện CRUD (Xem danh sách, Thêm mới, Chỉnh sửa, Xóa) và Tìm kiếm tài khoản người dùng hệ thống. Cấp phát tài khoản mới, phân quyền vai trò Staff (Role 2) hoặc Admin (Role 1)."
    )
    add_bullet_item(
        doc,
        "Ràng buộc nghiệp vụ Tài khoản (Account Rule)",
        "Chặn tuyệt đối việc xóa Tài khoản người dùng nếu tài khoản đó đã từng tạo bất kỳ bài viết tin tức nào trong hệ thống, nhằm bảo vệ toàn vẹn lịch sử tác giả bài báo.",
        level=1
    )
    add_bullet_item(
        doc,
        "Chống trùng lặp Email (Email Validation)",
        "Kiểm tra tính duy nhất của Email khi tạo hoặc sửa tài khoản: Tuyệt đối không cho phép tạo tài khoản có Email đã tồn tại trong database.",
        level=1
    )

    # 7. UI / UX
    add_bullet_item(
        doc,
        "Quy định về Giao diện & Trải nghiệm (UI / UX)",
        "Mọi thao tác Thêm mới (Create) và Chỉnh sửa (Update) bắt buộc phải thực hiện thông qua Popup Dialog / Modal. Mọi thao tác Xóa (Delete) bắt buộc phải đi kèm hộp thoại xác nhận (Confirm Dialog) trước khi thực thi."
    )

    # 8. Architecture
    add_bullet_item(
        doc,
        "Quy định về Kiến trúc hệ thống (3-Layer Architecture & REST API)",
        "Backend triển khai mô hình 3 lớp chuẩn mực (Controller - Service - Repository) bằng Spring Boot 3 + Spring Data JPA kết nối cơ sở dữ liệu thật MS SQL Server. Frontend phát triển bằng ReactJS gọi RESTful API qua Axios Client."
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    # =========================================================================
    # PHẦN 2: GIẢI PHÁP ĐỀ XUẤT (PROPOSED SOLUTION) VÀ Ý NGHĨA CỦA DỰ ÁN
    # =========================================================================
    h2 = doc.add_heading("2. Giải pháp kỹ thuật và Ý nghĩa của dự án", level=1)
    h2.paragraph_format.space_before = Pt(8)
    h2.paragraph_format.space_after = Pt(8)
    for r in h2.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(15)
        r.font.bold = True

    # 2.1 Giải pháp kỹ thuật đã triển khai
    h2_1 = doc.add_heading("2.1. Giải pháp kỹ thuật đã triển khai (Proposed Solution)", level=2)
    h2_1.paragraph_format.space_before = Pt(6)
    h2_1.paragraph_format.space_after = Pt(6)
    for r in h2_1.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(13)
        r.font.bold = True

    add_bullet_item(
        doc,
        "Kiến trúc Backend 3 lớp (3-Tier Enterprise)",
        "Xây dựng bằng Spring Boot 3 (Java 21) theo mô hình 3 lớp chuẩn mực: Controller (tiếp nhận HTTP REST API) -> Service (xử lý logic nghiệp vụ và ràng buộc) -> Repository (truy xuất cơ sở dữ liệu)."
    )
    add_bullet_item(
        doc,
        "Cơ sở dữ liệu & ORM",
        "Sử dụng Microsoft SQL Server (database: A2TranQuocThai_SE1910) kết nối qua official JDBC Driver. Dùng Spring Data JPA và Hibernate ORM để tự động ánh xạ các Entity (Category, NewsArticle, SystemAccount, Tag) và bảng liên kết trung gian NewsTag; tự sinh khóa chính."
    )
    add_bullet_item(
        doc,
        "Kiến trúc Frontend & Giao diện",
        "Xây dựng Single Page Application (SPA) bằng ReactJS 19 và công cụ build Vite. Sử dụng React-Bootstrap kết hợp tùy biến Bootstrap 5 hiện đại, giao diện responsive mượt mà trên mọi kích thước màn hình."
    )
    add_bullet_item(
        doc,
        "Xác thực & Bảo mật (Security & Session)",
        "Áp dụng cơ chế Session-Based Authentication qua HttpSession. Đăng ký HandlerInterceptor (AccessConfig.java) tự động kiểm tra phiên đăng nhập và phân quyền mã vai trò (Role 1 cho Admin, Role 2 cho Staff) cho toàn bộ route /api/**."
    )
    add_bullet_item(
        doc,
        "Giao tiếp API & Reverse Proxy",
        "Axios Client (axiosClient.js) cấu hình withCredentials: true để đồng bộ Cookie Session an toàn. Thiết lập Reverse Proxy trong vite.config.js chuyển tiếp các request /api sang cổng backend http://localhost:8082, giải quyết triệt để lỗi CORS."
    )
    add_bullet_item(
        doc,
        "Quản lý Trạng thái & Điều hướng",
        "Dùng React Context API (AuthContext) lưu trữ trạng thái người dùng đăng nhập toàn cục; kết hợp useState và useEffect quản lý form và tải dữ liệu. React Router DOM v6 điều hướng trang không reload, bảo vệ các Private Routes theo đúng vai trò."
    )
    add_bullet_item(
        doc,
        "Biểu mẫu Modal & Hộp thoại xác nhận",
        "Sử dụng Modal Popup của React-Bootstrap cho tất cả form Thêm mới và Chỉnh sửa (Category, News, Account). Sử dụng hộp thoại Confirm xác nhận an toàn trước khi xóa bất kỳ dữ liệu nào."
    )
    add_bullet_item(
        doc,
        "Tìm kiếm dữ liệu đa trường",
        "Kết hợp tìm kiếm đa trường ở Backend với các query Spring Data JPA (ContainingIgnoreCase) và bộ lọc Client-Side tức thì cho trang độc giả công khai."
    )
    add_bullet_item(
        doc,
        "Xử lý Ngoại lệ tập trung & Ràng buộc toàn vẹn",
        "Dùng @RestControllerAdvice (ApiExceptionHandler) bắt BusinessRuleViolationException và ResourceNotFoundException, trả về mã lỗi HTTP 400/404 JSON chuẩn REST. Tầng Service kiểm tra existsByCategory, existsByCreatedById, và findByAccountEmail trước khi ghi dữ liệu."
    )

    # 2.2 Ý nghĩa của dự án
    h2_2 = doc.add_heading("2.2. Ý nghĩa của dự án (Project Significance)", level=2)
    h2_2.paragraph_format.space_before = Pt(8)
    h2_2.paragraph_format.space_after = Pt(6)
    for r in h2_2.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(13)
        r.font.bold = True

    add_bullet_item(
        doc,
        "a) Ý nghĩa về mặt nghiệp vụ & thực tiễn (Business & Real-World Value)",
        ""
    )
    add_bullet_item(
        doc,
        "Số hóa tòa soạn tin tức đại học",
        "Cung cấp giải pháp phần mềm toàn diện giúp các trường đại học (hệ thống FUNews) chuyên nghiệp hóa quy trình biên tập, gắn thẻ chủ đề, phân loại chuyên mục và xuất bản tin tức chính thống đến sinh viên, cán bộ giảng viên.",
        level=1
    )
    add_bullet_item(
        doc,
        "Phân định quyền hạn minh bạch, an toàn",
        "Tách bạch rõ ràng quyền hạn giữa Độc giả (chỉ đọc tin active), Biên tập viên (Staff - toàn quyền sáng tạo nội dung, chọn thẻ tags và theo dõi lịch sử bài viết của chính mình) và Quản trị viên (Admin - kiểm soát tài khoản toàn hệ thống).",
        level=1
    )
    add_bullet_item(
        doc,
        "Bảo toàn dữ liệu lịch sử xuất bản",
        "Các quy tắc nghiệp vụ khắt khe (chặn xóa danh mục đang có bài viết, chặn xóa tác giả đã từng tạo tin) giúp lưu trữ trọn vẹn dấu vết lịch sử, loại bỏ hoàn toàn nguy cơ dữ liệu mồ côi (orphan records) trong cơ sở dữ liệu.",
        level=1
    )

    add_bullet_item(
        doc,
        "b) Ý nghĩa về mặt kỹ thuật & học thuật phần mềm (Technical & Educational Value)",
        ""
    )
    add_bullet_item(
        doc,
        "Thực hành chuẩn mực kiến trúc doanh nghiệp",
        "Giúp người học làm chủ mô hình 3 lớp (3-Tier Enterprise Architecture) - nền tảng thiết yếu cho các hệ thống phần mềm quy mô lớn trong thực tế.",
        level=1
    )
    add_bullet_item(
        doc,
        "Làm chủ công nghệ Fullstack tích hợp",
        "Kết nối thành công Backend Java Spring Boot 3 mạnh mẽ với Frontend ReactJS hiện đại qua RESTful API, giải quyết các bài toán CORS và Session Cookie.",
        level=1
    )
    add_bullet_item(
        doc,
        "Vận dụng sâu sắc ORM & quan hệ dữ liệu",
        "Hiểu rõ và làm chủ các mối quan hệ thực thể phức tạp trong thực tế (One-to-Many giữa Category/Author và News, Many-to-Many giữa News và Tag qua bảng trung gian).",
        level=1
    )
    add_bullet_item(
        doc,
        "Rèn luyện tư duy lập trình chuyên nghiệp",
        "Hình thành tư duy kiểm soát ngoại lệ tập trung (Global Exception Handling), xác thực dữ liệu chặt chẽ từ tầng gốc và xây dựng trải nghiệm người dùng (UX) nhất quán.",
        level=1
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    # =========================================================================
    # PHẦN 3: KẾT QUẢ KIỂM THỬ GIAO DIỆN VÀ MINH CHỨNG CHỨC NĂNG (33 BƯỚC)
    # =========================================================================
    doc.add_page_break()
    h3 = doc.add_heading("3. Kết quả kiểm thử giao diện & Minh chứng chức năng (33 Bước)", level=1)
    h3.paragraph_format.space_before = Pt(8)
    h3.paragraph_format.space_after = Pt(8)
    for r in h3.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(15)
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
    h4.paragraph_format.space_before = Pt(8)
    h4.paragraph_format.space_after = Pt(8)
    for r in h4.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(15)
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
