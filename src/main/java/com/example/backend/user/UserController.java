package com.example.backend.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/uzytkownicy")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nie znaleziono użytkownika"
                ));

        return UserResponse.from(user);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody UserRequest request) {

        if (request.login() == null || request.login().isBlank()
                || request.haslo() == null || request.haslo().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Login i hasło są wymagane"
            );
        }

        User user = new User();
        user.setImie(request.imie());
        user.setNazwisko(request.nazwisko());
        user.setLogin(request.login());
        user.setEmail(request.email());

        user.setHasloHash(
                passwordEncoder.encode(request.haslo())
        );

        user.setDataRejestracji(
                request.dataRejestracji() != null
                        ? request.dataRejestracji()
                        : LocalDateTime.now()
        );

        User savedUser = userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.from(savedUser));
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nie znaleziono użytkownika"
                ));

        existingUser.setImie(request.imie());
        existingUser.setNazwisko(request.nazwisko());
        existingUser.setLogin(request.login());
        existingUser.setEmail(request.email());

        // Brak hasła w żądaniu oznacza pozostawienie
        // dotychczasowego hasła bez zmian.
        if (request.haslo() != null && !request.haslo().isBlank()) {
            existingUser.setHasloHash(
                    passwordEncoder.encode(request.haslo())
            );
        }

        User savedUser = userRepository.save(existingUser);

        return UserResponse.from(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nie znaleziono użytkownika"
            );
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
