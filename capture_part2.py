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

        page.on("dialog", lambda dialog: dialog.accept())

        # Go to rooms and login as staff
        page.goto("http://localhost:5183/rooms")
        page.wait_for_selector(".card")
        page.click("button:has-text('Login')")
        page.wait_for_selector(".modal-dialog")
        page.click("button:has-text('Fill Staff Credentials')")
        time.sleep(0.3)
        page.click(".modal-footer button[type='submit']")
        page.wait_for_selector("a.nav-link:has-text('Manage Rooms')")
        time.sleep(0.5)

        # 16. Admin Create Room Modal
        page.click("a.nav-link:has-text('Manage Rooms')")
        page.wait_for_selector("table")
        page.click("button:has-text('+ Add New Room')")
        page.wait_for_selector(".modal-dialog")
        time.sleep(0.5)
        # Fill room fields
        inputs = page.query_selector_all(".modal-body input")
        if len(inputs) > 0:
            inputs[0].fill("999")
        page.select_option(".modal-body select", index=1)
        page.fill(".modal-body textarea", "Penthouse Luxury Suite with Panoramic City View")
        time.sleep(0.5)
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

        # 19. Admin Delete Room Hard (delete the newly created room 999)
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
        page.click("button:has-text('+ Add New Customer')")
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
        print("Part 2 captured successfully!")

if __name__ == "__main__":
    run()

