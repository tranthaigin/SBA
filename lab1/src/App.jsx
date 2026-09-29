import AppFooter from "./components/AppFooter";
import AppNavbar from "./components/AppNavbar";
import HeroSection from "./components/HeroSection";
import OrchidExplorer from "./components/OrchidExplorer";
import UserContext from "./context/UserContext";

const currentUser = {
  name: "SBA301 Student",
  role: "Learner"
};

function App() {
  return (
    <UserContext.Provider value={currentUser}>
      <AppNavbar />
      <main>
        <HeroSection />
        <OrchidExplorer />
      </main>
      <AppFooter />
    </UserContext.Provider>
  );
}

export default App;
