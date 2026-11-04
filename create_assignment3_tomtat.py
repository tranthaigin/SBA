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
    add_callout, add_summary_step, set_cell_background, set_cell_margins,
    PRIMARY_COLOR, SECONDARY_COLOR, DARK_TEXT, SUCCESS_COLOR
)

def add_dash_item(doc, label, description, indent=0.2):
    """Thêm một dòng dạng gạch đầu dòng '- label: description', hoàn toàn không kẻ bảng."""
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(3)
    p.paragraph_format.line_spacing = 1.15
    p.paragraph_format.left_indent = Inches(indent)

    r_dash = p.add_run("- ")
    r_dash.font.bold = True
    r_dash.font.size = Pt(10.5)
    r_dash.font.color.rgb = PRIMARY_COLOR

    if label:
        r_label = p.add_run(label + ": ")
        r_label.font.bold = True
        r_label.font.size = Pt(10.5)
        r_label.font.color.rgb = DARK_TEXT

    r_desc = p.add_run(description)
    r_desc.font.size = Pt(10.5)
    r_desc.font.color.rgb = DARK_TEXT

# 27 bước kiểm thử của Assignment 03
a3_steps = [
    (
        "noteTT/screenshots_a3/01_a3_public_rooms_catalog.png",
        "Bước 1: Khách vãng lai xem danh mục phòng nghỉ công khai không cần đăng nhập",
        "Trang danh mục hiển thị đầy đủ danh sách các phòng nghỉ Active kèm số phòng, loại phòng và giá thuê mà không yêu cầu đăng nhập."
    ),
    (
        "noteTT/screenshots_a3/02_a3_room_details_view.png",
        "Bước 2: Xem chi tiết thông số kỹ thuật, sức chứa và mô tả phòng nghỉ",
        "Card phòng thể hiện trực quan sức chứa tối đa (Max Capacity), loại phòng (Room Type), đơn giá theo ngày và mô tả chi tiết tiện nghi."
    ),
    (
        "noteTT/screenshots_a3/03_a3_login_modal.png",
        "Bước 3: Mở hộp thoại đăng nhập hệ thống (Login Modal)",
        "Hộp thoại Popup đăng nhập xuất hiện ở giữa màn hình với trường Email, Password và các nút điền nhanh tài khoản Staff/Customer."
    ),
    (
        "noteTT/screenshots_a3/04_a3_login_validation_error.png",
        "Bước 4: Xác thực đăng nhập thất bại khi nhập sai thông tin",
        "Khung thông báo màu đỏ hiển thị lỗi 'Invalid credentials' ngăn chặn truy cập trái phép khi sai mật khẩu."
    ),
    (
        "noteTT/screenshots_a3/05_a3_register_modal.png",
        "Bước 5: Khách hàng đăng ký tài khoản mới (Customer Registration)",
        "Hộp thoại đăng ký yêu cầu đầy đủ Họ tên, Email, Số điện thoại, Ngày sinh và Mật khẩu theo đúng quy chuẩn DTO."
    ),
    (
        "noteTT/screenshots_a3/06_a3_customer_login_success.png",
        "Bước 6: Khách hàng đăng nhập thành công với JSON Web Token",
        "Thanh điều hướng hiển thị tên khách hàng kèm Badge 'Customer' màu xanh và mở rộng các menu cá nhân (Explore Rooms, My Profile, My Bookings)."
    ),
    (
        "noteTT/screenshots_a3/07_a3_customer_booking_modal.png",
        "Bước 7: Mở biểu mẫu tạo đơn đặt phòng trực tuyến (Online Booking)",
        "Modal đặt phòng cho phép chọn ngày nhận phòng (Check-in), ngày trả phòng (Check-out) và tự động tính tổng số đêm lưu trú."
    ),
    (
        "noteTT/screenshots_a3/08_a3_customer_booking_multi_room.png",
        "Bước 8: Chọn thêm nhiều phòng trong cùng một lượt đặt (Multi-room Booking)",
        "Khách hàng tick chọn thêm các phòng phụ, giao diện tự động cập nhật danh sách chi tiết phòng và tính toán lại tổng tiền toàn bộ đơn."
    ),
    (
        "noteTT/screenshots_a3/09_a3_customer_booking_confirmed.png",
        "Bước 9: Khách hàng xác nhận và gửi đơn đặt phòng thành công",
        "Đơn đặt phòng cùng các chi tiết phòng được lưu bền vững vào database SQL Server, thông báo xác nhận thành công hiển thị rõ ràng."
    ),
    (
        "noteTT/screenshots_a3/10_a3_customer_profile_view.png",
        "Bước 10: Khách hàng xem và cập nhật hồ sơ cá nhân (My Profile)",
        "Trang cá nhân cho phép tra cứu thông tin định danh và cập nhật số điện thoại, ngày sinh hoặc đổi mật khẩu mới."
    ),
    (
        "noteTT/screenshots_a3/11_a3_customer_booking_history.png",
        "Bước 11: Khách hàng tra cứu lịch sử các lượt đặt phòng (My Bookings)",
        "Bảng lịch sử liệt kê đầy đủ các mã đơn, ngày đặt, danh sách các phòng đã chọn, thời gian lưu trú và tổng tiền thanh toán."
    ),
    (
        "noteTT/screenshots_a3/12_a3_staff_login.png",
        "Bước 12: Đăng nhập quyền Nhân viên quản trị (Staff Login)",
        "Staff đăng nhập bằng tài khoản nạp từ file cấu hình application.properties (staff@hotel.com) và nhận JWT chứa quyền ROLE_STAFF."
    ),
    (
        "noteTT/screenshots_a3/13_a3_staff_navbar_menu.png",
        "Bước 13: Giao diện quản trị với phân quyền Staff mở rộng",
        "Navbar xuất hiện nhãn đỏ 'Hotel Staff' và kích hoạt đầy đủ các menu nghiệp vụ: Manage Rooms, Manage Customers, Manage Reservations."
    ),
    (
        "noteTT/screenshots_a3/14_a3_admin_rooms_list.png",
        "Bước 14: Quản lý danh sách phòng khách sạn (Manage Rooms)",
        "Bảng quản trị liệt kê toàn bộ các phòng trong hệ thống kèm số phòng, loại phòng, sức chứa, giá thuê và Badge trạng thái hoạt động."
    ),
    (
        "noteTT/screenshots_a3/15_a3_admin_room_search.png",
        "Bước 15: Tìm kiếm và lọc phòng theo số phòng hoặc mô tả",
        "Nhập từ khóa vào ô tìm kiếm, bảng lọc dữ liệu tức thì theo đúng tiêu chí truy vấn không phân biệt chữ hoa/thường."
    ),
    (
        "noteTT/screenshots_a3/16_a3_admin_create_room_modal.png",
        "Bước 16: Biểu mẫu thêm mới phòng nghỉ khách sạn (Add Room Modal)",
        "Modal Popup yêu cầu nhập số phòng, chọn loại phòng từ danh mục, nhập sức chứa tối đa, đơn giá thuê và mô tả tiện nghi."
    ),
    (
        "noteTT/screenshots_a3/17_a3_admin_create_room_success.png",
        "Bước 17: Thêm mới phòng thành công vào cơ sở dữ liệu",
        "Phòng mới được lưu thành công vào SQL Server và hiển thị ngay trên bảng danh sách với trạng thái Active."
    ),
    (
        "noteTT/screenshots_a3/18_a3_admin_edit_room_modal.png",
        "Bước 18: Biểu mẫu cập nhật thông tin phòng nghỉ (Edit Room Modal)",
        "Modal tự động nạp sẵn dữ liệu của phòng được chọn, cho phép chỉnh sửa giá thuê, sức chứa hoặc mô tả tiện nghi."
    ),
    (
        "noteTT/screenshots_a3/19_a3_admin_delete_room_hard.png",
        "Bước 19: Xóa cứng phòng chưa phát sinh giao dịch (Hard Delete Room)",
        "Phòng mới chưa từng nằm trong chi tiết đơn đặt phòng nào được xóa hoàn toàn và vĩnh viễn khỏi database sau khi xác nhận."
    ),
    (
        "noteTT/screenshots_a3/20_a3_admin_delete_room_soft.png",
        "Bước 20: Xóa mềm phòng đã có lịch sử đặt phòng (Soft Delete Room)",
        "Phòng đã tồn tại trong booking details không bị xóa mất mà tự động chuyển trạng thái sang Inactive (xóa mềm) để bảo toàn dữ liệu hóa đơn."
    ),
    (
        "noteTT/screenshots_a3/21_a3_admin_customers_list.png",
        "Bước 21: Quản lý danh sách khách hàng lưu trú (Manage Customers)",
        "Bảng danh sách hiển thị toàn bộ hồ sơ khách hàng gồm ID, họ tên, email định danh, số điện thoại, ngày sinh và trạng thái."
    ),
    (
        "noteTT/screenshots_a3/22_a3_admin_customer_search.png",
        "Bước 22: Tìm kiếm khách hàng theo tên hoặc địa chỉ email",
        "Nhập từ khóa tra cứu, hệ thống lọc chính xác các hồ sơ khách hàng thỏa mãn điều kiện tìm kiếm."
    ),
    (
        "noteTT/screenshots_a3/23_a3_admin_create_customer_modal.png",
        "Bước 23: Biểu mẫu thêm mới khách hàng nội bộ (Add Customer Modal)",
        "Modal Popup cho phép nhân viên tiếp nhận thông tin và tạo tài khoản khách hàng trực tiếp tại quầy."
    ),
    (
        "noteTT/screenshots_a3/24_a3_admin_edit_customer_modal.png",
        "Bước 24: Biểu mẫu chỉnh sửa thông tin khách hàng (Edit Customer Modal)",
        "Modal Popup hỗ trợ cập nhật số điện thoại, ngày sinh và trạng thái kích hoạt/khóa tài khoản khách hàng."
    ),
    (
        "noteTT/screenshots_a3/25_a3_admin_bookings_list.png",
        "Bước 25: Quản lý toàn bộ danh sách đơn đặt phòng (Manage Reservations)",
        "Bảng quản trị liệt kê toàn bộ đơn đặt phòng của khách sạn kèm thông tin khách, chi tiết các phòng, tổng tiền và trạng thái đơn."
    ),
    (
        "noteTT/screenshots_a3/26_a3_admin_create_booking_modal.png",
        "Bước 26: Nhân viên tạo đơn đặt phòng trực tiếp tại quầy (Create Booking)",
        "Biểu mẫu quản trị hỗ trợ chọn khách hàng, thêm nhiều phòng vào đơn, chọn ngày lưu trú và tự động tính tổng tiền."
    ),
    (
        "noteTT/screenshots_a3/27_a3_admin_update_booking_status.png",
        "Bước 27: Cập nhật trạng thái đơn đặt phòng (Confirm / Complete / Cancel)",
        "Nhân viên bấm chuyển trạng thái đơn đặt sang Completed (hoàn thành) hoặc Confirmed (xác nhận) tức thì trên hệ thống."
    ),
]

