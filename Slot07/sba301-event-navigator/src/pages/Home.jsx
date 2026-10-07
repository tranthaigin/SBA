import { Button, Container } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

function Home() {
  const navigate = useNavigate();

  return (
    <Container className="py-5 text-center">
      <h1 className="display-5 fw-bold mb-3">Campus Event Navigator</h1>
      <p className="lead text-secondary mb-4">
        Discover technology, research, business, and essential skills events across campus.
      </p>
      <Button size="lg" variant="primary" onClick={() => navigate("/events")}>
        Explore Events
      </Button>
    </Container>
  );
}

export default Home;
