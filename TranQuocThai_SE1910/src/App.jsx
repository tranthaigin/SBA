import { useState } from 'react'
import './App.css'
import Header from './components/Header'
import Sidebar from './components/Sidebar'
import { initialCategories, initialNews, initialUsers } from './data/initialData'
import useLocalStorage from './hooks/useLocalStorage'
import CategoryPage from './pages/CategoryPage'
import DashboardPage from './pages/DashboardPage'
import LoginPage from './pages/LoginPage'
import NewsPage from './pages/NewsPage'
import SettingsPage from './pages/SettingsPage'
import UsersPage from './pages/UsersPage'

// Bản đồ ánh xạ mã trang (activePage) sang tiêu đề hiển thị ở Header
const PAGE_TITLES = {
  dashboard: 'Dashboard',
  categories: 'Category Management',
  news: 'News Management',
  users: 'User Management',
  settings: 'Settings',
}

/**
 * Component App chính của ứng dụng FUNewsManagementSystem.
 * Quản lý trạng thái xác thực toàn cục, chia sẻ dữ liệu và điều hướng giữa các trang.
 */
function App() {
  // Quản lý thông tin tài khoản đang đăng nhập, lưu đồng bộ trong localStorage
  const [currentUser, setCurrentUser] = useLocalStorage('funews-current-user', null)
  // Quản lý danh sách thể loại tin tức
  const [categories, setCategories] = useLocalStorage('funews-categories', initialCategories)
  // Quản lý danh sách các bài viết tin tức
  const [news, setNews] = useLocalStorage('funews-news', initialNews)
  // Quản lý danh sách tài khoản người dùng hệ thống
  const [users, setUsers] = useLocalStorage('funews-users', initialUsers)
  // Lưu mã trang hiện tại đang được chọn hiển thị
  const [activePage, setActivePage] = useState('dashboard')

  // Nếu chưa đăng nhập, chỉ hiển thị trang LoginPage để bảo vệ hệ thống
  if (!currentUser) {
    return <LoginPage onLogin={setCurrentUser} />
  }

  /**
   * Xử lý đăng xuất tài khoản: xóa phiên làm việc và đưa giao diện về trang Dashboard mặc định.
   */
  const handleLogout = () => {
    setCurrentUser(null)
    setActivePage('dashboard')
  }

  /**
   * Render component trang nội dung tương ứng theo trạng thái activePage đã chọn.
   */
  const renderPage = () => {
    switch (activePage) {
      case 'categories':
        return (
          <CategoryPage
            categories={categories}
            news={news}
            setCategories={setCategories}
          />
        )
      case 'news':
        return (
          <NewsPage
            categories={categories}
            currentUser={currentUser}
            news={news}
            setNews={setNews}
          />
        )
      case 'users':
        return <UsersPage setUsers={setUsers} users={users} />
      case 'settings':
        return <SettingsPage currentUser={currentUser} />
      default:
        return (
          <DashboardPage
            categories={categories}
            news={news}
            users={users}
            onNavigate={setActivePage}
          />
        )
    }
  }

  return (
    <div className="app-shell">
      <Sidebar activePage={activePage} onNavigate={setActivePage} />
      <div className="app-main">
        <Header
          currentUser={currentUser}
          pageTitle={PAGE_TITLES[activePage]}
          onLogout={handleLogout}
        />
        <main className="page-content">{renderPage()}</main>
      </div>
    </div>
  )
}

export default App
