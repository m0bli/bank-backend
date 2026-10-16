package com.example.backend.konto;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KontoRepository
        extends JpaRepository<Konto, Long> {

    boolean existsByNumerKonta(String numerKonta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM Konto k WHERE k.id = :id")
    Optional<Konto> findByIdForUpdate(@Param("id") Long id);
}