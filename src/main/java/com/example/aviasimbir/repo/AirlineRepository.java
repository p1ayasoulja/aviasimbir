package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
    @Query("SELECT COUNT(t) FROM Ticket t JOIN t.flight f JOIN f.plane p JOIN p.airline a WHERE a.id = ?1 AND t.sold = true")
    Long getTotalSoldTicketsByAirline(Long id);

    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t JOIN t.flight f JOIN f.plane p JOIN p.airline a WHERE a.id = ?1 AND t.sold = true")
    BigDecimal getTotalEarnedByAirline(Long id);
}
