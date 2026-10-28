import { useState, useEffect } from "react";
import { Container, Form, Button, Alert } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
export default function Profile() {
  const { user, logout } = useAuth();
  const [form, setForm] = useState({}),
    [message, setMessage] = useState("");
  const allowed = user?.accountRole === 2;
  useEffect(() => {
    if (allowed)
      axios
        .get("/api/auth/profile")
        .then((r) => setForm(r.data))
        .catch(() => setMessage("Cannot load profile"));
  }, [allowed]);
  if (!allowed)
    return (
      <Container>
        <Alert variant="danger">Please sign in with the required role.</Alert>
      </Container>
    );
  async function save(e) {
    e.preventDefault();
    try {
      await axios.put("/api/auth/profile", form);
      setMessage("Profile saved. Please sign in again.");
      logout();
    } catch (e) {
      setMessage(e.response?.data?.message || "Cannot save profile");
    }
  }
  return (
    <Container>
      <h2>My Profile</h2>
      {message && <Alert>{message}</Alert>}
      <Form onSubmit={save}>
        <Form.Group className="mb-3">
          <Form.Label>Name</Form.Label>
          <Form.Control
            aria-label="Name"
            type="text"
            required
            maxLength={100}
            value={form.accountName || ""}
            onChange={(e) => setForm({ ...form, accountName: e.target.value })}
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Email</Form.Label>
          <Form.Control
            aria-label="Email"
            type="email"
            required
            maxLength={70}
            value={form.accountEmail || ""}
            onChange={(e) => setForm({ ...form, accountEmail: e.target.value })}
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Password</Form.Label>
          <Form.Control
            aria-label="Password"
            type="password"
            maxLength={70}
            value={form.accountPassword || ""}
            onChange={(e) =>
              setForm({ ...form, accountPassword: e.target.value })
            }
          />
        </Form.Group>
        <Button type="submit">Save Profile</Button>
      </Form>
    </Container>
  );
}
