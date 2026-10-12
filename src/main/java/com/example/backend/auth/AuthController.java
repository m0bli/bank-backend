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

        if (request == null ||
                !authService.zaloguj(
                        request.login(),
                        request.haslo())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            false,
                            "Nieprawidłowy login lub hasło"
                    ));
        }

        return ResponseEntity.ok(
                new LoginResponse(true, "Zalogowano pomyślnie")
        );
    }
}
