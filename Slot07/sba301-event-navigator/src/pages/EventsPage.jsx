import { useState } from "react";
import { Col, Container, Form, Row } from "react-bootstrap";
import EventCard from "../components/EventCard";
import { events } from "../data/events";

function EventsPage() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("All");

  const categories = ["All", ...new Set(events.map((e) => e.category))];

  const filteredEvents = events.filter((event) => {
    const keyword = search.trim().toLowerCase();
    const matchesText =
      event.title.toLowerCase().includes(keyword) ||
      event.location.toLowerCase().includes(keyword);
    const matchesCategory = category === "All" || event.category === category;
    return matchesText && matchesCategory;
  });

  return (
    <Container className="py-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Campus Events</h2>
        <p className="text-muted mb-0">
          <strong>{filteredEvents.length}</strong> event(s) found
        </p>
      </div>

      <Row className="g-3 mb-4">
        <Col md={8}>
          <Form.Control
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by title or location..."
          />
        </Col>
        <Col md={4}>
          <Form.Select value={category} onChange={(e) => setCategory(e.target.value)}>
            {categories.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </Form.Select>
        </Col>
      </Row>

      <Row className="g-4">
        {filteredEvents.map((event) => (
          <Col key={event.id} sm={12} md={6} lg={4}>
            <EventCard event={event} />
          </Col>
        ))}
      </Row>

      {filteredEvents.length === 0 && (
        <p className="text-center text-muted mt-5 fs-5">No events found matching your query.</p>
      )}
    </Container>
  );
}

export default EventsPage;
