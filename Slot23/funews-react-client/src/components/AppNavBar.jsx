import { Container, Nav, Navbar } from "react-bootstrap";
import { Link } from "react-router-dom";

export default function AppNavBar() {
  return (
    <Navbar bg="dark" data-bs-theme="dark" className="mb-4">
      <Container>
        <Navbar.Brand as={Link} to="/categories">
          FUNews Management
        </Navbar.Brand>
        <Nav>
          <Nav.Link as={Link} to="/categories">
            Categories
          </Nav.Link>
        </Nav>
      </Container>
    </Navbar>
  );
}
