import { useState } from "react";
import { Modal, Button, Form, Alert } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";

export default function LoginModal({ show, onHide }) {
  const { login } = useAuth();
  const [email, setEmail] = useState("staff@funews.com");
  const [password, setPassword] = useState("@1");
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
      setError(err.response?.data?.message || "Login failed. Check email and password.");
    } finally {
      setLoading(false);
    }
  };

  const setCredentials = (role) => {
    if (role === "admin") {
      setEmail("admin@funews.com");
      setPassword("@1");
    } else {
      setEmail("staff@funews.com");
      setPassword("@1");
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered>
      <Modal.Header closeButton>
        <Modal.Title>Login to FUNews</Modal.Title>
      </Modal.Header>
      <Form onSubmit={handleSubmit}>
        <Modal.Body>
          {error && <Alert variant="danger">{error}</Alert>}
          <div className="d-flex gap-2 mb-3">
            <Button variant="outline-info" size="sm" type="button" onClick={() => setCredentials("staff")}>
              Fill Staff Credentials
            </Button>
            <Button variant="outline-danger" size="sm" type="button" onClick={() => setCredentials("admin")}>
              Fill Admin Credentials
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
        <Modal.Footer>
          <Button variant="secondary" onClick={onHide}>
            Cancel
          </Button>
          <Button variant="primary" type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </Button>
        </Modal.Footer>
      </Form>
    </Modal>
  );
}
