import { Container } from 'react-bootstrap';

function AppFooter() {
  return (
    <footer id="about" className="bg-dark text-light py-4 mt-auto">
      <Container className="d-flex flex-column flex-md-row justify-content-between gap-2">
        <span>SBA301 - React Component Architecture</span>
        <span>Slot 03 Practice Project • Orchid Explorer</span>
      </Container>
    </footer>
  );
}

export default AppFooter;
