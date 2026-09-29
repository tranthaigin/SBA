import { useContext } from "react";
import { Container } from "react-bootstrap";
import UserContext from "../context/UserContext";

function AppFooter() {
  const currentUser = useContext(UserContext);

  return (
    <footer className="border-top py-3 mt-4 bg-light">
      <Container className="small text-muted text-center">
        SBA301 Slot 04 Practice • Current Learner: <strong>{currentUser?.name ?? "Guest"}</strong> ({currentUser?.role ?? "Student"})
      </Container>
    </footer>
  );
}

export default AppFooter;
