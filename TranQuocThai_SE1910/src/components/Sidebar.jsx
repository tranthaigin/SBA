// Danh sách cấu hình 5 mục menu điều hướng chính của hệ thống
const menuItems = [
  { id: 'dashboard', label: 'Dashboard', shortLabel: 'DB' },
  { id: 'categories', label: 'Category', shortLabel: 'CT' },
  { id: 'news', label: 'News', shortLabel: 'NW' },
  { id: 'users', label: 'Users', shortLabel: 'US' },
  { id: 'settings', label: 'Settings', shortLabel: 'ST' },
]

/**
 * Component Sidebar thanh điều hướng bên trái giao diện quản trị.
 * Hiển thị logo thương hiệu FUNews và các nút chuyển trang nhanh.
 */
export default function Sidebar({ activePage, onNavigate }) {
  return (
    <aside className="sidebar">
      <div className="brand">
        <img src="/assets/funews-logo.png" alt="FUNews logo" />
        <div>
          <strong>FUNews</strong>
          <span>Management System</span>
        </div>
      </div>

      <nav className="main-nav" aria-label="Administration menu">
        {menuItems.map((item) => (
          <button
            className={activePage === item.id ? 'nav-item active' : 'nav-item'}
            key={item.id}
            type="button"
            onClick={() => onNavigate(item.id)}
          >
            <span className="nav-symbol" aria-hidden="true">{item.shortLabel}</span>
            <span>{item.label}</span>
          </button>
        ))}
      </nav>

      <p className="sidebar-note">ReactJS Assignment 01</p>
    </aside>
  )
}
