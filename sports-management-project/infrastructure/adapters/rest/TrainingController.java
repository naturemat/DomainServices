package infrastructure.adapters.rest;

import application.dto.RegisterSessionRequest;
import application.dto.RegisterSessionResponse;
import application.dto.RoutineResponse;
import application.usecase.GenerateRoutineUseCase;
import application.usecase.RegisterTrainingSessionUseCase;
import application.usecase.SearchSessionsUseCase;
import domain.model.entities.TrainingSession;
import domain.ports.out.TrainingSessionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

    private final RegisterTrainingSessionUseCase registerTrainingSessionUseCase;
    private final GenerateRoutineUseCase generateRoutineUseCase;
    private final SearchSessionsUseCase searchSessionsUseCase;
    private final TrainingSessionRepository trainingSessionRepository;

    public TrainingController(
            RegisterTrainingSessionUseCase registerTrainingSessionUseCase,
            GenerateRoutineUseCase generateRoutineUseCase,
            SearchSessionsUseCase searchSessionsUseCase,
            TrainingSessionRepository trainingSessionRepository) {
        this.registerTrainingSessionUseCase = registerTrainingSessionUseCase;
        this.generateRoutineUseCase = generateRoutineUseCase;
        this.searchSessionsUseCase = searchSessionsUseCase;
        this.trainingSessionRepository = trainingSessionRepository;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<TrainingSession>> getAllSessions() {
        return ResponseEntity.ok(trainingSessionRepository.findAll());
    }

    @GetMapping("/sessions/search")
    public ResponseEntity<List<TrainingSession>> searchSessions(@RequestParam String athleteName) {
        return ResponseEntity.ok(searchSessionsUseCase.executeByAthleteName(athleteName));
    }

    @GetMapping("/sessions/by-athlete/{athleteId}")
    public ResponseEntity<List<TrainingSession>> getSessionsByAthlete(@PathVariable UUID athleteId) {
        return ResponseEntity.ok(searchSessionsUseCase.executeByAthleteId(athleteId));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @PostMapping("/sessions")
    public ResponseEntity<RegisterSessionResponse> registerSession(
            @Valid @RequestBody RegisterSessionRequest request) {
        RegisterSessionResponse response = registerTrainingSessionUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/routines/{athleteId}")
    public ResponseEntity<RoutineResponse> generateRoutine(@PathVariable UUID athleteId) {
        RoutineResponse response = generateRoutineUseCase.execute(athleteId);
        return ResponseEntity.ok(response);
    }
}