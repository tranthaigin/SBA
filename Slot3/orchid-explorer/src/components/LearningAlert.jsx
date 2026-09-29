import { Alert, Container } from 'react-bootstrap';

function LearningAlert() {
  return (
    <section className="py-4">
      <Container>
        <Alert variant="info" className="mb-0">
          <Alert.Heading>Architecture Check</Alert.Heading>
          The six orchid cards repeat the same structure. In Slot 04, ask: how can one
          OrchidCard component receive different name, image and category values?
        </Alert>
      </Container>
    </section>
  );
}

export default LearningAlert;
