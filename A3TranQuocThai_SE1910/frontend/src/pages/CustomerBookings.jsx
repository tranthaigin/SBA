import { useEffect, useState } from "react";
import { Container, Table, Badge, Alert, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function CustomerBookings() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchBookings = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await axios.get("/api/customer/bookings");
      setBookings(res.data);
    } catch (err) {
      setError("Failed to load your booking reservations");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  if (!user || user.role !== "ROLE_CUSTOMER") {
    return (
      <Container className="py-5 text-center">
        <Alert variant="warning">Please log in as a Customer to view your bookings.</Alert>
      </Container>
    );
  }

  const getStatusBadge = (status) => {
    switch (status) {
      case 1:
        return <Badge bg="success">Confirmed</Badge>;
      case 2:
        return <Badge bg="info">Completed</Badge>;
      case 3:
        return <Badge bg="danger">Cancelled</Badge>;
      default:
        return <Badge bg="secondary">Unknown</Badge>;
    }
  };

  return (
    <Container className="py-2">
      <div className="mb-4">
        <h2>My Reservations</h2>
        <small className="text-muted">Booking history for {user.fullName || user.email}</small>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="warning" />
        </div>
      ) : bookings.length === 0 ? (
        <div className="text-center py-5 text-muted">You have no active reservations yet.</div>
      ) : (
        <Table responsive hover className="align-middle border shadow-sm">
          <thead className="table-light">
            <tr>
              <th>Reservation ID</th>
              <th>Date Booked</th>
              <th>Rooms & Duration</th>
              <th>Total Amount</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {bookings.map((b) => (
              <tr key={b.bookingReservationId}>
                <td className="fw-mono">#{b.bookingReservationId}</td>
                <td>{b.bookingDate}</td>
                <td>
                  {b.bookingDetails?.map((d, idx) => (
                    <div key={idx} className="small">
                      <strong>Room #{d.roomInformation?.roomNumber}</strong> ({d.startDate} ~ {d.endDate})
                    </div>
                  ))}
                </td>
                <td className="fw-bold text-success">${b.totalPrice}</td>
                <td>{getStatusBadge(b.bookingStatus)}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </Container>
  );
}
