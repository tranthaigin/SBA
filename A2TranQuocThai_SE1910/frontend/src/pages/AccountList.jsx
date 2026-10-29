import { useEffect, useState } from "react";
import { Container, Table, Button, Form, Modal, Alert, Badge, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

/**
 * Component trang quản trị Tài khoản hệ thống (AccountList) dành riêng cho Quản trị viên (Admin - Role 1).
 * Cung cấp đầy đủ chức năng CRUD và tìm kiếm tài khoản, bảo đảm quy tắc không xóa tác giả bài viết.
 */
export default function AccountList() {
  const { user } = useAuth();
  const [accounts, setAccounts] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [editingAcc, setEditingAcc] = useState(null);
  const [formData, setFormData] = useState({
    accountName: "",
    accountEmail: "",
    accountRole: 2,
    accountPassword: ""
  });

  /**
   * Gọi REST API để lấy danh sách tài khoản theo từ khóa tìm kiếm.
   */
  const fetchAccounts = async () => {
    setLoading(true);
    setError("");
    try {
      const url = search ? `/api/accounts?search=${encodeURIComponent(search)}` : "/api/accounts";
      const res = await axios.get(url);
      setAccounts(res.data);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to load accounts");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, [search]);

  // Chặn quyền nếu không phải Quản trị viên (Role 1)
  if (user?.accountRole !== 1) {
    return (
      <Container className="py-5 text-center">
        <Alert variant="danger">
          Access Denied. Only Administrator (Role 1) can manage System Accounts.
        </Alert>
      </Container>
    );
  }

  /**
   * Mở modal thêm tài khoản mới và thiết lập form rỗng.
   */
  const handleOpenCreate = () => {
    setEditingAcc(null);
    setFormData({
      accountName: "",
      accountEmail: "",
      accountRole: 2,
      accountPassword: ""
    });
    setShowModal(true);
  };

  /**
   * Mở modal chỉnh sửa tài khoản với dữ liệu được chọn.
   */
  const handleOpenEdit = (acc) => {
    setEditingAcc(acc);
    setFormData({
      accountName: acc.accountName,
      accountEmail: acc.accountEmail,
      accountRole: acc.accountRole,
      accountPassword: "" // Giữ rỗng nếu không muốn đổi mật khẩu
    });
    setShowModal(true);
  };

  /**
   * Xử lý lưu tài khoản (tạo mới hoặc cập nhật) lên backend.
   */
  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingAcc) {
        await axios.put(`/api/accounts/${editingAcc.accountId}`, formData);
      } else {
        await axios.post("/api/accounts", formData);
      }
      setShowModal(false);
      fetchAccounts();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to save account");
    }
  };

  /**
   * Xử lý xóa tài khoản; nhận phản hồi lỗi từ backend nếu tài khoản đã từng tạo bài viết tin tức.
   */
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this account?")) return;
    try {
      await axios.delete(`/api/accounts/${id}`);
      fetchAccounts();
    } catch (err) {
      setError(err.response?.data?.message || "Failed to delete account");
    }
  };

  return (
    <Container className="py-2">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2>System Accounts Management</h2>
          <small className="text-muted">Manage Staff and Administrator credentials</small>
        </div>
        <Button variant="primary" onClick={handleOpenCreate}>
          + Create Account
        </Button>
      </div>

      {error && <Alert variant="danger" dismissible onClose={() => setError("")}>{error}</Alert>}

      <Form.Group className="mb-4">
        <Form.Control
          type="text"
          placeholder="Search accounts by name or email..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </Form.Group>

      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="primary" />
        </div>
      ) : accounts.length === 0 ? (
        <div className="text-center py-5 text-muted">
          No accounts found.
        </div>
      ) : (
        <Table responsive hover className="align-middle border shadow-sm">
          <thead className="table-light">
            <tr>
              <th>ID</th>
              <th>Full Name</th>
              <th>Email</th>
              <th>Role</th>
              <th className="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            {accounts.map((acc) => (
              <tr key={acc.accountId}>
                <td>#{acc.accountId}</td>
                <td className="fw-semibold">{acc.accountName}</td>
                <td>{acc.accountEmail}</td>
                <td>
                  <Badge bg={acc.accountRole === 1 ? "danger" : "info"}>
                    {acc.accountRole === 1 ? "Admin" : "Staff"}
                  </Badge>
                </td>
                <td className="text-end">
                  <Button
                    variant="outline-secondary"
                    size="sm"
                    className="me-2"
                    onClick={() => handleOpenEdit(acc)}
                  >
                    Edit
                  </Button>
                  <Button
                    variant="outline-danger"
                    size="sm"
                    onClick={() => handleDelete(acc.accountId)}
                  >
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {/* Modal Create/Edit */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Form onSubmit={handleSave}>
          <Modal.Header closeButton>
            <Modal.Title>{editingAcc ? "Edit Account" : "Create Account"}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Full Name *</Form.Label>
              <Form.Control
                type="text"
                required
                value={formData.accountName}
                onChange={(e) => setFormData({ ...formData, accountName: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Email Address *</Form.Label>
              <Form.Control
                type="email"
                required
                value={formData.accountEmail}
                onChange={(e) => setFormData({ ...formData, accountEmail: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>
                {editingAcc ? "Password (Leave blank to keep unchanged)" : "Password *"}
              </Form.Label>
              <Form.Control
                type="password"
                required={!editingAcc}
                value={formData.accountPassword}
                onChange={(e) => setFormData({ ...formData, accountPassword: e.target.value })}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Role *</Form.Label>
              <Form.Select
                value={formData.accountRole}
                onChange={(e) => setFormData({ ...formData, accountRole: Number(e.target.value) })}
              >
                <option value={2}>Staff (Role 2)</option>
                <option value={1}>Administrator (Role 1)</option>
              </Form.Select>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>Cancel</Button>
            <Button variant="primary" type="submit">
              {editingAcc ? "Save Changes" : "Create Account"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}
