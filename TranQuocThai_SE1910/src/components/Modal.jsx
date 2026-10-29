/**
 * Component Modal hộp thoại popup tái sử dụng cho các chức năng Thêm mới và Chỉnh sửa.
 * Cung cấp lớp nền mờ (backdrop), tiêu đề và nút đóng hộp thoại.
 */
export default function Modal({ children, title, onClose }) {
  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={onClose}>
      <section
        aria-labelledby="modal-title"
        aria-modal="true"
        className="modal-card"
        role="dialog"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="modal-header">
          <h2 id="modal-title">{title}</h2>
          <button aria-label="Close dialog" className="icon-button" type="button" onClick={onClose}>
            ×
          </button>
        </div>
        {children}
      </section>
    </div>
  )
}
