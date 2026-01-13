package com.example.demo.repository;

import com.example.demo.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.numeroQuarto = :numeroQuarto
        AND r.status <> 'CANCELADO'
        AND r.checkin < :checkout
        AND r.checkout > :checkin
    """)
    List<Reserva> verificarConflito(
            Integer numeroQuarto,
            LocalDate checkin,
            LocalDate checkout
    );
}
