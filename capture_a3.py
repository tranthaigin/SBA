import os
import sys
import time
from playwright.sync_api import sync_playwright

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

output_dir = os.path.abspath("noteTT/screenshots_a3")
os.makedirs(output_dir, exist_ok=True)

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(channel="msedge", headless=True)
        context = browser.new_context(viewport={"width": 1366, "height": 820})
        page = context.new_page()

        # Auto accept confirm dialogs
        page.on("dialog", lambda dialog: dialog.accept())

        # 1. Public Rooms Catalog
        page.goto("http://localhost:5183/rooms")
        page.wait_for_selector(".card")
        time.sleep(1.0)
        page.screenshot(path=os.path.join(output_dir, "01_a3_public_rooms_catalog.png"))
        print("Captured 01_a3_public_rooms_catalog.png")

        # 2. Room Details View
        page.screenshot(path=os.path.join(output_dir, "02_a3_room_details_view.png"))
        print("Captured 02_a3_room_details_view.png")

        # 3. Login Modal
        page.click("button:has-text('Login')")
        page.wait_for_selector(".modal-dialog")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "03_a3_login_modal.png"))
        print("Captured 03_a3_login_modal.png")

        # 4. Login Validation Error
        page.fill("input[type='email']", "customer@hotel.com")
        page.fill("input[type='password']", "wrongpassword999")
        page.click(".modal-footer button[type='submit']")
        page.wait_for_selector(".modal-body .alert-danger")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "04_a3_login_validation_error.png"))
        print("Captured 04_a3_login_validation_error.png")

        # 5. Register Modal
        page.click("button:has-text('Register here')")
        page.wait_for_selector(".modal-title:has-text('Customer Registration')")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "05_a3_register_modal.png"))
        print("Captured 05_a3_register_modal.png")

        # Switch back to Login and sign in as Customer
        page.click("button:has-text('Already have an account? Sign in')")
        page.wait_for_selector(".modal-title:has-text('Login to FU Mini Hotel')")
        page.click("button:has-text('Fill Customer Credentials')")
        time.sleep(0.4)
        page.click(".modal-footer button[type='submit']")
        time.sleep(1.0)

        # 6. Customer Login Success
        page.screenshot(path=os.path.join(output_dir, "06_a3_customer_login_success.png"))
        print("Captured 06_a3_customer_login_success.png")

        # 7. Customer Booking Modal
        page.click(".card button:has-text('Book This Room')")
        page.wait_for_selector(".modal-dialog")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "07_a3_customer_booking_modal.png"))
        print("Captured 07_a3_customer_booking_modal.png")

        # 8. Customer Booking Multi Room (if extra rooms checkbox exists)
        checkboxes = page.query_selector_all(".modal-body input[type='checkbox']")
        if len(checkboxes) > 0:
            checkboxes[0].check()
            time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "08_a3_customer_booking_multi_room.png"))
        print("Captured 08_a3_customer_booking_multi_room.png")

        # 9. Customer Booking Confirmed
        page.click(".modal-footer button:has-text('Confirm Reservation')")
        time.sleep(1.2)
        page.screenshot(path=os.path.join(output_dir, "09_a3_customer_booking_confirmed.png"))
        print("Captured 09_a3_customer_booking_confirmed.png")

        # 10. Customer Profile View
        page.click("a.nav-link:has-text('My Profile')")
        page.wait_for_selector("input[aria-label='Name']")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "10_a3_customer_profile_view.png"))
        print("Captured 10_a3_customer_profile_view.png")

        # 11. Customer Booking History
        page.click("a.nav-link:has-text('My Bookings')")
        page.wait_for_selector(".table, .card")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "11_a3_customer_booking_history.png"))
        print("Captured 11_a3_customer_booking_history.png")

        # Logout Customer
        page.click("button:has-text('Logout')")
        time.sleep(0.8)

        # 12. Staff Login
        page.click("button:has-text('Login')")
        page.wait_for_selector(".modal-dialog")
        page.click("button:has-text('Fill Staff Credentials')")
        time.sleep(0.4)
        page.screenshot(path=os.path.join(output_dir, "12_a3_staff_login.png"))
        print("Captured 12_a3_staff_login.png")

        page.click(".modal-footer button[type='submit']")
        time.sleep(1.0)

        # 13. Staff Navbar Menu
        page.screenshot(path=os.path.join(output_dir, "13_a3_staff_navbar_menu.png"))
        print("Captured 13_a3_staff_navbar_menu.png")

        # 14. Admin Rooms List
        page.click("a.nav-link:has-text('Manage Rooms')")
        page.wait_for_selector("table")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "14_a3_admin_rooms_list.png"))
        print("Captured 14_a3_admin_rooms_list.png")

        # 15. Admin Room Search
        page.fill("input[placeholder*='Search']", "Suite")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "15_a3_admin_room_search.png"))
        print("Captured 15_a3_admin_room_search.png")
        page.fill("input[placeholder*='Search']", "")
        time.sleep(0.5)

        # 16. Admin Create Room Modal
        page.click("button:has-text('Add New Room')")
        page.wait_for_selector(".modal-dialog")
        time.sleep(0.5)
        # fill room number and detail
        page.fill("input#roomNumber, .modal-body input[type='text']", "999")
        page.fill("textarea", "Penthouse Luxury Suite with Panoramic City View")
        time.sleep(0.4)
        page.screenshot(path=os.path.join(output_dir, "16_a3_admin_create_room_modal.png"))
        print("Captured 16_a3_admin_create_room_modal.png")

        # 17. Admin Create Room Success
        page.click(".modal-footer button:has-text('Save Room')")
        time.sleep(1.2)
        page.screenshot(path=os.path.join(output_dir, "17_a3_admin_create_room_success.png"))
        print("Captured 17_a3_admin_create_room_success.png")

        # 18. Admin Edit Room Modal
        edit_buttons = page.query_selector_all("table button:has-text('Edit')")
        if len(edit_buttons) > 0:
            edit_buttons[0].click()
            page.wait_for_selector(".modal-dialog")
            time.sleep(0.5)
            page.screenshot(path=os.path.join(output_dir, "18_a3_admin_edit_room_modal.png"))
            print("Captured 18_a3_admin_edit_room_modal.png")
            page.click(".modal-header button.btn-close")
            time.sleep(0.5)

        # 19. Admin Delete Room Hard (delete room 999 which has no bookings)
        page.fill("input[placeholder*='Search']", "999")
        time.sleep(0.6)
        del_btn = page.query_selector("table button:has-text('Delete')")
        if del_btn:
            del_btn.click()
            time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "19_a3_admin_delete_room_hard.png"))
        print("Captured 19_a3_admin_delete_room_hard.png")
        page.fill("input[placeholder*='Search']", "")
        time.sleep(0.5)

        # 20. Admin Delete Room Soft (delete room that is reserved, status turns to Inactive)
        # Search for room 101 or 102
        del_btn_res = page.query_selector("table button:has-text('Delete')")
        if del_btn_res:
            del_btn_res.click()
            time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "20_a3_admin_delete_room_soft.png"))
        print("Captured 20_a3_admin_delete_room_soft.png")

        # 21. Admin Customers List
        page.click("a.nav-link:has-text('Manage Customers')")
        page.wait_for_selector("table")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "21_a3_admin_customers_list.png"))
        print("Captured 21_a3_admin_customers_list.png")

        # 22. Admin Customer Search
        page.fill("input[placeholder*='Search']", "Customer")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "22_a3_admin_customer_search.png"))
        print("Captured 22_a3_admin_customer_search.png")
        page.fill("input[placeholder*='Search']", "")
        time.sleep(0.5)

        # 23. Admin Create Customer Modal
        page.click("button:has-text('Add New Customer')")
        page.wait_for_selector(".modal-dialog")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "23_a3_admin_create_customer_modal.png"))
        print("Captured 23_a3_admin_create_customer_modal.png")
        page.click(".modal-header button.btn-close")
        time.sleep(0.4)

        # 24. Admin Edit Customer Modal
        edit_cust = page.query_selector("table button:has-text('Edit')")
        if edit_cust:
            edit_cust.click()
            page.wait_for_selector(".modal-dialog")
            time.sleep(0.5)
            page.screenshot(path=os.path.join(output_dir, "24_a3_admin_edit_customer_modal.png"))
            print("Captured 24_a3_admin_edit_customer_modal.png")
            page.click(".modal-header button.btn-close")
            time.sleep(0.4)

        # 25. Admin Bookings List
        page.click("a.nav-link:has-text('Manage Reservations')")
        page.wait_for_selector("table")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "25_a3_admin_bookings_list.png"))
        print("Captured 25_a3_admin_bookings_list.png")

        # 26. Admin Create Booking Modal
        page.click("button:has-text('Create Booking')")
        page.wait_for_selector(".modal-dialog")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "26_a3_admin_create_booking_modal.png"))
        print("Captured 26_a3_admin_create_booking_modal.png")
        page.click(".modal-header button.btn-close")
        time.sleep(0.4)

        # 27. Admin Booking Status Update
        status_btn = page.query_selector("table button:has-text('Complete')") or page.query_selector("table button:has-text('Confirm')")
        if status_btn:
            status_btn.click()
            time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "27_a3_admin_update_booking_status.png"))
        print("Captured 27_a3_admin_update_booking_status.png")

        browser.close()
        print("All A3 screenshots captured successfully!")

if __name__ == "__main__":
    run()
