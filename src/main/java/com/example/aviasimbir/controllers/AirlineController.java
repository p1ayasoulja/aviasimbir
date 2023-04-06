package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.requestresponse.AirlineResponse;
import com.example.aviasimbir.requestresponse.CreateAirlineRequest;
import com.example.aviasimbir.requestresponse.UpdateAirlineRequest;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.PlaneService;
import com.example.aviasimbir.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            AirlineResponse airlineResponse = new AirlineResponse(airline.get().getName());
            return ResponseEntity.ok(airlineResponse);
        } else return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AirlineResponse> createAirline(@RequestBody CreateAirlineRequest createAirlineRequest) {
        Airline airline = airlineService.createAirline(createAirlineRequest.getName());
        AirlineResponse airlineResponse = new AirlineResponse(airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AirlineResponse> updateAirline(@PathVariable("id") Long id,
                                                         @RequestBody UpdateAirlineRequest updateAirlineRequest) {
        Optional<Airline> airline = airlineService.getAirline(id);
        if (airline.isPresent()) {
            airlineService.updateAirline(id, updateAirlineRequest.getName());
            AirlineResponse airlineResponse = new AirlineResponse(airline.get().getName());
            return ResponseEntity.ok(airlineResponse);
        } else return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAirline(@PathVariable("id") Long id) {
        List<Plane> plane = planeService.getAllPlanes();
        plane.forEach(plane1 -> {
            if (plane1.getAirline().getId().equals(id)) {
                plane1.setAirline(null);
            }
        });
        airlineService.deleteAirline(id);
    }

    @RequestMapping(value = "/{id}/planes", method = RequestMethod.GET)
    public ResponseEntity<String> getNumberOfPlanes(@PathVariable("id") Long id) {
        Optional<Airline> airline = airlineService.getAirline(id);
        return airline.map(value -> ResponseEntity.ok("Number of planes of this airline = " + planeService.getPlanesCount(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //TODO ANALYZE!
    @RequestMapping(value = "/{id}/sold", method = RequestMethod.GET)
    public ResponseEntity<String> getNumberOfSoldTickets(@PathVariable("id") Long id) {
        Optional<Airline> airline = airlineService.getAirline(id);
        int totalSoldTickets = 0;
        if (airline.isPresent()) {
            List<Plane> planes = planeService.getListOfPlanes(airline.get());
            for (Plane plane : planes) {
                List<Flight> flights = flightService.getFlightsByPlane(plane);
                for (Flight flight : flights) {
                    totalSoldTickets += ticketService.getSoldTicketCount(flight);
                }
            }
        }
        return ResponseEntity.ok("Total tickets sold : " + totalSoldTickets);
    }
}
