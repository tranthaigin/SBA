import { useContext } from "react";
import { Container } from "react-bootstrap";
import UserContext from "../context/UserContext";

function AppFooter() {
  const currentUser = useContext(UserContext);

  return (
    <footer className="border-top py-3 mt-4 bg-light">
      <Container className="small text-muted">
        SBA301 Slot 04 practice - current learner: {currentUser?.name ?? "Guest"}
      </Container>
    </footer>
  );
}

export default AppFooter;
