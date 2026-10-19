package com.example.backend.transakcja;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransakcjaRepository extends JpaRepository<Transakcja, Long> {
    List<Transakcja> findByKontoZrodlowe_IdOrKontoDocelowe_IdOrderByDataOperacjiDesc(
            Long kontoZrodloweId,
            Long kontoDoceloweId
    );
}
