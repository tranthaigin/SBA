import AppNavbar from "./components/AppNavbar";
import HeroSection from "./components/HeroSection";
import EventList from "./components/EventList";
import AppFooter from "./components/AppFooter";

function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <AppNavbar />
      <main className="flex-fill">
        <HeroSection />
        <EventList />
      </main>
      <AppFooter />
    </div>
  );
}

export default App;
