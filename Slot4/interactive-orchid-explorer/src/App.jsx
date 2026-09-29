import AppFooter from "./components/AppFooter";
import AppNavbar from "./components/AppNavbar";
import HeroSection from "./components/HeroSection";
import OrchidExplorer from "./components/OrchidExplorer";
import UserContext from "./context/UserContext";

const currentUser = {
  name: "Trần Quốc Thái (CE190881)",
  role: "Learner / Developer"
};

function App() {
  return (
    <UserContext.Provider value={currentUser}>
      <div className="d-flex flex-column min-vh-100">
        <AppNavbar />
        <main className="flex-fill">
          <HeroSection />
          <OrchidExplorer />
        </main>
        <AppFooter />
      </div>
    </UserContext.Provider>
  );
}

export default App;
