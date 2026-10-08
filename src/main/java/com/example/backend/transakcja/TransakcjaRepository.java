package com.example.backend.transakcja;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransakcjaRepository extends JpaRepository<Transakcja, Long> {
}