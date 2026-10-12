package com.example.backend.konto;

import com.example.backend.konto.dto.KontoRequest;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.springframework.web.bind.annotation.*;

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
                .orElseThrow();
    }

    @PostMapping
    public Konto createKonto(@RequestBody KontoRequest request) {

        User user = userRepository.findById(request.uzytkownikId())
                .orElseThrow();

        Konto konto = new Konto();

        konto.setUzytkownik(user);
        konto.setNumerKonta(request.numerKonta());
        konto.setSaldo(request.saldo());
        konto.setWaluta(request.waluta());
        konto.setDataUtworzenia(request.dataUtworzenia());

        return kontoRepository.save(konto);
    }

    @DeleteMapping("/{id}")
    public void deleteKonto(@PathVariable Long id) {
        kontoRepository.deleteById(id);
    }
}