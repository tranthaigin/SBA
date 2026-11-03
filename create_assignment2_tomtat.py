import os
import sys
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

from generate_docs_helper import (
    add_callout, add_summary_step,
    PRIMARY_COLOR, SECONDARY_COLOR, DARK_TEXT, SUCCESS_COLOR
)
from generate_combined_docs import a2_steps, add_a2_matrix_table

def add_dash_item(doc, label, description):
    """Thêm một dòng dạng gạch đầu dòng '- label: description', hoàn toàn không kẻ bảng."""
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.line_spacing = 1.15
    p.paragraph_format.left_indent = Inches(0.2)

    r_dash = p.add_run("- ")
    r_dash.font.bold = True
    r_dash.font.size = Pt(10.5)
    r_dash.font.color.rgb = PRIMARY_COLOR

    r_label = p.add_run(label + ": ")
    r_label.font.bold = True
    r_label.font.size = Pt(10.5)
    r_label.font.color.rgb = DARK_TEXT

    r_desc = p.add_run(description)
    r_desc.font.size = Pt(10.5)
    r_desc.font.color.rgb = DARK_TEXT

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
    p_title.paragraph_format.space_before = Pt(8)
    p_title.paragraph_format.space_after = Pt(14)
    
    r_title = p_title.add_run("Assignment 2")
    r_title.font.bold = True
    r_title.font.size = Pt(26)
    r_title.font.color.rgb = PRIMARY_COLOR

    # =========================================================================
    # 1. YÊU CẦU (REQUIREMENTS) - DẠNG GẠCH ĐẦU DÒNG, TUYỆT ĐỐI KHÔNG KẺ BẢNG
    # =========================================================================
    h1 = doc.add_heading("1. Yêu cầu (Requirements)", level=1)
    h1.paragraph_format.space_before = Pt(6)
    h1.paragraph_format.space_after = Pt(6)
    for r in h1.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    requirements = [
        ("Yêu cầu 1 (Public News)", "Đọc, tìm kiếm và xem chi tiết bài viết Active không cần đăng nhập."),
        ("Yêu cầu 2 (Login & Authentication)", "Đăng nhập bằng Email và Password, phân quyền Staff (role 2) và Admin (role 1)."),
        ("Yêu cầu 3 (Category Management)", "Thêm, sửa, xóa, tìm kiếm danh mục; chặn xóa khi đang có bài viết liên kết."),
        ("Yêu cầu 4 (News Management)", "Thêm, sửa, xóa, tìm kiếm tin tức; gắn nhiều Thẻ Tags (Many-to-Many)."),
        ("Yêu cầu 5 (Staff Profile)", "Xem và cập nhật thông tin cá nhân (họ tên, email, đổi mật khẩu)."),
        ("Yêu cầu 6 (My News History)", "Xem lại toàn bộ danh sách các bài viết do chính mình xuất bản."),
        ("Yêu cầu 7 (User Management)", "Thêm, sửa, xóa, tìm kiếm tài khoản người dùng hệ thống; chặn xóa khi đã tạo bài viết."),
        ("Yêu cầu 8 (Validation)", "Kiểm tra dữ liệu đầu vào, thông báo lỗi, chống trùng Email và xác nhận trước khi xóa."),
        ("Yêu cầu 9 (3-Layer Architecture)", "Xây dựng Backend Spring Boot 3 + JPA + SQL Server và React Client."),
    ]

    for label, desc in requirements:
        add_dash_item(doc, label, desc)

    doc.add_paragraph().paragraph_format.space_after = Pt(8)

    # =========================================================================
    # 2. GIẢI PHÁP (PROPOSED SOLUTION) - DẠNG GẠCH ĐẦU DÒNG, TUYỆT ĐỐI KHÔNG KẺ BẢNG
    # =========================================================================
    h2 = doc.add_heading("2. Giải pháp (Proposed Solution)", level=1)
    h2.paragraph_format.space_before = Pt(6)
    h2.paragraph_format.space_after = Pt(6)
    for r in h2.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    solutions = [
        ("Frontend", "ReactJS + Vite"),
        ("Backend", "Spring Boot 3 (Java 21) kiến trúc 3 lớp (Controller - Service - Repository)."),
        ("Database", "Microsoft SQL Server, kết nối qua official JDBC Driver."),
        ("ORM & Storage", "Spring Data JPA & Hibernate, quản lý bảng liên kết NewsTag và tự sinh khóa."),
        ("UI", "React-Bootstrap + Bootstrap 5 tùy biến, giao diện responsive."),
        ("Authentication", "Session-Based Auth qua HttpSession + HandlerInterceptor kiểm tra role."),
        ("API Communication", "Axios Client (withCredentials: true) + Vite Reverse Proxy cổng 8082."),
        ("Routing", "React Router DOM v6 để chuyển trang."),
        ("State Management", "React Context API (AuthContext) + useState, useEffect."),
        ("CRUD", "Xây dựng chức năng Create, Read, Update, Delete."),
        ("Search", "Lọc dữ liệu theo từ khóa (kết hợp Backend JPA query và Client-side filter)."),
        ("Form", "Popup Modal để thêm và chỉnh sửa."),
        ("Validation", "Kiểm tra trường bắt buộc, chống trùng Email và xác nhận trước khi xóa."),
    ]

    for label, desc in solutions:
        add_dash_item(doc, label, desc)

    doc.add_paragraph().paragraph_format.space_after = Pt(6)

    # Mục đích của dự án (dạng gạch đầu dòng, không kẻ bảng)
    h2_obj = doc.add_heading("Mục đích của dự án (Project Objectives)", level=2)
    h2_obj.paragraph_format.space_before = Pt(8)
    h2_obj.paragraph_format.space_after = Pt(4)
    for r in h2_obj.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    add_dash_item(
        doc,
        "Về mặt nghiệp vụ (Thực tế)",
        "Xây dựng hệ thống quản lý tin tức nội bộ (FUNews) giúp nhà trường số hóa toàn diện quy trình biên tập, phân loại chuyên mục, gắn thẻ chủ đề và xuất bản tin tức chính thống đến sinh viên, cán bộ giảng viên."
    )
    add_dash_item(
        doc,
        "Về mặt kỹ thuật",
        "Triển khai hoàn chỉnh ứng dụng Web Service RESTful theo kiến trúc 3 lớp (3-Tier) với Backend Spring Boot 3 kết nối cơ sở dữ liệu Microsoft SQL Server qua Spring Data JPA và Frontend Single Page Application (SPA) bằng ReactJS qua Axios."
    )
    add_dash_item(
        doc,
        "Về mặt học phần SBA301",
        "Đáp ứng trọn vẹn các chuẩn đầu ra của môn học về thiết kế kiến trúc phần mềm doanh nghiệp, phân quyền bảo mật phiên (Session-based Auth qua HandlerInterceptor) và bảo toàn tính toàn vẹn dữ liệu qua các ràng buộc nghiệp vụ khắt khe."
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # Ý nghĩa của dự án (dạng gạch đầu dòng, không kẻ bảng)
    h2_sig = doc.add_heading("Ý nghĩa của dự án (Project Significance)", level=2)
    h2_sig.paragraph_format.space_before = Pt(6)
    h2_sig.paragraph_format.space_after = Pt(4)
    for r in h2_sig.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    add_dash_item(
        doc,
        "Về mặt nghiệp vụ",
        "Số hóa toàn bộ quy trình biên tập và xuất bản tin tức nội bộ trường đại học (FUNews); phân quyền minh bạch giữa Độc giả, Biên tập viên (Staff) và Quản trị viên (Admin); bảo toàn dữ liệu lịch sử xuất bản và loại bỏ hoàn toàn nguy cơ dữ liệu mồ côi (orphan records)."
    )
    add_dash_item(
        doc,
        "Về mặt kỹ thuật",
        "Làm chủ kiến trúc doanh nghiệp 3 lớp (3-Tier Enterprise); tích hợp hệ thống Fullstack hoàn chỉnh giữa Spring Boot 3 RESTful API và ReactJS; vận dụng sâu sắc ORM (quan hệ One-to-Many và Many-to-Many qua Spring Data JPA); chuẩn hóa xử lý ngoại lệ tập trung và tối ưu trải nghiệm người dùng với Popup Modal và Confirm Dialog."
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    # =========================================================================
    # 3. KẾT QUẢ KIỂM THỬ GIAO DIỆN VÀ MINH CHỨNG CHỨC NĂNG (33 BƯỚC)
    # =========================================================================
    doc.add_page_break()
    h3 = doc.add_heading("3. Kết quả kiểm thử giao diện & Minh chứng chức năng (33 Bước)", level=1)
    h3.paragraph_format.space_before = Pt(8)
    h3.paragraph_format.space_after = Pt(8)
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
    # 4. BẢNG TỔNG KẾT MA TRẬN YÊU CẦU VÀ KIỂM THỬ (TEST MATRIX)
    # =========================================================================
    doc.add_page_break()
    h4 = doc.add_heading("4. Bảng tổng kết Ma trận yêu cầu và kiểm thử Assignment 02 (Test Matrix)", level=1)
    h4.paragraph_format.space_before = Pt(8)
    h4.paragraph_format.space_after = Pt(8)
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

    # Lưu tài liệu ra assignment2-tomtat.docx trước
    out_file2 = os.path.abspath("assignment2-tomtat.docx")
    doc.save(out_file2)
    print(f"File 2 updated at: {out_file2}")

    # Cố gắng lưu đè vào noteTT/assignment2-tomtat.docx nếu không bị khóa bởi Word
    out_file1 = os.path.abspath("noteTT/assignment2-tomtat.docx")
    try:
        doc.save(out_file1)
        print(f"File 1 updated at: {out_file1}")
        if os.path.exists("noteTT/assignment2-tomtat_updated.docx"):
            try:
                os.remove("noteTT/assignment2-tomtat_updated.docx")
            except:
                pass
    except PermissionError:
        print("noteTT/assignment2-tomtat.docx đang mở trong Word, lưu sang noteTT/assignment2-tomtat_updated.docx")
        alt_path = os.path.abspath("noteTT/assignment2-tomtat_updated.docx")
        doc.save(alt_path)
        print(f"File backup updated at: {alt_path}")

if __name__ == "__main__":
    build_assignment2_tomtat_doc()
