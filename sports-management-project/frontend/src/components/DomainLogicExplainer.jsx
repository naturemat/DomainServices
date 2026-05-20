import './DomainExplainer.css';

const DomainServiceFlow = ({ session, sportType }) => {
  if (!session) return null;

  const intensityValues = { LOW: 1, MODERATE: 1.5, HIGH: 2, EXTREME: 2.5 };
  const intensity = session.intensity || 'MODERATE';
  const duration = session.durationMinutes || 60;
  const basePoints = Math.floor(duration / 10);
  const multiplier = intensityValues[intensity];
  const totalPoints = basePoints * multiplier;
  const fatigueLevel = session.fatigueLevel || 'MEDIUM';
  
  const getFatigueThreshold = (level) => {
    if (level === 'HIGH') return '30+ points';
    if (level === 'MEDIUM') return '15-29 points';
    return '0-14 points';
  };

  const sportIcon = sportType === 'FOOTBALL' ? '[F]' : '[G]';

  return (
    <div className="domain-flow-container">
      <div className="flow-header">
        <span className="sport-icon">{sportType === 'FOOTBALL' ? 'Football' : 'Gym'}</span>
        <h3>How Your Training Result Was Calculated</h3>
      </div>
      
      <div className="flow-step step-1">
        <div className="step-badge">1</div>
        <div className="step-content">
          <h4>Session Data Input</h4>
          <div className="input-data">
            <div className="data-item">
              <span className="data-label">Duration:</span>
              <span className="data-value">{duration} minutes</span>
            </div>
            <div className="data-item">
              <span className="data-label">Intensity:</span>
              <span className="data-value intensity-tag">{intensity}</span>
            </div>
          </div>
        </div>
      </div>

      <div className="flow-arrow">↓</div>

      <div className="flow-step step-2">
        <div className="step-badge">2</div>
        <div className="step-content">
          <h4>FatigueCalculationService</h4>
          <p className="service-desc">Calculates fatigue based on intensity × duration</p>
          <div className="calculation-display">
            <div className="calc-line">
              <span>{duration} min ÷ 10 = {basePoints} base points</span>
            </div>
            <div className="calc-line">
              <span>× {intensity} intensity multiplier ({multiplier}x)</span>
            </div>
            <div className="calc-line result">
              <span>= <strong>{totalPoints} fatigue points</strong></span>
            </div>
          </div>
        </div>
      </div>

      <div className="flow-arrow">↓</div>

      <div className="flow-step step-3">
        <div className="step-badge">3</div>
        <div className="step-content">
          <h4>Determine Fatigue Level</h4>
          <div className="threshold-display">
            <div className="threshold-bar">
              <div className="threshold-segment low">LOW (0-14)</div>
              <div className="threshold-segment medium">MEDIUM (15-29)</div>
              <div className="threshold-segment high">HIGH (30+)</div>
              <div className={`threshold-marker ${fatigueLevel.toLowerCase()}`}>
                {totalPoints} pts
              </div>
            </div>
            <p className="result-text">
              Your score: <strong>{totalPoints}</strong> → <span className={`level-${fatigueLevel.toLowerCase()}`}>{fatigueLevel}</span>
            </p>
          </div>
        </div>
      </div>

      <div className="flow-arrow">↓</div>

      <div className="flow-step step-4">
        <div className="step-badge">4</div>
        <div className="step-content">
          <h4>RoutineRecommendationService</h4>
          <p className="service-desc">Recommends routine based on fatigue + sport type</p>
          <div className="routine-decision">
            <div className="decision-path">
              <span className="path-label">Fatigue Level:</span>
              <span className="path-value">{fatigueLevel}</span>
            </div>
            <div className="decision-path">
              <span className="path-label">Sport Type:</span>
              <span className="path-value">{sportType}</span>
            </div>
            <div className="decision-path arrow">→</div>
            <div className="decision-path result">
              <span className="path-label">Recommended:</span>
              <span className="path-value">{session.recommendedRoutine?.name || 'N/A'}</span>
            </div>
          </div>
        </div>
      </div>

      <div className="flow-arrow">↓</div>

      <div className="flow-step step-5">
        <div className="step-badge">5</div>
        <div className="step-content">
          <h4>RecoverySuggestionService</h4>
          <p className="service-desc">Suggests recovery based on fatigue level</p>
          <div className="recovery-display">
            <div className={`recovery-card ${session.recoverySuggestion?.toLowerCase() || 'medium'}`}>
              <div className="recovery-icon">
                {session.recoverySuggestion === 'ABSOLUTE_REST' && '😴'}
                {session.recoverySuggestion === 'LIGHT_ACTIVITY' && '🚶'}
                {session.recoverySuggestion === 'ACTIVE_RECOVERY' && '🧘'}
                {session.recoverySuggestion === 'MODERATE_WORKOUT' && '💪'}
                {session.recoverySuggestion === 'INCREASE_INTENSITY' && '🔥'}
              </div>
              <div className="recovery-text">
                <strong>{session.recoverySuggestion?.replace(/_/g, ' ')}</strong>
              </div>
            </div>
            <div className="recovery-explanation">
              {session.recoverySuggestion === 'ABSOLUTE_REST' && 'Your body needs complete rest. No training today.'}
              {session.recoverySuggestion === 'LIGHT_ACTIVITY' && 'Light activities only - walking, stretching.'}
              {session.recoverySuggestion === 'ACTIVE_RECOVERY' && 'Low intensity movement to aid recovery.'}
              {session.recoverySuggestion === 'MODERATE_WORKOUT' && 'Maintain your current training level.'}
              {session.recoverySuggestion === 'INCREASE_INTENSITY' && 'You can handle more intensity - push yourself!'}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const SimpleFatigueExplanation = ({ session }) => {
  if (!session) return null;

  const intensityValues = { LOW: 1, MODERATE: 1.5, HIGH: 2, EXTREME: 2.5 };
  const basePoints = Math.floor((session.durationMinutes || 60) / 10);
  const totalPoints = basePoints * (intensityValues[session.intensity] || 1);

  return (
    <div className="fatigue-simple">
      <h4>Fatigue Calculation</h4>
      <div className="simple-formula">
        <span className="formula">{session.durationMinutes} ÷ 10 × {session.intensity === 'LOW' ? '1' : session.intensity === 'MODERATE' ? '1.5' : session.intensity === 'HIGH' ? '2' : '2.5'}</span>
        <span className="equals">=</span>
        <span className="result">{totalPoints} points</span>
      </div>
      <div className="threshold-info">
        <span className={session.fatigueLevel === 'LOW' ? 'active' : ''}>Low: 0-14</span>
        <span className={session.fatigueLevel === 'MEDIUM' ? 'active' : ''}>Medium: 15-29</span>
        <span className={session.fatigueLevel === 'HIGH' ? 'active' : ''}>High: 30+</span>
      </div>
    </div>
  );
};

const RoutineExplanationCard = ({ routine, sportType }) => {
  if (!routine) return null;

  return (
    <div className="routine-card">
      <h4>Routine Recommendation Logic</h4>
      <div className="logic-flow">
        <div className="logic-step">
          <span className="logic-label">Based on your</span>
          <span className="logic-value">{sportType} + {routine.recoverySuggestion?.replace(/_/g, ' ') || 'fatigue'}</span>
        </div>
        <div className="logic-arrow">↓</div>
        <div className="logic-step recommendation">
          <span className="logic-label">We recommend:</span>
          <span className="routine-name">{routine.name}</span>
        </div>
        <div className="routine-details">
          <div className="detail">
            <span>📅 Duration:</span>
            <span>{routine.recommendedDurationMinutes} min</span>
          </div>
          <div className="detail">
            <span>💪 Intensity:</span>
            <span>{routine.recommendedIntensity}</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export { DomainServiceFlow, SimpleFatigueExplanation, RoutineExplanationCard };