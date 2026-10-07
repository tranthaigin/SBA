import { useState } from "react";
import { Badge, Button, Card, Modal } from "react-bootstrap";

function OrchidCard({ orchid }) {
  const [showDetail, setShowDetail] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);

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
          <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
            <Card.Title className="mb-0">{orchid.orchidName}</Card.Title>
            {orchid.isSpecial && <Badge bg="warning" text="dark">Special</Badge>}
          </div>
          <Card.Text className="text-secondary small mb-3">
            <strong>Category:</strong> {orchid.category}<br />
            <strong>Origin:</strong> {orchid.origin}<br />
            <strong>Rating:</strong> {orchid.rating} ★
          </Card.Text>
          <div className="mt-auto d-flex gap-2">
            <Button
              className="flex-fill"
              variant="primary"
              size="sm"
              onClick={() => setShowDetail(true)}
            >
              View Detail
            </Button>
            <Button
              variant={isFavorite ? "danger" : "outline-danger"}
              size="sm"
              onClick={() => setIsFavorite(!isFavorite)}
            >
              {isFavorite ? "♥ Favorite" : "♡ Add Favorite"}
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
            style={{ maxHeight: "250px", objectFit: "cover" }}
          />
          <p><strong>Category:</strong> {orchid.category}</p>
          <p><strong>Origin:</strong> {orchid.origin}</p>
          <p><strong>Color:</strong> {orchid.color}</p>
          <p><strong>Rating:</strong> {orchid.rating} ★</p>
          <p className="mb-0"><strong>Description:</strong> {orchid.description}</p>
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
