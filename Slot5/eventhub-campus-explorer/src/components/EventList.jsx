import { useState } from "react";
import { Button, Col, Container, Form, Row } from "react-bootstrap";
import { events } from "../data/events";
import EventCard from "./EventCard";

function EventList() {
  const [searchText, setSearchText] = useState("");
  const [category, setCategory] = useState("All");
  const [featuredOnly, setFeaturedOnly] = useState(false);

  const categories = ["All", ...new Set(events.map((e) => e.category))];

  const filteredEvents = events.filter((event) => {
    const matchesText = event.title
      .toLowerCase()
      .includes(searchText.trim().toLowerCase());
    const matchesCategory = category === "All" || event.category === category;
    const matchesFeatured = !featuredOnly || event.featured;
    return matchesText && matchesCategory && matchesFeatured;
  });

  const handleReset = () => {
    setSearchText("");
    setCategory("All");
    setFeaturedOnly(false);
  };

  return (
    <Container id="events" className="py-5">
      <div className="d-flex flex-column flex-lg-row justify-content-between gap-3 mb-4">
        <div>
          <h2 className="mb-1 fw-bold">Upcoming Events</h2>
          <p className="text-muted mb-0">
            Showing <strong>{filteredEvents.length}</strong> of {events.length} events
          </p>
        </div>

        <div className="d-flex flex-column flex-md-row align-items-md-center gap-2">
          <Form.Control
            type="search"
            placeholder="Search events..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            style={{ minWidth: "220px" }}
          />

          <Form.Select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            style={{ minWidth: "160px" }}
          >
            {categories.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </Form.Select>

          <Form.Check
            type="switch"
            id="featured-only"
            label="Featured only"
            checked={featuredOnly}
            onChange={(e) => setFeaturedOnly(e.target.checked)}
            className="text-nowrap ms-md-2"
          />

          {(searchText || category !== "All" || featuredOnly) && (
            <Button variant="outline-secondary" size="sm" onClick={handleReset}>
              Reset
            </Button>
          )}
        </div>
      </div>

      {filteredEvents.length === 0 ? (
        <div className="text-center py-5 border rounded bg-light">
          <h4>No events found</h4>
          <p className="text-muted mb-3">Try changing your search term or filters.</p>
          <Button variant="outline-primary" onClick={handleReset}>
            Clear all filters
          </Button>
        </div>
      ) : (
        <Row className="g-4">
          {filteredEvents.map((event) => (
            <Col key={event.id} xs={12} md={6} lg={4}>
              <EventCard event={event} />
            </Col>
          ))}
        </Row>
      )}
    </Container>
  );
}

export default EventList;
