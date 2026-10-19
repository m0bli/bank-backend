package com.example.backend.transakcja.dto;

import com.example.backend.transakcja.Transakcja;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransakcjaResponse(
        Long id,
        Long kontoZrodloweId,
        Long kontoDoceloweId,
        String typ,
        BigDecimal kwota,
        String waluta,
        String tytul,
        LocalDateTime dataOperacji
) {
    public static TransakcjaResponse from(Transakcja transakcja) {
        return new TransakcjaResponse(
                transakcja.getId(),
                transakcja.getKontoZrodlowe().getId(),
                transakcja.getKontoDocelowe().getId(),
                transakcja.getTyp().name(),
                transakcja.getKwota(),
                transakcja.getWaluta(),
                transakcja.getTytul(),
                transakcja.getDataOperacji()
        );
    }
}
