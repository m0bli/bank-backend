package com.example.backend.transakcja;

import com.example.backend.konto.Konto;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transakcje")
public class Transakcja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "konto_zrodlowe_id")
    private Konto kontoZrodlowe;

    @ManyToOne
    @JoinColumn(name = "konto_docelowe_id")
    private Konto kontoDocelowe;

    @Enumerated(EnumType.STRING)
    @Column(name = "typ", nullable = false)
    private TypTransakcji typ;

    @Column(name = "kwota", nullable = false, precision = 15, scale = 2)
    private BigDecimal kwota;

    @Column(name = "waluta", nullable = false, length = 3)
    private String waluta;

    @Column(name = "tytul", nullable = false, length = 140)
    private String tytul;

    @Column(name = "data_operacji", nullable = false)
    private LocalDateTime dataOperacji;

    public Transakcja() {
    }

    public Long getId() {
        return id;
    }

    public Konto getKontoZrodlowe() {
        return kontoZrodlowe;
    }

    public void setKontoZrodlowe(Konto kontoZrodlowe) {
        this.kontoZrodlowe = kontoZrodlowe;
    }

    public Konto getKontoDocelowe() {
        return kontoDocelowe;
    }

    public void setKontoDocelowe(Konto kontoDocelowe) {
        this.kontoDocelowe = kontoDocelowe;
    }

    public TypTransakcji getTyp() {
        return typ;
    }

    public void setTyp(TypTransakcji typ) {
        this.typ = typ;
    }

    public BigDecimal getKwota() {
        return kwota;
    }

    public void setKwota(BigDecimal kwota) {
        this.kwota = kwota;
    }

    public String getWaluta() {
        return waluta;
    }

    public void setWaluta(String waluta) {
        this.waluta = waluta;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }

    public LocalDateTime getDataOperacji() {
        return dataOperacji;
    }

    public void setDataOperacji(LocalDateTime dataOperacji) {
        this.dataOperacji = dataOperacji;
    }
}