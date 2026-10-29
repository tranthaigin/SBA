import { useEffect, useState } from "react";
import { Alert, Button, Container, Form, Table } from "react-bootstrap";
import { getCategories, deleteCategory } from "../api/categoryApi";
import CategoryForm from "../components/CategoryForm";
import { useAuth } from "../context/AuthContext";

/**
 * Component trang quản lý Thể loại (CategoryList) dành riêng cho vai trò Staff (Role 2).
 * Cung cấp chức năng xem, tìm kiếm, mở modal tạo mới, sửa và xóa thể loại.
 */
export default function CategoryList() {
  const { user } = useAuth();
  const [items, setItems] = useState([]),
    [search, setSearch] = useState(""),
    [error, setError] = useState(""),
    [show, setShow] = useState(false),
    [editing, setEditing] = useState(null);

  /**
   * Gọi API backend để nạp danh sách thể loại theo từ khóa tìm kiếm.
   */
  async function load() {
    try {
      const r = await getCategories({ keyword: search });
      setItems(r.data);
      setError("");
    } catch (e) {
      setError(e.response?.data?.message || "Cannot load categories");
    }
  }

  // Tự động tải lại dữ liệu khi người dùng thay đổi từ khóa tìm kiếm hoặc đổi quyền
  useEffect(() => {
    if (user?.accountRole === 2) load();
  }, [search, user]);

  // Chặn truy cập nếu không phải là tài khoản Staff
  if (user?.accountRole !== 2)
    return (
      <Container>
        <Alert variant="danger">Staff access required.</Alert>
      </Container>
    );

  /**
   * Xử lý xóa thể loại: hiển thị hộp thoại xác nhận và bắt lỗi nghiệp vụ nếu có bài viết liên kết.
   */
  async function remove(c) {
    if (!window.confirm(`Delete ${c.categoryName}?`)) return;
    try {
      await deleteCategory(c.categoryId);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || "Cannot delete category");
    }
  }

  return (
    <Container>
      <h2>Category Management</h2>
      {error && <Alert variant="danger">{error}</Alert>}
      <Form.Control
        aria-label="Search categories"
        placeholder="Search categories"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <Button
        className="my-3"
        onClick={() => {
          setEditing(null);
          setShow(true);
        }}
      >
        Create Category
      </Button>
      <Table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Parent</th>
            <th>Active</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {items.map((c) => (
            <tr key={c.categoryId}>
              <td>{c.categoryId}</td>
              <td>{c.categoryName}</td>
              <td>{c.categoryDesciption}</td>
              <td>{c.parentCategoryId ?? "-"}</td>
              <td>{c.isActive ? "Yes" : "No"}</td>
              <td>
                <Button
                  size="sm"
                  variant="secondary"
                  className="me-2"
                  onClick={() => {
                    setEditing(c);
                    setShow(true);
                  }}
                >
                  Edit
                </Button>
                <Button size="sm" variant="danger" onClick={() => remove(c)}>
                  Delete
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
      <CategoryForm
        show={show}
        category={editing}
        categories={items}
        onHide={() => setShow(false)}
        onSaved={async () => {
          setShow(false);
          await load();
        }}
      />
    </Container>
  );
}
