import StatusBadge from '../components/StatusBadge'

/**
 * Component trang Tổng quan (DashboardPage).
 * Hiển thị các chỉ số thống kê tổng hợp và danh sách các bài viết tin tức mới nhất.
 */
export default function DashboardPage({ categories, news, users, onNavigate }) {
  // Cấu hình danh sách các thẻ thống kê tổng quan với dữ liệu động
  const cards = [
    { label: 'Categories', value: categories.length, page: 'categories', note: `${categories.filter((item) => item.status === 1).length} active` },
    { label: 'News articles', value: news.length, page: 'news', note: `${news.filter((item) => item.status === 1).length} active` },
    { label: 'User accounts', value: users.length, page: 'users', note: `${users.filter((item) => item.status === 1).length} active` },
  ]

  return (
    <div className="page-stack">
      <section className="welcome-card">
        <div>
          <p className="eyebrow">Overview</p>
          <h2>Welcome to FUNews Management System</h2>
          <p>Review the current mock data and open a management area to make changes.</p>
        </div>
        <img src="/assets/funews-logo.png" alt="" />
      </section>

      <section className="stats-grid" aria-label="System totals">
        {cards.map((card) => (
          <button className="stat-card" key={card.label} type="button" onClick={() => onNavigate(card.page)}>
            <span>{card.label}</span>
            <strong>{card.value}</strong>
            <small>{card.note}</small>
          </button>
        ))}
      </section>

      <section className="content-card">
        <div className="section-heading">
          <div>
            <p className="eyebrow">Recent content</p>
            <h2>Latest news articles</h2>
          </div>
          <button className="button button-secondary" type="button" onClick={() => onNavigate('news')}>
            View all news
          </button>
        </div>

        <div className="dashboard-list">
          {news.slice(0, 3).map((item) => {
            const category = categories.find((entry) => entry.id === item.categoryId)
            return (
              <article className="dashboard-list-item" key={item.id}>
                <div>
                  <strong>{item.title}</strong>
                  <span>{category?.name ?? 'Unknown category'} · by {item.createdBy}</span>
                </div>
                <StatusBadge status={item.status} />
              </article>
            )
          })}
        </div>
      </section>
    </div>
  )
}
