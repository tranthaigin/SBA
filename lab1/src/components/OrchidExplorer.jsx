import { useState } from "react";
import { Alert, Col, Container, Form, Row } from "react-bootstrap";
import { orchids } from "../data/orchids";
import OrchidCard from "./OrchidCard";

function OrchidExplorer() {
  const [searchText, setSearchText] = useState("");
  const [specialOnly, setSpecialOnly] = useState(false);

  const normalizedText = searchText.trim().toLowerCase();

  const visibleOrchids = orchids.filter((orchid) => {
    const matchesText =
      orchid.orchidName.toLowerCase().includes(normalizedText) ||
      orchid.category.toLowerCase().includes(normalizedText);

    const matchesSpecial = !specialOnly || orchid.isSpecial;

    return matchesText && matchesSpecial;
  });

  return (
    <Container className="py-4">
      <Row className="g-3 align-items-end mb-4">
        <Col md={8}>
          <Form.Label htmlFor="orchid-search">Search</Form.Label>
          <Form.Control
            id="orchid-search"
            type="text"
            placeholder="Search by name or category..."
            value={searchText}
            onChange={(event) => setSearchText(event.target.value)}
          />
        </Col>

        <Col md={4}>
          <Form.Check
            type="switch"
            id="special-only"
            label="Show special orchids only"
            checked={specialOnly}
            onChange={(event) => setSpecialOnly(event.target.checked)}
          />
        </Col>
      </Row>

      <p className="text-muted">
        Showing {visibleOrchids.length} of {orchids.length} orchids
      </p>

      {visibleOrchids.length === 0 ? (
        <Alert variant="info">No orchid matches the current filters.</Alert>
      ) : (
        <Row className="g-4">
          {visibleOrchids.map((orchid) => (
            <Col key={orchid.id} sm={12} md={6} lg={4}>
              <OrchidCard orchid={orchid} />
            </Col>
          ))}
        </Row>
      )}
    </Container>
  );
}

export default OrchidExplorer;
