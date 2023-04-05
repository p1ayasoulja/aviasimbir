package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.requestresponse.AirlineResponse;
import com.example.aviasimbir.requestresponse.CreateAirlineRequest;
import com.example.aviasimbir.requestresponse.UpdateAirlineRequest;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.PlaneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/airline")
public class AirlineController {
    private final AirlineService airlineService;
    private final PlaneService planeService;

    public AirlineController(AirlineService airlineService, PlaneService planeService) {
        this.airlineService = airlineService;
        this.planeService = planeService;
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
        Airline airline = airlineService.create(createAirlineRequest.getName());
        AirlineResponse airlineResponse = new AirlineResponse(airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<AirlineResponse> updateAirline(@PathVariable("id") Long id,
                                                         @RequestBody UpdateAirlineRequest updateAirlineRequest) {
        Optional<Airline> airline = airlineService.getAirline(id);
        if (airline.isPresent()) {
            airlineService.update(id, updateAirlineRequest.getName());
            AirlineResponse airlineResponse = new AirlineResponse(airline.get().getName());
            return ResponseEntity.ok(airlineResponse);
        } else return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteAirline(@PathVariable("id") Long id) {
        List<Plane> plane = planeService.getAll();
        plane.forEach(plane1 -> {
            if (plane1.getAirline().getId().equals(id)) {
                plane1.setAirline(null);
            }
        });
        airlineService.delete(id);
    }

}
