package com.example.backend.transakcja;

import com.example.backend.transakcja.dto.TransakcjaRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transakcje")
public class TransakcjaController {

    private final TransakcjaRepository transakcjaRepository;
    private final TransakcjaService transakcjaService;

    public TransakcjaController(
            TransakcjaRepository transakcjaRepository,
            TransakcjaService transakcjaService
    ) {
        this.transakcjaRepository = transakcjaRepository;
        this.transakcjaService = transakcjaService;
    }

    @GetMapping
    public List<Transakcja> getTransakcje() {
        return transakcjaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Transakcja getTransakcja(@PathVariable Long id) {
        return transakcjaRepository
                .findById(id)
                .orElseThrow();
    }

    @PostMapping
    public Transakcja createTransakcja(
            @RequestBody TransakcjaRequest request
    ) {
        return transakcjaService.wykonajTransakcje(request);
    }

    @DeleteMapping("/{id}")
    public void deleteTransakcja(@PathVariable Long id) {
        transakcjaRepository.deleteById(id);
    }
}