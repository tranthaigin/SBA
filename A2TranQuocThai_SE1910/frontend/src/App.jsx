import { useState } from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import AppNavBar from "./components/AppNavBar";
import LoginModal from "./components/LoginModal";
import NewsList from "./pages/NewsList";
import CategoryList from "./pages/CategoryList";
import Profile from "./pages/Profile";
import AccountList from "./pages/AccountList";
import MyHistory from "./pages/MyHistory";
import { AuthProvider } from "./context/AuthContext";

export default function App() {
  const [showLogin, setShowLogin] = useState(false);

  return (
    <AuthProvider>
      <AppNavBar onOpenLogin={() => setShowLogin(true)} />
      <Routes>
        <Route path="/" element={<Navigate to="/news" replace />} />
        <Route path="/news" element={<NewsList />} />
        <Route path="/categories" element={<CategoryList />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/accounts" element={<AccountList />} />
        <Route path="/my-history" element={<MyHistory />} />
        <Route path="*" element={<Navigate to="/news" replace />} />
      </Routes>
      <LoginModal show={showLogin} onHide={() => setShowLogin(false)} />
    </AuthProvider>
  );
}
