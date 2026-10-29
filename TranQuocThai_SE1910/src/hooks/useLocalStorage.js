import { useEffect, useState } from 'react'

/**
 * Hàm trợ giúp đọc dữ liệu từ window.localStorage theo khóa (key).
 * Tự động parse JSON hoặc trả về giá trị khởi tạo nếu chưa có dữ liệu.
 */
function readStoredValue(key, initialValue) {
  try {
    const storedValue = window.localStorage.getItem(key)
    return storedValue === null ? initialValue : JSON.parse(storedValue)
  } catch {
    return initialValue
  }
}

/**
 * Custom Hook useLocalStorage đồng bộ hóa React state với localStorage của trình duyệt.
 * Giúp lưu trữ dữ liệu bền vững ngay cả khi người dùng tải lại trang.
 */
export default function useLocalStorage(key, initialValue) {
  const [value, setValue] = useState(() => readStoredValue(key, initialValue))

  // Lắng nghe sự thay đổi của value để ghi lại vào localStorage
  useEffect(() => {
    if (value === null) {
      window.localStorage.removeItem(key)
      return
    }

    window.localStorage.setItem(key, JSON.stringify(value))
  }, [key, value])

  return [value, setValue]
}
