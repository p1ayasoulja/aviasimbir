package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {
    List<Ticket> findByFlight(Flight flight);
}
