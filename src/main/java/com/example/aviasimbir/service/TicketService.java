package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.repo.TicketRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepo ticketRepo;

    public TicketService(TicketRepo ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    public Optional<Ticket> get(Long id) {
        return ticketRepo.findById(id);
    }

    public List<Ticket> getAll() {
        return ticketRepo.findAll();
    }

    public Ticket create(Flight flight, Integer price, Boolean reserved, Boolean sold, Boolean commission) {
        Integer priceUpdated = price;
        if (commission) {
            priceUpdated = (int) (priceUpdated * 1.025);
        }
        Ticket ticket = new Ticket(flight, priceUpdated, reserved, sold, commission);
        ticketRepo.save(ticket);
        return ticket;
    }

    public Optional<Ticket> update(Long id, Integer price, boolean reserved, boolean sold) {
        Optional<Ticket> ticket = ticketRepo.findById(id);
        if (ticket.isPresent()) {
            if (price > 0) {
                ticket.get().setPrice(price);
            }
            if (!sold) {
                ticket.get().setReserved(reserved);
            } else {
                ticket.get().setSold(true);
            }
            ticketRepo.save(ticket.get());
            return ticket;
        } else return Optional.empty();
    }

    public void delete(Long id) {
        ticketRepo.deleteById(id);
    }

}
