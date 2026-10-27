import Modal from './Modal'

export default function ConfirmDialog({ itemName, onCancel, onConfirm }) {
  return (
    <Modal title="Confirm deletion" onClose={onCancel}>
      <div className="confirm-content">
        <p>
          Are you sure you want to delete <strong>{itemName}</strong>?
        </p>
        <p className="muted">This action cannot be undone.</p>
      </div>
      <div className="modal-actions">
        <button className="button button-secondary" type="button" onClick={onCancel}>
          Cancel
        </button>
        <button className="button button-danger" type="button" onClick={onConfirm}>
          Delete
        </button>
      </div>
    </Modal>
  )
}
