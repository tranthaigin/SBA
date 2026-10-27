import { useState } from 'react'
import ConfirmDialog from '../components/ConfirmDialog'
import Modal from '../components/Modal'
import StatusBadge from '../components/StatusBadge'

function makeEmptyForm(categories, username) {
  return {
    title: '',
    content: '',
    categoryId: categories[0]?.id ?? '',
    createdBy: username,
    status: 1,
  }
}

export default function NewsPage({ categories, currentUser, news, setNews }) {
  const [keyword, setKeyword] = useState('')
  const [modalMode, setModalMode] = useState(null)
  const [selectedId, setSelectedId] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [form, setForm] = useState(() => makeEmptyForm(categories, currentUser.username))
  const [formError, setFormError] = useState('')

  const normalizedKeyword = keyword.trim().toLowerCase()
  const displayedNews = news.filter((article) =>
    `${article.title} ${article.content}`.toLowerCase().includes(normalizedKeyword),
  )

  const openCreate = () => {
    setModalMode('create')
    setSelectedId(null)
    setForm(makeEmptyForm(categories, currentUser.username))
    setFormError('')
  }

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

  const closeModal = () => {
    setModalMode(null)
    setSelectedId(null)
    setFormError('')
  }

  const handleChange = (event) => {
    const { name, value } = event.target
    const numericFields = ['categoryId', 'status']
    setForm((current) => ({
      ...current,
      [name]: numericFields.includes(name) ? Number(value) : value,
    }))
    setFormError('')
  }

  const handleSubmit = (event) => {
    event.preventDefault()
    const title = form.title.trim()
    const content = form.content.trim()
    const categoryExists = categories.some((category) => category.id === Number(form.categoryId))

    if (!title || !content) {
      setFormError('Title and content are required.')
      return
    }

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
      const nextId = Math.max(0, ...news.map((article) => article.id)) + 1
      setNews((current) => [...current, { id: nextId, ...articleData }])
    } else {
      setNews((current) =>
        current.map((article) =>
          article.id === selectedId ? { ...article, ...articleData } : article,
        ),
      )
    }

    closeModal()
  }

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
