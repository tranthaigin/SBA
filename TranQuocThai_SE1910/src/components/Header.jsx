/**
 * Component Header hiển thị thanh điều hướng phía trên của hệ thống.
 * Cung cấp thông tin tiêu đề trang hiện tại, tên tài khoản đăng nhập và nút Đăng xuất.
 */
export default function Header({ currentUser, pageTitle, onLogout }) {
  return (
    <header className="topbar">
      <div>
        <p className="eyebrow">Administration</p>
        <h1>{pageTitle}</h1>
      </div>
      <div className="user-actions">
        <div className="user-summary">
          <span className="user-avatar">A</span>
          <div>
            <strong>{currentUser.username}</strong>
            <span>Administrator</span>
          </div>
        </div>
        <button className="button button-secondary" type="button" onClick={onLogout}>
          Log out
        </button>
      </div>
    </header>
  )
}
