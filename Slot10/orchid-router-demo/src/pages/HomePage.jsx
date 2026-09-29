import { Button, Container } from "react-bootstrap";
import { Link } from "react-router-dom";

export default function HomePage() {
  return (
    <Container className="py-5 text-center">
      <h1 className="display-4 fw-bold mb-3">Orchid Gallery SPA</h1>
      <p className="lead text-secondary mb-4">
        Explore exquisite orchids through client-side URL-driven navigation.
      </p>
      <Button as={Link} to="/orchids" size="lg" variant="primary">
        Browse Orchid Catalog
      </Button>
    </Container>
  );
}
