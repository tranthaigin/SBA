import { Container } from "react-bootstrap";

function HeroSection() {
  return (
    <section className="py-4 bg-light border-bottom">
      <Container>
        <h1 className="mb-2">Explore Orchids</h1>
        <p className="mb-0 text-secondary">
          Props configure reusable cards. State makes the UI interactive.
          Context shares selected app-level information.
        </p>
      </Container>
    </section>
  );
}

export default HeroSection;
