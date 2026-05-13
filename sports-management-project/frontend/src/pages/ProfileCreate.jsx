import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import './ProfileCreate.css';

const ProfileCreate = () => {
  const [name, setName] = useState('');
  const [sportType, setSportType] = useState('gym');
  const [duration, setDuration] = useState(60);
  const [intensity, setIntensity] = useState('MEDIUM');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await api.createSession({
        name,
        sportType,
        date: new Date().toISOString(),
        duration,
        intensity
      });
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Failed to create profile');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-create-container">
      <div className="profile-create-card">
        <h1>Create Your Profile</h1>
        <p className="subtitle">Start tracking your training journey</p>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Your Name</label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
              placeholder="Enter your name"
            />
          </div>

          <div className="form-group">
            <label htmlFor="sportType">Sport Type</label>
            <select
              id="sportType"
              value={sportType}
              onChange={(e) => setSportType(e.target.value)}
            >
              <option value="gym">Gym / Fitness</option>
              <option value="football">Football</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="duration">Training Duration (minutes)</label>
            <input
              type="number"
              id="duration"
              value={duration}
              onChange={(e) => setDuration(e.target.value)}
              min="1"
              max="300"
            />
          </div>

          <div className="form-group">
            <label htmlFor="intensity">Intensity</label>
            <select
              id="intensity"
              value={intensity}
              onChange={(e) => setIntensity(e.target.value)}
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          <button type="submit" disabled={loading}>
            {loading ? 'Creating...' : 'Create Profile'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ProfileCreate;