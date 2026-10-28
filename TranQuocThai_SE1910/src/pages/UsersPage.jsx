import { useState } from 'react'
import ConfirmDialog from '../components/ConfirmDialog'
import Modal from '../components/Modal'
import StatusBadge from '../components/StatusBadge'

const emptyForm = { username: '', password: '', role: 2, status: 1 }

function roleLabel(role) {
  return role === 1 ? 'Admin' : 'Staff'
}

export default function UsersPage({ users, setUsers }) {
  const [keyword, setKeyword] = useState('')
  const [modalMode, setModalMode] = useState(null)
  const [selectedId, setSelectedId] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [formError, setFormError] = useState('')

  const normalizedKeyword = keyword.trim().toLowerCase()
  const displayedUsers = users.filter((user) =>
    user.username.toLowerCase().includes(normalizedKeyword),
  )

  const openCreate = () => {
    setModalMode('create')
    setSelectedId(null)
    setForm(emptyForm)
    setFormError('')
  }

  const openUpdate = (user) => {
    setModalMode('update')
    setSelectedId(user.id)
    setForm({
      username: user.username,
      password: user.password,
      role: user.role,
      status: user.status,
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
    setForm((current) => ({
      ...current,
      [name]: ['role', 'status'].includes(name) ? Number(value) : value,
    }))
    setFormError('')
  }

  const handleSubmit = (event) => {
    event.preventDefault()
    const username = form.username.trim()

    if (!username || !form.password) {
      setFormError('Username and password are required.')
      return
    }

    const duplicate = users.some(
      (user) => user.id !== selectedId && user.username.toLowerCase() === username.toLowerCase(),
    )
    if (duplicate) {
      setFormError('Username must be unique.')
      return
    }

    const userData = {
      username,
      password: form.password,
      role: Number(form.role),
      status: Number(form.status),
    }

    if (modalMode === 'create') {
      const nextId = Math.max(0, ...users.map((user) => user.id)) + 1
      setUsers((current) => [...current, { id: nextId, ...userData }])
    } else {
      setUsers((current) =>
        current.map((user) => user.id === selectedId ? { ...user, ...userData } : user),
      )
    }

    closeModal()
  }

  const handleDelete = () => {
    setUsers((current) => current.filter((user) => user.id !== deleteTarget.id))
    setDeleteTarget(null)
  }

  return (
    <div className="page-stack">
      <section className="content-card">
        <div className="section-heading management-heading">
          <div>
            <p className="eyebrow">Access records</p>
            <h2>User accounts</h2>
            <span className="section-description">Maintain Admin and Staff mock accounts.</span>
          </div>
          <button className="button button-primary" type="button" onClick={openCreate}>+ Add user</button>
        </div>

        <div className="toolbar">
          <label className="search-box">
            <span className="sr-only">Search users</span>
            <input
              placeholder="Search by username"
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
                <th>ID</th>
                <th>Username</th>
                <th>Role</th>
                <th>Status</th>
                <th className="actions-column">Actions</th>
              </tr>
            </thead>
            <tbody>
              {displayedUsers.map((user) => (
                <tr key={user.id}>
                  <td>#{user.id}</td>
                  <td><strong>{user.username}</strong></td>
                  <td><span className="role-label">{roleLabel(user.role)}</span></td>
                  <td><StatusBadge status={user.status} /></td>
                  <td>
                    <div className="row-actions">
                      <button className="button button-small button-secondary" type="button" onClick={() => openUpdate(user)}>Edit</button>
                      <button className="button button-small button-danger-ghost" type="button" onClick={() => setDeleteTarget(user)}>Delete</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {displayedUsers.length === 0 && (
          <div className="empty-state">
            <strong>{users.length === 0 ? 'No user accounts yet' : 'No matching users'}</strong>
            <span>{users.length === 0 ? 'Add the first account to get started.' : 'Try another search keyword.'}</span>
          </div>
        )}
      </section>

      {modalMode && (
        <Modal title={modalMode === 'create' ? 'Add user account' : 'Update user account'} onClose={closeModal}>
          <form onSubmit={handleSubmit} noValidate>
            <div className="modal-body form-stack">
              <div className="field-group">
                <label htmlFor="user-name">Username</label>
                <input autoFocus id="user-name" name="username" value={form.username} onChange={handleChange} />
              </div>
              <div className="field-group">
                <label htmlFor="user-password">Mock password</label>
                <input id="user-password" name="password" type="password" value={form.password} onChange={handleChange} />
              </div>
              <div className="form-grid">
                <div className="field-group">
                  <label htmlFor="user-role">Role</label>
                  <select id="user-role" name="role" value={form.role} onChange={handleChange}>
                    <option value={1}>Admin</option>
                    <option value={2}>Staff</option>
                  </select>
                </div>
                <div className="field-group">
                  <label htmlFor="user-status">Status</label>
                  <select id="user-status" name="status" value={form.status} onChange={handleChange}>
                    <option value={1}>Active</option>
                    <option value={0}>Inactive</option>
                  </select>
                </div>
              </div>
              {formError && <div className="alert alert-error" role="alert">{formError}</div>}
            </div>
            <div className="modal-actions">
              <button className="button button-secondary" type="button" onClick={closeModal}>Cancel</button>
              <button className="button button-primary" type="submit">
                {modalMode === 'create' ? 'Create user' : 'Save changes'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          itemName={deleteTarget.username}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={handleDelete}
        />
      )}
    </div>
  )
}
