package com.example.backend.user;

import java.time.LocalDateTime;

public record UserRequest(
        String imie,
        String nazwisko,
        String login,
        String email,
        String haslo,
        LocalDateTime dataRejestracji
) {
}
