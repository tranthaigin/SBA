import { useState } from "react";
import { Badge, Button, Card, Modal } from "react-bootstrap";

function OrchidCard({ orchid }) {
  const [showDetail, setShowDetail] = useState(false);

  return (
    <>
      <Card className="h-100 shadow-sm">
        <Card.Img
          variant="top"
          src={orchid.image}
          alt={orchid.orchidName}
          className="orchid-image"
        />

        <Card.Body className="d-flex flex-column">
          <div className="d-flex justify-content-between align-items-start gap-2">
            <Card.Title>{orchid.orchidName}</Card.Title>
            {orchid.isSpecial && <Badge bg="warning" text="dark">Special</Badge>}
          </div>

          <Card.Text>
            Category: {orchid.category}<br />
            {/* Origin: {orchid.origin}<br />
            Rating: {orchid.rating} */}
          </Card.Text>
          <div className="mt-auto d-flex gap-2">
            <Button
              variant="primary"
              onClick={() => setShowDetail(true)}
              className="flex-grow-1"
            >
              View Detail
            </Button>
          </div>
        </Card.Body>
      </Card>

      <Modal show={showDetail} onHide={() => setShowDetail(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>{orchid.orchidName}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <img
            src={orchid.image}
            alt={orchid.orchidName}
            className="img-fluid rounded mb-3 w-100"
            style={{ maxHeight: '300px', objectFit: 'cover' }}
          />
          <p><strong>Category:</strong> {orchid.category}</p>
          <p><strong>Location:</strong> {orchid.origin}</p>
          <p><strong>Area:</strong> {orchid.color}</p>
          <p><strong>Rating:</strong> {orchid.rating}</p>
          <p className="mb-0">{orchid.description}</p>
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

export default OrchidCard;
