package com.example.backend.konto;

import java.math.BigDecimal;

public record KontoResponse(
        Long id,
        String numerKonta,
        BigDecimal saldo,
        String waluta
) {
    public static KontoResponse from(Konto konto) {
        return new KontoResponse(
                konto.getId(),
                konto.getNumerKonta(),
                konto.getSaldo(),
                konto.getWaluta()
        );
    }
}
