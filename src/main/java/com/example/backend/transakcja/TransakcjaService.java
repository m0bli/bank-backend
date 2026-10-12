package com.example.backend.transakcja;

import com.example.backend.konto.Konto;
import com.example.backend.konto.KontoRepository;
import com.example.backend.transakcja.dto.TransakcjaRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransakcjaService {

    private final TransakcjaRepository transakcjaRepository;
    private final KontoRepository kontoRepository;

    public TransakcjaService(
            TransakcjaRepository transakcjaRepository,
            KontoRepository kontoRepository
    ) {
        this.transakcjaRepository = transakcjaRepository;
        this.kontoRepository = kontoRepository;
    }

    @Transactional
    public Transakcja wykonajTransakcje(
            TransakcjaRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Dane transakcji są wymagane"
            );
        }

        if (request.getTyp() == null) {
            throw new IllegalArgumentException(
                    "Typ transakcji jest wymagany"
            );
        }

        BigDecimal kwota = request.getKwota();

        if (kwota == null ||
                kwota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Kwota musi być większa od 0"
            );
        }

        if (kwota.scale() > 2) {
            throw new IllegalArgumentException(
                    "Kwota może mieć maksymalnie 2 miejsca po przecinku"
            );
        }

        String waluta = request.getWaluta();

        if (waluta == null ||
                !waluta.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Nieprawidłowy format waluty"
            );
        }

        Transakcja transakcja = new Transakcja();

        transakcja.setTyp(request.getTyp());
        transakcja.setKwota(kwota);
        transakcja.setWaluta(waluta);
        transakcja.setTytul(request.getTytul());

        // Data operacji jest ustalana przez serwer.
        transakcja.setDataOperacji(LocalDateTime.now());

        switch (request.getTyp()) {
            case PRZELEW ->
                    wykonajPrzelew(request, transakcja);

            case WPLATA ->
                    wykonajWplate(request, transakcja);

            case WYPLATA ->
                    wykonajWyplate(request, transakcja);
        }

        return transakcjaRepository.save(transakcja);
    }

    private void wykonajPrzelew(
            TransakcjaRequest request,
            Transakcja transakcja
    ) {
        Long zrodloId = request.getKontoZrodloweId();
        Long celId = request.getKontoDoceloweId();

        if (zrodloId == null || celId == null) {
            throw new IllegalArgumentException(
                    "Przelew wymaga konta źródłowego i docelowego"
            );
        }

        if (zrodloId.equals(celId)) {
            throw new IllegalArgumentException(
                    "Konta źródłowe i docelowe nie mogą być takie same"
            );
        }

        // Blokujemy konta zawsze w kolejności rosnącego ID.
        Konto pierwsze;
        Konto drugie;

        if (zrodloId < celId) {
            pierwsze = pobierzDoBlokady(zrodloId);
            drugie = pobierzDoBlokady(celId);
        } else {
            pierwsze = pobierzDoBlokady(celId);
            drugie = pobierzDoBlokady(zrodloId);
        }

        Konto kontoZrodlowe =
                zrodloId.equals(pierwsze.getId())
                        ? pierwsze : drugie;

        Konto kontoDocelowe =
                celId.equals(pierwsze.getId())
                        ? pierwsze : drugie;

        sprawdzWalute(kontoZrodlowe, request.getWaluta());
        sprawdzWalute(kontoDocelowe, request.getWaluta());
        sprawdzSaldo(kontoZrodlowe, request.getKwota());

        kontoZrodlowe.setSaldo(
                kontoZrodlowe.getSaldo()
                        .subtract(request.getKwota())
        );

        kontoDocelowe.setSaldo(
                kontoDocelowe.getSaldo()
                        .add(request.getKwota())
        );

        // Encje są zarządzane przez JPA w tej transakcji.
        transakcja.setKontoZrodlowe(kontoZrodlowe);
        transakcja.setKontoDocelowe(kontoDocelowe);
    }

    private void wykonajWplate(
            TransakcjaRequest request,
            Transakcja transakcja
    ) {
        if (request.getKontoDoceloweId() == null) {
            throw new IllegalArgumentException(
                    "Wpłata wymaga konta docelowego"
            );
        }

        if (request.getKontoZrodloweId() != null) {
            throw new IllegalArgumentException(
                    "Wpłata nie może mieć konta źródłowego"
            );
        }

        Konto konto = pobierzDoBlokady(
                request.getKontoDoceloweId()
        );

        sprawdzWalute(konto, request.getWaluta());

        konto.setSaldo(
                konto.getSaldo().add(request.getKwota())
        );

        transakcja.setKontoDocelowe(konto);
    }

    private void wykonajWyplate(
            TransakcjaRequest request,
            Transakcja transakcja
    ) {
        if (request.getKontoZrodloweId() == null) {
            throw new IllegalArgumentException(
                    "Wypłata wymaga konta źródłowego"
            );
        }

        if (request.getKontoDoceloweId() != null) {
            throw new IllegalArgumentException(
                    "Wypłata nie może mieć konta docelowego"
            );
        }

        Konto konto = pobierzDoBlokady(
                request.getKontoZrodloweId()
        );

        sprawdzWalute(konto, request.getWaluta());
        sprawdzSaldo(konto, request.getKwota());

        konto.setSaldo(
                konto.getSaldo().subtract(request.getKwota())
        );

        transakcja.setKontoZrodlowe(konto);
    }

    private Konto pobierzDoBlokady(Long id) {
        return kontoRepository.findByIdForUpdate(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Nie znaleziono konta o ID: " + id
                        )
                );
    }

    private void sprawdzSaldo(
            Konto konto,
            BigDecimal kwota
    ) {
        if (konto.getSaldo() == null ||
                konto.getSaldo().compareTo(kwota) < 0) {
            throw new IllegalArgumentException(
                    "Brak wystarczających środków na koncie"
            );
        }
    }

    private void sprawdzWalute(
            Konto konto,
            String waluta
    ) {
        if (konto.getWaluta() == null ||
                !konto.getWaluta().equals(waluta)) {
            throw new IllegalArgumentException(
                    "Waluta transakcji nie zgadza się z walutą konta"
            );
        }
    }
}