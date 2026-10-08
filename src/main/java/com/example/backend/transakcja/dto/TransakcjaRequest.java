package com.example.backend.transakcja.dto;

import com.example.backend.transakcja.TypTransakcji;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransakcjaRequest {

    private Long kontoZrodloweId;

    private Long kontoDoceloweId;

    private TypTransakcji typ;

    private BigDecimal kwota;

    private String waluta;

    private String tytul;

    private LocalDateTime dataOperacji;

    public Long getKontoZrodloweId() {
        return kontoZrodloweId;
    }

    public void setKontoZrodloweId(Long kontoZrodloweId) {
        this.kontoZrodloweId = kontoZrodloweId;
    }

    public Long getKontoDoceloweId() {
        return kontoDoceloweId;
    }

    public void setKontoDoceloweId(Long kontoDoceloweId) {
        this.kontoDoceloweId = kontoDoceloweId;
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