package com.example.backend.transakcja;

import com.example.backend.transakcja.dto.TransakcjaHistoriaResponse;
import com.example.backend.transakcja.dto.TransakcjaRequest;
import com.example.backend.transakcja.dto.TransakcjaResponse;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/konto/{kontoId}")
    public List<TransakcjaHistoriaResponse> getHistoriaKonta(@PathVariable Long kontoId) {
        return transakcjaService.historiaKonta(kontoId);
    }

    @GetMapping("/{id}")
    public TransakcjaResponse getTransakcja(@PathVariable Long id) {
        Transakcja transakcja = transakcjaRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Nie znaleziono transakcji"));
        return TransakcjaResponse.from(transakcja);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransakcjaResponse createTransakcja(@RequestBody TransakcjaRequest request) {
        return TransakcjaResponse.from(transakcjaService.wykonajTransakcje(request));
    }
}
