import { NavLink, Outlet } from "react-router-dom";

export default function DashboardLayout() {
  const cls = ({ isActive }) => (isActive ? "active" : "");

  return (
    <section>
      <h1>User Dashboard</h1>
      <div className="subnav">
        <NavLink to="." end className={cls}>
          Overview
        </NavLink>
        <NavLink to="profile" className={cls}>
          Profile
        </NavLink>
        <NavLink to="orders" className={cls}>
          Orders
        </NavLink>
      </div>
      <Outlet />
    </section>
  );
}

export const DashboardHome = () => (
  <div>
    <h3>Dashboard Overview</h3>
    <p>Welcome to your personal account overview. Use the tabs above to manage your profile and view your past orders.</p>
  </div>
);

export const ProfilePage = () => (
  <div>
    <h3>User Profile</h3>
    <p><strong>Name:</strong> Trần Quốc Thái</p>
    <p><strong>Student ID:</strong> CE190881</p>
    <p><strong>Class:</strong> SE1910</p>
  </div>
);

export const OrdersPage = () => (
  <div>
    <h3>Order History</h3>
    <ul>
      <li>Order #1001 - MacBook Air M3 (Delivered)</li>
      <li>Order #1002 - Wireless Mouse (Processing)</li>
    </ul>
  </div>
);
