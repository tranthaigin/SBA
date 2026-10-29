import { useState } from 'react'
import ConfirmDialog from '../components/ConfirmDialog'
import Modal from '../components/Modal'
import StatusBadge from '../components/StatusBadge'

/**
 * Khởi tạo dữ liệu mặc định cho form tạo bài viết tin tức mới.
 */
function makeEmptyForm(categories, username) {
  return {
    title: '',
    content: '',
    categoryId: categories[0]?.id ?? '',
    createdBy: username,
    status: 1,
  }
}

/**
 * Component quản lý Tin tức (News Management).
 * Hỗ trợ tìm kiếm, thêm mới, xem, sửa và xóa các bài viết tin tức kèm liên kết thể loại.
 */
export default function NewsPage({ categories, currentUser, news, setNews }) {
  // Trạng thái từ khóa tìm kiếm tin tức
  const [keyword, setKeyword] = useState('')
  // Chế độ mở modal: 'create', 'update' hoặc null (đóng)
  const [modalMode, setModalMode] = useState(null)
  // ID bài viết đang được chọn chỉnh sửa
  const [selectedId, setSelectedId] = useState(null)
  // Đối tượng bài viết đang chọn để xóa
  const [deleteTarget, setDeleteTarget] = useState(null)
  // Dữ liệu binding của form nhập liệu tin tức
  const [form, setForm] = useState(() => makeEmptyForm(categories, currentUser.username))
  // Thông báo lỗi validation của form
  const [formError, setFormError] = useState('')

  // Tìm kiếm bài viết theo cả tiêu đề hoặc nội dung
  const normalizedKeyword = keyword.trim().toLowerCase()
  const displayedNews = news.filter((article) =>
    `${article.title} ${article.content}`.toLowerCase().includes(normalizedKeyword),
  )

  /**
   * Mở modal ở chế độ thêm bài viết mới và thiết lập giá trị form ban đầu.
   */
  const openCreate = () => {
    setModalMode('create')
    setSelectedId(null)
    setForm(makeEmptyForm(categories, currentUser.username))
    setFormError('')
  }

  /**
   * Mở modal ở chế độ cập nhật bài viết với dữ liệu của tin tức đã chọn.
   */
  const openUpdate = (article) => {
    setModalMode('update')
    setSelectedId(article.id)
    setForm({
      title: article.title,
      content: article.content,
      categoryId: article.categoryId,
      createdBy: article.createdBy,
      status: article.status,
    })
    setFormError('')
  }

  /**
   * Đóng modal và reset trạng thái lựa chọn.
   */
  const closeModal = () => {
    setModalMode(null)
    setSelectedId(null)
    setFormError('')
  }

  /**
   * Xử lý thay đổi dữ liệu trên form nhập liệu và tự động ép kiểu số cho các trường số.
   */
  const handleChange = (event) => {
    const { name, value } = event.target
    const numericFields = ['categoryId', 'status']
    setForm((current) => ({
      ...current,
      [name]: numericFields.includes(name) ? Number(value) : value,
    }))
    setFormError('')
  }

  /**
   * Xử lý gửi form lưu bài viết: kiểm tra các trường bắt buộc và tính tồn tại của Category.
   */
  const handleSubmit = (event) => {
    event.preventDefault()
    const title = form.title.trim()
    const content = form.content.trim()
    const categoryExists = categories.some((category) => category.id === Number(form.categoryId))

    // Bắt buộc nhập tiêu đề và nội dung bài viết
    if (!title || !content) {
      setFormError('Title and content are required.')
      return
    }

    // Đảm bảo thể loại được chọn phải tồn tại trong danh sách
    if (!categoryExists) {
      setFormError('Please select a valid category.')
      return
    }

    const articleData = {
      title,
      content,
      categoryId: Number(form.categoryId),
      createdBy: form.createdBy.trim() || currentUser.username,
      status: Number(form.status),
    }

    if (modalMode === 'create') {
      // Tự sinh ID tiếp theo cho bài viết mới
      const nextId = Math.max(0, ...news.map((article) => article.id)) + 1
      setNews((current) => [...current, { id: nextId, ...articleData }])
    } else {
      // Cập nhật thông tin bài viết tương ứng
      setNews((current) =>
        current.map((article) =>
          article.id === selectedId ? { ...article, ...articleData } : article,
        ),
      )
    }

    closeModal()
  }

  /**
   * Xử lý xóa bài viết tin tức đã chọn khỏi danh sách.
   */
  const handleDelete = () => {
    setNews((current) => current.filter((article) => article.id !== deleteTarget.id))
    setDeleteTarget(null)
  }

  return (
    <div className="page-stack">
      <section className="content-card">
        <div className="section-heading management-heading">
          <div>
            <p className="eyebrow">Editorial content</p>
            <h2>News articles</h2>
            <span className="section-description">Create, update and publish news linked to a category.</span>
          </div>
          <button className="button button-primary" type="button" onClick={openCreate} disabled={categories.length === 0}>
            + Add news
          </button>
        </div>

        {categories.length === 0 && (
          <div className="alert alert-error" role="alert">Create a category before adding a news article.</div>
        )}

        <div className="toolbar">
          <label className="search-box">
            <span className="sr-only">Search news</span>
            <input
              placeholder="Search by title or content"
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
            />
          </label>
          {keyword && (
            <button className="button button-link" type="button" onClick={() => setKeyword('')}>Clear search</button>
          )}
        </div>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Article</th>
                <th>Category</th>
                <th>Created by</th>
                <th>Status</th>
                <th className="actions-column">Actions</th>
              </tr>
            </thead>
            <tbody>
              {displayedNews.map((article) => {
                const category = categories.find((item) => item.id === article.categoryId)
                return (
                  <tr key={article.id}>
                    <td>
                      <div className="primary-cell">
                        <strong>{article.title}</strong>
                        <span>{article.content}</span>
                      </div>
                    </td>
                    <td>{category?.name ?? 'Unknown'}</td>
                    <td>{article.createdBy}</td>
                    <td><StatusBadge status={article.status} /></td>
                    <td>
                      <div className="row-actions">
                        <button className="button button-small button-secondary" type="button" onClick={() => openUpdate(article)}>Edit</button>
                        <button className="button button-small button-danger-ghost" type="button" onClick={() => setDeleteTarget(article)}>Delete</button>
                      </div>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>

        {displayedNews.length === 0 && (
          <div className="empty-state">
            <strong>{news.length === 0 ? 'No news articles yet' : 'No matching news articles'}</strong>
            <span>{news.length === 0 ? 'Add the first article to get started.' : 'Try another search keyword.'}</span>
          </div>
        )}
      </section>

      {modalMode && (
        <Modal title={modalMode === 'create' ? 'Add news article' : 'Update news article'} onClose={closeModal}>
          <form onSubmit={handleSubmit} noValidate>
            <div className="modal-body form-stack">
              <div className="field-group">
                <label htmlFor="news-title">Title</label>
                <input autoFocus id="news-title" name="title" value={form.title} onChange={handleChange} />
              </div>
              <div className="field-group">
                <label htmlFor="news-content">Content</label>
                <textarea id="news-content" name="content" rows="5" value={form.content} onChange={handleChange} />
              </div>
              <div className="form-grid">
                <div className="field-group">
                  <label htmlFor="news-category">Category</label>
                  <select id="news-category" name="categoryId" value={form.categoryId} onChange={handleChange}>
                    {categories.map((category) => (
                      <option key={category.id} value={category.id}>{category.name}</option>
                    ))}
                  </select>
                </div>
                <div className="field-group">
                  <label htmlFor="news-status">Status</label>
                  <select id="news-status" name="status" value={form.status} onChange={handleChange}>
                    <option value={1}>Active</option>
                    <option value={0}>Inactive</option>
                  </select>
                </div>
              </div>
              <div className="field-group">
                <label htmlFor="news-author">Created by</label>
                <input id="news-author" name="createdBy" value={form.createdBy} onChange={handleChange} />
              </div>
              {formError && <div className="alert alert-error" role="alert">{formError}</div>}
            </div>
            <div className="modal-actions">
              <button className="button button-secondary" type="button" onClick={closeModal}>Cancel</button>
              <button className="button button-primary" type="submit">
                {modalMode === 'create' ? 'Create article' : 'Save changes'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          itemName={deleteTarget.title}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={handleDelete}
        />
      )}
    </div>
  )
}
