import { useState, useEffect } from "react";
import { Container, Form, Button, Alert } from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
export default function Profile() {
  const { user, logout } = useAuth();
  const [form, setForm] = useState({}),
    [message, setMessage] = useState("");
  const allowed = user?.role === "ROLE_CUSTOMER";
  useEffect(() => {
    if (allowed)
      axios
        .get("/api/customer/profile")
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
      await axios.put("/api/customer/profile", form);
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
            maxLength={50}
            value={form.customerFullName || ""}
            onChange={(e) =>
              setForm({ ...form, customerFullName: e.target.value })
            }
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Email</Form.Label>
          <Form.Control
            aria-label="Email"
            type="email"
            required
            maxLength={50}
            value={form.emailAddress || ""}
            onChange={(e) => setForm({ ...form, emailAddress: e.target.value })}
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Telephone</Form.Label>
          <Form.Control
            aria-label="Telephone"
            type="tel"
            maxLength={12}
            value={form.telephone || ""}
            onChange={(e) => setForm({ ...form, telephone: e.target.value })}
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Birthday</Form.Label>
          <Form.Control
            aria-label="Birthday"
            type="date"
            value={form.customerBirthday || ""}
            onChange={(e) =>
              setForm({ ...form, customerBirthday: e.target.value })
            }
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>New password</Form.Label>
          <Form.Control
            aria-label="New password"
            type="password"
            maxLength={70}
            value={form.password || ""}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
        </Form.Group>
        <Button type="submit">Save Profile</Button>
      </Form>
    </Container>
  );
}
