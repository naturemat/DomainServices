import { useState, useEffect } from 'react';
import { api } from '../services/api';
import './Dashboard.css';

const FootballDashboard = () => {
  const [sessionData, setSessionData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newSession, setNewSession] = useState({ duration: 60, intensity: 'MEDIUM', date: new Date().toISOString().split('T')[0] });
  const [submitting, setSubmitting] = useState(false);

  const athlete = api.getStoredAthlete();

  useEffect(() => {
    const loadRoutine = async () => {
      if (!athlete?.athleteId) {
        setLoading(false);
        return;
      }
      try {
        const routine = await api.getRoutine(athlete.athleteId);
        setSessionData({ routine });
      } catch (err) {
        console.log('No routine data yet');
      } finally {
        setLoading(false);
      }
    };
    loadRoutine();
  }, [athlete?.athleteId]);

  const handleNewSession = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      const result = await api.createSession({
        name: athlete.name,
        sportType: 'football',
        date: newSession.date,
        duration: newSession.duration,
        intensity: newSession.intensity
      });
      setSessionData({
        ...sessionData,
        session: result.session
      });
    } catch (err) {
      setError(err.message || 'Failed to register session');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="dashboard-loading">Loading...</div>;

  const session = sessionData?.session;
  const athleteName = athlete?.name || 'Athlete';

  return (
    <div className="dashboard">
      <h1>Football Dashboard</h1>
      <div className="dashboard-welcome">Welcome, {athleteName}</div>

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <h2>Team Statistics</h2>
          <div className="stats-grid">
            <div className="stat-item">
              <span className="stat-value">{session ? '1' : '0'}</span>
              <span className="stat-label">Sessions</span>
            </div>
            <div className="stat-item">
              <span className="stat-value">{session?.durationMinutes || '-'}</span>
              <span className="stat-label">Last Duration</span>
            </div>
            <div className="stat-item">
              <span className="stat-value">{session?.intensity || '-'}</span>
              <span className="stat-label">Intensity</span>
            </div>
          </div>
        </div>

        <div className="dashboard-card">
          <h2>Fatigue Metrics</h2>
          {session?.fatigueLevel ? (
            <div className="fatigue-display">
              <span className={`fatigue-badge fatigue-${session.fatigueLevel.toLowerCase()}`}>
                {session.fatigueLevel}
              </span>
            </div>
          ) : (
            <p className="empty-state">Register a session to see metrics</p>
          )}
        </div>

        <div className="dashboard-card full-width">
          <h2>Recommended Routine</h2>
          {sessionData?.routine ? (
            <div className="routine-info">
              <p><strong>{sessionData.routine.name}</strong></p>
              <p>{sessionData.routine.description}</p>
              <p><strong>Duration:</strong> {sessionData.routine.recommendedDurationMinutes} min</p>
              <p><strong>Intensity:</strong> {sessionData.routine.recommendedIntensity}</p>
            </div>
          ) : (
            <p className="empty-state">No routine assigned - register a session to get recommendations</p>
          )}
        </div>

        <div className="dashboard-card full-width">
          <h2>Register Training Session</h2>
          {error && <div className="form-error">{error}</div>}
          <form onSubmit={handleNewSession} className="session-form-inline">
            <div className="form-group">
              <label>Duration (min)</label>
              <input
                type="number"
                value={newSession.duration}
                onChange={(e) => setNewSession({ ...newSession, duration: e.target.value })}
                min="1"
              />
            </div>
            <div className="form-group">
              <label>Intensity</label>
              <select
                value={newSession.intensity}
                onChange={(e) => setNewSession({ ...newSession, intensity: e.target.value })}
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </div>
            <div className="form-group">
              <label>Date</label>
              <input
                type="date"
                value={newSession.date}
                onChange={(e) => setNewSession({ ...newSession, date: e.target.value })}
              />
            </div>
            <button type="submit" disabled={submitting}>
              {submitting ? 'Registering...' : 'Register'}
            </button>
          </form>
        </div>

        {session && (
          <div className="dashboard-card full-width">
            <h2>Latest Session Result</h2>
            <div className="session-result">
              <p><strong>Fatigue Level:</strong> {session.fatigueLevel}</p>
              <p><strong>Recovery:</strong> {session.recoverySuggestion}</p>
              {session.recommendedRoutine && (
                <div className="recommended-routine">
                  <p><strong>Recommended Routine:</strong></p>
                  <p>{session.recommendedRoutine.name}</p>
                  <p>{session.recommendedRoutine.description}</p>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default FootballDashboard;