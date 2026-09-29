import { Badge, Button, Card } from "react-bootstrap";
import { Link } from "react-router-dom";

function EventCard({ event }) {
  return (
    <Card className="h-100 shadow-sm">
      <Card.Body className="d-flex flex-column">
        <div className="mb-2">
          <Badge bg={event.featured ? "warning" : "secondary"} text={event.featured ? "dark" : "light"}>
            {event.featured ? "Featured" : event.category}
          </Badge>
        </div>
        <Card.Title className="fs-5 fw-bold">{event.title}</Card.Title>
        <Card.Text className="mb-1 text-muted small">
          <strong>Date:</strong> {event.date}
        </Card.Text>
        <Card.Text className="mb-3 text-muted small">
          <strong>Location:</strong> {event.location}
        </Card.Text>
        <Button
          as={Link}
          to={`/events/${event.id}`}
          variant="outline-primary"
          className="mt-auto"
        >
          View Details
        </Button>
      </Card.Body>
    </Card>
  );
}

export default EventCard;
