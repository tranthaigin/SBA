import { Container } from "react-bootstrap";

export default function AboutPage() {
  return (
    <Container className="py-5">
      <h1 className="fw-bold mb-3">About Orchid Gallery SPA</h1>
      <p className="lead text-secondary">
        Slot 10 comprehensive demonstration for React Router and SPA Architecture.
      </p>
      <p>
        This application highlights nested layouts, URL query parameters, dynamic route segments with <code>useParams</code>, and history stack navigation with <code>useNavigate</code>.
      </p>
    </Container>
  );
}
