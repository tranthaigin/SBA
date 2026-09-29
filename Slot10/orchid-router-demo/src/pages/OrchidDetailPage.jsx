import { Button, Container } from "react-bootstrap";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import { OrchidsData } from "../data/orchids";

export default function OrchidDetailPage() {
  const { id } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const orchid = OrchidsData.find((item) => item.id === id);

  if (!orchid) {
    return (
      <Container className="py-5">
        <h1 className="text-danger">Orchid not found</h1>
        <p className="lead">
          The route exists, but resource ID <code>{id}</code> does not match any entry in our catalog.
        </p>
        <Button as={Link} to="/orchids" variant="primary">
          Back to Orchids
        </Button>
      </Container>
    );
  }

  return (
    <Container className="py-4">
      <Button
        variant="outline-secondary"
        onClick={() => navigate(-1)}
        className="mb-3"
      >
        ← Browser Back
      </Button>
      <h1 className="mt-2 fw-bold">{orchid.orchidName}</h1>
      <img
        src={orchid.image}
        alt={orchid.orchidName}
        className="detail-image mb-4 d-block shadow-sm"
      />
      <div className="card p-3 mb-3 bg-light border-0">
        <p className="mb-2"><strong>Category:</strong> {orchid.category}</p>
        <p className="mb-2"><strong>Special Selection:</strong> {orchid.isSpecial ? "Yes (Featured)" : "No"}</p>
        <p className="mb-0"><strong>Description:</strong> {orchid.description}</p>
      </div>
      <div>
        <small className="text-muted">
          Navigation source: <strong>{location.state?.from ?? "direct URL / refresh"}</strong>
        </small>
      </div>
    </Container>
  );
}
