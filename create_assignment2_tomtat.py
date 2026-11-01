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
    # 1. YÊU CẦU (REQUIREMENTS)
    # =========================================================================
    h1 = doc.add_heading("1. Yêu cầu (Requirements)", level=1)
    h1.paragraph_format.space_before = Pt(6)
    h1.paragraph_format.space_after = Pt(6)
    for r in h1.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    # Bảng Requirements ngắn gọn: STT | Yêu cầu | Mô tả
    tbl_req = doc.add_table(rows=1, cols=3)
    tbl_req.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_req.autofit = False
    headers_req = ["STT", "Yêu cầu", "Mô tả"]
    col_w_req = [Inches(0.6), Inches(2.2), Inches(4.2)]

    for j, h_text in enumerate(headers_req):
        c = tbl_req.rows[0].cells[j]
        c.width = col_w_req[j]
        set_cell_background(c, "1E3A8A")
        set_cell_margins(c, 50, 50, 80, 80)
        p = c.paragraphs[0]
        r = p.add_run(h_text)
        r.font.bold = True
        r.font.size = Pt(9.5)
        r.font.color.rgb = RGBColor(255, 255, 255)

    requirements_data = [
        ("1", "Public News", "Đọc, tìm kiếm và xem chi tiết bài viết Active không cần đăng nhập."),
        ("2", "Login & Authentication", "Đăng nhập bằng Email và Password, phân quyền Staff (role 2) và Admin (role 1)."),
        ("3", "Category Management", "Thêm, sửa, xóa, tìm kiếm danh mục; chặn xóa khi đang có bài viết."),
        ("4", "News Management", "Thêm, sửa, xóa, tìm kiếm tin tức; gắn nhiều Thẻ Tags (Many-to-Many)."),
        ("5", "Staff Profile", "Xem và cập nhật thông tin cá nhân (họ tên, email, đổi mật khẩu)."),
        ("6", "My News History", "Xem lại toàn bộ danh sách các bài viết do chính mình xuất bản."),
        ("7", "User Management", "Thêm, sửa, xóa, tìm kiếm tài khoản người dùng hệ thống; chặn xóa khi đã tạo bài viết."),
        ("8", "Validation", "Kiểm tra dữ liệu đầu vào, thông báo lỗi, chống trùng Email và xác nhận trước khi xóa."),
        ("9", "3-Layer Architecture", "Xây dựng Backend Spring Boot 3 + JPA + SQL Server và React Client."),
    ]

    for req_row in requirements_data:
        row = tbl_req.add_row()
        for c_idx, val in enumerate(req_row):
            cell = row.cells[c_idx]
            cell.width = col_w_req[c_idx]
            set_cell_background(cell, "F8FAFC" if c_idx == 0 else "FFFFFF")
            set_cell_margins(cell, 45, 45, 75, 75)
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

    doc.add_paragraph().paragraph_format.space_after = Pt(10)

    # =========================================================================
    # 2. GIẢI PHÁP (PROPOSED SOLUTION) VÀ Ý NGHĨA DỰ ÁN
    # =========================================================================
    h2 = doc.add_heading("2. Giải pháp (Proposed Solution)", level=1)
    h2.paragraph_format.space_before = Pt(6)
    h2.paragraph_format.space_after = Pt(6)
    for r in h2.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    # Bảng Giải pháp ngắn gọn: Thành phần | Giải pháp
    tbl_sol = doc.add_table(rows=1, cols=2)
    tbl_sol.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl_sol.autofit = False
    headers_sol = ["Thành phần", "Giải pháp"]
    col_w_sol = [Inches(2.2), Inches(4.8)]

    for j, h_text in enumerate(headers_sol):
        c = tbl_sol.rows[0].cells[j]
        c.width = col_w_sol[j]
        set_cell_background(c, "1E3A8A")
        set_cell_margins(c, 50, 50, 80, 80)
        p = c.paragraphs[0]
        r = p.add_run(h_text)
        r.font.bold = True
        r.font.size = Pt(9.5)
        r.font.color.rgb = RGBColor(255, 255, 255)

    solutions_data = [
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

    for sol_row in solutions_data:
        row = tbl_sol.add_row()
        for c_idx, val in enumerate(sol_row):
            cell = row.cells[c_idx]
            cell.width = col_w_sol[c_idx]
            set_cell_background(cell, "F8FAFC" if c_idx == 0 else "FFFFFF")
            set_cell_margins(cell, 45, 45, 75, 75)
            p = cell.paragraphs[0]
            r = p.add_run(val)
            r.font.size = Pt(9)
            if c_idx == 0:
                r.font.bold = True
                r.font.color.rgb = PRIMARY_COLOR
            else:
                r.font.color.rgb = DARK_TEXT

    doc.add_paragraph().paragraph_format.space_after = Pt(10)

    # Mục Ý nghĩa của dự án (ngắn gọn, súc tích)
    h2_sig = doc.add_heading("Ý nghĩa của dự án (Project Significance)", level=2)
    h2_sig.paragraph_format.space_before = Pt(6)
    h2_sig.paragraph_format.space_after = Pt(4)
    for r in h2_sig.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    p_sig = doc.add_paragraph()
    p_sig.paragraph_format.space_before = Pt(2)
    p_sig.paragraph_format.space_after = Pt(4)
    p_sig.paragraph_format.line_spacing = 1.15
    
    r_sig_biz = p_sig.add_run("• Về mặt nghiệp vụ: ")
    r_sig_biz.font.bold = True
    r_sig_biz.font.color.rgb = DARK_TEXT
    r_sig_biz.font.size = Pt(10)
    p_sig.add_run(
        "Số hóa toàn bộ quy trình biên tập và xuất bản tin tức nội bộ trường đại học (FUNews); "
        "phân quyền minh bạch giữa Độc giả, Biên tập viên (Staff) và Quản trị viên (Admin); "
        "bảo toàn dữ liệu lịch sử xuất bản và loại bỏ hoàn toàn nguy cơ dữ liệu mồ côi (orphan records).\n"
    )

    r_sig_tech = p_sig.add_run("• Về mặt kỹ thuật: ")
    r_sig_tech.font.bold = True
    r_sig_tech.font.color.rgb = DARK_TEXT
    r_sig_tech.font.size = Pt(10)
    p_sig.add_run(
        "Làm chủ kiến trúc doanh nghiệp 3 lớp (3-Tier Enterprise); "
        "tích hợp hệ thống Fullstack hoàn chỉnh giữa Spring Boot 3 RESTful API và ReactJS; "
        "vận dụng sâu sắc ORM (quan hệ One-to-Many và Many-to-Many qua Spring Data JPA); "
        "chuẩn hóa xử lý ngoại lệ tập trung và tối ưu trải nghiệm người dùng với Popup Modal và Confirm Dialog."
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

    out_file2 = os.path.abspath("assignment2-tomtat.docx")
    doc.save(out_file2)
    print(f"File created successfully at: {out_file2}")

    try:
        out_file1 = os.path.abspath("noteTT/assignment2-tomtat.docx")
        doc.save(out_file1)
        print(f"File in noteTT updated successfully at: {out_file1}")
    except PermissionError:
        print("noteTT/assignment2-tomtat.docx is currently open in Word.")
        out_file1_alt = os.path.abspath("noteTT/assignment2-tomtat_updated.docx")
        doc.save(out_file1_alt)
        print(f"Saved copy to: {out_file1_alt}")

if __name__ == "__main__":
    build_assignment2_tomtat_doc()
