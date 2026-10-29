import os
import sys
import time
from playwright.sync_api import sync_playwright

if hasattr(sys.stdout, 'reconfigure'):
    sys.stdout.reconfigure(encoding='utf-8')

output_dir = os.path.abspath("noteTT/screenshots_a2")
os.makedirs(output_dir, exist_ok=True)

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(channel="msedge", headless=True)
        context = browser.new_context(viewport={"width": 1366, "height": 820})
        page = context.new_page()

        # Handle browser confirmation dialogs automatically
        page.on("dialog", lambda dialog: dialog.accept())

        # 1. Public News Reading
        page.goto("http://localhost:5182/news")
        page.wait_for_selector(".card")
        time.sleep(1.0)
        page.screenshot(path=os.path.join(output_dir, "01_a2_public_news_list.png"))
        print("Captured 01_a2_public_news_list.png")

        # 2. Public News Search
        page.fill("input[placeholder*='Search news']", "student")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "02_a2_public_news_search.png"))
        print("Captured 02_a2_public_news_search.png")
        page.fill("input[placeholder*='Search news']", "")
        time.sleep(0.5)

        # 3. Login Modal
        page.click("button:has-text('Login (Staff / Admin)')")
        page.wait_for_selector(".modal-dialog")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "03_a2_login_modal.png"))
        print("Captured 03_a2_login_modal.png")

        # 4. Login Validation Error
        page.fill("input[type='email']", "staff@funews.com")
        page.fill("input[type='password']", "wrongpass123")
        page.click(".modal-footer button[type='submit']")
        page.wait_for_selector(".modal-body .alert-danger")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "04_a2_login_validation_error.png"))
        print("Captured 04_a2_login_validation_error.png")

        # 5. Staff Login Success
        page.fill("input[type='password']", "@1")
        page.click(".modal-footer button[type='submit']")
        page.wait_for_selector("span.badge:has-text('Staff')")
        time.sleep(1.0)
        page.screenshot(path=os.path.join(output_dir, "05_a2_staff_login_success.png"))
        print("Captured 05_a2_staff_login_success.png")

        # 6. Staff Category List
        page.click("a.nav-link:has-text('Categories')")
        page.wait_for_selector("table")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "06_a2_category_list.png"))
        print("Captured 06_a2_category_list.png")

        # 7. Category Create Modal
        page.click("button:has-text('Create Category')")
        page.wait_for_selector(".modal-dialog")
        page.locator("input[name='categoryName']").fill("Artificial Intelligence")
        page.locator("textarea[name='categoryDescription']").fill("Research and development in generative AI and models")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "07_a2_category_create_modal.png"))
        print("Captured 07_a2_category_create_modal.png")

        # 8. Category Create Success
        page.click(".modal-footer button[type='submit']")
        page.wait_for_selector("tr:has-text('Artificial Intelligence')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "08_a2_category_create_success.png"))
        print("Captured 08_a2_category_create_success.png")

        # 9. Category Update Modal
        ai_row = page.locator("tr:has-text('Artificial Intelligence')").first
        ai_row.locator("button:has-text('Edit')").first.click()
        page.wait_for_selector(".modal-dialog")
        page.locator("input[name='categoryName']").fill("Artificial Intelligence & Data")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "09_a2_category_update_modal.png"))
        print("Captured 09_a2_category_update_modal.png")

        # 10. Category Update Success
        page.click(".modal-footer button[type='submit']")
        page.wait_for_selector("tr:has-text('Artificial Intelligence & Data')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "10_a2_category_update_success.png"))
        print("Captured 10_a2_category_update_success.png")

        # 11. Category Delete Prevent In Use
        first_row = page.locator("table tbody tr").first
        first_row.locator("button:has-text('Delete')").click()
        page.wait_for_selector(".alert-danger")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "11_a2_category_delete_prevent_in_use.png"))
        print("Captured 11_a2_category_delete_prevent_in_use.png")

        # 12. Category Delete Success
        ai_data_row = page.locator("tr:has-text('Artificial Intelligence & Data')").first
        ai_data_row.locator("button:has-text('Delete')").first.click()
        time.sleep(1.0)
        page.screenshot(path=os.path.join(output_dir, "12_a2_category_delete_success.png"))
        print("Captured 12_a2_category_delete_success.png")

        # 13. Category Search
        page.fill("input[placeholder*='Search categories']", "tech")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "13_a2_category_search.png"))
        print("Captured 13_a2_category_search.png")
        page.fill("input[placeholder*='Search categories']", "")
        time.sleep(0.5)

        # 14. Staff News Management List
        page.click("a.nav-link:has-text('News Articles')")
        page.wait_for_selector("button:has-text('+ Create News Article')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "14_a2_news_management_list.png"))
        print("Captured 14_a2_news_management_list.png")

        # 15. News Create Modal with Tags
        page.click("button:has-text('+ Create News Article')")
        page.wait_for_selector(".modal-dialog")
        page.locator(".modal-body input[type='text']").first.fill("Cloud Computing Workshop 2026")
        page.locator(".modal-body input[type='text']").nth(1).fill("Hands-on modern cloud deployment architecture")
        page.locator(".modal-body textarea").first.fill("Comprehensive workshop for software engineering students covering Spring Boot microservices, containerization, and React SPA integration.")
        page.locator(".modal-body input[type='text']").nth(2).fill("FPT University Tech Center")
        # Toggle a tag button
        first_tag = page.locator(".modal-body button:has-text('#')").first
        if first_tag.count() > 0:
            first_tag.click()
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "15_a2_news_create_modal_tags.png"))
        print("Captured 15_a2_news_create_modal_tags.png")

        # 16. News Create Success
        page.click(".modal-footer button:has-text('Create Article')")
        page.wait_for_selector(".card:has-text('Cloud Computing Workshop 2026')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "16_a2_news_create_success.png"))
        print("Captured 16_a2_news_create_success.png")

        # 17. News Detail Read Modal
        cloud_card = page.locator(".card:has-text('Cloud Computing Workshop 2026')").first
        cloud_card.locator("button:has-text('Read')").first.click()
        page.wait_for_selector(".modal-dialog")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "17_a2_news_detail_read_modal.png"))
        print("Captured 17_a2_news_detail_read_modal.png")
        page.click(".modal-footer button:has-text('Close')")
        time.sleep(0.4)

        # 18. News Update Modal
        cloud_card.locator("button:has-text('Edit')").first.click()
        page.wait_for_selector(".modal-dialog")
        page.locator(".modal-body input[type='text']").first.fill("Cloud & DevOps Summit 2026 - Extended")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "18_a2_news_update_modal.png"))
        print("Captured 18_a2_news_update_modal.png")

        # 19. News Update Success
        page.click(".modal-footer button:has-text('Save Changes')")
        page.wait_for_selector(".card:has-text('Cloud & DevOps Summit 2026 - Extended')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "19_a2_news_update_success.png"))
        print("Captured 19_a2_news_update_success.png")

        # 20. News Delete Success
        cloud_updated_card = page.locator(".card:has-text('Cloud & DevOps Summit 2026 - Extended')").first
        cloud_updated_card.locator("button:has-text('Delete')").first.click()
        time.sleep(1.0)
        page.screenshot(path=os.path.join(output_dir, "20_a2_news_delete_success.png"))
        print("Captured 20_a2_news_delete_success.png")

        # 21. Staff Profile
        page.click("a.nav-link:has-text('My Profile')")
        page.wait_for_selector("form")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "21_a2_staff_profile.png"))
        print("Captured 21_a2_staff_profile.png")

        # 22. Staff My History
        page.click("a.nav-link:has-text('My News History')")
        page.wait_for_selector("h2:has-text('My Publishing History')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "22_a2_staff_my_history.png"))
        print("Captured 22_a2_staff_my_history.png")

        # 23. Logout & Admin Login
        page.click("button:has-text('Logout')")
        time.sleep(0.6)
        page.click("button:has-text('Login (Staff / Admin)')")
        page.wait_for_selector(".modal-dialog")
        page.click("button:has-text('Fill Admin Credentials')")
        page.click(".modal-footer button[type='submit']")
        page.wait_for_selector("span.badge:has-text('Admin')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "23_a2_admin_login_success.png"))
        print("Captured 23_a2_admin_login_success.png")

        # 24. Admin Account List
        page.click("a.nav-link:has-text('Accounts Management')")
        page.wait_for_selector("table")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "24_a2_account_list.png"))
        print("Captured 24_a2_account_list.png")

        # 25. Account Create Modal
        page.click("button:has-text('+ Create Account')")
        page.wait_for_selector(".modal-dialog")
        page.locator(".modal-body input[type='text']").first.fill("Thai Assistant")
        page.locator(".modal-body input[type='email']").first.fill("assistant_thai@funews.com")
        page.locator(".modal-body input[type='password']").first.fill("secret123")
        page.locator(".modal-body select").first.select_option("2")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "25_a2_account_create_modal.png"))
        print("Captured 25_a2_account_create_modal.png")

        # 26. Account Create Success
        page.click(".modal-footer button:has-text('Create Account')")
        page.wait_for_selector("tr:has-text('assistant_thai@funews.com')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "26_a2_account_create_success.png"))
        print("Captured 26_a2_account_create_success.png")

        # 27. Account Validation Duplicate Email
        page.click("button:has-text('+ Create Account')")
        page.wait_for_selector(".modal-dialog")
        page.locator(".modal-body input[type='text']").first.fill("Thai Clone")
        page.locator(".modal-body input[type='email']").first.fill("assistant_thai@funews.com")
        page.locator(".modal-body input[type='password']").first.fill("secret123")
        page.click(".modal-footer button:has-text('Create Account')")
        page.wait_for_selector(".modal-body .alert-danger, .container .alert-danger")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "27_a2_account_validation_email_duplicate.png"))
        print("Captured 27_a2_account_validation_email_duplicate.png")
        page.click(".modal-footer button:has-text('Cancel')")
        time.sleep(0.5)

        # 28. Account Update Modal
        thai_row = page.locator("tr:has-text('assistant_thai@funews.com')").first
        thai_row.locator("button:has-text('Edit')").first.click()
        page.wait_for_selector(".modal-dialog")
        page.locator(".modal-body select").select_option("1")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "28_a2_account_update_modal.png"))
        print("Captured 28_a2_account_update_modal.png")

        # 29. Account Update Success
        page.click(".modal-footer button:has-text('Save Changes')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "29_a2_account_update_success.png"))
        print("Captured 29_a2_account_update_success.png")

        # 30. Account Delete Prevent When In Use
        staff_row = page.locator("tr:has-text('staff@funews.com')").first
        staff_row.locator("button:has-text('Delete')").first.click()
        page.wait_for_selector(".alert-danger")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "30_a2_account_delete_prevent_in_use.png"))
        print("Captured 30_a2_account_delete_prevent_in_use.png")

        # 31. Account Delete Success (delete assistant_thai)
        assistant_row = page.locator("tr:has-text('assistant_thai@funews.com')").first
        assistant_row.locator("button:has-text('Delete')").first.click()
        time.sleep(1.0)
        page.screenshot(path=os.path.join(output_dir, "31_a2_account_delete_success.png"))
        print("Captured 31_a2_account_delete_success.png")

        # 32. Account Search
        page.fill("input[placeholder*='Search accounts']", "staff")
        time.sleep(0.6)
        page.screenshot(path=os.path.join(output_dir, "32_a2_account_search.png"))
        print("Captured 32_a2_account_search.png")
        page.fill("input[placeholder*='Search accounts']", "")
        time.sleep(0.5)

        # 33. Logout
        page.click("button:has-text('Logout')")
        time.sleep(0.8)
        page.screenshot(path=os.path.join(output_dir, "33_a2_logout_success.png"))
        print("Captured 33_a2_logout_success.png")

        browser.close()
        print("All 33 screenshots for Assignment 02 captured successfully!")

if __name__ == "__main__":
    run()
