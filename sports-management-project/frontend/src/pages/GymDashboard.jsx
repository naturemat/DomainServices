import { useState, useEffect } from 'react';
import { api } from '../services/api';
import './Dashboard.css';

const GymDashboard = () => {
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
        sportType: athlete.sportType,
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
      <h1>Gym Dashboard</h1>
      <div className="dashboard-welcome">Welcome, {athleteName}</div>

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <h2>Athlete Profile</h2>
          <div className="profile-info">
            <p><strong>Sport:</strong> {athlete?.sportType === 'football' ? 'Football' : 'Gym'}</p>
            <p><strong>Status:</strong> Active</p>
          </div>
        </div>

        <div className="dashboard-card">
          <h2>Fatigue Level</h2>
          {session?.fatigueLevel ? (
            <div className="fatigue-display">
              <span className={`fatigue-badge fatigue-${session.fatigueLevel.toLowerCase()}`}>
                {session.fatigueLevel}
              </span>
            </div>
          ) : (
            <p className="empty-state">Register a session to see fatigue</p>
          )}
        </div>

        <div className="dashboard-card">
          <h2>Recovery Suggestion</h2>
          {session?.recoverySuggestion ? (
            <div className="recovery-suggestion">
              <p>{session.recoverySuggestion.replace(/_/g, ' ')}</p>
            </div>
          ) : (
            <p className="empty-state">No recovery data yet</p>
          )}
        </div>

        <div className="dashboard-card">
          <h2>Current Routine</h2>
          {sessionData?.routine ? (
            <div className="routine-info">
              <p><strong>{sessionData.routine.name}</strong></p>
              <p>{sessionData.routine.description}</p>
              <p><strong>Duration:</strong> {sessionData.routine.recommendedDurationMinutes} min</p>
              <p><strong>Intensity:</strong> {sessionData.routine.recommendedIntensity}</p>
            </div>
          ) : (
            <p className="empty-state">No routine assigned</p>
          )}
        </div>

        <div className="dashboard-card full-width">
          <h2>Register New Session</h2>
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

export default GymDashboard;