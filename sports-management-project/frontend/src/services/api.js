const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const handleResponse = async (response) => {
  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || 'An error occurred');
  }
  return response.json();
};

const getStoredAthleteId = () => {
  return localStorage.getItem('athleteId');
};

const saveAthleteId = (id) => {
  localStorage.setItem('athleteId', id);
};

const clearAthleteData = () => {
  localStorage.removeItem('athleteId');
  localStorage.removeItem('athleteName');
  localStorage.removeItem('sportType');
};

export const api = {
  createSession: async (sessionData) => {
    const { name, sportType, date, duration, intensity } = sessionData;
    
    const athleteId = getStoredAthleteId() || crypto.randomUUID();
    const sportTypeEnum = sportType === 'football' ? 'FOOTBALL' : 'GYM';
    const intensityEnum = intensity.toUpperCase();

    const requestBody = {
      athleteId: athleteId,
      sessionDate: date || new Date().toISOString(),
      durationMinutes: parseInt(duration),
      intensity: intensityEnum
    };

    const response = await fetch(`${API_URL}/api/v1/training/sessions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody)
    });

    const data = await handleResponse(response);
    
    saveAthleteId(athleteId);
    localStorage.setItem('athleteName', name);
    localStorage.setItem('sportType', sportType);
    
    return {
      athleteId: athleteId,
      name: name,
      sportType: sportType,
      session: data
    };
  },

  getRoutine: async (athleteId) => {
    const response = await fetch(`${API_URL}/api/v1/training/routines/${athleteId}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    });
    return handleResponse(response);
  },

  getStoredAthlete: () => {
    const athleteId = getStoredAthleteId();
    if (!athleteId) return null;
    return {
      athleteId: athleteId,
      name: localStorage.getItem('athleteName'),
      sportType: localStorage.getItem('sportType')
    };
  },

  logout: () => {
    clearAthleteData();
  }
};