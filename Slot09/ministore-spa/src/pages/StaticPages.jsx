import { Link } from "react-router-dom";

export function HomePage() {
  return (
    <section>
      <h1>Welcome to MiniStore SPA</h1>
      <p className="lead">React Router demo illustrating URL state, dynamic routes, and nested layouts for SBA301.</p>
      <Link to="/products">
        <button>Explore Products</button>
      </Link>
    </section>
  );
}

export function AboutPage() {
  return (
    <section>
      <h1>About MiniStore</h1>
      <p>This single page application is built to demonstrate modern declarative routing in React using React Router v6.</p>
      <p>Features include query parameters for filters, path parameters for product details, and nested routes for user dashboard.</p>
    </section>
  );
}
