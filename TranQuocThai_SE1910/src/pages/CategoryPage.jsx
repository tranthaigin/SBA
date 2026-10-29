import { useState } from 'react'
import ConfirmDialog from '../components/ConfirmDialog'
import Modal from '../components/Modal'
import StatusBadge from '../components/StatusBadge'

// Dữ liệu mặc định cho form tạo mới thể loại
const emptyForm = { name: '', status: 1 }

/**
 * Component quản lý Thể loại tin tức (Category Management).
 * Cung cấp đầy đủ các thao tác CRUD và tìm kiếm thể loại tin tức.
 */
export default function CategoryPage({ categories, news, setCategories }) {
  // Trạng thái từ khóa tìm kiếm
  const [keyword, setKeyword] = useState('')
  // Chế độ mở modal: 'create', 'update' hoặc null (đóng)
  const [modalMode, setModalMode] = useState(null)
  // ID thể loại đang được chọn chỉnh sửa
  const [selectedId, setSelectedId] = useState(null)
  // Đối tượng thể loại đang được chọn để xác nhận xóa
  const [deleteTarget, setDeleteTarget] = useState(null)
  // Dữ liệu binding của form nhập liệu thể loại
  const [form, setForm] = useState(emptyForm)
  // Thông báo lỗi validation bên trong modal
  const [formError, setFormError] = useState('')
  // Thông báo lỗi nghiệp vụ hiển thị trên trang (ví dụ lỗi ràng buộc dữ liệu)
  const [pageError, setPageError] = useState('')

  // Lọc danh sách thể loại theo từ khóa tìm kiếm (không phân biệt chữ hoa/thường)
  const normalizedKeyword = keyword.trim().toLowerCase()
  const displayedCategories = categories.filter((category) =>
    category.name.toLowerCase().includes(normalizedKeyword),
  )

  /**
   * Mở modal ở chế độ thêm mới thể loại và làm sạch dữ liệu form.
   */
  const openCreate = () => {
    setModalMode('create')
    setSelectedId(null)
    setForm(emptyForm)
    setFormError('')
    setPageError('')
  }

  /**
   * Mở modal ở chế độ chỉnh sửa với thông tin của thể loại đã chọn.
   */
  const openUpdate = (category) => {
    setModalMode('update')
    setSelectedId(category.id)
    setForm({ name: category.name, status: category.status })
    setFormError('')
    setPageError('')
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
   * Xử lý lưu thể loại (thêm mới hoặc cập nhật), kiểm tra tính hợp lệ và chống trùng lặp tên.
   */
  const handleSubmit = (event) => {
    event.preventDefault()
    const name = form.name.trim()

    // Bắt buộc nhập tên thể loại
    if (!name) {
      setFormError('Category name is required.')
      return
    }

    // Kiểm tra tên thể loại có bị trùng lặp với bản ghi khác hay không
    const duplicate = categories.some(
      (category) => category.id !== selectedId && category.name.toLowerCase() === name.toLowerCase(),
    )
    if (duplicate) {
      setFormError('Category name must be unique.')
      return
    }

    if (modalMode === 'create') {
      // Tự sinh ID tiếp theo dựa trên max ID hiện tại
      const nextId = Math.max(0, ...categories.map((category) => category.id)) + 1
      setCategories((current) => [...current, { id: nextId, name, status: Number(form.status) }])
    } else {
      // Cập nhật thông tin thể loại đã chọn trong mảng
      setCategories((current) =>
        current.map((category) =>
          category.id === selectedId
            ? { ...category, name, status: Number(form.status) }
            : category,
        ),
      )
    }

    closeModal()
  }

  /**
   * Xử lý xác nhận xóa thể loại: kiểm tra ràng buộc xem thể loại có bài viết tin tức hay không trước khi xóa.
   */
  const handleDelete = () => {
    // Ràng buộc toàn vẹn dữ liệu: không cho phép xóa thể loại đang có tin tức liên kết
    const isInUse = news.some((article) => article.categoryId === deleteTarget.id)
    if (isInUse) {
      setPageError(`Cannot delete “${deleteTarget.name}” because it is used by a news article.`)
      setDeleteTarget(null)
      return
    }

    // Loại bỏ thể loại khỏi mảng state
    setCategories((current) => current.filter((category) => category.id !== deleteTarget.id))
    setDeleteTarget(null)
    setPageError('')
  }

  return (
    <div className="page-stack">
      <section className="content-card">
        <div className="section-heading management-heading">
          <div>
            <p className="eyebrow">Content structure</p>
            <h2>Categories</h2>
            <span className="section-description">Create and organize categories used by news articles.</span>
          </div>
          <button className="button button-primary" type="button" onClick={openCreate}>
            + Add category
          </button>
        </div>

        <div className="toolbar">
          <label className="search-box">
            <span className="sr-only">Search categories</span>
            <input
              placeholder="Search by category name"
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
            />
          </label>
          {keyword && (
            <button className="button button-link" type="button" onClick={() => setKeyword('')}>
              Clear search
            </button>
          )}
        </div>

        {pageError && <div className="alert alert-error" role="alert">{pageError}</div>}

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Status</th>
                <th className="actions-column">Actions</th>
              </tr>
            </thead>
            <tbody>
              {displayedCategories.map((category) => (
                <tr key={category.id}>
                  <td>#{category.id}</td>
                  <td><strong>{category.name}</strong></td>
                  <td><StatusBadge status={category.status} /></td>
                  <td>
                    <div className="row-actions">
                      <button className="button button-small button-secondary" type="button" onClick={() => openUpdate(category)}>
                        Edit
                      </button>
                      <button className="button button-small button-danger-ghost" type="button" onClick={() => setDeleteTarget(category)}>
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {displayedCategories.length === 0 && (
          <div className="empty-state">
            <strong>{categories.length === 0 ? 'No categories yet' : 'No matching categories'}</strong>
            <span>{categories.length === 0 ? 'Add the first category to get started.' : 'Try another search keyword.'}</span>
          </div>
        )}
      </section>

      {modalMode && (
        <Modal title={modalMode === 'create' ? 'Add category' : 'Update category'} onClose={closeModal}>
          <form onSubmit={handleSubmit} noValidate>
            <div className="modal-body form-stack">
              <div className="field-group">
                <label htmlFor="category-name">Category name</label>
                <input
                  autoFocus
                  id="category-name"
                  placeholder="Example: Technology"
                  value={form.name}
                  onChange={(event) => {
                    setForm((current) => ({ ...current, name: event.target.value }))
                    setFormError('')
                  }}
                />
              </div>
              <div className="field-group">
                <label htmlFor="category-status">Status</label>
                <select
                  id="category-status"
                  value={form.status}
                  onChange={(event) => setForm((current) => ({ ...current, status: Number(event.target.value) }))}
                >
                  <option value={1}>Active</option>
                  <option value={0}>Inactive</option>
                </select>
              </div>
              {formError && <div className="alert alert-error" role="alert">{formError}</div>}
            </div>
            <div className="modal-actions">
              <button className="button button-secondary" type="button" onClick={closeModal}>Cancel</button>
              <button className="button button-primary" type="submit">
                {modalMode === 'create' ? 'Create category' : 'Save changes'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          itemName={deleteTarget.name}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={handleDelete}
        />
      )}
    </div>
  )
}
