package com.example.backend.konto;

import java.math.BigDecimal;

public record SaldoResponse(Long kontoId, BigDecimal saldo, String waluta) {
}
