import React, { useState } from 'react';
import { api } from '../services/api';
import './ProfileCreate.css';

function ProfileCreate() {
  const [mode, setMode] = useState('login'); // 'create' or 'login'
  const [name, setName] = useState('');
  const [sportType, setSportType] = useState('GYM');
  const [birthDate, setBirthDate] = useState('2000-01-01');
  const [searchName, setSearchName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleCreate = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await api.registerAthlete({
        name: name,
        sportType: sportType,
        birthDate: birthDate
      });
      
      if (sportType === 'GYM') {
        window.location.href = '/gym';
      } else {
        window.location.href = '/football';
      }
      
    } catch (err) {
      setError(err.message || 'Failed to create athlete. Make sure backend is running.');
      setLoading(false);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Search athletes by name using the search endpoint
      const response = await fetch(`${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/api/v1/athletes/search?name=${encodeURIComponent(searchName)}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
      });

      if (!response.ok) {
        throw new Error('Search failed');
      }

      const athletes = await response.json();
      
      if (!athletes || athletes.length === 0) {
        throw new Error('No athlete found with this name');
      }

      // Use the first matching athlete
      const athlete = athletes[0];
      
      // Store the athlete data
      localStorage.setItem('athleteId', athlete.id);
      localStorage.setItem('athleteName', athlete.name);
      localStorage.setItem('sportType', athlete.sportType);
      
      if (athlete.sportType === 'GYM') {
        window.location.href = '/gym';
      } else {
        window.location.href = '/football';
      }
      
    } catch (err) {
      setError(err.message || 'Athlete not found. Try a different name.');
      setLoading(false);
    }
  };

  return (
    <div className="profile-create-container">
      <div className="profile-create-card">
        <h1>Sports Management</h1>
        <p className="subtitle">
          {mode === 'create' ? 'Create a new athlete profile' : 'Login with existing athlete'}
        </p>

        <div className="mode-switch">
          <button 
            className={mode === 'create' ? 'active' : ''} 
            onClick={() => setMode('create')}
          >
            New Athlete
          </button>
          <button 
            className={mode === 'login' ? 'active' : ''} 
            onClick={() => setMode('login')}
          >
            Existing Athlete
          </button>
        </div>

        {error ? <div className="error-message">{error}</div> : null}
        
        {mode === 'create' ? (
          <form onSubmit={handleCreate}>
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
              {loading ? 'Creating...' : 'Create Athlete'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleLogin}>
            <div className="form-group">
              <label htmlFor="searchName">Your Name</label>
              <input
                type="text"
                id="searchName"
                value={searchName}
                onChange={(e) => setSearchName(e.target.value)}
                required
                placeholder="Enter your name to login"
              />
              <small className="form-hint">Use the same name you used to create your profile</small>
            </div>

            <button type="submit" disabled={loading}>
              {loading ? 'Searching...' : 'Login'}
            </button>
          </form>
        )}
        
        <div className="backend-note">
          <p>Backend: http://localhost:8080</p>
        </div>
      </div>
    </div>
  );
}

export default ProfileCreate;