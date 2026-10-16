package com.example.backend.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        Long uzytkownikId = request == null
                ? null
                : authService.zaloguj(
                        request.login(),
                        request.haslo()
                );

        if (uzytkownikId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            false,
                            "Nieprawidłowy login lub hasło",
                            null
                    ));
        }

        return ResponseEntity.ok(
                new LoginResponse(
                        true,
                        "Zalogowano pomyślnie",
                        uzytkownikId
                )
        );
    }
}
