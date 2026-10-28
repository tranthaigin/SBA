import { useState } from "react";
import { Modal, Button, Form, Alert } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";

export default function LoginModal({ show, onHide, onSwitchToRegister }) {
  const { login } = useAuth();
  const [email, setEmail] = useState("staff@hotel.com");
  const [password, setPassword] = useState("staff123");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(email, password);
      onHide();
    } catch (err) {
      setError(err.response?.data?.message || "Invalid credentials. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const setCredentials = (role) => {
    if (role === "staff") {
      setEmail("staff@hotel.com");
      setPassword("staff123");
    } else {
      setEmail("customer@hotel.com");
      setPassword("123456");
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Login to FU Mini Hotel</Modal.Title>
      </Modal.Header>
      <Form onSubmit={handleSubmit}>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}
          <div className="d-flex gap-2 mb-3">
            <Button variant="outline-danger" size="sm" type="button" onClick={() => setCredentials("staff")}>
              Fill Staff Credentials
            </Button>
            <Button variant="outline-primary" size="sm" type="button" onClick={() => setCredentials("customer")}>
              Fill Customer Credentials
            </Button>
          </div>
          <Form.Group className="mb-3">
            <Form.Label>Email Address</Form.Label>
            <Form.Control
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Password</Form.Label>
            <Form.Control
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </Form.Group>
        </Modal.Body>
        <Modal.Footer className="d-flex justify-content-between">
          <Button variant="link" size="sm" type="button" onClick={onSwitchToRegister}>
            Don't have an account? Register here
          </Button>
          <div className="d-flex gap-2">
            <Button variant="secondary" onClick={onHide}>
              Cancel
            </Button>
            <Button variant="warning" type="submit" disabled={loading}>
              {loading ? "Signing in..." : "Login"}
            </Button>
          </div>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
