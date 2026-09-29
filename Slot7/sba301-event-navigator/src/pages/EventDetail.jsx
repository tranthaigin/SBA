import { Alert, Badge, Button, Card, Container } from "react-bootstrap";
import { Link, useNavigate, useParams } from "react-router-dom";
import { events } from "../data/events";

function EventDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const event = events.find((item) => item.id === id);

  if (!event) {
    return (
      <Container className="py-5">
        <Alert variant="warning" className="shadow-sm">
          <Alert.Heading>Event Not Found</Alert.Heading>
          <p>No campus event found with identifier: <strong>{id}</strong>.</p>
          <hr />
          <Button as={Link} to="/events" variant="primary">
            Back to Events
          </Button>
        </Alert>
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <Card className="shadow-sm border-0">
        <Card.Body className="p-4">
          <div className="mb-3">
            <Badge bg={event.featured ? "warning" : "secondary"} text={event.featured ? "dark" : "light"} className="px-3 py-2 fs-6">
              {event.featured ? "Featured Event" : event.category}
            </Badge>
          </div>
          <Card.Title as="h2" className="fw-bold mb-3">{event.title}</Card.Title>
          <Card.Text className="lead text-secondary">{event.description}</Card.Text>
          <hr />
          <div className="row g-3 mb-4">
            <div className="col-md-6">
              <p className="mb-2"><strong>📅 Date:</strong> {event.date}</p>
              <p className="mb-2"><strong>📍 Location:</strong> {event.location}</p>
            </div>
            <div className="col-md-6">
              <p className="mb-2"><strong>🏛️ Organizer:</strong> {event.organizer}</p>
              <p className="mb-2"><strong>👥 Available Seats:</strong> {event.seats} seats</p>
            </div>
          </div>
          <div className="d-flex gap-2">
            <Button as={Link} to="/events" variant="primary">
              Back to Events
            </Button>
            <Button variant="outline-secondary" onClick={() => navigate(-1)}>
              Go Back
            </Button>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
}

export default EventDetail;
