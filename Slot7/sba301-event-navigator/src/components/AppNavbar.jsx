import { Container, Nav, Navbar } from "react-bootstrap";
import { Link } from "react-router-dom";

function AppNavbar() {
  return (
    <Navbar bg="dark" data-bs-theme="dark" expand="lg" sticky="top">
      <Container>
        <Navbar.Brand as={Link} to="/">Campus Event Navigator</Navbar.Brand>
        <Navbar.Toggle aria-controls="main-nav" />
        <Navbar.Collapse id="main-nav">
          <Nav className="ms-auto">
            <Nav.Link as={Link} to="/">Home</Nav.Link>
            <Nav.Link as={Link} to="/events">Events</Nav.Link>
            <Nav.Link as={Link} to="/about">About</Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default AppNavbar;
