package com.example.backend.konto;

import java.math.BigDecimal;

public record SaldoResponse(BigDecimal saldo, String waluta) {
}