def add_a3_matrix_table(doc):
    """Bảng ma trận đối chiếu yêu cầu và kiểm thử của Assignment 03."""
    tbl = doc.add_table(rows=1, cols=4)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    tbl.autofit = False
    headers = ["Mã Yêu Cầu", "Nội Dung Yêu Cầu Đề Bài", "Thành Phần Code & Bằng Chứng UI", "Kết Quả Kiểm Thử"]
    col_w = [Inches(1.1), Inches(2.2), Inches(2.2), Inches(1.1)]
    for j, h_text in enumerate(headers):
        tbl.rows[0].cells[j].width = col_w[j]
        set_cell_background(tbl.rows[0].cells[j], "1E3A8A")
        set_cell_margins(tbl.rows[0].cells[j], 80, 80, 100, 100)
        p = tbl.rows[0].cells[j].paragraphs[0]
        r = p.add_run(h_text)
        r.font.bold = True
        r.font.size = Pt(9)
        r.font.color.rgb = RGBColor(255, 255, 255)

    matrix_data = [
        ("A3-01", "Kiến trúc 3 lớp Spring Boot 3 & Spring Data JPA", "fu.edu.sba301.assignment03, pom.xml, Controller -> Service -> Repository", "ĐẠT (PASS 100%)"),
        ("A3-02", "Cơ sở dữ liệu Microsoft SQL Server", "Database: A3TranQuocThai_SE1910, 5 entities JPA", "ĐẠT (PASS 100%)"),
        ("A3-03", "Bảo mật REST API bằng JSON Web Token (JWT)", "SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter", "ĐẠT (PASS 100%)"),
        ("A3-04", "Tài khoản Staff cấu hình trong application.properties", "StaffProperties.java, hotel.staff.email=staff@hotel.com", "ĐẠT (PASS 100%)"),
        ("A3-05", "Khách vãng lai xem thông tin phòng công khai không cần login", "PublicRoomController.java, RoomCatalog.jsx (Bước 1, 2)", "ĐẠT (PASS 100%)"),
        ("A3-06", "Khách hàng đăng ký tài khoản mới & Validation", "AuthController.register, RegisterModal.jsx (Bước 5)", "ĐẠT (PASS 100%)"),
        ("A3-07", "Đăng nhập Customer bằng Email/Password qua JWT", "AuthController.login, LoginModal.jsx (Bước 3, 4, 6)", "ĐẠT (PASS 100%)"),
        ("A3-08", "Khách hàng đặt 1 hoặc nhiều phòng trong 1 booking", "CustomerPortalController.createBooking, RoomCatalog.jsx (Bước 7, 8, 9)", "ĐẠT (PASS 100%)"),
        ("A3-09", "Khách hàng tự quản lý và cập nhật Hồ sơ cá nhân", "CustomerPortalController.updateProfile, Profile.jsx (Bước 10)", "ĐẠT (PASS 100%)"),
        ("A3-10", "Khách hàng tra cứu lịch sử đặt phòng đa phòng", "CustomerPortalController.getMyBookings, CustomerBookings.jsx (Bước 11)", "ĐẠT (PASS 100%)"),
        ("A3-11", "Staff đăng nhập và quản lý danh sách phòng (CRUD & Search)", "AdminController, RoomService, AdminRooms.jsx (Bước 12, 13, 14, 15)", "ĐẠT (PASS 100%)"),
        ("A3-12", "Quy tắc xóa phòng: Xóa cứng (Hard Delete) phòng chưa phát sinh giao dịch", "RoomService.deleteRoom, AdminRooms.jsx (Bước 19)", "ĐẠT (PASS 100%)"),
        ("A3-13", "Quy tắc xóa phòng: Xóa mềm (Soft Delete -> Inactive) phòng đã có đơn đặt", "RoomService.deleteRoom, AdminRooms.jsx (Bước 20)", "ĐẠT (PASS 100%)"),
        ("A3-14", "Staff quản lý thông tin khách hàng (CRUD & Search)", "AdminController, CustomerService, AdminCustomers.jsx (Bước 21, 22, 23, 24)", "ĐẠT (PASS 100%)"),
        ("A3-15", "Staff quản lý đặt phòng và cập nhật trạng thái đơn (Confirmed/Complete)", "AdminController, BookingService, AdminBookings.jsx (Bước 25, 26, 27)", "ĐẠT (PASS 100%)"),
    ]

    for i, row in enumerate(matrix_data):
        r = tbl.add_row()
        bg = "FFFFFF" if i % 2 == 0 else "F9FAFB"
        for j, val in enumerate(row):
            cell = r.cells[j]
            cell.width = col_w[j]
            set_cell_background(cell, bg)
            set_cell_margins(cell, 60, 60, 90, 90)
            p = cell.paragraphs[0]
            p.paragraph_format.space_after = Pt(0)
            run = p.add_run(val)
            run.font.size = Pt(8.5)
            if j == 0:
                run.font.bold = True
                run.font.color.rgb = PRIMARY_COLOR
            elif j == 3:
                run.font.bold = True
                run.font.color.rgb = SUCCESS_COLOR
            else:
                run.font.color.rgb = DARK_TEXT

    doc.add_paragraph().paragraph_format.space_after = Pt(8)

