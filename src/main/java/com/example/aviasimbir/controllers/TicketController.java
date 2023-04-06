package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.requestresponse.CreateTicketRequest;
import com.example.aviasimbir.requestresponse.TicketResponse;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.PlaneService;
import com.example.aviasimbir.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final FlightService flightService;
    private final TicketService ticketService;
    private final PlaneService planeService;

    public TicketController(FlightService flightService, TicketService ticketService, PlaneService planeService) {
        this.flightService = flightService;
        this.ticketService = ticketService;
        this.planeService = planeService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<TicketResponse> createTicket(@PathVariable("id") Long id, @RequestBody CreateTicketRequest createTickerRequest) {
        Ticket ticket = ticketService.createTicket(flightService.getFlight(id).get(), createTickerRequest.getPrice(),
                false, false, createTickerRequest.getCommission());
        TicketResponse ticketResponse = new TicketResponse(ticket.getFlight().getDeparture(), ticket.getFlight().getDestination(),
                ticket.getPrice(), ticket.getReserved(), ticket.getSold());
        return ResponseEntity.ok(ticketResponse);
    }

    @RequestMapping(value = "/avgcommission", method = RequestMethod.GET)
    public ResponseEntity<String> avgCommission() {
        return ResponseEntity.ok("Average Commission = " + ticketService.getAverageCommissionOfSoldTickets());

    }
}
