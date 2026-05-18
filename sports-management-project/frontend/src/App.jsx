import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProfileCreate from './pages/ProfileCreate';
import GymDashboard from './pages/GymDashboard';
import FootballDashboard from './pages/FootballDashboard';
import SessionCreate from './pages/SessionCreate';
import { api } from './services/api';
import './index.css';

const Layout = ({ children }) => {
  return (
    <div className="app-layout">
      <header className="app-header">
        <div className="header-brand">Sports Management</div>
        <nav className="header-nav">
          <a href="/dashboard">Dashboard</a>
        </nav>
        <div className="header-user">
          <button 
            onClick={() => { 
              api.logout(); 
              window.location.href = '/'; 
            }} 
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

function DashboardRouter() {
  const athlete = api.getStoredAthlete();
  
  if (!athlete || !athlete.athleteId) {
    return <Navigate to="/" replace />;
  }

  const defaultRoute = athlete.sportType === 'GYM' ? '/gym' : '/football';
  
  return (
    <Routes>
      <Route path="/" element={<Navigate to={defaultRoute} replace />} />
      <Route path="/dashboard" element={<Navigate to={defaultRoute} replace />} />
      <Route path="/gym" element={<GymDashboard />} />
      <Route path="/football" element={<FootballDashboard />} />
      <Route path="/session/new" element={<SessionCreate />} />
      <Route path="*" element={<Navigate to={defaultRoute} replace />} />
    </Routes>
  );
}

function App() {
  const [hasProfile, setHasProfile] = useState(null);

  useEffect(() => {
    checkProfile();
  }, []);

  const checkProfile = () => {
    const athlete = api.getStoredAthlete();
    setHasProfile(!!(athlete && athlete.athleteId));
  };

  if (hasProfile === null) {
    return (
      <div className="loading-screen">
        <div className="loading-spinner"></div>
        <div className="loading-text">Loading...</div>
      </div>
    );
  }

  return (
    <BrowserRouter>
      {hasProfile ? (
        <Layout>
          <DashboardRouter />
        </Layout>
      ) : (
        <Routes>
          <Route path="*" element={<ProfileCreate />} />
        </Routes>
      )}
    </BrowserRouter>
  );
}

export default App;