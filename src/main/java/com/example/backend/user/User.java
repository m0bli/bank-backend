package com.example.backend.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "uzytkownicy")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imie;

    private String nazwisko;

    private String login;

    private String email;

    @Column(name = "haslo_hash")
    private String hasloHash;

    @Column(name = "data_rejestracji")
    private LocalDateTime dataRejestracji;

    public User() {
    }

    public User(String imie, String nazwisko, String login, String email,
                String hasloHash, LocalDateTime dataRejestracji) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.email = email;
        this.hasloHash = hasloHash;
        this.dataRejestracji = dataRejestracji;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHasloHash() {
        return hasloHash;
    }

    public void setHasloHash(String hasloHash) {
        this.hasloHash = hasloHash;
    }

    public LocalDateTime getDataRejestracji() {
        return dataRejestracji;
    }

    public void setDataRejestracji(LocalDateTime dataRejestracji) {
        this.dataRejestracji = dataRejestracji;
    }
}