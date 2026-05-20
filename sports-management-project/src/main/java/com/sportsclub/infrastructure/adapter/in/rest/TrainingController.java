package com.sportsclub.infrastructure.adapter.in.rest;

import com.sportsclub.training.application.dto.request.RegisterSessionRequest;
import com.sportsclub.training.application.dto.response.RegisterSessionResponse;
import com.sportsclub.training.application.dto.response.RoutineResponse;
import com.sportsclub.training.application.usecase.GenerateRoutineUseCase;
import com.sportsclub.training.application.usecase.RegisterTrainingSessionUseCase;
import com.sportsclub.training.application.usecase.SearchSessionsUseCase;
import com.sportsclub.training.domain.model.entity.TrainingSession;
import com.sportsclub.training.domain.port.out.TrainingSessionRepository;
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

    public TrainingController(RegisterTrainingSessionUseCase registerTrainingSessionUseCase, GenerateRoutineUseCase generateRoutineUseCase, SearchSessionsUseCase searchSessionsUseCase, TrainingSessionRepository trainingSessionRepository) {
        this.registerTrainingSessionUseCase = registerTrainingSessionUseCase; this.generateRoutineUseCase = generateRoutineUseCase; this.searchSessionsUseCase = searchSessionsUseCase; this.trainingSessionRepository = trainingSessionRepository;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<TrainingSession>> getAllSessions() { return ResponseEntity.ok(trainingSessionRepository.findAll()); }

    @GetMapping("/sessions/search")
    public ResponseEntity<List<TrainingSession>> searchSessions(@RequestParam String athleteName) { return ResponseEntity.ok(searchSessionsUseCase.executeByAthleteName(athleteName)); }

    @GetMapping("/sessions/by-athlete/{athleteId}")
    public ResponseEntity<List<TrainingSession>> getSessionsByAthlete(@PathVariable UUID athleteId) { return ResponseEntity.ok(searchSessionsUseCase.executeByAthleteId(athleteId)); }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() { return ResponseEntity.ok("pong"); }

    @PostMapping("/sessions")
    public ResponseEntity<RegisterSessionResponse> registerSession(@Valid @RequestBody RegisterSessionRequest request) {
        RegisterSessionResponse response = registerTrainingSessionUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/routines/{athleteId}")
    public ResponseEntity<RoutineResponse> generateRoutine(@PathVariable UUID athleteId) { return ResponseEntity.ok(generateRoutineUseCase.execute(athleteId)); }
}