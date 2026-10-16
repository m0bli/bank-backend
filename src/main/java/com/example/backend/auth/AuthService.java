package com.example.backend.auth;

import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long zaloguj(String login, String haslo) {
        if (login == null || haslo == null) {
            return null;
        }

        User user = userRepository.findByLogin(login)
                .orElse(null);

        if (user == null || user.getHasloHash() == null) {
            return null;
        }

        boolean poprawneHaslo = passwordEncoder.matches(
                haslo,
                user.getHasloHash()
        );

        return poprawneHaslo ? user.getId() : null;
    }
}
