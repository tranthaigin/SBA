import { useEffect, useState } from "react";
import { Container, Table, Button, Form, Modal, Alert, Badge, Spinner, Row, Col } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function AdminRooms() {
  const { user } = useAuth();
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [editingRoom, setEditingRoom] = useState(null);
  const [formData, setFormData] = useState({
    roomNumber: "",
    roomDetailDescription: "",
    roomMaxCapacity: 2,
    roomTypeId: "",
    roomStatus: 1,
    roomPricePerDay: 50.00
  });

  const fetchRooms = async () => {
    setLoading(true);
    setError("");
    try {
      const url = search ? `/api/admin/rooms?search=${encodeURIComponent(search)}` : "/api/admin/rooms";
      const [roomsRes, typesRes] = await Promise.all([
        axios.get(url),
        axios.get("/api/admin/room-types")
      ]);
      setRooms(roomsRes.data);
      setRoomTypes(typesRes.data);
    } catch (err) {
      setError("Failed to load rooms");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRooms();
  }, [search]);

  if (user?.role !== "ROLE_STAFF") {
    return (
      <Container className="py-5 text-center">
        <Alert variant="danger">Access Denied. Staff privilege required.</Alert>
      </Container>
    );
  }

  const handleOpenCreate = () => {
    setEditingRoom(null);
    setFormData({
      roomNumber: "",
      roomDetailDescription: "",
      roomMaxCapacity: 2,
      roomTypeId: roomTypes[0]?.roomTypeId || "",
      roomStatus: 1,
      roomPricePerDay: 50.00
    });
    setShowModal(true);
  };

  const handleOpenEdit = (r) => {
    setEditingRoom(r);
    setFormData({
      roomNumber: r.roomNumber || "",
      roomDetailDescription: r.roomDetailDescription || "",
      roomMaxCapacity: r.roomMaxCapacity || 2,
      roomTypeId: r.roomType?.roomTypeId || "",
      roomStatus: r.roomStatus,
      roomPricePerDay: r.roomPricePerDay || 50.00
    });
    setShowModal(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...formData,
        roomMaxCapacity: Number(formData.roomMaxCapacity),
        roomTypeId: Number(formData.roomTypeId),
        roomPricePerDay: Number(formData.roomPricePerDay)
      };

      if (editingRoom) {
        await axios.put(`/api/admin/rooms/${editingRoom.roomId}`, payload);
      } else {
        await axios.post("/api/admin/rooms", payload);
      }
      setShowModal(false);
      fetchRooms();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to save room");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this room?")) return;
    try {
      const res = await axios.delete(`/api/admin/rooms/${id}`);
      alert(res.data?.message || "Room deleted successfully");
      fetchRooms();
    } catch (err) {
      alert("Failed to delete room");
    }
  };

  return (
    <Container className="py-2">
      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
        <div>
          <h2>Manage Hotel Rooms</h2>
          <small className="text-muted">Staff workspace</small>
        </div>
        <div className="d-flex gap-2">
          <Form.Control
            type="text"
            placeholder="Search by room number or description..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ width: "300px" }}
          />
          <Button variant="warning" onClick={handleOpenCreate}>
            + Add New Room
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
              <th>Room #</th>
              <th>Type</th>
              <th>Capacity</th>
              <th>Price/Day</th>
              <th>Status</th>
              <th className="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            {rooms.map((r) => (
              <tr key={r.roomId}>
                <td>{r.roomId}</td>
                <td className="fw-bold">{r.roomNumber}</td>
                <td>{r.roomType?.roomTypeName || "Standard"}</td>
                <td>{r.roomMaxCapacity} Guests</td>
                <td className="text-success fw-semibold">${r.roomPricePerDay}</td>
                <td>
                  <Badge bg={r.roomStatus === 1 ? "success" : "secondary"}>
                    {r.roomStatus === 1 ? "Active" : "Inactive / Maintenance"}
                  </Badge>
                </td>
                <td className="text-end">
                  <Button variant="outline-secondary" size="sm" className="me-2" onClick={() => handleOpenEdit(r)}>
                    Edit
                  </Button>
                  <Button variant="outline-danger" size="sm" onClick={() => handleDelete(r.roomId)}>
                    Delete
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {/* Room Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} size="lg" centered>
        <Modal.Header closeButton>
          <Modal.Title>{editingRoom ? "Edit Room" : "Add New Room"}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSave}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Room Number *</Form.Label>
                  <Form.Control
                    required
                    value={formData.roomNumber}
                    onChange={(e) => setFormData({ ...formData, roomNumber: e.target.value })}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Room Type *</Form.Label>
                  <Form.Select
                    required
                    value={formData.roomTypeId}
                    onChange={(e) => setFormData({ ...formData, roomTypeId: e.target.value })}
                  >
                    <option value="">Select Room Type</option>
                    {roomTypes.map((t) => (
                      <option key={t.roomTypeId} value={t.roomTypeId}>
                        {t.roomTypeName}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Max Capacity (Guests)</Form.Label>
                  <Form.Control
                    type="number"
                    min={1}
                    value={formData.roomMaxCapacity}
                    onChange={(e) => setFormData({ ...formData, roomMaxCapacity: e.target.value })}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Price Per Day ($) *</Form.Label>
                  <Form.Control
                    type="number"
                    step="0.01"
                    required
                    value={formData.roomPricePerDay}
                    onChange={(e) => setFormData({ ...formData, roomPricePerDay: e.target.value })}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={formData.roomDetailDescription}
                onChange={(e) => setFormData({ ...formData, roomDetailDescription: e.target.value })}
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Status</Form.Label>
              <Form.Select
                value={formData.roomStatus}
                onChange={(e) => setFormData({ ...formData, roomStatus: Number(e.target.value) })}
              >
                <option value={1}>Active (Available for booking)</option>
                <option value={2}>Inactive / Maintenance</option>
              </Form.Select>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="warning" type="submit">
              Save Room
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}
