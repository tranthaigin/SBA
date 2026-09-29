import Button from "react-bootstrap/Button";
import { useTheme } from "../context/ThemeContext";

export default function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();

  return (
    <Button variant="outline-light" size="sm" onClick={toggleTheme}>
      {theme === "light" ? "🌙 Dark mode" : "☀️ Light mode"}
    </Button>
  );
}
