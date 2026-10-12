package com.example.backend.konto;

import com.example.backend.konto.dto.KontoRequest;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/konta")
public class KontoController {

    private final KontoRepository kontoRepository;
    private final UserRepository userRepository;

    public KontoController(
            KontoRepository kontoRepository,
            UserRepository userRepository
    ) {
        this.kontoRepository = kontoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Konto> getKonta() {
        return kontoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Konto getKonto(@PathVariable Long id) {
        return kontoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Nie znaleziono konta"
                        )
                );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Konto createKonto(
            @RequestBody KontoRequest request
    ) {
        if (request.uzytkownikId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Użytkownik jest wymagany"
            );
        }

        if (request.numerKonta() == null ||
                request.numerKonta().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Numer konta jest wymagany"
            );
        }

        if (request.waluta() == null ||
                !request.waluta().matches("[A-Z]{3}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Waluta musi mieć format np. PLN"
            );
        }

        User user = userRepository.findById(
                request.uzytkownikId()
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nie znaleziono użytkownika"
                )
        );

        Konto konto = new Konto();

        konto.setUzytkownik(user);
        konto.setNumerKonta(request.numerKonta().trim());
        konto.setSaldo(BigDecimal.ZERO.setScale(2));
        konto.setWaluta(request.waluta());
        konto.setDataUtworzenia(LocalDateTime.now());

        return kontoRepository.save(konto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKonto(@PathVariable Long id) {
        Konto konto = kontoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Nie znaleziono konta"
                        )
                );

        kontoRepository.delete(konto);
    }
}