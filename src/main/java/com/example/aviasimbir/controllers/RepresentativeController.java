package com.example.aviasimbir.controllers;

import com.example.aviasimbir.Jwt.JwtUser;
import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.NotRepresentativeException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.*;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.PlaneService;
import com.example.aviasimbir.service.TicketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/representative/{id}")
public class RepresentativeController {
    private final PlaneService planeService;
    private final AirlineService airlineService;
    private final TicketService ticketService;

    public RepresentativeController(PlaneService planeService, AirlineService airlineService,
                                    TicketService ticketService) {
        this.planeService = planeService;
        this.airlineService = airlineService;
        this.ticketService = ticketService;

    }

    @RequestMapping(value = "/planes", method = RequestMethod.GET)
    @ApiOperation("Получить число самолетов авиалинии")
    public ResponseEntity<GetNumberOfPlanesResponse> getNumberOfPlanes(@PathVariable("id") Long id,
                                                                       @AuthenticationPrincipal JwtUser jwtUser) throws NotRepresentativeException {
        String username = jwtUser.getUsername();
        long planesCount = planeService.getAirlinePlanesCount(id, username);
        GetNumberOfPlanesResponse getNumberOfPlanesResponse = new GetNumberOfPlanesResponse(planesCount);
        return ResponseEntity.ok(getNumberOfPlanesResponse);
    }

    @RequestMapping(value = "/sold", method = RequestMethod.GET)
    @ApiOperation("Получить число проданных билетов авиалинии")
    public ResponseEntity<GetNumberOfSoldTicketsResponse> getNumberOfSoldTickets(@PathVariable("id") Long id, @AuthenticationPrincipal JwtUser jwtUser) throws NotRepresentativeException {
        String username = jwtUser.getUsername();
        long totalSoldTickets = airlineService.getTotalSoldTickets(id, username);
        GetNumberOfSoldTicketsResponse getNumberOfSoldTicketsResponse = new GetNumberOfSoldTicketsResponse(totalSoldTickets);
        return ResponseEntity.ok(getNumberOfSoldTicketsResponse);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation("Обновить информацию об авиалинии")
    public ResponseEntity<AirlineResponse> updateAirline(@PathVariable("id") Long id, @AuthenticationPrincipal JwtUser jwtUser,
                                                         @RequestBody UpdateAirlineRequest updateAirlineRequest) throws WrongArgumentException, NoSuchIdException, NotRepresentativeException {
        String username = jwtUser.getUsername();
        Airline airline = airlineService.updateAirline(id, updateAirlineRequest.getName(), username);
        AirlineResponse airlineResponse = new AirlineResponse(airline.getId(), airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }

    @RequestMapping(value = "/totalEarned", method = RequestMethod.GET)
    @ApiOperation("Подсчитать заработанную сумму")
    public ResponseEntity<GetTotalEarnedResponse> getTotalEarned(@PathVariable("id") Long id, @AuthenticationPrincipal JwtUser jwtUser) throws NotRepresentativeException {
        String username = jwtUser.getUsername();
        GetTotalEarnedResponse getNumberOfPlanesResponse =
                new GetTotalEarnedResponse(airlineService.getTotalEarnedByAirline(id, username));
        return ResponseEntity.ok(getNumberOfPlanesResponse);
    }

    @RequestMapping(value = "/tickets", method = RequestMethod.GET)
    @ApiOperation("Получить список всех билетов или список непроданных билетов в зависимости от запроса")
    public ResponseEntity<List<GetAllTicketsResponse>> getTickets(@PathVariable("id") Long id, @AuthenticationPrincipal JwtUser jwtUser,
                                                                  @RequestBody GetAllTicketsRequest getAllTicketsRequest) throws NotRepresentativeException, NoSuchIdException {
        String username = jwtUser.getUsername();
        List<Ticket> tickets = ticketService.getTicketsByAirline(id, getAllTicketsRequest.getSold(), username);
        List<GetAllTicketsResponse> getTicketResponses = tickets.stream()
                .map(ticket -> new GetAllTicketsResponse(ticket.getId(), ticket.getPrice(), ticket.getCommission())).collect(Collectors.toList());
        return ResponseEntity.ok(getTicketResponses);
    }

    @RequestMapping(value = "/plane", method = RequestMethod.POST)
    @ApiOperation("Создать самолет")
    public ResponseEntity<PlaneResponse> createPlane(@RequestBody CreatePlaneRequest createPlaneRequest,
                                                     @AuthenticationPrincipal JwtUser jwtUser,
                                                     @PathVariable("id") Long id) throws NoSuchIdException, WrongArgumentException, NotRepresentativeException {
        String username = jwtUser.getUsername();
        Plane plane = planeService.createPlane(username, id, createPlaneRequest.getBrand(), createPlaneRequest.getModel(),
                createPlaneRequest.getSeats(), createPlaneRequest.getAirlineId());
        PlaneResponse planeResponse = new PlaneResponse(plane.getId(), plane.getBrand(), plane.getModel(), plane.getSeats(), plane.getAirline().getName());
        return ResponseEntity.ok(planeResponse);
    }

    @RequestMapping(value = "/{id}/plane/{planeId}", method = RequestMethod.DELETE)
    @ApiOperation("Удалить самолет")
    public void deletePlane(@PathVariable("planeId") Long planeId,
                            @AuthenticationPrincipal JwtUser jwtUser,
                            @PathVariable("id") Long id) throws NoSuchIdException, NotRepresentativeException {
        String username = jwtUser.getUsername();
        planeService.deletePlane(username, id, planeId);
    }
}
