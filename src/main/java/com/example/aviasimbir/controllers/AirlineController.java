package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.AirlineResponse;
import com.example.aviasimbir.requestresponse.CreateAirlineRequest;
import com.example.aviasimbir.service.AirlineService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/airline")
public class AirlineController {
    private final AirlineService airlineService;

    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation("Получение информации об авиалинии")
    public ResponseEntity<AirlineResponse> getAirline(@PathVariable("id") Long id) throws NoSuchIdException {
        Airline airline = airlineService.getAirline(id);
        AirlineResponse airlineResponse = new AirlineResponse(airline.getId(), airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("Создать авиалинию")
    public ResponseEntity<AirlineResponse> createAirline(@RequestBody CreateAirlineRequest createAirlineRequest) throws WrongArgumentException {
        Airline airline = airlineService.createAirline(createAirlineRequest.getName());
        AirlineResponse airlineResponse = new AirlineResponse(airline.getId(), airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }
}
