import { Container } from "react-bootstrap";

function About() {
  return (
    <Container className="py-5">
      <h2>About Campus Event Navigator</h2>
      <p className="lead">
        Campus Event Navigator is an SBA301 routing practice application demonstrating client-side routing with React Router DOM.
      </p>
      <p className="text-muted">
        Current event data is driven by a local JavaScript data module (no external backend request). In subsequent slots, this will be connected with RESTful APIs powered by Spring Boot.
      </p>
    </Container>
  );
}

export default About;
