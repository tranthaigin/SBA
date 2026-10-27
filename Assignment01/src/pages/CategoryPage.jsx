import { useState } from 'react'
import ConfirmDialog from '../components/ConfirmDialog'
import Modal from '../components/Modal'
import StatusBadge from '../components/StatusBadge'

const emptyForm = { name: '', status: 1 }

export default function CategoryPage({ categories, news, setCategories }) {
  const [keyword, setKeyword] = useState('')
  const [modalMode, setModalMode] = useState(null)
  const [selectedId, setSelectedId] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [formError, setFormError] = useState('')
  const [pageError, setPageError] = useState('')

  const normalizedKeyword = keyword.trim().toLowerCase()
  const displayedCategories = categories.filter((category) =>
    category.name.toLowerCase().includes(normalizedKeyword),
  )

  const openCreate = () => {
    setModalMode('create')
    setSelectedId(null)
    setForm(emptyForm)
    setFormError('')
    setPageError('')
  }

  const openUpdate = (category) => {
    setModalMode('update')
    setSelectedId(category.id)
    setForm({ name: category.name, status: category.status })
    setFormError('')
    setPageError('')
  }

  const closeModal = () => {
    setModalMode(null)
    setSelectedId(null)
    setFormError('')
  }

  const handleSubmit = (event) => {
    event.preventDefault()
    const name = form.name.trim()

    if (!name) {
      setFormError('Category name is required.')
      return
    }

    const duplicate = categories.some(
      (category) => category.id !== selectedId && category.name.toLowerCase() === name.toLowerCase(),
    )
    if (duplicate) {
      setFormError('Category name must be unique.')
      return
    }

    if (modalMode === 'create') {
      const nextId = Math.max(0, ...categories.map((category) => category.id)) + 1
      setCategories((current) => [...current, { id: nextId, name, status: Number(form.status) }])
    } else {
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

  const handleDelete = () => {
    const isInUse = news.some((article) => article.categoryId === deleteTarget.id)
    if (isInUse) {
      setPageError(`Cannot delete “${deleteTarget.name}” because it is used by a news article.`)
      setDeleteTarget(null)
      return
    }

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
