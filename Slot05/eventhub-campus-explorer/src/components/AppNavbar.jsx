import { Container, Nav, Navbar } from "react-bootstrap";

function AppNavbar() {
  return (
    <Navbar bg="dark" data-bs-theme="dark" expand="lg" sticky="top">
      <Container>
        <Navbar.Brand href="#top">EventHub</Navbar.Brand>
        <Navbar.Toggle aria-controls="eventhub-nav" />
        <Navbar.Collapse id="eventhub-nav">
          <Nav className="ms-auto">
            <Nav.Link href="#top">Home</Nav.Link>
            <Nav.Link href="#events">Events</Nav.Link>
            <Nav.Link href="#about">About</Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default AppNavbar;
