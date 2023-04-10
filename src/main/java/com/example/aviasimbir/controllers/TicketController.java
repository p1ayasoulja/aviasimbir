package com.example.aviasimbir.controllers;

import com.example.aviasimbir.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @RequestMapping(value = "/averagecommission", method = RequestMethod.GET)
    public ResponseEntity<BigDecimal> getAverageCommission() {
        return ResponseEntity.ok(ticketService.getAverageCommissionOfSoldTickets());
    }

    @RequestMapping(value = "/kazan", method = RequestMethod.GET)
    public ResponseEntity<Long> getSoldTicketsFromKazanCount() {
        return ResponseEntity.ok(ticketService.getTicketsFromKazanCount());
    }
}
