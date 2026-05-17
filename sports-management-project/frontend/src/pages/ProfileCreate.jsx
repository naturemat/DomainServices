import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../services/api';
import './ProfileCreate.css';

const ProfileCreate = () => {
  const [name, setName] = useState('');
  const [sportType, setSportType] = useState('GYM');
  const [birthDate, setBirthDate] = useState('2000-01-01');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await api.registerAthlete({
        name,
        sportType,
        birthDate
      });
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Failed to create profile. Make sure backend is running.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="profile-create-container">
      <div className="profile-create-card">
        <h1>Create Your Athlete Profile</h1>
        <p className="subtitle">Register as an athlete to start tracking your training</p>
        
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
              <option value="GYM">Gym / Fitness</option>
              <option value="FOOTBALL">Football</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="birthDate">Birth Date</label>
            <input
              type="date"
              id="birthDate"
              value={birthDate}
              onChange={(e) => setBirthDate(e.target.value)}
              required
            />
          </div>

          <button type="submit" disabled={loading}>
            {loading ? 'Registering...' : 'Register Athlete'}
          </button>
        </form>
        
        <div className="backend-note">
          <p>Backend must be running at http://localhost:8080</p>
        </div>
      </div>
    </div>
  );
};

export default ProfileCreate;