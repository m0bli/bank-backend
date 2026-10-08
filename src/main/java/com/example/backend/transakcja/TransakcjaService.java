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
    public Transakcja wykonajTransakcje(TransakcjaRequest request) {

        if (request.getKwota() == null ||
                request.getKwota().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Kwota musi być większa od 0"
            );
        }

        Transakcja transakcja = new Transakcja();

        transakcja.setTyp(request.getTyp());
        transakcja.setKwota(request.getKwota());
        transakcja.setWaluta(request.getWaluta());
        transakcja.setTytul(request.getTytul());

        if (request.getDataOperacji() != null) {
            transakcja.setDataOperacji(request.getDataOperacji());
        } else {
            transakcja.setDataOperacji(LocalDateTime.now());
        }

        switch (request.getTyp()) {

            case PRZELEW -> wykonajPrzelew(request, transakcja);

            case WPLATA -> wykonajWplate(request, transakcja);

            case WYPLATA -> wykonajWyplate(request, transakcja);
        }

        return transakcjaRepository.save(transakcja);
    }

    private void wykonajPrzelew(
            TransakcjaRequest request,
            Transakcja transakcja
    ) {

        if (request.getKontoZrodloweId() == null ||
                request.getKontoDoceloweId() == null) {

            throw new IllegalArgumentException(
                    "Przelew wymaga konta źródłowego i docelowego"
            );
        }

        if (request.getKontoZrodloweId()
                .equals(request.getKontoDoceloweId())) {

            throw new IllegalArgumentException(
                    "Konto źródłowe i docelowe nie mogą być takie same"
            );
        }

        Konto kontoZrodlowe = kontoRepository
                .findById(request.getKontoZrodloweId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Nie znaleziono konta źródłowego"
                        )
                );

        Konto kontoDocelowe = kontoRepository
                .findById(request.getKontoDoceloweId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Nie znaleziono konta docelowego"
                        )
                );

        sprawdzWalute(kontoZrodlowe, request.getWaluta());
        sprawdzWalute(kontoDocelowe, request.getWaluta());

        sprawdzSaldo(
                kontoZrodlowe,
                request.getKwota()
        );

        // ODEJMUJEMY z konta źródłowego
        kontoZrodlowe.setSaldo(
                kontoZrodlowe.getSaldo()
                        .subtract(request.getKwota())
        );

        // DODAJEMY na konto docelowe
        kontoDocelowe.setSaldo(
                kontoDocelowe.getSaldo()
                        .add(request.getKwota())
        );

        kontoRepository.save(kontoZrodlowe);
        kontoRepository.save(kontoDocelowe);

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

        Konto kontoDocelowe = kontoRepository
                .findById(request.getKontoDoceloweId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Nie znaleziono konta docelowego"
                        )
                );

        sprawdzWalute(kontoDocelowe, request.getWaluta());

        // DODAJEMY pieniądze
        kontoDocelowe.setSaldo(
                kontoDocelowe.getSaldo()
                        .add(request.getKwota())
        );

        kontoRepository.save(kontoDocelowe);

        transakcja.setKontoDocelowe(kontoDocelowe);
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

        Konto kontoZrodlowe = kontoRepository
                .findById(request.getKontoZrodloweId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Nie znaleziono konta źródłowego"
                        )
                );

        sprawdzWalute(kontoZrodlowe, request.getWaluta());

        sprawdzSaldo(
                kontoZrodlowe,
                request.getKwota()
        );

        // ODEJMUJEMY pieniądze
        kontoZrodlowe.setSaldo(
                kontoZrodlowe.getSaldo()
                        .subtract(request.getKwota())
        );

        kontoRepository.save(kontoZrodlowe);

        transakcja.setKontoZrodlowe(kontoZrodlowe);
    }

    private void sprawdzSaldo(
            Konto konto,
            BigDecimal kwota
    ) {

        if (konto.getSaldo().compareTo(kwota) < 0) {
            throw new IllegalArgumentException(
                    "Brak wystarczających środków na koncie"
            );
        }
    }

    private void sprawdzWalute(
            Konto konto,
            String waluta
    ) {

        if (!konto.getWaluta().equals(waluta)) {
            throw new IllegalArgumentException(
                    "Waluta transakcji nie zgadza się z walutą konta"
            );
        }
    }
}