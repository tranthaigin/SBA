import Modal from './Modal'

/**
 * Component ConfirmDialog hộp thoại xác nhận trước khi thực hiện hành động nguy hiểm (xóa dữ liệu).
 * Đảm bảo người dùng xác nhận rõ ràng, tránh mất mát dữ liệu do bấm nhầm.
 */
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
