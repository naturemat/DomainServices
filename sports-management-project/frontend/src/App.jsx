import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import ProfileCreate from './pages/ProfileCreate';
import DashboardSelector from './components/DashboardSelector';
import GymDashboard from './pages/GymDashboard';
import FootballDashboard from './pages/FootballDashboard';
import { api } from './services/api';
import './index.css';

const Layout = ({ children }) => {
  return (
    <div className="app-layout">
      <header className="app-header">
        <div className="header-brand">Sports Management</div>
        <nav className="header-nav">
          <a href="/dashboard">Dashboards</a>
        </nav>
        <div className="header-user">
          <button 
            onClick={() => { api.logout(); window.location.href = '/'; }} 
            className="logout-btn"
          >
            Start Over
          </button>
        </div>
      </header>
      <main className="app-main">{children}</main>
    </div>
  );
};

const DashboardRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<DashboardSelector />} />
      <Route path="/gym" element={<GymDashboard />} />
      <Route path="/football" element={<FootballDashboard />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
};

const App = () => {
  const [hasProfile, setHasProfile] = useState(null);

  useEffect(() => {
    const athlete = api.getStoredAthlete();
    setHasProfile(!!athlete);
  }, []);

  if (hasProfile === null) {
    return <div>Loading...</div>;
  }

  return (
    <BrowserRouter>
      <Routes>
        {!hasProfile ? (
          <Route path="*" element={<ProfileCreate />} />
        ) : (
          <Route
            path="/dashboard/*"
            element={
              <Layout>
                <DashboardRouter />
              </Layout>
            }
          />
        )}
      </Routes>
    </BrowserRouter>
  );
};

export default App;