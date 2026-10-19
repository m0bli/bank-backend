package com.example.backend.transakcja.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransakcjaHistoriaResponse(
        Long id,
        Long kontoId,
        String kierunek,
        String numerKontaKontrahenta,
        String typ,
        BigDecimal kwota,
        String waluta,
        String tytul,
        LocalDateTime dataOperacji
) {
}
