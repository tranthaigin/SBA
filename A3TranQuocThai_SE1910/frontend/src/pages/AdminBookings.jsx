import { useEffect, useState } from "react";
import {
  Container,
  Table,
  Badge,
  Button,
  Alert,
  Spinner,
  Form,
  Modal,
} from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function AdminBookings() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [search, setSearch] = useState(""),
    [editing, setEditing] = useState(null),
    [show, setShow] = useState(false),
    [customers, setCustomers] = useState([]),
    [rooms, setRooms] = useState([]);
  const emptyItem = () => ({
    roomId: "",
    startDate: "",
    endDate: "",
    actualPrice: "",
  });
  const [form, setForm] = useState({ customerId: "", items: [emptyItem()] });
  async function open(booking = null) {
    try {
      const [c, r] = await Promise.all([
        axios.get("/api/admin/customers"),
        axios.get("/api/admin/rooms"),
      ]);
      setCustomers(c.data);
      setRooms(r.data);
      setEditing(booking);
      setForm(
        booking
          ? {
              customerId: booking.customer.customerId,
              items: booking.bookingDetails.map((d) => ({
                roomId: d.roomId,
                startDate: d.startDate,
                endDate: d.endDate,
                actualPrice: d.actualPrice,
              })),
            }
          : { customerId: "", items: [emptyItem()] },
      );
      setShow(true);
      setError("");
    } catch (e) {
      setError("Cannot load booking form");
    }
  }
  async function save(e) {
    e.preventDefault();
    try {
      const payload = {
        customerId: Number(form.customerId),
        items: form.items.map((i) => ({
          ...i,
          roomId: Number(i.roomId),
          actualPrice: i.actualPrice === "" ? null : Number(i.actualPrice),
        })),
      };
      if (editing)
        await axios.put(
          `/api/admin/bookings/${editing.bookingReservationId}`,
          payload,
        );
      else await axios.post("/api/admin/bookings", payload);
      setShow(false);
      fetchBookings();
    } catch (e) {
      setError(e.response?.data?.message || "Cannot save booking");
    }
  }
  async function remove(b) {
    if (!window.confirm("Delete this booking and its details?")) return;
    try {
      await axios.delete(`/api/admin/bookings/${b.bookingReservationId}`);
      fetchBookings();
    } catch (e) {
      setError("Cannot delete booking");
    }
  }
  function itemChange(index, key, value) {
    setForm({
      ...form,
      items: form.items.map((i, n) =>
        n === index ? { ...i, [key]: value } : i,
      ),
    });
  }

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchBookings = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await axios.get("/api/admin/bookings");
      setBookings(res.data);
    } catch (err) {
      setError("Failed to load reservations");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.role === "ROLE_STAFF") fetchBookings();
  }, [user]);

  if (user?.role !== "ROLE_STAFF") {
    return (
      <Container className="py-5 text-center">
        <Alert variant="danger">Access Denied. Staff privilege required.</Alert>
      </Container>
    );
  }

  const handleUpdateStatus = async (id, status) => {
    try {
      await axios.put(`/api/admin/bookings/${id}/status?status=${status}`);
      fetchBookings();
    } catch (err) {
      alert("Failed to update status");
    }
  };

  return (
    <Container className="py-2">
      <div className="mb-4">
        <h2>Booking Reservations Management</h2>
        <small className="text-muted">
          Staff overview of all customer room rentals
        </small>
      </div>

      <Form.Control
        aria-label="Search bookings"
        placeholder="Search bookings by customer or ID"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <Button className="my-3" onClick={() => open()}>
        Create Booking
      </Button>
      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="warning" />
        </div>
      ) : bookings.length === 0 ? (
        <div className="text-center py-5 text-muted">
          No reservations recorded.
        </div>
      ) : (
        <Table responsive hover className="align-middle border shadow-sm">
          <thead className="table-light">
            <tr>
              <th>ID</th>
              <th>Customer</th>
              <th>Date</th>
              <th>Rooms & Duration</th>
              <th>Total ($)</th>
              <th>Status</th>
              <th className="text-end">Update Status</th>
            </tr>
          </thead>
          <tbody>
            {bookings
              .filter((b) =>
                `${b.bookingReservationId} ${b.customer?.customerFullName} ${b.customer?.emailAddress}`
                  .toLowerCase()
                  .includes(search.toLowerCase()),
              )
              .map((b) => (
                <tr key={b.bookingReservationId}>
                  <td className="fw-mono">#{b.bookingReservationId}</td>
                  <td>
                    <strong>{b.customer?.customerFullName}</strong>
                    <div className="small text-muted">
                      {b.customer?.emailAddress}
                    </div>
                  </td>
                  <td>{b.bookingDate}</td>
                  <td>
                    {b.bookingDetails?.map((d, idx) => (
                      <div key={idx} className="small">
                        Room #{d.roomInformation?.roomNumber} ({d.startDate} ~{" "}
                        {d.endDate})
                      </div>
                    ))}
                  </td>
                  <td className="fw-bold text-success">${b.totalPrice}</td>
                  <td>
                    <Badge
                      bg={
                        b.bookingStatus === 1
                          ? "success"
                          : b.bookingStatus === 2
                            ? "info"
                            : "danger"
                      }
                    >
                      {b.bookingStatus === 1
                        ? "Confirmed"
                        : b.bookingStatus === 2
                          ? "Completed"
                          : "Cancelled"}
                    </Badge>
                  </td>
                  <td className="text-end">
                    <div className="d-inline-flex gap-1">
                      <Button size="sm" onClick={() => open(b)}>
                        Edit
                      </Button>
                      <Button
                        size="sm"
                        variant="danger"
                        onClick={() => remove(b)}
                      >
                        Delete
                      </Button>
                      <Button
                        variant="outline-success"
                        size="sm"
                        disabled={b.bookingStatus === 1}
                        onClick={() =>
                          handleUpdateStatus(b.bookingReservationId, 1)
                        }
                      >
                        Confirm
                      </Button>
                      <Button
                        variant="outline-info"
                        size="sm"
                        disabled={b.bookingStatus === 2}
                        onClick={() =>
                          handleUpdateStatus(b.bookingReservationId, 2)
                        }
                      >
                        Complete
                      </Button>
                      <Button
                        variant="outline-danger"
                        size="sm"
                        disabled={b.bookingStatus === 3}
                        onClick={() =>
                          handleUpdateStatus(b.bookingReservationId, 3)
                        }
                      >
                        Cancel
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
          </tbody>
        </Table>
      )}
      <Modal show={show} onHide={() => setShow(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>{editing ? "Edit" : "Create"} Booking</Modal.Title>
        </Modal.Header>
        <Form onSubmit={save}>
          <Modal.Body>
            {error && <Alert variant="danger">{error}</Alert>}
            <Form.Label>Customer</Form.Label>
            <Form.Select
              required
              value={form.customerId}
              onChange={(e) => setForm({ ...form, customerId: e.target.value })}
            >
              <option value="">Choose customer</option>
              {customers.map((c) => (
                <option key={c.customerId} value={c.customerId}>
                  {c.customerFullName}
                </option>
              ))}
            </Form.Select>
            {form.items.map((item, index) => (
              <fieldset key={index} className="border p-3 my-2">
                <legend>Room {index + 1}</legend>
                <Form.Select
                  required
                  aria-label={`Room ${index + 1}`}
                  value={item.roomId}
                  onChange={(e) => itemChange(index, "roomId", e.target.value)}
                >
                  <option value="">Choose room</option>
                  {rooms.map((r) => (
                    <option key={r.roomId} value={r.roomId}>
                      {r.roomNumber}
                    </option>
                  ))}
                </Form.Select>
                <Form.Label>Start date</Form.Label>
                <Form.Control
                  required
                  type="date"
                  value={item.startDate}
                  onChange={(e) =>
                    itemChange(index, "startDate", e.target.value)
                  }
                />
                <Form.Label>End date</Form.Label>
                <Form.Control
                  required
                  type="date"
                  min={item.startDate}
                  value={item.endDate}
                  onChange={(e) => itemChange(index, "endDate", e.target.value)}
                />
                <Form.Label>Price per day (optional)</Form.Label>
                <Form.Control
                  type="number"
                  min="0"
                  step="0.01"
                  value={item.actualPrice}
                  onChange={(e) =>
                    itemChange(index, "actualPrice", e.target.value)
                  }
                />
                <Button
                  variant="secondary"
                  disabled={form.items.length === 1}
                  onClick={() =>
                    setForm({
                      ...form,
                      items: form.items.filter((_, n) => n !== index),
                    })
                  }
                >
                  Remove room
                </Button>
              </fieldset>
            ))}
            <Button
              onClick={() =>
                setForm({ ...form, items: [...form.items, emptyItem()] })
              }
            >
              Add room
            </Button>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShow(false)}>
              Close
            </Button>
            <Button type="submit">Save Booking</Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}
