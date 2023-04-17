package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.CreatePlaneRequest;
import com.example.aviasimbir.requestresponse.PlaneResponse;
import com.example.aviasimbir.service.PlaneService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plane")
public class PlaneController {
    private final PlaneService planeService;

    public PlaneController(PlaneService planeService) {
        this.planeService = planeService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation("Получить информацию о самолете")
    public ResponseEntity<PlaneResponse> getPlane(@PathVariable("id") Long id) throws NoSuchIdException {
        Plane plane = planeService.getPlane(id);
        PlaneResponse getPlaneResponse = new PlaneResponse(plane.getId(), plane.getBrand(), plane.getModel(),
                plane.getSeats(), plane.getAirline().getName());
        return ResponseEntity.ok(getPlaneResponse);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("Создать самолет")
    public ResponseEntity<PlaneResponse> createPlane(@RequestBody CreatePlaneRequest createPlaneRequest) throws NoSuchIdException, WrongArgumentException {
        Plane plane = planeService.createPlane(createPlaneRequest.getBrand(), createPlaneRequest.getModel(),
                createPlaneRequest.getSeats(), createPlaneRequest.getAirlineId());
        PlaneResponse planeResponse = new PlaneResponse(plane.getId(), plane.getBrand(), plane.getModel(), plane.getSeats(), plane.getAirline().getName());
        return ResponseEntity.ok(planeResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Удалить самолет")
    public void deletePlane(@PathVariable("id") Long id) throws NoSuchIdException {
        planeService.deletePlane(id);
    }
}
