package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepo extends JpaRepository<Ticket, Long> {
}