def build_assignment3_tomtat_doc():
    doc = Document()

    # Thiết lập lề trang chuẩn báo cáo (0.75 inch)
    for s in doc.sections:
        s.top_margin = Inches(0.75)
        s.bottom_margin = Inches(0.75)
        s.left_margin = Inches(0.75)
        s.right_margin = Inches(0.75)

    # =========================================================================
    # TIÊU ĐỀ LỚN DUY NHẤT: Assignment 3
    # =========================================================================
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_title.paragraph_format.space_before = Pt(8)
    p_title.paragraph_format.space_after = Pt(14)

    r_title = p_title.add_run("Assignment 3")
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
        ("Yêu cầu 1 (Public Room Viewing)", "Xem thông tin danh sách các phòng khách sạn công khai mà không cần đăng nhập."),
        ("Yêu cầu 2 (Customer Authentication & JWT)", "Đăng ký tài khoản và đăng nhập bằng Email/Password, cấp phát JSON Web Token (JWT) để bảo mật các API giao dịch."),
        ("Yêu cầu 3 (Customer Online Booking)", "Tạo đơn đặt phòng trực tuyến (Booking Reservation) với một hoặc nhiều phòng trong cùng giao dịch, tính tổng giá tự động."),
        ("Yêu cầu 4 (Customer Profile & History)", "Khách hàng tự quản lý hồ sơ cá nhân và tra cứu toàn bộ lịch sử các lượt đặt phòng của bản thân."),
        ("Yêu cầu 5 (Staff Configuration)", "Tài khoản nhân viên quản trị (Staff) được nạp bảo mật từ tệp cấu hình application.properties."),
        ("Yêu cầu 6 (Room Management & Soft/Hard Delete)", "Quản lý phòng (CRUD, tìm kiếm); xóa cứng khi phòng chưa có giao dịch đặt phòng, chuyển trạng thái Inactive (xóa mềm) khi phòng đã phát sinh giao dịch."),
        ("Yêu cầu 7 (Customer Management)", "Nhân viên quản trị thêm, sửa, xóa, tìm kiếm thông tin khách hàng và trạng thái tài khoản."),
        ("Yêu cầu 8 (Booking Reservation Management)", "Quản lý danh sách các lượt đặt phòng, chi tiết phòng (Booking Details), cập nhật trạng thái đơn (Confirmed, Completed, Cancelled)."),
        ("Yêu cầu 9 (Validation & Data Integrity)", "Kiểm tra kiểu và tính hợp lệ cho toàn bộ các trường dữ liệu, chặn trùng Email, chặn ngày đặt không hợp lệ."),
        ("Yêu cầu 10 (3-Layer Architecture & RESTful API)", "Xây dựng Backend Spring Boot 3 theo mô hình 3 lớp kết hợp Spring Security, Spring Data JPA, SQL Server và Frontend ReactJS SPA."),
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
        ("Frontend", "ReactJS 19 + Vite 6 Single Page Application (SPA)."),
        ("Backend", "Spring Boot 3 (Java 21) kiến trúc 3 lớp (Controller - Service - Repository)."),
        ("Database", "Microsoft SQL Server, kết nối qua official JDBC Driver."),
        ("ORM & Persistence", "Spring Data JPA & Hibernate, quản lý khóa phức hợp BookingDetailId và tự sinh schema."),
        ("Security & Auth", "Spring Security 6 kết hợp JWT (io.jsonwebtoken JJWT 0.12.6) và JwtAuthenticationFilter."),
        ("Staff Account", "Đọc trực tiếp từ application.properties qua @ConfigurationProperties (StaffProperties)."),
        ("UI Framework", "React-Bootstrap + Bootstrap 5 tùy biến, giao diện hiện đại và responsive."),
        ("API Communication", "Axios Client (gửi Bearer Token trong Authorization header) + Vite Reverse Proxy cổng 8083."),
        ("Routing", "React Router DOM v6 quản lý điều hướng và phân quyền giao diện (Protected Routes)."),
        ("State Management", "React Context API (AuthContext) lưu trữ JWT token và user role + useState, useEffect."),
        ("CRUD", "Xây dựng đầy đủ chức năng Create, Read, Update, Delete cho Room, Customer, Booking."),
        ("Search", "Lọc dữ liệu phòng và khách hàng kết hợp JPA Query (ContainingIgnoreCase) và Client-side filter."),
        ("Form & Dialog", "Popup Modal linh hoạt cho Tạo mới / Chỉnh sửa và Confirm Dialog xác nhận xóa."),
        ("Validation", "Bean Validation (@Valid, @NotNull, @NotBlank, @Min, @Email) và Exception Handler tập trung."),
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
        "Xây dựng hệ thống quản lý khách sạn mini (FUMiniHotel Management System) hỗ trợ quy trình đặt phòng trực tuyến đa phòng, quản lý thông tin khách lưu trú, phòng nghỉ và lịch sử giao dịch minh bạch."
    )
    add_dash_item(
        doc,
        "Về mặt kỹ thuật",
        "Xây dựng ứng dụng doanh nghiệp chuẩn hóa với Backend Spring Boot 3 RESTful API bảo mật bằng JWT và Frontend ReactJS Single Page Application, tương tác cơ sở dữ liệu Microsoft SQL Server qua Spring Data JPA."
    )
    add_dash_item(
        doc,
        "Về mặt học phần SBA301",
        "Làm chủ thiết kế kiến trúc 3 lớp kết hợp Spring Security 6 không trạng thái (Stateless Session with JWT), xử lý quan hệ phức hợp (Composite Key, Many-to-Many gián tiếp) và logic nghiệp vụ xóa mềm/xóa cứng chuẩn mực."
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # Cấu trúc tổ chức mã nguồn (Source Code Organization)
    h2_struct = doc.add_heading("Cấu trúc tổ chức mã nguồn (Source Code Organization)", level=2)
    h2_struct.paragraph_format.space_before = Pt(8)
    h2_struct.paragraph_format.space_after = Pt(4)
    for r in h2_struct.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    add_dash_item(doc, "Phần Backend (Spring Boot 3 - Java)", "Tổ chức theo kiến trúc 3 lớp phân tách trách nhiệm rõ ràng:")
    backend_parts = [
        ("controller", "Chứa các REST Controller tiếp nhận request HTTP từ Client (AuthController, CustomerPortalController, PublicRoomController, AdminController)."),
        ("service", "Chứa logic nghiệp vụ cốt lõi, kiểm tra ràng buộc xóa phòng mềm/cứng, tính tổng tiền đơn và mã hóa bảo mật (RoomService, CustomerService, BookingService, AuthService)."),
        ("repository", "Chứa các interface Spring Data JPA truy xuất dữ liệu từ SQL Server (RoomInformationRepository, CustomerRepository, BookingReservationRepository, BookingDetailRepository, RoomTypeRepository)."),
        ("entity", "Định nghĩa các bảng dữ liệu thực thể và khóa chính phức hợp (RoomInformation, RoomType, Customer, BookingReservation, BookingDetail, BookingDetailId)."),
        ("dto", "Chứa các Data Transfer Object chuẩn hóa dữ liệu yêu cầu và phản hồi (AuthLoginRequest, AuthRegisterRequest, AuthResponse, RoomRequest, CustomerRequest, BookingCreateRequest)."),
        ("security", "Cấu hình Spring Security 6, bộ lọc giải mã JWT (JwtAuthenticationFilter), trình sinh/kiểm tra token (JwtTokenProvider) và cấu hình tài khoản Staff từ file properties (StaffProperties, SecurityConfig)."),
        ("config", "Cấu hình khởi tạo dữ liệu mẫu ban đầu (DataInitializer) hỗ trợ kiểm thử tức thì."),
        ("exception / advice", "Bộ bắt lỗi và xử lý ngoại lệ tập trung (@RestControllerAdvice ApiExceptionHandler) chuyển hóa lỗi thành mã HTTP 400/404/401/403 kèm thông báo chi tiết."),
    ]
    for pkg, desc in backend_parts:
        add_dash_item(doc, pkg, desc, indent=0.4)

    add_dash_item(doc, "Phần Frontend (ReactJS + Vite)", "Tổ chức theo mô hình Component hóa hiện đại:")
    frontend_parts = [
        ("main.jsx & App.jsx", "Khởi tạo BrowserRouter, nạp Context toàn cục và cấu hình toàn bộ các tuyến đường Routes."),
        ("pages", "Chứa các trang giao diện chính (RoomCatalog, CustomerBookings, Profile, AdminRooms, AdminCustomers, AdminBookings)."),
        ("components", "Chứa các component dùng chung (AppNavBar, LoginModal, RegisterModal)."),
        ("context", "Quản lý trạng thái xác thực toàn cục, lưu giữ JWT token và thông tin người dùng trong LocalStorage (AuthContext)."),
        ("api", "Cấu hình Axios Client tự động đính kèm Bearer Token vào header và bộ bắt lỗi API tập trung (axiosClient, apiError)."),
    ]
    for folder, desc in frontend_parts:
        add_dash_item(doc, folder, desc, indent=0.4)

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
        "Tối ưu hóa vận hành cho chuỗi khách sạn mini, cho phép khách hàng chủ động tìm kiếm phòng theo nhu cầu, đặt nhiều phòng cùng lúc trong một đơn, xem chi tiết chi phí và quản lý lịch sử lưu trú; hỗ trợ bộ phận lễ tân/quản trị quản lý toàn diện phòng, khách hàng và đặt phòng."
    )
    add_dash_item(
        doc,
        "Về mặt kỹ thuật",
        "Nâng tầm giải pháp bảo mật với kiến trúc JWT Stateless hiện đại, bảo vệ tài nguyên RESTful API bằng Spring Security 6; giải quyết xuất sắc bài toán toàn vẹn dữ liệu với quy tắc xóa thông minh (xóa cứng phòng trống và xóa mềm phòng đã có giao dịch); thiết kế mô hình thực thể quan hệ trung gian có thuộc tính bổ sung (BookingDetail với composite key, actualPrice, start/end dates)."
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    # =========================================================================
    # 3. KẾT QUẢ KIỂM THỬ GIAO DIỆN VÀ MINH CHỨNG CHỨC NĂNG (27 BƯỚC)
    # =========================================================================
    doc.add_page_break()
    h3 = doc.add_heading("3. Kết quả kiểm thử giao diện & Minh chứng chức năng (27 Bước)", level=1)
    h3.paragraph_format.space_before = Pt(8)
    h3.paragraph_format.space_after = Pt(8)
    for r in h3.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    add_callout(
        doc,
        "Ghi chú hướng dẫn đọc phần kiểm thử Assignment 03",
        "Dưới đây là toàn bộ 27 bước kiểm thử thực tế của Assignment 03 trên trình duyệt với hình ảnh thực tế sắc nét. "
        "Mỗi bước được kèm theo một khung nhận định kết quả nhìn như thế nào là đúng đúng 1 câu cốt lõi giúp người đọc nắm bắt luồng chức năng nhanh nhất.",
        bg_hex="F0FDF4",
        title_color=SUCCESS_COLOR
    )

    # Render 27 steps from a3_steps
    for item in a3_steps:
        # item[0]: image path, item[1]: step title, item[2]: short summary
        add_summary_step(doc, item[0], item[1], item[2])

    # =========================================================================
    # 4. BẢNG TỔNG KẾT MA TRẬN YÊU CẦU VÀ KIỂM THỬ (TEST MATRIX)
    # =========================================================================
    doc.add_page_break()
    h4 = doc.add_heading("4. Bảng tổng kết Ma trận yêu cầu và kiểm thử Assignment 03 (Test Matrix)", level=1)
    h4.paragraph_format.space_before = Pt(8)
    h4.paragraph_format.space_after = Pt(8)
    for r in h4.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    p_mat_intro = doc.add_paragraph()
    p_mat_intro.paragraph_format.space_after = Pt(8)
    p_mat_intro.add_run(
        "Bảng ma trận đối chiếu 15 yêu cầu kỹ thuật trọng tâm của Assignment 03 với các thành phần mã nguồn và bằng chứng giao diện thực tế:"
    )

    add_a3_matrix_table(doc)

    # =========================================================================
    # 5. GIẢI THÍCH LUỒNG CHẠY HỆ THỐNG VÀ CÁC PHẦN CODE TRỌNG TÂM
    # =========================================================================
    doc.add_page_break()
    h5 = doc.add_heading("5. Giải thích luồng chạy hệ thống và các phần code trọng tâm", level=1)
    h5.paragraph_format.space_before = Pt(8)
    h5.paragraph_format.space_after = Pt(8)
    for r in h5.runs:
        r.font.color.rgb = PRIMARY_COLOR
        r.font.size = Pt(14)
        r.font.bold = True

    # 5.1. Khởi đầu kiến trúc ứng dụng từ main.jsx và App.jsx
    h5_1 = doc.add_heading("5.1. Khởi đầu kiến trúc ứng dụng từ main.jsx và App.jsx (App Entry & Routing Setup)", level=2)
    h5_1.paragraph_format.space_before = Pt(6)
    h5_1.paragraph_format.space_after = Pt(4)
    for r in h5_1.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    add_dash_item(
        doc,
        "Điểm gắn kết ứng dụng (main.jsx)",
        "Tại main.jsx dòng 8-12, ReactDOM.createRoot khởi tạo ứng dụng React và bao bọc bằng <BrowserRouter>, kết nối với DOM HTML root tại index.html."
    )
    add_dash_item(
        doc,
        "Khởi tạo phiên và định tuyến tổng thể (App.jsx)",
        "Tại App.jsx dòng 28-56, bọc toàn bộ giao diện trong <AuthProvider> (AuthContext.jsx) để cấp phát token và trạng thái người dùng toàn cục. App.jsx đồng thời render thanh điều hướng <AppNavBar> và khai báo danh sách <Routes>: định tuyến '/' chuyển hướng sang '/rooms' (RoomCatalog.jsx), '/profile' (Profile.jsx), '/my-bookings' (CustomerBookings.jsx), '/admin/rooms' (AdminRooms.jsx), '/admin/customers' (AdminCustomers.jsx), '/admin/bookings' (AdminBookings.jsx). Hai Modal đăng nhập (<LoginModal>) và đăng ký (<RegisterModal>) luôn được gắn sẵn ở cuối App.jsx để bật lên ngay khi có sự kiện nhấn nút."
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 5.2. Luồng chạy tổng quan từ sự kiện nhấn nút trên JSX đến Database
    h5_2 = doc.add_heading("5.2. Luồng chạy tổng quan từ sự kiện nhấn nút trên JSX đến Database (End-to-End Button Trigger Flow)", level=2)
    h5_2.paragraph_format.space_before = Pt(6)
    h5_2.paragraph_format.space_after = Pt(4)
    for r in h5_2.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    add_dash_item(doc, "Bước 1 (User Button Click trên JSX)", "Người dùng nhấp chuột vào một nút bấm cụ thể trên giao diện (ví dụ nút 'Login', nút 'Book This Room', nút 'Save Room', nút 'Delete' hoặc nút đổi trạng thái). Thao tác này kích hoạt trực tiếp sự kiện onClick hoặc onSubmit trên thẻ <Button> hoặc <Form> trong file JSX tương ứng.")
    add_dash_item(doc, "Bước 2 (JSX Handler Function & State Package)", "Hàm xử lý (handler function như handleSubmit, handleConfirmBooking, handleDelete) trong component JSX thu thập dữ liệu từ các React state (useState), thực hiện validate sơ bộ và gọi thư viện Axios.")
    add_dash_item(doc, "Bước 3 (Axios Interceptor & Bearer Token)", "Axios Client (axiosClient.js) tự động gắn JWT Bearer Token lấy từ localStorage vào header 'Authorization: Bearer <token>' của request. Vite Proxy chuyển tiếp request từ port 5183 sang Backend port 8083.")
    add_dash_item(doc, "Bước 4 (Security Filter Chain - JwtAuthenticationFilter)", "Tại Backend, JwtAuthenticationFilter.java chặn bắt request, trích xuất chuỗi JWT từ header Authorization. JwtTokenProvider.java kiểm tra chữ ký HMAC SHA-256: nếu hợp lệ, tạo đối tượng UsernamePasswordAuthenticationToken kèm danh sách quyền (ROLE_STAFF hoặc ROLE_CUSTOMER) và nạp vào SecurityContextHolder. SecurityConfig.java kiểm tra quyền hạn trước khi cho phép đi tiếp.")
    add_dash_item(doc, "Bước 5 (Controller Layer Mapping)", "Request đi vào đúng phương thức trong Controller được chú thích @PostMapping, @GetMapping, @PutMapping hoặc @DeleteMapping (AuthController, CustomerPortalController, PublicRoomController, AdminController). Controller nhận DTO đã được @Valid kiểm tra hợp lệ và chuyển tiếp xuống tầng Service.")
    add_dash_item(doc, "Bước 6 (Service Layer - Business Rules Execution)", "Tầng Service (RoomService, CustomerService, BookingService, AuthService) thực thi nghiệp vụ: kiểm tra ràng buộc xóa phòng mềm/cứng, tính tổng tiền đơn đặt phòng từ số đêm lưu trú và giá gốc, kiểm tra trùng lặp email khách hàng.")
    add_dash_item(doc, "Bước 7 (Repository & SQL Server Execution)", "Service gọi Repository interface (kế thừa JpaRepository). Hibernate sinh câu lệnh SQL chuẩn (SELECT, INSERT, UPDATE, DELETE) và thực thi trực tiếp trên cơ sở dữ liệu Microsoft SQL Server (database A3TranQuocThai_SE1910).")
    add_dash_item(doc, "Bước 8 (Global Exception Handling - Nếu có lỗi)", "Nếu xảy ra lỗi vi phạm ràng buộc dữ liệu hoặc không tìm thấy bản ghi, ControllerAdvice (ApiExceptionHandler.java) bắt ngoại lệ và trả về phản hồi JSON chuẩn mã HTTP 400 hoặc 404 kèm thông điệp lỗi rõ ràng.")
    add_dash_item(doc, "Bước 9 (Response & Re-render React UI)", "Backend trả về ResponseEntity HTTP 200/201 kèm dữ liệu JSON. Frontend JSX nhận kết quả qua Promise/await, cập nhật lại state (useState), đóng Modal popup, hiển thị Alert thông báo và tự động re-render bảng dữ liệu mới.")

    doc.add_paragraph().paragraph_format.space_after = Pt(4)

    # 5.3. Chi tiết các luồng nghiệp vụ trọng tâm
    h5_3 = doc.add_heading("5.3. Chi tiết các luồng nghiệp vụ trọng tâm (Truy vết từ dòng nhấn nút trên JSX đến mã nguồn Backend)", level=2)
    h5_3.paragraph_format.space_before = Pt(6)
    h5_3.paragraph_format.space_after = Pt(4)
    for r in h5_3.runs:
        r.font.color.rgb = SECONDARY_COLOR
        r.font.size = Pt(12)
        r.font.bold = True

    add_dash_item(
        doc,
        "Luồng 1 - Đăng nhập và cấp phát JWT (Login & JWT Authentication Flow)",
        "Từ App.jsx -> AppNavBar.jsx người dùng nhấn nút 'Login (Staff / Customer)' tại dòng 58 (onClick={onOpenLogin}) làm state showLogin=true, kích hoạt mở LoginModal.jsx -> Người dùng nhập Email, Password và nhấn nút 'Login' tại dòng 84 của LoginModal.jsx kích hoạt onSubmit={handleSubmit} (dòng 20) -> Gọi hàm login(email, password) trong AuthContext.jsx -> Gửi POST /api/auth/login -> AuthController.java (phương thức login, dòng 28) nhận AuthLoginRequest -> Gọi AuthService.login() kiểm tra Staff từ StaffProperties hoặc tìm Customer trong SQL Server qua CustomerRepository -> Gọi JwtTokenProvider.generateToken() tạo JWT Bearer Token -> Trả về AuthResponse -> AuthContext.jsx lưu token vào localStorage và setUser() -> AppNavBar.jsx re-render mở rộng menu tương ứng vai trò."
    )
    add_dash_item(
        doc,
        "Luồng 2 - Khách hàng đặt phòng trực tuyến đa phòng (Online Multi-room Booking Flow)",
        "Tại RoomCatalog.jsx, khách hàng xem danh sách phòng và nhấn nút 'Book This Room' tại dòng 167 (onClick={() => handleOpenBooking(room)}) -> Modal đặt phòng mở ra -> Khách hàng chọn ngày nhận/trả phòng, tick chọn thêm phòng phụ tại dòng 219 (onChange={(e) => toggleExtraRoom(r.roomId, e.target.checked)}) -> Nhấn nút 'Confirm Reservation' tại dòng 274 kích hoạt onSubmit={handleConfirmBooking} (dòng 71) -> Gửi POST /api/customer/bookings kèm Bearer Token -> JwtAuthenticationFilter xác thực ROLE_CUSTOMER -> CustomerPortalController.java (phương thức createBooking, dòng 33) -> BookingService.java (createBooking) kiểm tra ngày hợp lệ, trích xuất giá phòng từ database, tính tổng tiền, tạo BookingReservation và danh sách BookingDetail với khóa phức hợp BookingDetailId -> BookingReservationRepository và BookingDetailRepository ghi dữ liệu vào SQL Server -> Trả về HTTP 201 -> RoomCatalog.jsx hiển thị Alert xanh và reset form."
    )
    add_dash_item(
        doc,
        "Luồng 3 - Quy tắc xóa phòng thông minh: Xóa cứng vs Xóa mềm (Room Delete Logic Flow)",
        "Tại AdminRooms.jsx, Staff nhấn nút 'Delete' tại dòng 175 (onClick={() => handleDelete(r.roomId)}) -> Hàm handleDelete (dòng 108) bật hộp thoại window.confirm() xác nhận -> Nhấn OK gửi DELETE /api/admin/rooms/{id} kèm Bearer Token Staff -> JwtAuthenticationFilter xác thực ROLE_STAFF -> AdminController.java (deleteRoom, dòng 48) -> RoomService.java (phương thức deleteRoom, dòng 78) kiểm tra: if (bookingDetailRepository.existsByRoomInformation(room)) -> [Trường hợp 1]: Phòng chưa từng có booking detail -> Gọi roomRepository.delete(room) (Xóa cứng - Hard Delete khỏi SQL Server); [Trường hợp 2]: Phòng đã tồn tại trong booking detail -> Gán room.setRoomStatus((byte) 2) và gọi roomRepository.save(room) (Xóa mềm - Soft Delete chuyển trạng thái Inactive để bảo toàn hóa đơn) -> AdminRooms.jsx nhận phản hồi, hiển thị alert thành công và gọi fetchRooms() tải lại bảng."
    )
    add_dash_item(
        doc,
        "Luồng 4 - Quản lý hồ sơ cá nhân và tra cứu lịch sử đặt phòng của Khách hàng",
        "Khách hàng nhấn menu 'My Profile' trên AppNavBar.jsx (dòng 29) -> Chuyển đến Profile.jsx -> Hook useEffect gọi GET /api/customer/profile -> CustomerPortalController.getProfile() nạp dữ liệu lên form -> Khách hàng sửa thông tin và nhấn nút 'Save Profile' tại dòng 108 (onSubmit={save}) -> Gửi PUT /api/customer/profile cập nhật thông tin và mật khẩu vào SQL Server. Khách hàng nhấn menu 'My Bookings' (dòng 30) -> Chuyển đến CustomerBookings.jsx -> useEffect gọi GET /api/customer/bookings -> CustomerPortalController.getMyBookings() -> bookingReservationRepository.findByCustomerOrderByBookingDateDesc() truy vấn toàn bộ lịch sử đơn đặt kèm chi tiết phòng hiển thị ra bảng."
    )
    add_dash_item(
        doc,
        "Luồng 5 - Quản trị đơn đặt và cập nhật trạng thái đơn (Staff Bookings Management Flow)",
        "Staff nhấn menu 'Manage Reservations' trên AppNavBar.jsx (dòng 40) -> Chuyển đến AdminBookings.jsx -> useEffect gọi GET /api/admin/bookings -> AdminController.getAllBookings() nạp bảng đơn đặt. Khi Staff nhấn nút 'Confirm' tại dòng 244 hoặc 'Complete' tại dòng 254 kích hoạt onClick={() => updateStatus(b.bookingReservationId, newStatus)} (dòng 92) -> Gửi PUT /api/admin/bookings/{id}/status?status={status} -> AdminController.updateBookingStatus() gọi bookingService.updateBookingStatus() cập nhật trường booking_status trong SQL Server -> Giao diện tự động đổi màu Badge trạng thái tức thì."
    )
    add_dash_item(
        doc,
        "Luồng 6 - Khách vãng lai xem và tìm kiếm danh mục phòng công khai (Public Room Browsing Flow)",
        "Người dùng truy cập ứng dụng -> App.jsx định tuyến mặc định sang '/rooms' nạp RoomCatalog.jsx -> Hook useEffect tự động gọi hàm fetchRooms() (dòng 43) -> Gửi axios.get('/api/rooms/public') -> PublicRoomController.java (getActiveRooms, dòng 20) -> RoomService.getActiveRooms() -> RoomInformationRepository.findByRoomStatus((byte) 1) truy vấn các phòng Active từ SQL Server -> Frontend nhận mảng JSON, setRooms(res.data) và dùng hàm .map() render thành lưới các thẻ Card phòng trực quan."
    )

    doc.add_paragraph().paragraph_format.space_after = Pt(12)

    # Lưu tài liệu ra assignment3-tomtat.docx trước
    out_file3 = os.path.abspath("assignment3-tomtat.docx")
    doc.save(out_file3)
    print(f"Assignment 3 summary document updated successfully at: {out_file3}")

    # Copy / Lưu thêm bản vào noteTT/assignment3-tomtat.docx
    target_noteTT = os.path.abspath("noteTT/assignment3-tomtat.docx")
    try:
        doc.save(target_noteTT)
        print(f"Also saved successfully to: {target_noteTT}")
    except PermissionError:
        fallback = os.path.abspath("noteTT/assignment3-tomtat_updated.docx")
        doc.save(fallback)
        print(f"noteTT/assignment3-tomtat.docx was locked. Saved fallback to: {fallback}")

if __name__ == "__main__":
    build_assignment3_tomtat_doc()
