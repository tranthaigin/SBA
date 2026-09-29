import { Container, Nav, Navbar } from "react-bootstrap";
import { NavLink } from "react-router-dom";

const navClass = ({ isActive }) =>
  isActive ? "nav-link active fw-semibold" : "nav-link";

export default function AppNavbar() {
  return (
    <Navbar bg="primary" data-bs-theme="dark" expand="lg" sticky="top">
      <Container>
        <Navbar.Brand as={NavLink} to="/">Orchid Gallery</Navbar.Brand>
        <Navbar.Toggle aria-controls="main-nav" />
        <Navbar.Collapse id="main-nav">
          <Nav className="me-auto">
            <Nav.Link as={NavLink} to="/" end className={navClass}>Home</Nav.Link>
            <Nav.Link as={NavLink} to="/orchids" className={navClass}>Orchids</Nav.Link>
            <Nav.Link as={NavLink} to="/dashboard" className={navClass}>Dashboard</Nav.Link>
            <Nav.Link as={NavLink} to="/about" className={navClass}>About</Nav.Link>
            <Nav.Link as={NavLink} to="/contact" className={navClass}>Contact</Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
