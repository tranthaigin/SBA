import { Button, Container } from 'react-bootstrap';

function HeroSection() {
  return (
    <section id="top" className="hero-section py-5">
      <Container>
        <p className="text-uppercase fw-semibold mb-2">SBA301 - Slot 03 Practice</p>
        <h1 className="display-5 fw-bold">Explore orchids through component-based UI</h1>
        <p className="lead hero-copy">
          A medium-size React interface built with functional components, composition and React-Bootstrap.
        </p>
        <Button href="#gallery" variant="light" className="me-2">
          View Gallery
        </Button>
        <Button href="#care" variant="outline-light">
          Care Tips
        </Button>
      </Container>
    </section>
  );
}

export default HeroSection;
