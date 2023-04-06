package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.requestresponse.CreateFlightRequest;
import com.example.aviasimbir.requestresponse.FlightResponse;
import com.example.aviasimbir.requestresponse.TicketResponse;
import com.example.aviasimbir.requestresponse.UpdateFlightRequest;
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
        Optional<Flight> flight = flightService.get(id);
        if (flight.isPresent()) {
            FlightResponse flightResponse = new FlightResponse(flight.get().getPlane().getAirline().getName(), flight.get().getDeparture(),
                    flight.get().getDestination(), flight.get().getDepartureTime(), flight.get().getArrivalTime());
            return ResponseEntity.ok(flightResponse);
        } else return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<FlightResponse> createFlight(@RequestBody CreateFlightRequest createFlightRequest) {
        Flight flight = flightService.create(planeService.get(createFlightRequest.getPlane_id()).get(), createFlightRequest.getDeparture(),
                createFlightRequest.getDestination(), createFlightRequest.getDepartureTime(),
                createFlightRequest.getArrivalTime());
        Integer price = createFlightRequest.getTicket_price();
        if (createFlightRequest.getCommission()) {
            price = (int) (price * 1.025);
        }
        for (int i = 0; i < flight.getPlane().getSeats() / 2; i++) {
            ticketService.create(flight, price,
                    false, false, createFlightRequest.getCommission());
        }
        FlightResponse flightResponse = new FlightResponse(flight.getPlane().getAirline().getName(), flight.getDeparture(),
                flight.getDestination(), flight.getDepartureTime(), flight.getArrivalTime());
        return ResponseEntity.ok(flightResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<FlightResponse> updateFlight(@PathVariable("id") Long id, @RequestBody UpdateFlightRequest updateFlightRequest) {
        Optional<Flight> flight = flightService.get(id);
        if (flight.isPresent()) {
            flightService.update(id, updateFlightRequest.getDepartureTime(), updateFlightRequest.getArrivalTime(),
                    planeService.get(updateFlightRequest.getPlane_id()).get(),
                    updateFlightRequest.getDeparture(), updateFlightRequest.getDestination());

            FlightResponse flightResponse = new FlightResponse(flight.get().getPlane().getAirline().getName(),
                    flight.get().getDeparture(), flight.get().getDestination(), flight.get().getDepartureTime(),
                    flight.get().getArrivalTime());
            return ResponseEntity.ok(flightResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteFlight(@PathVariable("id") Long id) {
        flightService.delete(id);
    }
}
