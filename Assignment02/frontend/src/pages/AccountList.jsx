import { useEffect, useState } from "react";
import { Container, Table, Button, Form, Modal, Alert, Badge, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

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

  if (user?.accountRole !== 1) {
    return (
      <Container className="py-5 text-center">
        <Alert variant="danger">
          Access Denied. Only Administrator (Role 1) can manage System Accounts.
        </Alert>
      </Container>
    );
  }

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

  const handleOpenEdit = (acc) => {
    setEditingAcc(acc);
    setFormData({
      accountName: acc.accountName || "",
      accountEmail: acc.accountEmail || "",
      accountRole: acc.accountRole,
      accountPassword: ""
    });
    setShowModal(true);
  };

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
      alert(err.response?.data?.message || "Failed to save account");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this account?")) return;
    try {
      await axios.delete(`/api/accounts/${id}`);
      fetchAccounts();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to delete account");
    }
  };

  return (
    <Container className="py-2">
      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <div>
          <h2>System Account Management</h2>
          <small className="text-muted">Administrator workspace</small>
        </div>
        <div className="d-flex gap-2">
          <Form.Control
            type="text"
            placeholder="Search accounts..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ width: "260px" }}
          />
          <Button variant="primary" onClick={handleOpenCreate}>
            + Add New Account
          </Button>
        </div>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="primary" />
        </div>
      ) : (
        <Table responsive hover className="align-middle border shadow-sm">
          <thead className="table-light">
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th className="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            {accounts.map((acc) => (
              <tr key={acc.accountId}>
                <td>{acc.accountId}</td>
                <td className="fw-semibold">{acc.accountName}</td>
                <td>{acc.accountEmail}</td>
                <td>
                  <Badge bg={acc.accountRole === 1 ? "danger" : "info"}>
                    {acc.accountRole === 1 ? "Admin (1)" : "Staff (2)"}
                  </Badge>
                </td>
                <td className="text-end">
                  <Button variant="outline-secondary" size="sm" className="me-2" onClick={() => handleOpenEdit(acc)}>
                    Edit
                  </Button>
                  <Button variant="outline-danger" size="sm" onClick={() => handleDelete(acc.accountId)}>
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {/* Account Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>{editingAcc ? "Edit Account" : "Add New Account"}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSave}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Full Name *</Form.Label>
              <Form.Control
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
              <Form.Label>Role *</Form.Label>
              <Form.Select
                value={formData.accountRole}
                onChange={(e) => setFormData({ ...formData, accountRole: Number(e.target.value) })}
              >
                <option value={1}>Admin (1)</option>
                <option value={2}>Staff (2)</option>
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>{editingAcc ? "New Password (leave blank to keep current)" : "Password *"}</Form.Label>
              <Form.Control
                type="password"
                required={!editingAcc}
                value={formData.accountPassword}
                onChange={(e) => setFormData({ ...formData, accountPassword: e.target.value })}
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              Save Account
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}
