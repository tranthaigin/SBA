import { useEffect, useState } from "react";
import { Container, Table, Button, Form, Modal, Alert, Badge, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function AdminCustomers() {
  const { user } = useAuth();
  const [customers, setCustomers] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [editingCust, setEditingCust] = useState(null);
  const [formData, setFormData] = useState({
    customerFullName: "",
    telephone: "",
    emailAddress: "",
    customerBirthday: "1995-01-01",
    customerStatus: 1,
    password: ""
  });

  const fetchCustomers = async () => {
    setLoading(true);
    setError("");
    try {
      const url = search ? `/api/admin/customers?search=${encodeURIComponent(search)}` : "/api/admin/customers";
      const res = await axios.get(url);
      setCustomers(res.data);
    } catch (err) {
      setError("Failed to load customers");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, [search]);

  if (user?.role !== "ROLE_STAFF") {
    return (
      <Container className="py-5 text-center">
        <Alert variant="danger">Access Denied. Staff privilege required.</Alert>
      </Container>
    );
  }

  const handleOpenCreate = () => {
    setEditingCust(null);
    setFormData({
      customerFullName: "",
      telephone: "",
      emailAddress: "",
      customerBirthday: "1995-01-01",
      customerStatus: 1,
      password: ""
    });
    setShowModal(true);
  };

  const handleOpenEdit = (c) => {
    setEditingCust(c);
    setFormData({
      customerFullName: c.customerFullName || "",
      telephone: c.telephone || "",
      emailAddress: c.emailAddress || "",
      customerBirthday: c.customerBirthday || "1995-01-01",
      customerStatus: c.customerStatus,
      password: ""
    });
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      if (editingCust) {
        await axios.put(`/api/admin/customers/${editingCust.customerId}`, formData);
      } else {
        await axios.post("/api/admin/customers", formData);
      }
      setShowModal(false);
      fetchCustomers();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to save customer");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this customer?")) return;
    try {
      await axios.delete(`/api/admin/customers/${id}`);
      fetchCustomers();
    } catch (err) {
      alert("Failed to delete customer");
    }
  };

  return (
    <Container className="py-2">
      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <div>
          <h2>Customer Management</h2>
          <small className="text-muted">Staff workspace</small>
        </div>
        <div className="d-flex gap-2">
          <Form.Control
            type="text"
            placeholder="Search by name or email..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ width: "260px" }}
          />
          <Button variant="warning" onClick={handleOpenCreate}>
            + Add New Customer
          </Button>
        </div>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="warning" />
        </div>
      ) : (
        <Table responsive hover className="align-middle border shadow-sm">
          <thead className="table-light">
            <tr>
              <th>ID</th>
              <th>Full Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Birthday</th>
              <th>Status</th>
              <th className="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            {customers.map((c) => (
              <tr key={c.customerId}>
                <td>{c.customerId}</td>
                <td className="fw-semibold">{c.customerFullName}</td>
                <td>{c.emailAddress}</td>
                <td>{c.telephone}</td>
                <td>{c.customerBirthday}</td>
                <td>
                  <Badge bg={c.customerStatus === 1 ? "success" : "secondary"}>
                    {c.customerStatus === 1 ? "Active" : "Disabled"}
                  </Badge>
                </td>
                <td className="text-end">
                  <Button variant="outline-secondary" size="sm" className="me-2" onClick={() => handleOpenEdit(c)}>
                    Edit
                  </Button>
                  <Button variant="outline-danger" size="sm" onClick={() => handleDelete(c.customerId)}>
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {/* Customer Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>{editingCust ? "Edit Customer" : "Add New Customer"}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSave}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Full Name *</Form.Label>
              <Form.Control
                required
                value={formData.customerFullName}
                onChange={(e) => setFormData({ ...formData, customerFullName: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Email Address *</Form.Label>
              <Form.Control
                type="email"
                required
                value={formData.emailAddress}
                onChange={(e) => setFormData({ ...formData, emailAddress: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Telephone</Form.Label>
              <Form.Control
                value={formData.telephone}
                onChange={(e) => setFormData({ ...formData, telephone: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Birthday</Form.Label>
              <Form.Control
                type="date"
                value={formData.customerBirthday}
                onChange={(e) => setFormData({ ...formData, customerBirthday: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>{editingCust ? "New Password (leave blank to keep)" : "Password *"}</Form.Label>
              <Form.Control
                type="password"
                required={!editingCust}
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="warning" type="submit">
              Save Customer
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}
