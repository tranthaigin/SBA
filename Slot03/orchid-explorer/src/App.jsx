import AppNavbar from './components/AppNavbar.jsx';
import HeroSection from './components/HeroSection.jsx';
import QuickStats from './components/QuickStats.jsx';
import OrchidGallery from './components/OrchidGallery.jsx';
import CareTips from './components/CareTips.jsx';
import LearningAlert from './components/LearningAlert.jsx';
import AppFooter from './components/AppFooter.jsx';

function App() {
  return (
    <div className="app-shell">
      <AppNavbar />
      <main>
        <HeroSection />
        <QuickStats />
        <OrchidGallery />
        <CareTips />
        <LearningAlert />
      </main>
      <AppFooter />
    </div>
  );
}

export default App;
