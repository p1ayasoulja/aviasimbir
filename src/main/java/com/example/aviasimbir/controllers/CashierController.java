package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.service.TicketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/cashier")
public class CashierController {
    private final TicketService ticketService;

    public CashierController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @RequestMapping(value = "/{id}/sell", method = RequestMethod.PUT)
    @ApiOperation("Продать билет")
    public ResponseEntity<Object> sellTicket(@PathVariable("id") Long id) {
        Optional<Ticket> ticket = ticketService.findTicket(id);
        if (ticket.isPresent()) {
            ticketService.sellTicket(ticket.get());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}/cancelreservation", method = RequestMethod.PUT)
    @ApiOperation("Снять бронь с билета")
    public ResponseEntity<Object> cancelReservationTicket(@PathVariable("id") Long id) {
        Optional<Ticket> ticket = ticketService.findTicket(id);
        if (ticket.isPresent()) {
            ticketService.cancelTicketReserve(ticket.get());
            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }
}
