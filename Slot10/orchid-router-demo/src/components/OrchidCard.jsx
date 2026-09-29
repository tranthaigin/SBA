import { Button, Card, Badge } from "react-bootstrap";
import { Link } from "react-router-dom";

export default function OrchidCard({ orchid }) {
  return (
    <Card className="h-100 shadow-sm border-0">
      <Card.Img
        className="orchid-image"
        variant="top"
        src={orchid.image}
        alt={orchid.orchidName}
      />
      <Card.Body className="d-flex flex-column">
        <div className="d-flex justify-content-between align-items-start mb-2">
          <Card.Title className="fs-5 fw-bold mb-0">{orchid.orchidName}</Card.Title>
          {orchid.isSpecial && <Badge bg="warning" text="dark">Special</Badge>}
        </div>
        <Card.Text className="text-secondary small mb-3">
          Category: {orchid.category}
        </Card.Text>
        <Button
          as={Link}
          to={`/orchids/${orchid.id}`}
          state={{ from: "/orchids" }}
          variant="outline-primary"
          className="mt-auto"
        >
          View Detail
        </Button>
      </Card.Body>
    </Card>
  );
}
