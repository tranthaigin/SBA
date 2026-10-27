import { useEffect, useState } from "react";
import { Container, Row, Col, Card, Button, Badge, Modal, Form, Alert, Spinner } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export default function RoomCatalog({ onRequireLogin }) {
  const { user } = useAuth();
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Booking Modal State
  const [bookingRoom, setBookingRoom] = useState(null);
  const [startDate, setStartDate] = useState(() => {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().split("T")[0];
  });
  const [endDate, setEndDate] = useState(() => {
    const d = new Date();
    d.setDate(d.getDate() + 3);
    return d.toISOString().split("T")[0];
  });
  const [bookingSuccess, setBookingSuccess] = useState("");
  const [bookingLoading, setBookingLoading] = useState(false);

  const fetchRooms = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await axios.get("/api/rooms/public");
      setRooms(res.data);
    } catch (err) {
      setError("Failed to load available rooms");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRooms();
  }, []);

  const handleOpenBooking = (room) => {
    if (!user) {
      onRequireLogin();
      return;
    }
    setBookingSuccess("");
    setBookingRoom(room);
  };

  const calculateDays = () => {
    if (!startDate || !endDate) return 1;
    const diff = new Date(endDate) - new Date(startDate);
    const days = Math.ceil(diff / (1000 * 60 * 60 * 24));
    return days > 0 ? days : 1;
  };

  const handleConfirmBooking = async (e) => {
    e.preventDefault();
    if (!bookingRoom) return;
    setBookingLoading(true);
    try {
      await axios.post("/api/customer/bookings", {
        items: [
          {
            roomId: bookingRoom.roomId,
            startDate,
            endDate,
            actualPrice: bookingRoom.roomPricePerDay
          }
        ]
      });
      setBookingSuccess(`Booking confirmed successfully for Room ${bookingRoom.roomNumber}!`);
      setTimeout(() => {
        setBookingRoom(null);
        setBookingSuccess("");
      }, 1800);
    } catch (err) {
      alert(err.response?.data?.message || "Booking failed");
    } finally {
      setBookingLoading(false);
    }
  };

  return (
    <Container className="py-2">
      <div className="text-center mb-5">
        <h1 className="fw-bold">Discover Luxury Accommodations</h1>
        <p className="text-muted">
          Experience world-class comfort at FU Mini Hotel. Browse our curated selection of rooms and suites.
        </p>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}
      {loading ? (
        <div className="text-center py-5">
          <Spinner animation="border" variant="warning" />
        </div>
      ) : rooms.length === 0 ? (
        <div className="text-center py-5 text-muted">No rooms currently available.</div>
      ) : (
        <Row className="g-4">
          {rooms.map((room) => (
            <Col md={6} lg={4} key={room.roomId}>
              <Card className="h-100 shadow-sm border-0 d-flex flex-column">
                <div
                  style={{
                    height: "180px",
                    background: "linear-gradient(135deg, #1e3c72 0%, #2a5298 100%)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    color: "white",
                    fontSize: "2.5rem"
                  }}
                >
                  🛏️
                </div>
                <Card.Body className="d-flex flex-column">
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <span className="fw-bold fs-5">Room #{room.roomNumber}</span>
                    <Badge bg="warning" text="dark">
                      {room.roomType?.roomTypeName || "Standard"}
                    </Badge>
                  </div>
                  <p className="text-muted small flex-grow-1">
                    {room.roomDetailDescription || "High comfort room with full amenities."}
                  </p>
                  <div className="d-flex justify-content-between align-items-center py-2 border-top">
                    <div>
                      <span className="text-muted small">Max Capacity: </span>
                      <strong>{room.roomMaxCapacity || 2} Guests</strong>
                    </div>
                    <div className="text-end">
                      <div className="fs-5 fw-bold text-success">
                        ${room.roomPricePerDay}
                        <small className="text-muted fs-6">/day</small>
                      </div>
                    </div>
                  </div>
                  <Button
                    variant="warning"
                    className="w-100 mt-2 fw-semibold"
                    onClick={() => handleOpenBooking(room)}
                  >
                    {user?.role === "ROLE_STAFF" ? "View Room Details" : "Book This Room"}
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          ))}
        </Row>
      )}

      {/* Booking Modal */}
      {bookingRoom && (
        <Modal show={true} onHide={() => setBookingRoom(null)} centered>
          <Modal.Header closeButton>
            <Modal.Title>Book Room #{bookingRoom.roomNumber}</Modal.Title>
          </Modal.Header>
          <Form onSubmit={handleConfirmBooking}>
            <Modal.Body>
              {bookingSuccess && <Alert variant="success">{bookingSuccess}</Alert>}
              <div className="mb-3">
                <Badge bg="info" className="me-2">{bookingRoom.roomType?.roomTypeName}</Badge>
                <span className="text-muted small">Daily Rate: ${bookingRoom.roomPricePerDay}</span>
              </div>
              <Row>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Check-in Date *</Form.Label>
                    <Form.Control
                      type="date"
                      required
                      value={startDate}
                      onChange={(e) => setStartDate(e.target.value)}
                    />
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group className="mb-3">
                    <Form.Label>Check-out Date *</Form.Label>
                    <Form.Control
                      type="date"
                      required
                      value={endDate}
                      onChange={(e) => setEndDate(e.target.value)}
                    />
                  </Form.Group>
                </Col>
              </Row>
              <div className="bg-light p-3 rounded mt-2">
                <div className="d-flex justify-content-between">
                  <span>Duration:</span>
                  <strong>{calculateDays()} days</strong>
                </div>
                <div className="d-flex justify-content-between fs-5 mt-1 border-top pt-1">
                  <span>Estimated Total:</span>
                  <strong className="text-success">
                    ${(calculateDays() * Number(bookingRoom.roomPricePerDay || 50)).toFixed(2)}
                  </strong>
                </div>
              </div>
            </Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={() => setBookingRoom(null)}>
                Cancel
              </Button>
              <Button variant="warning" type="submit" disabled={bookingLoading}>
                {bookingLoading ? "Confirming..." : "Confirm Reservation"}
              </Button>
            </Modal.Footer>
          </Form>
        </Modal>
      )}
    </Container>
  );
}
