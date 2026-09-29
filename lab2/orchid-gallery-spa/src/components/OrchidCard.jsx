// src/components/OrchidCard.jsx
import { Badge, Button, Card } from 'react-bootstrap';

export default function OrchidCard({ orchid, onDetail }) {
  return (
    <Card className="orchid-card shadow-sm">
      <Card.Img
        className="orchid-card-img"
        variant="top"
        src={orchid.image}
        alt={orchid.orchidName}
      />
      <Card.Body className="d-flex flex-column">
        <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
          <Card.Title className="mb-0 fs-5">{orchid.orchidName}</Card.Title>
          {orchid.isSpecial && <Badge bg="warning" text="dark">Special</Badge>}
        </div>
        <Card.Text className="text-muted mb-3">{orchid.category}</Card.Text>
        <Button
          variant="primary"
          className="mt-auto"
          onClick={() => onDetail(orchid)}
        >
          Detail
        </Button>
      </Card.Body>
    </Card>
  );
}
