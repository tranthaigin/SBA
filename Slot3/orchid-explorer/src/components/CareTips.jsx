import { Card, Col, Container, Row } from 'react-bootstrap';

function CareTips() {
  return (
    <section id="care" className="py-5 bg-light">
      <Container>
        <h2 className="fw-bold mb-4">Care Tips</h2>
        <Row className="g-4">
          <Col xs={12} md={4}>
            <Card className="h-100 border-0 shadow-sm">
              <Card.Body>
                <Card.Title>Light</Card.Title>
                <Card.Text>Prefer bright, indirect light. Observe leaf color instead of guessing.</Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col xs={12} md={4}>
            <Card className="h-100 border-0 shadow-sm">
              <Card.Body>
                <Card.Title>Water</Card.Title>
                <Card.Text>
                  Water based on medium dryness and root condition, not a fixed daily schedule.
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col xs={12} md={4}>
            <Card className="h-100 border-0 shadow-sm">
              <Card.Body>
                <Card.Title>Airflow</Card.Title>
                <Card.Text>Good airflow helps reduce excessive moisture around leaves and roots.</Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </section>
  );
}

export default CareTips;
