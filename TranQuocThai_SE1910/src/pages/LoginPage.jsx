import { useState } from 'react'

/**
 * Component trang đăng nhập (LoginPage) cho phép người dùng nhập thông tin xác thực.
 * Kiểm tra dữ liệu hợp lệ và phân quyền đăng nhập vào hệ thống.
 */
export default function LoginPage({ onLogin }) {
  // Trạng thái lưu trữ giá trị username và password người dùng nhập
  const [form, setForm] = useState({ username: '', password: '' })
  // Trạng thái lưu trữ thông báo lỗi validation nếu nhập sai hoặc để trống
  const [error, setError] = useState('')

  /**
   * Xử lý thay đổi giá trị trong input và tự động xóa thông báo lỗi trước đó.
   */
  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
    setError('')
  }

  /**
   * Xử lý sự kiện gửi form đăng nhập, kiểm tra thông tin và gọi callback onLogin nếu đúng.
   */
  const handleSubmit = (event) => {
    event.preventDefault()

    // Kiểm tra không được để trống username hoặc password
    if (!form.username.trim() || !form.password) {
      setError('Username and password are required.')
      return
    }

    // Xác thực tài khoản Admin mặc định theo yêu cầu đề bài
    if (form.username === 'Admin' && form.password === 'Admin') {
      onLogin({ username: 'Admin', role: 1 })
      return
    }

    // Hiển thị thông báo khi sai thông tin đăng nhập
    setError('Incorrect username or password.')
  }

  return (
    <main className="login-page">
      <section className="login-panel" aria-labelledby="login-title">
        <div className="login-brand">
          <img src="/assets/funews-logo.png" alt="FUNews logo" />
          <div>
            <strong>FUNews</strong>
            <span>Management System</span>
          </div>
        </div>

        <div className="login-copy">
          <p className="eyebrow">Welcome back</p>
          <h1 id="login-title">Sign in to continue</h1>
          <p>Manage categories, news articles and user accounts from one place.</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit} noValidate>
          <div className="field-group">
            <label htmlFor="username">Username</label>
            <input
              autoComplete="username"
              id="username"
              name="username"
              placeholder="Enter username"
              value={form.username}
              onChange={handleChange}
            />
          </div>

          <div className="field-group">
            <label htmlFor="password">Password</label>
            <input
              autoComplete="current-password"
              id="password"
              name="password"
              placeholder="Enter password"
              type="password"
              value={form.password}
              onChange={handleChange}
            />
          </div>

          {error && <div className="alert alert-error" role="alert">{error}</div>}

          <button className="button button-primary button-full" type="submit">
            Sign in
          </button>
        </form>

        <div className="demo-credentials">
          <span>Demo account</span>
          <code>Admin / Admin</code>
        </div>
      </section>
      <aside className="login-visual" aria-hidden="true">
        <div className="visual-card">
          <span className="visual-tag">FUNews Admin</span>
          <h2>News management made clear and simple.</h2>
          <p>Organize content, categories and accounts with reliable local mock data.</p>
          <div className="visual-lines">
            <span />
            <span />
            <span />
          </div>
        </div>
      </aside>
    </main>
  )
}
