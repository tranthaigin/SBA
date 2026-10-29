/**
 * Component trang Cài đặt và Hồ sơ người dùng (SettingsPage).
 * Hiển thị thông tin tài khoản hiện tại, vai trò và cơ chế lưu trữ dữ liệu của hệ thống.
 */
export default function SettingsPage({ currentUser }) {
  return (
    <div className="page-stack">
      <section className="content-card settings-card">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Account</p>
            <h2>Profile information</h2>
          </div>
        </div>
        <dl className="settings-list">
          <div>
            <dt>Username</dt>
            <dd>{currentUser.username}</dd>
          </div>
          <div>
            <dt>Role</dt>
            <dd>Admin</dd>
          </div>
          <div>
            <dt>Data storage</dt>
            <dd>Browser localStorage</dd>
          </div>
          <div>
            <dt>Application</dt>
            <dd>FUNewsManagementSystem</dd>
          </div>
        </dl>
      </section>

      <section className="content-card note-card">
        <h2>Mock environment</h2>
        <p>
          This assignment uses local mock data. Changes are stored in this browser and remain available after a page reload.
        </p>
      </section>
    </div>
  )
}
