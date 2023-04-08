package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.service.TicketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @RequestMapping(value = "/averagecommission", method = RequestMethod.GET)
    @ApiOperation("Получить среднюю коммиссию по проданным билетам")
    public ResponseEntity<BigDecimal> getAverageCommission() {
        return ResponseEntity.ok(ticketService.getAverageCommissionOfSoldTickets());
    }

    @RequestMapping(value = "/kazan", method = RequestMethod.GET)
    @ApiOperation("Получить число проданных билетов с точкой отправления Казань")
    public ResponseEntity<Long> getSoldTicketsFromKazanCount() {
        return ResponseEntity.ok(ticketService.getTicketsFromKazanCount());
    }
    @RequestMapping(value = "/{id}/reserve", method = RequestMethod.PUT)
    @ApiOperation("Забронировать билет")
    public ResponseEntity<Object> reserveTicket(@PathVariable("id") Long id) {
        Optional<Ticket> ticket = ticketService.findTicket(id);
        if (ticket.isPresent()) {
            ticketService.reserveTicket(ticket.get());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }
}
