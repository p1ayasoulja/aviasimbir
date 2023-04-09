package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.requestresponse.*;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.PlaneService;
import com.example.aviasimbir.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/flight")
public class FlightController {
    private final FlightService flightService;
    private final TicketService ticketService;
    private final PlaneService planeService;

    public FlightController(FlightService flightService, TicketService ticketService, PlaneService planeService) {
        this.flightService = flightService;
        this.ticketService = ticketService;
        this.planeService = planeService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<FlightResponse> getFlight(@PathVariable("id") Long id) {
        Optional<Flight> flight = flightService.getFlight(id);
        if (flight.isPresent()) {
            FlightResponse flightResponse = new FlightResponse(flight.get().getId(), flight.get().getPlane().getAirline().getName(), flight.get().getDeparture(),
                    flight.get().getDestination(), flight.get().getDepartureTime(), flight.get().getArrivalTime());
            return ResponseEntity.ok(flightResponse);
        } else return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<FlightResponse> createFlight(@RequestBody CreateFlightRequest createFlightRequest) {
        Flight flight = flightService.createFlight(planeService.findPlane(createFlightRequest.getPlaneId()).get(), createFlightRequest.getDeparture(),
                createFlightRequest.getDestination(), createFlightRequest.getDepartureTime(),
                createFlightRequest.getArrivalTime());
        ticketService.createTicketsForCreatedFlight(flight.getId(), createFlightRequest.getCommission(), flight.getPlane().getSeats(), createFlightRequest.getTicketPrice());
        FlightResponse flightResponse = new FlightResponse(flight.getId(), flight.getPlane().getAirline().getName(), flight.getDeparture(),
                flight.getDestination(), flight.getDepartureTime(), flight.getArrivalTime());
        return ResponseEntity.ok(flightResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<FlightResponse> updateFlight(@PathVariable("id") Long id, @RequestBody UpdateFlightRequest updateFlightRequest) {
        Optional<Flight> flight = flightService.updateFlight(id, updateFlightRequest.getDepartureTime(), updateFlightRequest.getArrivalTime(),
                planeService.findPlane(updateFlightRequest.getPlaneId()).get(),
                updateFlightRequest.getDeparture(), updateFlightRequest.getDestination());

        FlightResponse flightResponse = new FlightResponse(flight.get().getId(), flight.get().getPlane().getAirline().getName(),
                flight.get().getDeparture(), flight.get().getDestination(), flight.get().getDepartureTime(),
                flight.get().getArrivalTime());
        return ResponseEntity.ok(flightResponse);
    }

    @RequestMapping(value = "/{id}/addticket", method = RequestMethod.POST)
    public ResponseEntity<TicketResponse> createTicketForFlight(@PathVariable("id") Long id, @RequestBody CreateTicketRequest createTickerRequest) {
        Ticket ticket = ticketService.createTicket(flightService.getFlight(id).get(), createTickerRequest.getPrice(),
                false, false, createTickerRequest.getCommission());
        TicketResponse ticketResponse = new TicketResponse(ticket.getFlight().getDeparture(), ticket.getFlight().getDestination(),
                ticket.getPrice(), ticket.getReserved(), ticket.getSold());
        return ResponseEntity.ok(ticketResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    //todo удаляется не то как и deletePlane
    public void deleteFlight(@PathVariable("id") Long id) {
        flightService.deleteFlight(id);
    }
}
