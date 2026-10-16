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

    public TransakcjaService(TransakcjaRepository transakcjaRepository, KontoRepository kontoRepository) {
        this.transakcjaRepository = transakcjaRepository;
        this.kontoRepository = kontoRepository;
    }

    @Transactional
    public Transakcja wykonajTransakcje(TransakcjaRequest request) {
        if (request == null) throw new IllegalArgumentException("Dane przelewu są wymagane");
        if (request.getTyp() != TypTransakcji.PRZELEW) {
            throw new IllegalArgumentException("Ten endpoint obsługuje wyłącznie przelewy");
        }
        if (request.getUzytkownikId() == null || request.getUzytkownikId() <= 0) {
            throw new IllegalArgumentException("Użytkownik jest wymagany");
        }
        if (request.getKontoZrodloweId() == null || request.getKontoZrodloweId() <= 0) {
            throw new IllegalArgumentException("Konto źródłowe jest wymagane");
        }

        BigDecimal kwota = request.getKwota();
        if (kwota == null || kwota.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Kwota musi być większa od 0");
        }
        if (kwota.scale() > 2) {
            throw new IllegalArgumentException("Kwota może mieć maksymalnie 2 miejsca po przecinku");
        }

        String waluta = request.getWaluta();
        if (waluta == null || !waluta.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Nieprawidłowa waluta. Wybierz np. PLN");
        }
        String tytul = request.getTytul() == null ? "" : request.getTytul().trim();
        if (tytul.isEmpty()) throw new IllegalArgumentException("Tytuł przelewu jest wymagany");
        if (tytul.length() > 140) throw new IllegalArgumentException("Tytuł przelewu może mieć maksymalnie 140 znaków");

        String numerOdbiorcy = normalizujNumerKonta(request.getNumerKontaOdbiorcy());
        String imieOdbiorcy = request.getImieOdbiorcy() == null ? "" : request.getImieOdbiorcy().trim();
        if (imieOdbiorcy.isEmpty()) throw new IllegalArgumentException("Imię odbiorcy jest wymagane");

        Konto znalezioneKontoDocelowe = kontoRepository.findByNumerKonta(numerOdbiorcy)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono konta odbiorcy"));
        Long zrodloId = request.getKontoZrodloweId();
        Long celId = znalezioneKontoDocelowe.getId();
        if (zrodloId.equals(celId)) throw new IllegalArgumentException("Nie można wykonać przelewu na to samo konto");

        // Blokuj oba rachunki w stałej kolejności, aby ograniczyć deadlocki.
        Konto pierwsze;
        Konto drugie;
        if (zrodloId < celId) {
            pierwsze = pobierzDoBlokady(zrodloId);
            drugie = pobierzDoBlokady(celId);
        } else {
            pierwsze = pobierzDoBlokady(celId);
            drugie = pobierzDoBlokady(zrodloId);
        }
        Konto kontoZrodlowe = zrodloId.equals(pierwsze.getId()) ? pierwsze : drugie;
        Konto kontoDocelowe = celId.equals(pierwsze.getId()) ? pierwsze : drugie;

        // To sprawdzenie nie zastępuje uwierzytelniania tokenem; obecny login zwraca samo ID.
        if (kontoZrodlowe.getUzytkownik() == null
                || !request.getUzytkownikId().equals(kontoZrodlowe.getUzytkownik().getId())) {
            throw new IllegalArgumentException("Konto źródłowe nie należy do tego użytkownika");
        }
        if (kontoDocelowe.getUzytkownik() == null
                || kontoDocelowe.getUzytkownik().getImie() == null
                || !kontoDocelowe.getUzytkownik().getImie().trim().equalsIgnoreCase(imieOdbiorcy)) {
            throw new IllegalArgumentException("Imię odbiorcy nie zgadza się z właścicielem konta");
        }
        sprawdzWalute(kontoZrodlowe, waluta);
        sprawdzWalute(kontoDocelowe, waluta);
        if (!kontoZrodlowe.getWaluta().equals(kontoDocelowe.getWaluta())) {
            throw new IllegalArgumentException("Konta mają różne waluty. Przelewy między różnymi walutami są niedostępne");
        }
        if (kontoZrodlowe.getSaldo() == null || kontoZrodlowe.getSaldo().compareTo(kwota) < 0) {
            throw new IllegalArgumentException("Brak wystarczających środków na koncie");
        }

        // Salda i zapis transakcji są jedną operacją DB dzięki @Transactional.
        kontoZrodlowe.setSaldo(kontoZrodlowe.getSaldo().subtract(kwota));
        kontoDocelowe.setSaldo(kontoDocelowe.getSaldo().add(kwota));

        Transakcja transakcja = new Transakcja();
        transakcja.setKontoZrodlowe(kontoZrodlowe);
        transakcja.setKontoDocelowe(kontoDocelowe);
        transakcja.setTyp(TypTransakcji.PRZELEW);
        transakcja.setKwota(kwota);
        transakcja.setWaluta(waluta);
        transakcja.setTytul(tytul);
        transakcja.setDataOperacji(LocalDateTime.now());
        return transakcjaRepository.save(transakcja);
    }

    private String normalizujNumerKonta(String numer) {
        if (numer == null) throw new IllegalArgumentException("Numer konta odbiorcy jest wymagany");
        String wynik = numer.replaceAll("\\s+", "");
        if (!wynik.matches("\\d{26}")) {
            throw new IllegalArgumentException("Numer konta musi zawierać 26 cyfr");
        }
        return wynik;
    }

    private Konto pobierzDoBlokady(Long id) {
        return kontoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono konta"));
    }

    private void sprawdzWalute(Konto konto, String waluta) {
        if (konto.getWaluta() == null || !konto.getWaluta().equals(waluta)) {
            throw new IllegalArgumentException("Wybrana waluta musi być zgodna z walutą obu kont");
        }
    }
}