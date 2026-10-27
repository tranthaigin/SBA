import { Navigate, Route, Routes } from "react-router-dom";
import AppNavBar from "./components/AppNavBar";
import CategoryList from "./pages/CategoryList";
import EditCategory from "./pages/EditCategory";

export default function App() {
  return (
    <>
      <AppNavBar />
      <Routes>
        <Route path="/" element={<Navigate to="/categories" replace />} />
        <Route path="/categories" element={<CategoryList />} />
        <Route path="/categories/:id/edit" element={<EditCategory />} />
        <Route path="*" element={<Navigate to="/categories" replace />} />
      </Routes>
    </>
  );
}
