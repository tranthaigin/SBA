import os
import time
from playwright.sync_api import sync_playwright

output_dir = os.path.abspath("noteTT/screenshots_a1")
os.makedirs(output_dir, exist_ok=True)

def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(channel="msedge", headless=True)
        context = browser.new_context(viewport={"width": 1366, "height": 820})
        page = context.new_page()

        # 1. Login initial
        page.goto("http://localhost:5173")
        page.evaluate("() => localStorage.clear()")
        page.reload()
        page.wait_for_selector(".login-panel")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "01_login_initial.png"))
        print("Captured 01_login_initial.png")

        # 2. Login empty error
        page.click("button[type='submit']")
        page.wait_for_selector(".alert-error")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "02_login_empty_error.png"))
        print("Captured 02_login_empty_error.png")

        # 3. Login wrong credentials
        page.fill("#username", "WrongUser")
        page.fill("#password", "WrongPass")
        page.click("button[type='submit']")
        page.wait_for_selector(".alert-error")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "03_login_wrong_credentials.png"))
        print("Captured 03_login_wrong_credentials.png")

        # 4. Login success -> Dashboard
        page.fill("#username", "Admin")
        page.fill("#password", "Admin")
        page.click("button[type='submit']")
        page.wait_for_selector(".welcome-card")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "04_login_success_dashboard.png"))
        print("Captured 04_login_success_dashboard.png")

        # 5. Layout Sidebar & Header overview
        page.screenshot(path=os.path.join(output_dir, "05_layout_sidebar_header.png"))
        print("Captured 05_layout_sidebar_header.png")

        # 6. Dashboard stats and latest news
        page.screenshot(path=os.path.join(output_dir, "06_dashboard_stats_and_news.png"))
        print("Captured 06_dashboard_stats_and_news.png")

        # --- CATEGORY MANAGEMENT ---
        # 7. Category list
        page.click("nav.main-nav button:has-text('Category')")
        page.wait_for_selector("table")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "07_category_list.png"))
        print("Captured 07_category_list.png")

        # 8. Category create modal
        page.click("button:has-text('+ Add category')")
        page.wait_for_selector(".modal-card")
        page.fill("#category-name", "Sports")
        page.select_option("#category-status", "1")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "08_category_create_modal.png"))
        print("Captured 08_category_create_modal.png")

        # 9. Category create success
        page.click(".modal-actions button[type='submit']")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "09_category_create_success.png"))
        print("Captured 09_category_create_success.png")

        # 10. Category validation duplicate
        page.click("button:has-text('+ Add category')")
        page.wait_for_selector(".modal-card")
        page.fill("#category-name", "Sports")
        page.click(".modal-actions button[type='submit']")
        page.wait_for_selector(".modal-card .alert-error")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "10_category_validation_duplicate.png"))
        print("Captured 10_category_validation_duplicate.png")
        page.click(".modal-header button.icon-button") # Close modal

        # 11. Category update modal
        # Find row with "Sports"
        sports_row = page.locator("tr:has-text('Sports')")
        sports_row.locator("button:has-text('Edit')").click()
        page.wait_for_selector(".modal-card")
        page.fill("#category-name", "Sports & Recreation")
        page.select_option("#category-status", "0")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "11_category_update_modal.png"))
        print("Captured 11_category_update_modal.png")

        # 12. Category update success
        page.click(".modal-actions button[type='submit']")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "12_category_update_success.png"))
        print("Captured 12_category_update_success.png")

        # 13. Category delete prevent when in use
        tech_row = page.locator("tr:has-text('Technology')")
        tech_row.locator("button:has-text('Delete')").click()
        page.wait_for_selector(".modal-card")
        page.click(".modal-actions button.button-danger")
        page.wait_for_selector(".page-stack .alert-error")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "13_category_delete_prevent_in_use.png"))
        print("Captured 13_category_delete_prevent_in_use.png")

        # 14. Category delete confirm dialog
        sports_updated_row = page.locator("tr:has-text('Sports & Recreation')")
        sports_updated_row.locator("button:has-text('Delete')").click()
        page.wait_for_selector(".modal-card")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "14_category_delete_confirm_dialog.png"))
        print("Captured 14_category_delete_confirm_dialog.png")

        # 15. Category delete success
        page.click(".modal-actions button.button-danger")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "15_category_delete_success.png"))
        print("Captured 15_category_delete_success.png")

        # 16. Category search success
        page.fill(".search-box input", "Edu")
        time.sleep(0.4)
        page.screenshot(path=os.path.join(output_dir, "16_category_search_success.png"))
        print("Captured 16_category_search_success.png")

        # 17. Category search empty
        page.fill(".search-box input", "NonExistentCategory999")
        time.sleep(0.4)
        page.screenshot(path=os.path.join(output_dir, "17_category_search_empty.png"))
        print("Captured 17_category_search_empty.png")
        page.click("button:has-text('Clear search')")
        time.sleep(0.3)

        # --- NEWS MANAGEMENT ---
        # 18. News list
        page.click("nav.main-nav button:has-text('News')")
        page.wait_for_selector("table")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "18_news_list.png"))
        print("Captured 18_news_list.png")

        # 19. News create modal
        page.click("button:has-text('+ Add news')")
        page.wait_for_selector(".modal-card")
        page.fill("#news-title", "AI Innovation Summit 2026")
        page.fill("#news-content", "FPT University organizes the annual artificial intelligence summit for researchers and software engineering students.")
        page.select_option("#news-category", "2")
        page.select_option("#news-status", "1")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "19_news_create_modal.png"))
        print("Captured 19_news_create_modal.png")

        # 20. News create success
        page.click(".modal-actions button[type='submit']")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "20_news_create_success.png"))
        print("Captured 20_news_create_success.png")

        # 21. News validation empty
        page.click("button:has-text('+ Add news')")
        page.wait_for_selector(".modal-card")
        page.fill("#news-title", "")
        page.fill("#news-content", "")
        page.click(".modal-actions button[type='submit']")
        page.wait_for_selector(".modal-card .alert-error")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "21_news_validation_empty.png"))
        print("Captured 21_news_validation_empty.png")
        page.click(".modal-header button.icon-button")

        # 22. News update modal
        ai_row = page.locator("tr:has-text('AI Innovation Summit 2026')")
        ai_row.locator("button:has-text('Edit')").click()
        page.wait_for_selector(".modal-card")
        page.fill("#news-title", "AI & Robotics Summit 2026 - Extended")
        page.select_option("#news-category", "1")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "22_news_update_modal.png"))
        print("Captured 22_news_update_modal.png")

        # 23. News update success
        page.click(".modal-actions button[type='submit']")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "23_news_update_success.png"))
        print("Captured 23_news_update_success.png")

        # 24. News delete confirm dialog
        ai_updated_row = page.locator("tr:has-text('AI & Robotics Summit 2026 - Extended')")
        ai_updated_row.locator("button:has-text('Delete')").click()
        page.wait_for_selector(".modal-card")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "24_news_delete_confirm_dialog.png"))
        print("Captured 24_news_delete_confirm_dialog.png")

        # 25. News delete success
        page.click(".modal-actions button.button-danger")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "25_news_delete_success.png"))
        print("Captured 25_news_delete_success.png")

        # 26. News search success
        page.fill(".search-box input", "orientation")
        time.sleep(0.4)
        page.screenshot(path=os.path.join(output_dir, "26_news_search_success.png"))
        print("Captured 26_news_search_success.png")

        # 27. News search empty
        page.fill(".search-box input", "UnknownArticleKeyword123")
        time.sleep(0.4)
        page.screenshot(path=os.path.join(output_dir, "27_news_search_empty.png"))
        print("Captured 27_news_search_empty.png")
        page.click("button:has-text('Clear search')")
        time.sleep(0.3)

        # --- USERS MANAGEMENT ---
        # 28. Users list
        page.click("nav.main-nav button:has-text('Users')")
        page.wait_for_selector("table")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "28_users_list.png"))
        print("Captured 28_users_list.png")

        # 29. Users create modal
        page.click("button:has-text('+ Add user')")
        page.wait_for_selector(".modal-card")
        page.fill("#user-name", "editor_thai")
        page.fill("#user-password", "thai123456")
        page.select_option("#user-role", "2")
        page.select_option("#user-status", "1")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "29_users_create_modal.png"))
        print("Captured 29_users_create_modal.png")

        # 30. Users create success
        page.click(".modal-actions button[type='submit']")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "30_users_create_success.png"))
        print("Captured 30_users_create_success.png")

        # 31. Users validation duplicate
        page.click("button:has-text('+ Add user')")
        page.wait_for_selector(".modal-card")
        page.fill("#user-name", "editor_thai")
        page.fill("#user-password", "123")
        page.click(".modal-actions button[type='submit']")
        page.wait_for_selector(".modal-card .alert-error")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "31_users_validation_duplicate.png"))
        print("Captured 31_users_validation_duplicate.png")
        page.click(".modal-header button.icon-button")

        # 32. Users update modal
        user_row = page.locator("tr:has-text('editor_thai')")
        user_row.locator("button:has-text('Edit')").click()
        page.wait_for_selector(".modal-card")
        page.select_option("#user-role", "1")
        page.select_option("#user-status", "0")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "32_users_update_modal.png"))
        print("Captured 32_users_update_modal.png")

        # 33. Users update success
        page.click(".modal-actions button[type='submit']")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "33_users_update_success.png"))
        print("Captured 33_users_update_success.png")

        # 34. Users delete confirm dialog
        user_updated_row = page.locator("tr:has-text('editor_thai')")
        user_updated_row.locator("button:has-text('Delete')").click()
        page.wait_for_selector(".modal-card")
        time.sleep(0.3)
        page.screenshot(path=os.path.join(output_dir, "34_users_delete_confirm_dialog.png"))
        print("Captured 34_users_delete_confirm_dialog.png")

        # 35. Users delete success
        page.click(".modal-actions button.button-danger")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "35_users_delete_success.png"))
        print("Captured 35_users_delete_success.png")

        # 36. Users search success
        page.fill(".search-box input", "staff")
        time.sleep(0.4)
        page.screenshot(path=os.path.join(output_dir, "36_users_search_success.png"))
        print("Captured 36_users_search_success.png")

        # 37. Users search empty
        page.fill(".search-box input", "ghostuser999")
        time.sleep(0.4)
        page.screenshot(path=os.path.join(output_dir, "37_users_search_empty.png"))
        print("Captured 37_users_search_empty.png")
        page.click("button:has-text('Clear search')")
        time.sleep(0.3)

        # --- SETTINGS PAGE ---
        # 38. Settings profile
        page.click("nav.main-nav button:has-text('Settings')")
        page.wait_for_selector(".settings-card")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "38_settings_profile.png"))
        print("Captured 38_settings_profile.png")

        # --- LOGOUT ---
        # 39. Logout action
        page.click("button:has-text('Log out')")
        page.wait_for_selector(".login-panel")
        time.sleep(0.5)
        page.screenshot(path=os.path.join(output_dir, "39_logout_action.png"))
        print("Captured 39_logout_action.png")

        browser.close()
        print("All 39 screenshots captured successfully!")

if __name__ == "__main__":
    run()
