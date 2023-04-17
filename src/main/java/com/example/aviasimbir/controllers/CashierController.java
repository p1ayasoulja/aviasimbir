package com.example.aviasimbir.controllers;

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
        ticketService.sellTicket(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}/cancelreservation", method = RequestMethod.PUT)
    @ApiOperation("Снять бронь с билета")
    public ResponseEntity<Object> cancelTicketReservation(@PathVariable("id") Long id) throws NoSuchIdException {
        ticketService.cancelTicketReserve(id);
        return ResponseEntity.ok().build();
    }
}
