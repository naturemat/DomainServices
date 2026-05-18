const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const handleResponse = async (response) => {
  if (!response.ok) {
    const errorText = await response.text();
    let errorMsg = errorText;
    try {
      const errorObj = JSON.parse(errorText);
      errorMsg = errorObj.error || errorText;
    } catch {}
    throw new Error(errorMsg || 'An error occurred');
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
  registerAthlete: async (athleteData) => {
    const { name, sportType, birthDate } = athleteData;
    
    const sportTypeEnum = sportType.toUpperCase();
    if (sportTypeEnum !== 'GYM' && sportTypeEnum !== 'FOOTBALL') {
      throw new Error('Invalid sportType. Must be GYM or FOOTBALL');
    }

    const requestBody = {
      name: name,
      sportType: sportTypeEnum,
      birthDate: birthDate
    };

    const response = await fetch(`${API_URL}/api/v1/athletes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody)
    });

    const data = await handleResponse(response);
    
    saveAthleteId(data.id);
    localStorage.setItem('athleteName', name);
    localStorage.setItem('sportType', sportTypeEnum);
    
    return data;
  },

  createSession: async (sessionData) => {
    const { name, sportType, date, duration, intensity } = sessionData;
    
    let athleteId = getStoredAthleteId();
    const sportTypeEnum = sportType.toUpperCase();
    const intensityEnum = intensity.toUpperCase().replace('LOW', 'LIGHT');

    if (sportTypeEnum !== 'GYM' && sportTypeEnum !== 'FOOTBALL') {
      throw new Error('Invalid sportType. Must be GYM or FOOTBALL');
    }

    if (!athleteId) {
      const athlete = await api.registerAthlete({
        name: name || 'Default Athlete',
        sportType: sportType,
        birthDate: '2000-01-01'
      });
      athleteId = athlete.id;
    }

    const sessionDateTime = date ? new Date(date).toISOString() : new Date().toISOString();
    
    const requestBody = {
      athleteId: athleteId,
      sessionDate: sessionDateTime,
      durationMinutes: parseInt(duration),
      intensity: intensityEnum
    };

    const response = await fetch(`${API_URL}/api/v1/training/sessions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody)
    });

    const data = await handleResponse(response);
    
    localStorage.setItem('athleteName', name);
    localStorage.setItem('sportType', sportTypeEnum);
    
    return {
      athleteId: athleteId,
      name: name,
      sportType: sportTypeEnum,
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

  getSessions: async (athleteId) => {
    const response = await fetch(`${API_URL}/api/v1/training/sessions?athleteId=${athleteId}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    });
    return handleResponse(response);
  },

  searchSessions: async (athleteName) => {
    const response = await fetch(`${API_URL}/api/v1/training/sessions/search?athleteName=${encodeURIComponent(athleteName)}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    });
    return handleResponse(response);
  },

  getSessionsByAthlete: async (athleteId) => {
    const response = await fetch(`${API_URL}/api/v1/training/sessions/by-athlete/${athleteId}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    });
    return handleResponse(response);
  },

  getStoredAthlete: () => {
    const athleteId = getStoredAthleteId();
    if (!athleteId) return null;
    const sportType = localStorage.getItem('sportType');
    return {
      athleteId: athleteId,
      name: localStorage.getItem('athleteName'),
      sportType: sportType,
      sportTypeDisplay: sportType === 'GYM' ? 'Gym' : sportType === 'FOOTBALL' ? 'Football' : sportType
    };
  },

  logout: () => {
    clearAthleteData();
  }
};