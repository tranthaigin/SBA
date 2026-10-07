import { Card, Col, Container, Row } from 'react-bootstrap';

function QuickStats() {
  return (
    <section className="py-4 bg-light">
      <Container>
        <Row className="g-3 text-center">
          <Col xs={12} md={4}>
            <Card className="h-100 shadow-sm">
              <Card.Body>
                <div className="display-6 fw-bold">6</div>
                <Card.Text>Featured orchids</Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col xs={12} md={4}>
            <Card className="h-100 shadow-sm">
              <Card.Body>
                <div className="display-6 fw-bold">3</div>
                <Card.Text>Care topics</Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col xs={12} md={4}>
            <Card className="h-100 shadow-sm">
              <Card.Body>
                <div className="display-6 fw-bold">7</div>
                <Card.Text>Custom components</Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </section>
  );
}

export default QuickStats;
