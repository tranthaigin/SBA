# Requirement Traceability Matrix

| ID | Yêu cầu | Bằng chứng UI/code | Kiểm tra |
|---|---|---|---|
| R01 | ReactJS + Vite | `package.json`, `vite.config.js` | `npm run dev`, `npm run build` |
| R02 | Login page | `src/pages/LoginPage.jsx` | Empty, wrong và đúng credential |
| R03 | Admin/Admin vào admin page | Auth state trong `src/App.jsx` | `Admin/Admin` vào Dashboard; logout về Login |
| R04 | Logo tạo bằng AI | `public/assets/funews-logo.png` | Logo hiển thị tại Login và Sidebar |
| R05 | Header và admin layout | `Header.jsx`, `Sidebar.jsx`, `App.jsx` | Mọi admin page nằm trong layout dùng chung |
| R06 | Menu đủ 5 mục | `src/components/Sidebar.jsx` | Dashboard, Category, News, Users, Settings |
| R07 | Category CRUD + Search | `src/pages/CategoryPage.jsx` | Create/Read/Update/Delete/Search |
| R08 | News CRUD + Search | `src/pages/NewsPage.jsx` | Create/Read/Update/Delete/Search |
| R09 | User CRUD + Search | `src/pages/UsersPage.jsx` | Create/Read/Update/Delete/Search |
| R10 | Create/Update bằng dialog | `Modal.jsx` và form trong 3 management pages | Mở đúng create/update mode, submit và close |
| R11 | Delete confirmation | `ConfirmDialog.jsx` | Cancel giữ dữ liệu; Confirm gọi immutable filter |
| R12 | Data model hợp lý | `src/data/initialData.js` | ID unique, role/status mapping, News-Category relation |

## Phạm vi

Project dùng mock data và `localStorage`. Không triển khai Spring Boot, database, JWT hoặc production deployment vì các phần này không bắt buộc trong Assignment 01.
