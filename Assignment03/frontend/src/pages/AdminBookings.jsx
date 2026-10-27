import { useEffect, useState } from "react";
import { Container, Table, Badge, Button, Alert, Spinner, Form } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function AdminBookings() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
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
    fetchBookings();
  }, []);

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
        <small className="text-muted">Staff overview of all customer room rentals</small>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="warning" />
        </div>
      ) : bookings.length === 0 ? (
        <div className="text-center py-5 text-muted">No reservations recorded.</div>
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
            {bookings.map((b) => (
              <tr key={b.bookingReservationId}>
                <td className="fw-mono">#{b.bookingReservationId}</td>
                <td>
                  <strong>{b.customer?.customerFullName}</strong>
                  <div className="small text-muted">{b.customer?.emailAddress}</div>
                </td>
                <td>{b.bookingDate}</td>
                <td>
                  {b.bookingDetails?.map((d, idx) => (
                    <div key={idx} className="small">
                      Room #{d.roomInformation?.roomNumber} ({d.startDate} ~ {d.endDate})
                    </div>
                  ))}
                </td>
                <td className="fw-bold text-success">${b.totalPrice}</td>
                <td>
                  <Badge bg={b.bookingStatus === 1 ? "success" : b.bookingStatus === 2 ? "info" : "danger"}>
                    {b.bookingStatus === 1 ? "Confirmed" : b.bookingStatus === 2 ? "Completed" : "Cancelled"}
                  </Badge>
                </td>
                <td className="text-end">
                  <div className="d-inline-flex gap-1">
                    <Button
                      variant="outline-success"
                      size="sm"
                      disabled={b.bookingStatus === 1}
                      onClick={() => handleUpdateStatus(b.bookingReservationId, 1)}
                    >
                      Confirm
                    </Button>
                    <Button
                      variant="outline-info"
                      size="sm"
                      disabled={b.bookingStatus === 2}
                      onClick={() => handleUpdateStatus(b.bookingReservationId, 2)}
                    >
                      Complete
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      disabled={b.bookingStatus === 3}
                      onClick={() => handleUpdateStatus(b.bookingReservationId, 3)}
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
    </Container>
  );
}
