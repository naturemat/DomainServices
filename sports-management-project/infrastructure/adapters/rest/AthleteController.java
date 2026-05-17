package infrastructure.adapters.rest;

import application.dto.RegisterAthleteRequest;
import application.dto.RegisterAthleteResponse;
import application.usecase.RegisterAthleteUseCase;
import application.usecase.SearchAthletesUseCase;
import domain.model.entities.Athlete;
import domain.ports.out.AthleteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/athletes")
public class AthleteController {

    private final RegisterAthleteUseCase registerAthleteUseCase;
    private final SearchAthletesUseCase searchAthletesUseCase;
    private final AthleteRepository athleteRepository;

    public AthleteController(RegisterAthleteUseCase registerAthleteUseCase,
                           SearchAthletesUseCase searchAthletesUseCase,
                           AthleteRepository athleteRepository) {
        this.registerAthleteUseCase = registerAthleteUseCase;
        this.searchAthletesUseCase = searchAthletesUseCase;
        this.athleteRepository = athleteRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Athlete>> searchAthletes(@RequestParam String name) {
        return ResponseEntity.ok(searchAthletesUseCase.execute(name));
    }

    @GetMapping
    public ResponseEntity<List<Athlete>> getAllAthletes() {
        return ResponseEntity.ok(athleteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Athlete> getAthleteById(@PathVariable java.util.UUID id) {
        return athleteRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RegisterAthleteResponse> registerAthlete(
            @Valid @RequestBody RegisterAthleteRequest request) {
        RegisterAthleteResponse response = registerAthleteUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}