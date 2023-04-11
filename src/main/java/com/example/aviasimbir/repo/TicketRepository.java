package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByFlight(Flight flight);

    @Query("select t from Ticket t where t.sold = true")
    List<Ticket> findBySold();
}
