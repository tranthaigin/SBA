import { useState } from "react";
import { Badge, Button, Card, Modal } from "react-bootstrap";

function EventCard({ event }) {
  const [showDetail, setShowDetail] = useState(false);

  return (
    <>
      <Card className="h-100 shadow-sm event-card">
        <Card.Img
          className="event-image"
          variant="top"
          src={event.image}
          alt={event.title}
        />
        <Card.Body className="d-flex flex-column">
          <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
            <Card.Title className="mb-0 fs-5">{event.title}</Card.Title>
            {event.featured && <Badge bg="warning" text="dark">Featured</Badge>}
          </div>
          <Card.Text className="mb-1 text-muted small">
            <strong>Category:</strong> {event.category}
          </Card.Text>
          <Card.Text className="mb-1 text-muted small">
            <strong>Date:</strong> {event.date}
          </Card.Text>
          <Card.Text className="mb-3 text-muted small">
            <strong>Location:</strong> {event.location}
          </Card.Text>
          <Button
            className="mt-auto"
            variant="primary"
            onClick={() => setShowDetail(true)}
          >
            View Detail
          </Button>
        </Card.Body>
      </Card>

      <Modal show={showDetail} onHide={() => setShowDetail(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>{event.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <img
            src={event.image}
            alt={event.title}
            className="img-fluid rounded mb-3 w-100"
            style={{ maxHeight: "200px", objectFit: "cover" }}
          />
          <p><strong>Category:</strong> {event.category}</p>
          <p><strong>Date:</strong> {event.date}</p>
          <p><strong>Location:</strong> {event.location}</p>
          <p><strong>Available Seats:</strong> {event.seats}</p>
          <p className="mb-0"><strong>Description:</strong> {event.description}</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDetail(false)}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
}

export default EventCard;
