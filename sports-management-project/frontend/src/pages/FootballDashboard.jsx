import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import './Dashboard.css';

function FootballDashboard() {
  const [sessionData, setSessionData] = useState(null);
  const [sessionHistory, setSessionHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [newSession, setNewSession] = useState({ 
    duration: 60, 
    intensity: 'LIGHT', 
    date: new Date().toISOString().split('T')[0] 
  });
  const [submitting, setSubmitting] = useState(false);
  const [activeTab, setActiveTab] = useState('register');
  const [selectedSession, setSelectedSession] = useState(null);

  const athlete = api.getStoredAthlete();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    if (!athlete || !athlete.athleteId) {
      window.location.href = '/';
      return;
    }
    try {
      const [routineData, sessions] = await Promise.all([
        api.getRoutine(athlete.athleteId),
        api.getSessionsByAthlete(athlete.athleteId)
      ]);
      setSessionData({ routine: routineData });
      setSessionHistory(sessions || []);
    } catch (err) {
      console.log('No data yet');
    } finally {
      setLoading(false);
    }
  };

  const handleNewSession = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');

    try {
      const result = await api.createSession({
        name: athlete.name,
        sportType: 'FOOTBALL',
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
      setActiveTab('fatigue');
    } catch (err) {
      setError(err.message || 'Error al registrar sesión');
    } finally {
      setSubmitting(false);
    }
  };

  const calculateSessionPoints = (session) => {
    const intensityMultipliers = { LIGHT: 1, MODERATE: 2, HIGH: 3, EXTREME: 4 };
    const basePoints = Math.floor(session.durationMinutes / 10);
    return basePoints * (intensityMultipliers[session.intensity] || 1);
  };

  const getIntensityLabel = (intensity) => {
    const labels = { LIGHT: 'Ligera', MODERATE: 'Moderada', HIGH: 'Alta', EXTREME: 'Extrema' };
    return labels[intensity] || intensity;
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loading-spinner"></div>
        <div className="loading-text">Cargando...</div>
      </div>
    );
  }

  const session = sessionData?.session;
  const athleteName = athlete?.name || 'Atleta';

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <div>
          <h1>⚽ Dashboard Fútbol</h1>
          <div className="dashboard-welcome">Bienvenido, {athleteName}</div>
        </div>
        <button className="refresh-btn" onClick={() => window.location.reload()}>
          ↻ Actualizar
        </button>
      </div>

      <div className="dashboard-tabs">
        <button 
          className={`tab-btn ${activeTab === 'register' ? 'active' : ''}`}
          onClick={() => { setActiveTab('register'); setSelectedSession(null); }}
        >
          Registrar Sesión
        </button>
        <button 
          className={`tab-btn ${activeTab === 'fatigue' ? 'active' : ''}`}
          onClick={() => { setActiveTab('fatigue'); setSelectedSession(null); }}
        >
          Historial de Fatiga ({sessionHistory.length})
        </button>
        <button 
          className={`tab-btn ${activeTab === 'domain' ? 'active' : ''}`}
          onClick={() => { setActiveTab('domain'); setSelectedSession(null); }}
        >
          Servicios de Dominio
        </button>
      </div>

      {activeTab === 'register' && (
        <div className="dashboard-grid">
          <div className="dashboard-card">
            <h2>Perfil del Atleta</h2>
            <div className="profile-info">
              <p><strong>Nombre:</strong> {athleteName}</p>
              <p><strong>Deporte:</strong> Fútbol</p>
              <p><strong>ID:</strong> <span className="athlete-id">{athlete?.athleteId}</span></p>
              <p><strong>Total Sesiones:</strong> {sessionHistory.length}</p>
            </div>
          </div>

          <div className="dashboard-card">
            <h2>Nivel de Fatiga Actual</h2>
            {session?.fatigueLevel ? (
              <div className="fatigue-display">
                <span className={`fatigue-badge fatigue-${session.fatigueLevel.toLowerCase()}`}>
                  {session.fatigueLevel === 'LOW' ? 'BAJA' : session.fatigueLevel === 'MEDIUM' ? 'MEDIA' : 'ALTA'}
                </span>
                <div className="fatigue-calc">
                  <small>Cálculo: {session.durationMinutes} min ÷ 10 × {session.intensity === 'LIGHT' ? '1' : session.intensity === 'MODERATE' ? '2' : session.intensity === 'HIGH' ? '3' : '4'} = {calculateSessionPoints(session)} puntos</small>
                </div>
              </div>
            ) : (
              <p className="empty-state">Registra una sesión para ver la fatiga</p>
            )}
          </div>

          <div className="dashboard-card">
            <h2>Sugerencia de Recuperación</h2>
            {session?.recoverySuggestion ? (
              <div className="recovery-suggestion">
                <p className="suggestion-text">
                  {session.recoverySuggestion === 'ABSOLUTE_REST' ? 'DESCANSO ABSOLUTO' : 
                   session.recoverySuggestion === 'LIGHT_ACTIVITY' ? 'ACTIVIDAD LIGERA' : 
                   session.recoverySuggestion === 'ACTIVE_RECOVERY' ? 'RECUPERACIÓN ACTIVA' : 
                   session.recoverySuggestion === 'MODERATE_WORKOUT' ? 'ENTRENAMIENTO MODERADO' : 
                   'AUMENTAR INTENSIDAD'}
                </p>
                <p className="domain-explanation">
                  Basado en: FatigueCalculationService → {session.fatigueLevel} → RecoverySuggestionService
                </p>
              </div>
            ) : (
              <p className="empty-state">Sin datos aún</p>
            )}
          </div>

          <div className="dashboard-card">
            <h2>Rutina Recomendada</h2>
            {session?.recommendedRoutine ? (
              <div className="routine-info">
                <p><strong>{session.recommendedRoutine.name}</strong></p>
                <p>{session.recommendedRoutine.description}</p>
                <p><strong>Duración:</strong> {session.recommendedRoutine.recommendedDurationMinutes} min</p>
                <p><strong>Intensidad:</strong> {session.recommendedRoutine.recommendedIntensity}</p>
              </div>
            ) : (
              <p className="empty-state">Sin rutina aún</p>
            )}
          </div>

          <div className="dashboard-card full-width">
            <h2>Registrar Nueva Sesión</h2>
            {error ? <div className="form-error">{error}</div> : null}
            <form onSubmit={handleNewSession} className="session-form-full">
              <div className="form-row">
                <div className="form-group">
                  <label>Fecha</label>
                  <input
                    type="date"
                    value={newSession.date}
                    onChange={(e) => setNewSession({ ...newSession, date: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Duración (min)</label>
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
                  <label>Intensidad</label>
                  <select
                    value={newSession.intensity}
                    onChange={(e) => setNewSession({ ...newSession, intensity: e.target.value })}
                    required
                  >
                    <option value="LIGHT">Ligera (1x)</option>
                    <option value="MODERATE">Moderada (2x)</option>
                    <option value="HIGH">Alta (3x)</option>
                    <option value="EXTREME">Extrema (4x)</option>
                  </select>
                </div>
              </div>
              <button type="submit" disabled={submitting} className="submit-btn">
                {submitting ? 'Registrando...' : 'Registrar y Calcular'}
              </button>
            </form>
          </div>

          <div className="dashboard-card full-width">
            <h2>📈 Evolución de Fatiga - Últimos 7 Días</h2>
            <div className="seven-day-chart">
              {(() => {
                const today = new Date();
                const last7Days = [];
                for (let i = 6; i >= 0; i--) {
                  const date = new Date(today);
                  date.setDate(date.getDate() - i);
                  last7Days.push(date);
                }

                return (
                  <>
                    <div className="chart-days">
                      {last7Days.map((date, idx) => {
                        const dateStr = date.toISOString().split('T')[0];
                        const daySessions = sessionHistory.filter(s => 
                          new Date(s.sessionDate).toISOString().split('T')[0] === dateStr
                        );
                        const avgPoints = daySessions.length > 0 
                          ? Math.round(daySessions.reduce((sum, s) => sum + calculateSessionPoints(s), 0) / daySessions.length)
                          : 0;
                        const maxHeight = 100;
                        const barHeight = Math.min((avgPoints / 40) * maxHeight, maxHeight);
                        
                        return (
                          <div key={idx} className="day-column">
                            <div className="day-bar-container">
                              <div 
                                className={`day-bar ${avgPoints === 0 ? 'empty' : avgPoints <= 14 ? 'low' : avgPoints <= 29 ? 'medium' : 'high'}`}
                                style={{ height: `${barHeight}%` }}
                              >
                                {avgPoints > 0 && <span className="day-points">{avgPoints}</span>}
                              </div>
                            </div>
                            <span className="day-name">
                              {date.toLocaleDateString('es-ES', { weekday: 'short' })}
                            </span>
                            <span className="day-date">
                              {date.getDate()}
                            </span>
                          </div>
                        );
                      })}
                    </div>
                    <div className="chart-legend">
                      <span><span className="dot low"></span> 0-14 pts</span>
                      <span><span className="dot medium"></span> 15-29 pts</span>
                      <span><span className="dot high"></span> 30+ pts</span>
                    </div>
                  </>
                );
              })()}
            </div>
          </div>
        </div>
      )}

      {activeTab === 'fatigue' && (
        <div className="fatigue-history-section">
          <h2>📊 Historial de Fatiga - Haz clic en una tarjeta</h2>
          <p className="section-description">Cada tarjeta representa una sesión registrada. Haz clic para ver los detalles.</p>
          
          <div className="sessions-grid">
            {sessionHistory.slice().reverse().map((s, idx) => (
              <div 
                key={idx} 
                className={`session-card ${selectedSession === s ? 'selected' : ''}`}
                onClick={() => setSelectedSession(selectedSession === s ? null : s)}
              >
                <div className="session-card-header">
                  <span className="session-date">
                    {new Date(s.sessionDate).toLocaleDateString('es-ES', { 
                      weekday: 'short', 
                      day: 'numeric',
                      month: 'short'
                    })}
                  </span>
                  <span className={`fatigue-badge fatigue-${s.fatigueLevel?.toLowerCase()}`}>
                    {s.fatigueLevel === 'LOW' ? 'BAJA' : s.fatigueLevel === 'MEDIUM' ? 'MEDIA' : 'ALTA'}
                  </span>
                </div>
                
                <div className="session-card-body">
                  <div className="session-stat">
                    <span className="stat-icon">⏱️</span>
                    <span>{s.durationMinutes} min</span>
                  </div>
                  <div className="session-stat">
                    <span className="stat-icon">💪</span>
                    <span>{getIntensityLabel(s.intensity)}</span>
                  </div>
                  <div className="session-stat">
                    <span className="stat-icon">⚡</span>
                    <span>{calculateSessionPoints(s)} pts</span>
                  </div>
                </div>

                {selectedSession === s && (
                  <div className="session-card-details">
                    <h4>Detalles de la Sesión</h4>
                    
                    <div className="detail-row">
                      <span>Duración:</span>
                      <strong>{s.durationMinutes} minutos</strong>
                    </div>
                    <div className="detail-row">
                      <span>Intensidad:</span>
                      <strong>{getIntensityLabel(s.intensity)} ({s.intensity})</strong>
                    </div>
                    <div className="detail-row">
                      <span>Calorías:</span>
                      <strong>{s.caloriesBurned || 'N/A'}</strong>
                    </div>
                    <div className="detail-row">
                      <span>Nivel de Fatiga:</span>
                      <strong className={`fatigue-text-${s.fatigueLevel?.toLowerCase()}`}>
                        {s.fatigueLevel === 'LOW' ? 'BAJA' : s.fatigueLevel === 'MEDIUM' ? 'MEDIA' : 'ALTA'}
                      </strong>
                    </div>
                    <div className="detail-row">
                      <span>Puntos de Fatiga:</span>
                      <strong>{calculateSessionPoints(s)}</strong>
                    </div>

                    <div className="calculation-box">
                      <h5>🔢 Desglose del Cálculo:</h5>
                      <p className="formula">{s.durationMinutes} min ÷ 10 = {Math.floor(s.durationMinutes / 10)} puntos base</p>
                      <p className="formula">× Multiplicador de intensidad {s.intensity === 'LIGHT' ? '1' : s.intensity === 'MODERATE' ? '2' : s.intensity === 'HIGH' ? '3' : '4'}</p>
                      <p className="formula-result">= <strong>{calculateSessionPoints(s)} puntos de fatiga</strong></p>
                      <p className="formula-result">= Nivel: <strong>{s.fatigueLevel === 'LOW' ? 'BAJA' : s.fatigueLevel === 'MEDIUM' ? 'MEDIA' : 'ALTA'}</strong></p>
                    </div>

                    {s.recommendedRoutine && (
                      <div className="routine-box">
                        <h5>📋 Rutina Recomendada:</h5>
                        <p><strong>{s.recommendedRoutine.name}</strong></p>
                        <p>{s.recommendedRoutine.description}</p>
                        <p>Duración: {s.recommendedRoutine.recommendedDurationMinutes} min | Intensidad: {s.recommendedRoutine.recommendedIntensity}</p>
                      </div>
                    )}

                    <div className="recovery-box">
                      <h5>💡 Sugerencia de Recuperación:</h5>
                      <p className="recovery-text">
                        {s.recoverySuggestion === 'ABSOLUTE_REST' ? 'DESCANSO ABSOLUTO - Tu cuerpo necesita descanso completo' : 
                         s.recoverySuggestion === 'LIGHT_ACTIVITY' ? 'ACTIVIDAD LIGERA - Solo caminata o estiramiento' : 
                         s.recoverySuggestion === 'ACTIVE_RECOVERY' ? 'RECUPERACIÓN ACTIVA - Ejercicios técnicos suaves de fútbol' : 
                         s.recoverySuggestion === 'MODERATE_WORKOUT' ? 'ENTRENAMIENTO MODERADO - Mantén tu nivel actual' : 
                         'AUMENTAR INTENSIDAD - ¡Puedes pushear más!'}
                      </p>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>

          {sessionHistory.length === 0 && (
            <div className="empty-history">
              <p>No hay sesiones registradas aún.</p>
              <p>Registra tu primera sesión en la pestaña "Registrar Sesión"</p>
            </div>
          )}
        </div>
      )}

      {activeTab === 'domain' && (
        <div className="domain-services-section">
          <h2>🎓 Cómo Funcionan los Servicios de Dominio</h2>
          <p className="section-description">Aprende cómo el sistema calcula tu fatiga, rutinas y recuperación</p>
          
          <div className="domain-flow-visual">
            <div className="service-card-visual">
              <div className="service-icon">⚡</div>
              <div className="service-content">
                <h3>1. FatigueCalculationService</h3>
                <p className="service-purpose">Calcula el nivel de fatiga basado en tus sesiones de entrenamiento</p>
                
                <div className="formula-box">
                  <h4>Fórmula:</h4>
                  <code>fatiga = (duración ÷ 10) × multiplicadorIntensidad</code>
                </div>

                <div className="multipliers-list">
                  <h4>Multiplicadores de Intensidad:</h4>
                  <div className="multiplier-item">
                    <span className="multiplier-label light">🟢 Ligera</span>
                    <span className="multiplier-value">1x</span>
                  </div>
                  <div className="multiplier-item">
                    <span className="multiplier-label moderate">🟡 Moderada</span>
                    <span className="multiplier-value">2x</span>
                  </div>
                  <div className="multiplier-item">
                    <span className="multiplier-label high">🔴 Alta</span>
                    <span className="multiplier-value">3x</span>
                  </div>
                  <div className="multiplier-item">
                    <span className="multiplier-label extreme">⚫ Extrema</span>
                    <span className="multiplier-value">4x</span>
                  </div>
                </div>

                <div className="threshold-box">
                  <h4>Umbrales de Fatiga:</h4>
                  <div className="threshold-visual">
                    <div className="threshold-segment low">
                      <span>BAJA</span>
                      <small>0-14 pts</small>
                    </div>
                    <div className="threshold-segment medium">
                      <span>MEDIA</span>
                      <small>15-29 pts</small>
                    </div>
                    <div className="threshold-segment high">
                      <span>ALTA</span>
                      <small>30+ pts</small>
                    </div>
                  </div>
                </div>

                <div className="example-box">
                  <h4>Ejemplo Práctico:</h4>
                  <p>Sesión: 90 minutos × Intensidad Alta (3x)</p>
                  <p>90 ÷ 10 = 9 puntos base</p>
                  <p>9 × 3 = 27 puntos</p>
                  <p className="result">Resultado: Fatiga MEDIA (15-29 puntos)</p>
                </div>
              </div>
            </div>

            <div className="arrow-visual">⬇️</div>

            <div className="service-card-visual">
              <div className="service-icon">📋</div>
              <div className="service-content">
                <h3>2. RoutineRecommendationService</h3>
                <p className="service-purpose">Recomienda una rutina de entrenamiento basada en tu nivel de fatiga y tipo de deporte</p>
                
                <div className="decision-table">
                  <h4>Tabla de Decisiones para Fútbol:</h4>
                  <table>
                    <thead>
                      <tr>
                        <th>Fatiga</th>
                        <th>Deporte</th>
                        <th>Rutina Recomendada</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td><span className="fatigue-badge fatigue-low">BAJA</span></td>
                        <td>Fútbol</td>
                        <td>Rutina Intensa Fútbol (60 min, Alta)</td>
                      </tr>
                      <tr>
                        <td><span className="fatigue-badge fatigue-medium">MEDIA</span></td>
                        <td>Fútbol</td>
                        <td>Rutina de Mantenimiento (45 min, Moderada)</td>
                      </tr>
                      <tr>
                        <td><span className="fatigue-badge fatigue-high">ALTA</span></td>
                        <td>Fútbol</td>
                        <td>Rutina de Recuperación (30 min, Ligera)</td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            <div className="arrow-visual">⬇️</div>

            <div className="service-card-visual">
              <div className="service-icon">💡</div>
              <div className="service-content">
                <h3>3. RecoverySuggestionService</h3>
                <p className="service-purpose">Sugiere acciones de recuperación basadas en tu nivel de fatiga</p>
                
                <div className="recovery-cards">
                  <div className="recovery-card-item high">
                    <div className="recovery-emoji">😴</div>
                    <h4>Fatiga ALTA</h4>
                    <p className="recovery-action">DESCANSO ABSOLUTO</p>
                    <p className="recovery-desc">Tu cuerpo necesita descanso completo. No entrenes hoy.</p>
                  </div>
                  
                  <div className="recovery-card-item medium">
                    <div className="recovery-emoji">⚽</div>
                    <h4>Fatiga MEDIA</h4>
                    <p className="recovery-action">RECUPERACIÓN ACTIVA</p>
                    <p className="recovery-desc">Ejercicios técnicos suaves, estiramientos o caminata.</p>
                  </div>
                  
                  <div className="recovery-card-item low">
                    <div className="recovery-emoji">🔥</div>
                    <h4>Fatiga BAJA</h4>
                    <p className="recovery-action">AUMENTAR INTENSIDAD</p>
                    <p className="recovery-desc">¡Puedes pushear más! Es momento de entrenar intensamente.</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default FootballDashboard;