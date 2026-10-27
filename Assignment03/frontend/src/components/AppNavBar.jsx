import { Container, Nav, Navbar, Button, Badge } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function AppNavBar({ onOpenLogin, onOpenRegister }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const isStaff = user?.role === "ROLE_STAFF";
  const isCustomer = user?.role === "ROLE_CUSTOMER";

  const handleLogout = () => {
    logout();
    navigate("/rooms");
  };

  return (
    <Navbar bg="dark" data-bs-theme="dark" expand="lg" className="mb-4 shadow-sm">
      <Container>
        <Navbar.Brand as={Link} to="/rooms" className="fw-bold text-warning">
          🏨 FU Mini Hotel
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="hotel-nav" />
        <Navbar.Collapse id="hotel-nav">
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/rooms">
              Explore Rooms
            </Nav.Link>
            {isCustomer && (
              <Nav.Link as={Link} to="/my-bookings">
                My Bookings
              </Nav.Link>
            )}
            {isStaff && (
              <>
                <Nav.Link as={Link} to="/admin/rooms">
                  Manage Rooms
                </Nav.Link>
                <Nav.Link as={Link} to="/admin/customers">
                  Manage Customers
                </Nav.Link>
                <Nav.Link as={Link} to="/admin/bookings">
                  Manage Reservations
                </Nav.Link>
              </>
            )}
          </Nav>

          <Nav className="align-items-center gap-2">
            {user ? (
              <>
                <span className="text-light small">
                  {user.fullName || user.email} (
                  <Badge bg={isStaff ? "danger" : "info"}>
                    {isStaff ? "Hotel Staff" : "Customer"}
                  </Badge>
                  )
                </span>
                <Button variant="outline-light" size="sm" onClick={handleLogout}>
                  Logout
                </Button>
              </>
            ) : (
              <>
                <Button variant="outline-light" size="sm" onClick={onOpenRegister}>
                  Register
                </Button>
                <Button variant="warning" size="sm" onClick={onOpenLogin}>
                  Login (Staff / Customer)
                </Button>
              </>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
