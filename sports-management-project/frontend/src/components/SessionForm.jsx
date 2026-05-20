import { useState } from 'react';
import { athleteService } from '../services/api';

const SessionForm = ({ athleteId, onSuccess }) => {
  const [formData, setFormData] = useState({
    type: 'Strength',
    duration: 60,
    intensity: 'Medium',
    date: new Date().toISOString().split('T')[0],
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess(false);

    try {
      await athleteService.registerSession({
        athleteId,
        type: formData.type,
        duration: parseInt(formData.duration),
        intensity: formData.intensity,
        date: formData.date,
      });
      setSuccess(true);
      setFormData({
        type: 'Strength',
        duration: 60,
        intensity: 'Medium',
        date: new Date().toISOString().split('T')[0],
      });
      if (onSuccess) onSuccess();
    } catch (err) {
      setError(err.message || 'Failed to register session');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="session-form">
      <h3>Register Training Session</h3>
      {error && <div className="form-error">{error}</div>}
      {success && <div className="form-success">Session registered successfully!</div>}
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label>Type</label>
            <select
              value={formData.type}
              onChange={(e) => setFormData({ ...formData, type: e.target.value })}
            >
              <option value="Strength">Strength</option>
              <option value="Cardio">Cardio</option>
              <option value="Flexibility">Flexibility</option>
              <option value="HIIT">HIIT</option>
            </select>
          </div>
          <div className="form-group">
            <label>Duration (min)</label>
            <input
              type="number"
              value={formData.duration}
              onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
              min="1"
            />
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label>Intensity</label>
            <select
              value={formData.intensity}
              onChange={(e) => setFormData({ ...formData, intensity: e.target.value })}
            >
              <option value="Low">Low</option>
              <option value="Medium">Medium</option>
              <option value="High">High</option>
            </select>
          </div>
          <div className="form-group">
            <label>Date</label>
            <input
              type="date"
              value={formData.date}
              onChange={(e) => setFormData({ ...formData, date: e.target.value })}
            />
          </div>
        </div>
        <button type="submit" disabled={loading}>
          {loading ? 'Registering...' : 'Register Session'}
        </button>
      </form>
    </div>
  );
};

export default SessionForm;