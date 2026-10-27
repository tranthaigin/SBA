import { useState } from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import AppNavBar from "./components/AppNavBar";
import LoginModal from "./components/LoginModal";
import RegisterModal from "./components/RegisterModal";
import RoomCatalog from "./pages/RoomCatalog";
import CustomerBookings from "./pages/CustomerBookings";
import AdminRooms from "./pages/AdminRooms";
import AdminCustomers from "./pages/AdminCustomers";
import AdminBookings from "./pages/AdminBookings";
import { AuthProvider } from "./context/AuthContext";

export default function App() {
  const [showLogin, setShowLogin] = useState(false);
  const [showRegister, setShowRegister] = useState(false);

  const switchToRegister = () => {
    setShowLogin(false);
    setShowRegister(true);
  };

  const switchToLogin = () => {
    setShowRegister(false);
    setShowLogin(true);
  };

  return (
    <AuthProvider>
      <AppNavBar
        onOpenLogin={() => setShowLogin(true)}
        onOpenRegister={() => setShowRegister(true)}
      />
      <Routes>
        <Route path="/" element={<Navigate to="/rooms" replace />} />
        <Route path="/rooms" element={<RoomCatalog onRequireLogin={() => setShowLogin(true)} />} />
        <Route path="/my-bookings" element={<CustomerBookings />} />
        <Route path="/admin/rooms" element={<AdminRooms />} />
        <Route path="/admin/customers" element={<AdminCustomers />} />
        <Route path="/admin/bookings" element={<AdminBookings />} />
        <Route path="*" element={<Navigate to="/rooms" replace />} />
      </Routes>
      <LoginModal
        show={showLogin}
        onHide={() => setShowLogin(false)}
        onSwitchToRegister={switchToRegister}
      />
      <RegisterModal
        show={showRegister}
        onHide={() => setShowRegister(false)}
        onSwitchToLogin={switchToLogin}
      />
    </AuthProvider>
  );
}
