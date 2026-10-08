package com.example.backend.konto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KontoRepository extends JpaRepository<Konto, Long> {
}
// TODO: Nie dziala odejmowanie i dodwania pienidzy przelewem
// TODO: całe transakcje do naprawy