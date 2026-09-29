import { Container } from "react-bootstrap";

function AppFooter() {
  return (
    <footer id="about" className="border-top py-4 bg-light mt-auto">
      <Container className="text-center text-muted small">
        <p className="mb-1"><strong>SBA301 EventHub</strong> – Slot 05 Integrated React Lab Project</p>
        <p className="mb-0">Built by Trần Quốc Thái (CE190881) • SE1910 • FPT University</p>
      </Container>
    </footer>
  );
}

export default AppFooter;
