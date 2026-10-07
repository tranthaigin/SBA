import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <section style={{ textAlign: "center", padding: "40px 0" }}>
      <h1 style={{ fontSize: "3rem", color: "#dc2626" }}>404</h1>
      <h2>Page Not Found</h2>
      <p>The URL you requested does not match any client route in MiniStore SPA.</p>
      <Link to="/">
        <button>Go to Homepage</button>
      </Link>
    </section>
  );
}
