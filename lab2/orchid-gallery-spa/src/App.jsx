// src/App.jsx
import NavBar from './components/NavBar';
import Orchids from './components/Orchids';

export default function App() {
  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      <NavBar />
      <main className="flex-grow-1">
        <Orchids />
      </main>
      <footer className="text-center py-3 bg-white border-top text-muted small mt-auto">
        SBA301 - Lab 02: Orchid Gallery SPA &copy; 2026
      </footer>
    </div>
  );
}
