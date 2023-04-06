package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.requestresponse.CreatePlaneRequest;
import com.example.aviasimbir.requestresponse.PlaneResponse;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.PlaneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/plane")
public class PlaneController {
    private final PlaneService planeService;
    private final AirlineService airlineService;
    private final FlightService flightService;

    public PlaneController(PlaneService planeService, AirlineService airlineService, FlightService flightService) {
        this.planeService = planeService;
        this.airlineService = airlineService;
        this.flightService = flightService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PlaneResponse> getPlane(@PathVariable("id") Long id) {
        Optional<Plane> plane = planeService.getPlane(id);
        if (plane.isPresent()) {
            PlaneResponse getPlaneResponse = new PlaneResponse(plane.get().getBrand(), plane.get().getModel(),
                    plane.get().getSeats(), plane.get().getAirline().getName());
            return ResponseEntity.ok(getPlaneResponse);
        } else return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PlaneResponse> createPlane(@RequestBody CreatePlaneRequest createPlaneRequest) {
        Plane plane = planeService.createPlane(createPlaneRequest.getBrand(), createPlaneRequest.getModel(),
                createPlaneRequest.getSeats(), airlineService.getAirline(createPlaneRequest.getAirline()).get());
        PlaneResponse planeResponse = new PlaneResponse(plane.getBrand(), plane.getModel(), plane.getSeats(), plane.getAirline().getName());
        return ResponseEntity.ok(planeResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deletePlane(@PathVariable("id") Long id) {
        flightService.setPlaneFieldToNull(id);
        planeService.deletePlane(id);
    }
}
