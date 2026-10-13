import { Navbar, Container, Nav } from "react-bootstrap";
import { Link } from "react-router-dom";

export default function NavBar() {
  return (
    <Navbar bg="dark" data-bs-theme="dark">
      <Container>
        <Navbar.Brand as={Link} to="/">Orchid Management</Navbar.Brand>
        <Nav className="me-auto">
          <Nav.Link as={Link} to="/">List</Nav.Link>
          <Nav.Link as={Link} to="/orchids/new">Create</Nav.Link>
        </Nav>
      </Container>
    </Navbar>
  );
}
