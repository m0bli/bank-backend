package com.example.backend.konto;

import com.example.backend.user.User;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class KontoService {

    private static final String WALUTA_DOMYSLNA = "PLN";
    private static final BigInteger MODULO_97 = BigInteger.valueOf(97);

    private final KontoRepository kontoRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public KontoService(KontoRepository kontoRepository) {
        this.kontoRepository = kontoRepository;
    }

    public Konto utworzKontoDla(User user) {
        Konto konto = new Konto();
        konto.setUzytkownik(user);
        konto.setNumerKonta(wygenerujUnikalnyNumerKonta());
        konto.setSaldo(new BigDecimal("0.00"));
        konto.setWaluta(WALUTA_DOMYSLNA);
        konto.setDataUtworzenia(LocalDateTime.now());

        return kontoRepository.save(konto);
    }

    private String wygenerujUnikalnyNumerKonta() {
        // Polski NRB: 2 cyfry kontrolne i 24 cyfry numeru BBAN.
        for (int proba = 0; proba < 20; proba++) {
            StringBuilder bban = new StringBuilder(24);

            for (int i = 0; i < 24; i++) {
                bban.append(secureRandom.nextInt(10));
            }

            String numerBban = bban.toString();
            String doKontroli = numerBban + "252100";
            int reszta = new BigInteger(doKontroli)
                    .mod(MODULO_97)
                    .intValue();
            int cyfryKontrolne = 98 - reszta;

            String numerNrb = String.format(
                    "%02d%s",
                    cyfryKontrolne,
                    numerBban
            );

            if (!kontoRepository.existsByNumerKonta(numerNrb)) {
                return numerNrb;
            }
        }

        throw new IllegalStateException(
                "Nie udało się wygenerować unikalnego numeru konta"
        );
    }
}
