import { Button, Container, Form } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

export default function ContactPage() {
  const navigate = useNavigate();

  function handleSubmit(event) {
    event.preventDefault();
    navigate("/", { replace: true, state: { message: "Contact form submitted successfully!" } });
  }

  return (
    <Container className="py-5" style={{ maxWidth: "600px" }}>
      <h1 className="fw-bold mb-3">Contact Us</h1>
      <p className="text-secondary mb-4">Send us feedback or inquiries about the orchid collection.</p>
      <Form onSubmit={handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label>Your Name</Form.Label>
          <Form.Control type="text" placeholder="Trần Quốc Thái" defaultValue="Trần Quốc Thái" required />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Message</Form.Label>
          <Form.Control as="textarea" rows={3} placeholder="Inquiry details..." defaultValue="Interested in Cattleya propagation." required />
        </Form.Group>
        <Button type="submit" variant="primary">
          Submit Form (Redirects to Home)
        </Button>
      </Form>
    </Container>
  );
}
