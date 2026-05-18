import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import './SessionCreate.css';

function SessionCreate() {
  const navigate = useNavigate();
  const athlete = api.getStoredAthlete();
  
  const [formData, setFormData] = useState({
    date: new Date().toISOString().split('T')[0],
    duration: 60,
    intensity: 'MODERATE'
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const sessionResult = await api.createSession({
        name: athlete?.name || 'Athlete',
        sportType: athlete?.sportType || 'GYM',
        date: formData.date,
        duration: formData.duration,
        intensity: formData.intensity
      });
      
      setResult(sessionResult.session);
      
      setTimeout(() => {
        if ((athlete?.sportType || 'GYM') === 'GYM') {
          window.location.href = '/gym';
        } else {
          window.location.href = '/football';
        }
      }, 3000);
      
    } catch (err) {
      setError(err.message || 'Failed to register session');
    } finally {
      setLoading(false);
    }
  };

  const intensityInfo = {
    LOW: '1x',
    MODERATE: '1.5x',
    HIGH: '2x',
    EXTREME: '2.5x'
  };

  const basePoints = Math.floor(formData.duration / 10);
  const totalPoints = basePoints * (formData.intensity === 'LOW' ? 1 : formData.intensity === 'MODERATE' ? 1.5 : formData.intensity === 'HIGH' ? 2 : 2.5);

  if (result) {
    return (
      <div className="session-result-page">
        <div className="result-header">
          <div className="result-icon">✓</div>
          <h2>Session Registered!</h2>
        </div>
        
        <div className="result-summary-cards">
          <div className="result-card">
            <span className="card-label">Fatigue</span>
            <span className={`card-value fatigue-${result.fatigueLevel?.toLowerCase()}`}>
              {result.fatigueLevel}
            </span>
          </div>
          <div className="result-card">
            <span className="card-label">Routine</span>
            <span className="card-value">{result.recommendedRoutine?.name}</span>
          </div>
          <div className="result-card">
            <span className="card-label">Recovery</span>
            <span className="card-value">{result.recoverySuggestion?.replace(/_/g, ' ')}</span>
          </div>
        </div>

        <p>Redirecting to dashboard in 3 seconds...</p>
      </div>
    );
  }

  return (
    <div className="session-create-page">
      <div className="session-form-card">
        <div className="form-header">
          <h1>Register Training Session</h1>
          <p>Athlete: {athlete?.name}</p>
        </div>

        {error ? <div className="error-message">{error}</div> : null}

        <form onSubmit={handleSubmit}>
          <div className="form-section">
            <div className="form-group">
              <label>Date</label>
              <input
                type="date"
                value={formData.date}
                onChange={(e) => setFormData({ ...formData, date: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label>Duration (minutes)</label>
              <input
                type="number"
                value={formData.duration}
                onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                min="1"
                max="300"
                required
              />
            </div>
          </div>

          <div className="form-section">
            <div className="form-group">
              <label>Intensity</label>
              <select
                value={formData.intensity}
                onChange={(e) => setFormData({ ...formData, intensity: e.target.value })}
                required
              >
                <option value="LOW">Low ({intensityInfo.LOW})</option>
                <option value="MODERATE">Moderate ({intensityInfo.MODERATE})</option>
                <option value="HIGH">High ({intensityInfo.HIGH})</option>
                <option value="EXTREME">Extreme ({intensityInfo.EXTREME})</option>
              </select>
            </div>
          </div>

          <div className="formula-preview">
            <p>Calculation Preview: {basePoints} × {intensityInfo[formData.intensity]} = <strong>{totalPoints}</strong> points</p>
          </div>

          <div className="form-actions">
            <button 
              type="button" 
              className="cancel-btn"
              onClick={() => window.history.back()}
            >
              Back
            </button>
            <button type="submit" disabled={loading} className="submit-btn">
              {loading ? 'Processing...' : 'Register'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default SessionCreate;