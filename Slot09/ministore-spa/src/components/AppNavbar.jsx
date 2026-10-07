import { NavLink } from "react-router-dom";

export default function AppNavbar() {
  const cls = ({ isActive }) => (isActive ? "nav-item active" : "nav-item");

  return (
    <nav className="navbar">
      <strong>MiniStore</strong>
      <NavLink to="/" end className={cls}>
        Home
      </NavLink>
      <NavLink to="/products" className={cls}>
        Products
      </NavLink>
      <NavLink to="/about" className={cls}>
        About
      </NavLink>
      <NavLink to="/dashboard" className={cls}>
        Dashboard
      </NavLink>
    </nav>
  );
}
