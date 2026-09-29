import { Button, Container } from "react-bootstrap";
import { Link, useLocation } from "react-router-dom";

export default function NotFoundPage() {
  const location = useLocation();

  return (
    <Container className="py-5 text-center">
      <h1 className="display-4 fw-bold text-danger">404</h1>
      <h2 className="mb-3">Route Not Found</h2>
      <p className="lead text-secondary mb-4">
        No page matches the client path <code>{location.pathname}</code>.
      </p>
      <Button as={Link} to="/" variant="primary">
        Go to Homepage
      </Button>
    </Container>
  );
}
