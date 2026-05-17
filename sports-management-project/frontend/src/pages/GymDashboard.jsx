import { useState, useEffect } from 'react';
import { api } from '../services/api';
import { DomainServiceFlow, SimpleFatigueExplanation, RoutineExplanationCard } from '../components/DomainLogicExplainer';
import './Dashboard.css';

const GymDashboard = () => {
  const [sessionData, setSessionData] = useState(null);
  const [sessionHistory, setSessionHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newSession, setNewSession] = useState({ duration: 60, intensity: 'MODERATE', date: new Date().toISOString().split('T')[0] });
  const [submitting, setSubmitting] = useState(false);
  const [showExplanation, setShowExplanation] = useState(false);
  const [activeTab, setActiveTab] = useState('register');

  const athlete = api.getStoredAthlete();

  useEffect(() => {
    const loadData = async () => {
      if (!athlete?.athleteId) {
        setLoading(false);
        return;
      }
      try {
        const [routine, sessions] = await Promise.all([
          api.getRoutine(athlete.athleteId),
          api.getSessionsByAthlete(athlete.athleteId)
        ]);
        setSessionData({ routine });
        setSessionHistory(sessions || []);
      } catch (err) {
        console.log('No data yet');
      } finally {
        setLoading(false);
      }
    };
    loadData();
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
        session: result.session,
        routine: result.session.recommendedRoutine
      });
      const sessions = await api.getSessionsByAthlete(athlete.athleteId);
      setSessionHistory(sessions || []);
      setShowExplanation(true);
      setActiveTab('result');
    } catch (err) {
      setError(err.message || 'Failed to register session');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="dashboard-loading">Loading...</div>;

  const session = sessionData?.session;
  const athleteName = athlete?.name || 'Athlete';
  const sportType = athlete?.sportType || 'GYM';

  return (
    <div className="dashboard">
      <h1>🏋️ Gym Dashboard</h1>
      <div className="dashboard-welcome">Welcome back, {athleteName}</div>

      <div className="dashboard-tabs">
        <button 
          className={`tab-btn ${activeTab === 'register' ? 'active' : ''}`}
          onClick={() => setActiveTab('register')}
        >
          📝 Register Session
        </button>
        <button 
          className={`tab-btn ${activeTab === 'result' ? 'active' : ''}`}
          onClick={() => setActiveTab('result')}
          disabled={!session}
        >
          🧠 How It Works
        </button>
        <button 
          className={`tab-btn ${activeTab === 'history' ? 'active' : ''}`}
          onClick={() => setActiveTab('history')}
          disabled={sessionHistory.length === 0}
        >
          📊 History ({sessionHistory.length})
        </button>
      </div>

      {activeTab === 'register' && (
        <div className="dashboard-grid">
          <div className="dashboard-card">
            <h2>Athlete Profile</h2>
            <div className="profile-info">
              <p><strong>Name:</strong> {athleteName}</p>
              <p><strong>Sport:</strong> 🏋️ Gym</p>
              <p><strong>Status:</strong> <span className="status-active">Active</span></p>
              <p><strong>Total Sessions:</strong> {sessionHistory.length}</p>
            </div>
          </div>

          <div className="dashboard-card">
            <h2>Current Fatigue Level</h2>
            {session?.fatigueLevel ? (
              <div className="fatigue-display">
                <span className={`fatigue-badge fatigue-${session.fatigueLevel.toLowerCase()}`}>
                  {session.fatigueLevel}
                </span>
                <p className="fatigue-hint">Click "How It Works" to see calculation</p>
              </div>
            ) : (
              <p className="empty-state">Register a session to see fatigue</p>
            )}
          </div>

          <div className="dashboard-card">
            <h2>Recommended Routine</h2>
            {session?.recommendedRoutine ? (
              <div className="routine-info">
                <p><strong>{session.recommendedRoutine.name}</strong></p>
                <p>{session.recommendedRoutine.description}</p>
                <p><strong>Duration:</strong> {session.recommendedRoutine.recommendedDurationMinutes} min</p>
                <p><strong>Intensity:</strong> {session.recommendedRoutine.recommendedIntensity}</p>
              </div>
            ) : (
              <p className="empty-state">No routine yet</p>
            )}
          </div>

          <div className="dashboard-card">
            <h2>Recovery Suggestion</h2>
            {session?.recoverySuggestion ? (
              <div className="recovery-suggestion">
                <div className={`recovery-icon-large ${session.recoverySuggestion.toLowerCase()}`}>
                  {session.recoverySuggestion === 'ABSOLUTE_REST' && '😴'}
                  {session.recoverySuggestion === 'LIGHT_ACTIVITY' && '🚶'}
                  {session.recoverySuggestion === 'ACTIVE_RECOVERY' && '🧘'}
                  {session.recoverySuggestion === 'MODERATE_WORKOUT' && '💪'}
                  {session.recoverySuggestion === 'INCREASE_INTENSITY' && '🔥'}
                </div>
                <p className="suggestion-text">
                  {session.recoverySuggestion.replace(/_/g, ' ')}
                </p>
              </div>
            ) : (
              <p className="empty-state">No recovery data yet</p>
            )}
          </div>

          <div className="dashboard-card full-width">
            <h2>Register New Training Session</h2>
            {error && <div className="form-error">{error}</div>}
            <form onSubmit={handleNewSession} className="session-form-full">
              <div className="form-row">
                <div className="form-group">
                  <label>📅 Date</label>
                  <input
                    type="date"
                    value={newSession.date}
                    onChange={(e) => setNewSession({ ...newSession, date: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>⏱️ Duration (minutes)</label>
                  <input
                    type="number"
                    value={newSession.duration}
                    onChange={(e) => setNewSession({ ...newSession, duration: e.target.value })}
                    min="1"
                    max="300"
                    required
                  />
                </div>
                <div className="form-group">
                  <label>💪 Intensity</label>
                  <select
                    value={newSession.intensity}
                    onChange={(e) => setNewSession({ ...newSession, intensity: e.target.value })}
                    required
                  >
                    <option value="LOW">🟢 Low (1x)</option>
                    <option value="MODERATE">🟡 Moderate (1.5x)</option>
                    <option value="HIGH">🔴 High (2x)</option>
                    <option value="EXTREME">⚫ Extreme (2.5x)</option>
                  </select>
                </div>
              </div>
              <button type="submit" disabled={submitting} className="submit-btn">
                {submitting ? '⏳ Processing...' : '🚀 Register & Calculate'}
              </button>
            </form>
          </div>
        </div>
      )}

      {activeTab === 'result' && session && (
        <div className="result-section">
          <div className="result-summary">
            <div className="summary-card">
              <div className="summary-icon">⚡</div>
              <div className="summary-content">
                <h3>Fatigue Level: {session.fatigueLevel}</h3>
                <p>{session.durationMinutes} min × {session.intensity} = {Math.floor(session.durationMinutes/10) * (session.intensity === 'LOW' ? 1 : session.intensity === 'MODERATE' ? 1.5 : session.intensity === 'HIGH' ? 2 : 2.5)} points</p>
              </div>
            </div>
            <div className="summary-card">
              <div className="summary-icon">📋</div>
              <div className="summary-content">
                <h3>Routine: {session.recommendedRoutine?.name}</h3>
                <p>{session.recommendedRoutine?.recommendedDurationMinutes} min, {session.recommendedRoutine?.recommendedIntensity}</p>
              </div>
            </div>
            <div className="summary-card">
              <div className="summary-icon">💡</div>
              <div className="summary-content">
                <h3>Recovery: {session.recoverySuggestion?.replace(/_/g, ' ')}</h3>
              </div>
            </div>
          </div>

          <DomainServiceFlow session={session} sportType={sportType} />
        </div>
      )}

      {activeTab === 'history' && sessionHistory.length > 0 && (
        <div className="history-section">
          <h2>📊 Your Training History</h2>
          <div className="history-grid">
            {sessionHistory.slice(0, 10).map((s, idx) => (
              <div key={idx} className="history-card">
                <div className="history-date">
                  {new Date(s.sessionDate).toLocaleDateString('en-US', { 
                    weekday: 'short', 
                    month: 'short', 
                    day: 'numeric' 
                  })}
                </div>
                <div className="history-details">
                  <div className="history-detail">
                    <span className="label">⏱️</span>
                    <span>{s.durationMinutes} min</span>
                  </div>
                  <div className="history-detail">
                    <span className="label">💪</span>
                    <span className={`intensity-${s.intensity?.toLowerCase()}`}>{s.intensity}</span>
                  </div>
                  <div className="history-detail">
                    <span className="label">⚡</span>
                    <span className={`fatigue-badge fatigue-${s.fatigueLevel?.toLowerCase()}`}>
                      {s.fatigueLevel}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default GymDashboard;