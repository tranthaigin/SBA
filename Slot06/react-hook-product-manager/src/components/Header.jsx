import Container from "react-bootstrap/Container";
import Navbar from "react-bootstrap/Navbar";
import ThemeToggle from "./ThemeToggle";

export default function Header() {
  return (
    <Navbar bg="primary" data-bs-theme="dark" className="shadow-sm">
      <Container>
        <Navbar.Brand>React Hook Product Manager</Navbar.Brand>
        <ThemeToggle />
      </Container>
    </Navbar>
  );
}
