package infrastructure.adapters.rest;

import application.dto.RegisterSessionRequest;
import application.dto.RegisterSessionResponse;
import application.dto.RoutineResponse;
import application.usecase.GenerateRoutineUseCase;
import application.usecase.RegisterTrainingSessionUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

    private final RegisterTrainingSessionUseCase registerTrainingSessionUseCase;
    private final GenerateRoutineUseCase generateRoutineUseCase;

    public TrainingController(
            RegisterTrainingSessionUseCase registerTrainingSessionUseCase,
            GenerateRoutineUseCase generateRoutineUseCase) {
        this.registerTrainingSessionUseCase = registerTrainingSessionUseCase;
        this.generateRoutineUseCase = generateRoutineUseCase;
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