package com.example.backend.konto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record KontoRequest(
        Long uzytkownikId,
        String numerKonta,
        BigDecimal saldo,
        String waluta,
        LocalDateTime dataUtworzenia
) {
}