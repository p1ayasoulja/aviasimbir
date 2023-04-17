package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("select (sum (t.price)) / count (t) from Ticket t where t.sold = true and t.commission = true")
    BigDecimal getAverageTicketPrice();

    @Query("select (sum (t.price)) from Ticket t where t.sold = true")
    BigDecimal getTotalEarnedSum();

    @Query("select t from Ticket t where t.flight = ?1 and t.reserved = false ")
    List<Ticket> getTicketByFlightAndNotReserved(Flight flight);

    @Query("select count (t) from Ticket t where t.flight.departure = ?1 and t.sold =  true")
    Long getTicketsByFlightDepartureAndSold(String destination);

    @Query("select t from Ticket t join t.flight f join f.plane p join p.airline a where a.id = ?1 and t.sold = ?2")
    List<Ticket> getAllTicketsByAirlineAndSold(Long airlineId, Boolean sold);

    @Query("select count(t) from Ticket t where t.sold = true")
    Long countBySold();
}
