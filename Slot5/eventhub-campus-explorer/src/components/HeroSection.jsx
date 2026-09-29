import { Container } from "react-bootstrap";

function HeroSection() {
  return (
    <section id="top" className="py-5 bg-light border-bottom">
      <Container>
        <h1 className="display-6 fw-bold">Discover Campus Events</h1>
        <p className="lead mb-0 text-secondary">
          Explore technology, career, community, sports and cultural activities at FPT University.
        </p>
      </Container>
    </section>
  );
}

export default HeroSection;
