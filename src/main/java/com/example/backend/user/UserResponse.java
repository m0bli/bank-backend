package com.example.backend.user;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String imie,
        String nazwisko,
        String login,
        String email,
        LocalDateTime dataRejestracji
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getImie(),
                user.getNazwisko(),
                user.getLogin(),
                user.getEmail(),
                user.getDataRejestracji()
        );
    }
}
