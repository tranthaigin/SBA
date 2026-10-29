/**
 * Component StatusBadge hiển thị huy hiệu trạng thái (Active / Inactive) với màu sắc trực quan.
 */
export default function StatusBadge({ status }) {
  return (
    <span className={status === 1 ? 'badge badge-active' : 'badge badge-inactive'}>
      {status === 1 ? 'Active' : 'Inactive'}
    </span>
  )
}
