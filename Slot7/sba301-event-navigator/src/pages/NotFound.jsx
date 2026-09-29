import { Button, Container } from "react-bootstrap";
import { Link } from "react-router-dom";

function NotFound() {
  return (
    <Container className="py-5 text-center">
      <h1 className="display-4 fw-bold text-danger">404</h1>
      <h2>Page Not Found</h2>
      <p className="text-secondary mb-4">The client route you requested does not exist in this application.</p>
      <Button as={Link} to="/" variant="primary">
        Go Home
      </Button>
    </Container>
  );
}

export default NotFound;
