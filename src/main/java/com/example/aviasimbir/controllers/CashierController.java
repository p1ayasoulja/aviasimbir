package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.PlaneAlreadyLeftException;
import com.example.aviasimbir.exceptions.TicketSoldException;
import com.example.aviasimbir.service.TicketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cashier")
public class CashierController {
    private final TicketService ticketService;

    public CashierController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @RequestMapping(value = "/{id}/sell", method = RequestMethod.PUT)
    @ApiOperation("Продать билет")
    public ResponseEntity<Object> sellTicket(@PathVariable("id") Long id) throws NoSuchIdException, TicketSoldException, PlaneAlreadyLeftException {
        Ticket ticket = ticketService.getTicket(id);
        ticketService.sellTicket(ticket);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/cancelreservation", method = RequestMethod.PUT)
    @ApiOperation("Снять бронь с билета")
    public ResponseEntity<Object> cancelReservationTicket(@PathVariable("id") Long id) throws NoSuchIdException {
        Ticket ticket = ticketService.getTicket(id);
        ticketService.cancelTicketReserve(ticket);
        return ResponseEntity.ok().build();
    }
}
