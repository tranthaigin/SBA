import { Container, Nav, Navbar, Button, Badge } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function AppNavBar({ onOpenLogin }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/news");
  };

  return (
    <Navbar bg="dark" data-bs-theme="dark" expand="lg" className="mb-4 shadow-sm">
      <Container>
        <Navbar.Brand as={Link} to="/news" className="fw-bold text-primary">
          FUNews Management System
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="funews-nav" />
        <Navbar.Collapse id="funews-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/news">
              News Articles
            </Nav.Link>
            {user?.accountRole === 2 && (
              <>
                <Nav.Link as={Link} to="/categories">
                  Categories
                </Nav.Link>
                <Nav.Link as={Link} to="/profile">My Profile</Nav.Link>
                <Nav.Link as={Link} to="/my-history">
                  My News History
                </Nav.Link>
              </>
            )}
            {user?.accountRole === 1 && (
              <Nav.Link as={Link} to="/accounts">
                Accounts Management
              </Nav.Link>
            )}
          </Nav>

          <Nav className="align-items-center gap-2">
            {user ? (
              <>
                <span className="text-light small">
                  {user.accountName} (
                  <Badge bg={user.accountRole === 1 ? "danger" : "info"}>
                    {user.accountRole === 1 ? "Admin" : "Staff"}
                  </Badge>
                  )
                </span>
                <Button variant="outline-light" size="sm" onClick={handleLogout}>
                  Logout
                </Button>
              </>
            ) : (
              <Button variant="primary" size="sm" onClick={onOpenLogin}>
                Login (Staff / Admin)
              </Button>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
