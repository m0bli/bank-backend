package com.example.backend.transakcja.dto;

import com.example.backend.transakcja.TypTransakcji;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransakcjaRequest {
    private Long uzytkownikId;
    private Long kontoZrodloweId;
    private String numerKontaOdbiorcy;
    private String imieOdbiorcy;
    private TypTransakcji typ;
    private BigDecimal kwota;
    private String waluta;
    private String tytul;
    // Zachowane dla zgodności wstecznej; backend nie ufa dacie klienta.
    private LocalDateTime dataOperacji;

    public Long getUzytkownikId() { return uzytkownikId; }
    public void setUzytkownikId(Long uzytkownikId) { this.uzytkownikId = uzytkownikId; }
    public Long getKontoZrodloweId() { return kontoZrodloweId; }
    public void setKontoZrodloweId(Long kontoZrodloweId) { this.kontoZrodloweId = kontoZrodloweId; }
    public String getNumerKontaOdbiorcy() { return numerKontaOdbiorcy; }
    public void setNumerKontaOdbiorcy(String numerKontaOdbiorcy) { this.numerKontaOdbiorcy = numerKontaOdbiorcy; }
    public String getImieOdbiorcy() { return imieOdbiorcy; }
    public void setImieOdbiorcy(String imieOdbiorcy) { this.imieOdbiorcy = imieOdbiorcy; }
    public TypTransakcji getTyp() { return typ; }
    public void setTyp(TypTransakcji typ) { this.typ = typ; }
    public BigDecimal getKwota() { return kwota; }
    public void setKwota(BigDecimal kwota) { this.kwota = kwota; }
    public String getWaluta() { return waluta; }
    public void setWaluta(String waluta) { this.waluta = waluta; }
    public String getTytul() { return tytul; }
    public void setTytul(String tytul) { this.tytul = tytul; }
    public LocalDateTime getDataOperacji() { return dataOperacji; }
    public void setDataOperacji(LocalDateTime dataOperacji) { this.dataOperacji = dataOperacji; }
}