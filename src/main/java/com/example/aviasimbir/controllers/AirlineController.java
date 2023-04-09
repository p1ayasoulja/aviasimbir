package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.requestresponse.AirlineResponse;
import com.example.aviasimbir.requestresponse.CreateAirlineRequest;
import com.example.aviasimbir.requestresponse.UpdateAirlineRequest;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.PlaneService;
import com.example.aviasimbir.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/airline")
public class AirlineController {
    private final AirlineService airlineService;
    private final PlaneService planeService;
    private final TicketService ticketService;
    private final FlightService flightService;

    public AirlineController(AirlineService airlineService, PlaneService planeService,
                             TicketService ticketService, FlightService flightService) {
        this.airlineService = airlineService;
        this.planeService = planeService;
        this.ticketService = ticketService;
        this.flightService = flightService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<AirlineResponse> getAirline(@PathVariable("id") Long id) {
        Optional<Airline> airline = airlineService.getAirline(id);
        if (airline.isPresent()) {
            AirlineResponse airlineResponse = new AirlineResponse(airline.get().getId(), airline.get().getName());
            return ResponseEntity.ok(airlineResponse);
        } else return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AirlineResponse> createAirline(@RequestBody CreateAirlineRequest createAirlineRequest) {
        Airline airline = airlineService.createAirline(createAirlineRequest.getName());
        AirlineResponse airlineResponse = new AirlineResponse(airline.getId(), airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AirlineResponse> updateAirline(@PathVariable("id") Long id,
                                                         @RequestBody UpdateAirlineRequest updateAirlineRequest) {
        Optional<Airline> airline = airlineService.updateAirline(id, updateAirlineRequest.getName());
        AirlineResponse airlineResponse = new AirlineResponse(airline.get().getId(), airline.get().getName());
        return ResponseEntity.ok(airlineResponse);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAirline(@PathVariable("id") Long id) {
        airlineService.deleteAirline(id);
    }

    @RequestMapping(value = "/{id}/planes", method = RequestMethod.GET)
    public ResponseEntity<Long> getNumberOfPlanes(@PathVariable("id") Long id) {
        return ResponseEntity.ok(planeService.getPlanesCountByAirlineId(id));
    }

    @GetMapping("/{id}/sold")
    public ResponseEntity<Long> getNumberOfSoldTickets(@PathVariable Long id) {
        long totalSoldTickets = airlineService.getAirline(id)
                .map(airline -> ticketService.getAllSoldTicketsCountByFlights(
                        flightService.getFlightsByPlane(planeService.getListOfPlanes(airline))))
                .orElse(0L);
        return ResponseEntity.ok(totalSoldTickets);
    }
}
