import { Link } from 'react-router-dom';
import { api } from '../services/api';
import './DashboardSelector.css';

const DashboardSelector = () => {
  const athlete = api.getStoredAthlete();
  const handleLogout = () => {
    api.logout();
    window.location.href = '/';
  };

  return (
    <div className="dashboard-selector">
      <div className="selector-header">
        <h1>Select Dashboard</h1>
        <p>Welcome, {athlete?.name || 'Athlete'}</p>
        <button className="logout-btn" onClick={handleLogout}>
          Start Over
        </button>
      </div>

      <div className="dashboard-options">
        <Link to="/dashboard/gym" className="dashboard-option gym">
          <div className="option-icon">🏋️</div>
          <h2>Gym Dashboard</h2>
          <p>Track your fitness training, fatigue levels, and recovery</p>
        </Link>

        <Link to="/dashboard/football" className="dashboard-option football">
          <div className="option-icon">⚽</div>
          <h2>Football Dashboard</h2>
          <p>View team statistics and football-specific metrics</p>
        </Link>
      </div>
    </div>
  );
};

export default DashboardSelector;