package com.example.aviasimbir.controllers;

import com.example.aviasimbir.Jwt.JwtTokenProvider;
import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.NotRepresentativeException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.AirlineResponse;
import com.example.aviasimbir.requestresponse.GetAllTicketsRequest;
import com.example.aviasimbir.requestresponse.GetAllTicketsResponse;
import com.example.aviasimbir.requestresponse.UpdateAirlineRequest;
import com.example.aviasimbir.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/representative")
public class RepresentativeController {
    private final PlaneService planeService;
    private final AirlineService airlineService;
    private final TicketService ticketService;
    private final FlightService flightService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;


    public RepresentativeController(PlaneService planeService, AirlineService airlineService,
                                    TicketService ticketService, FlightService flightService,
                                    JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.planeService = planeService;
        this.airlineService = airlineService;
        this.ticketService = ticketService;
        this.flightService = flightService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @RequestMapping(value = "/{id}/planes", method = RequestMethod.GET)
    @ApiOperation("Получить число самолетов авиалинии")
    public ResponseEntity<Long> getNumberOfPlanes(@PathVariable("id") Long id,
                                                  HttpServletRequest request) throws NoSuchIdException, NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        return ResponseEntity.ok(planeService.getPlanesCountByAirlineId(id));
    }

    @RequestMapping(value = "/{id}/sold", method = RequestMethod.GET)
    @ApiOperation("Получить число проданных билетов авиалинии")
    public ResponseEntity<Long> getNumberOfSoldTickets(@PathVariable Long id, HttpServletRequest request) throws NoSuchIdException, NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        long totalSoldTickets = ticketService.getAllSoldTicketsCountByFlights(
                flightService.getFlightsByPlanes(planeService.getListOfPlanes(airlineService.getAirline(id))));
        return ResponseEntity.ok(totalSoldTickets);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation("Обновить информацию об авиалинии")
    public ResponseEntity<AirlineResponse> updateAirline(@PathVariable("id") Long id, HttpServletRequest request,
                                                         @RequestBody UpdateAirlineRequest updateAirlineRequest) throws WrongArgumentException, NoSuchIdException, NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        Airline airline = airlineService.updateAirline(id, updateAirlineRequest.getName());
        AirlineResponse airlineResponse = new AirlineResponse(airline.getId(), airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Удалить авиалинию")
    public void deleteAirline(@PathVariable("id") Long id, HttpServletRequest request) throws NoSuchIdException, NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        airlineService.deleteAirline(id);
    }

    @RequestMapping(value = "/{id}/earnings", method = RequestMethod.GET)
    @ApiOperation("Подсчитать заработанную сумму")
    public ResponseEntity<BigDecimal> getTotalEarned(@PathVariable("id") Long id, HttpServletRequest request) throws NoSuchIdException, NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        BigDecimal totalEarned = ticketService.getTotalEarnedByFlights
                (flightService.getFlightsByPlanes(planeService.getListOfPlanes(airlineService.getAirline(id))));
        return ResponseEntity.ok(totalEarned);
    }

    @RequestMapping(value = "/{id}/tickets", method = RequestMethod.GET)
    @ApiOperation("Получить список всех билетов или список непроданных билетов в зависимости от запроса")
    public ResponseEntity<List<GetAllTicketsResponse>> getTickets(@PathVariable("id") Long id, HttpServletRequest request,
                                                                  @RequestBody GetAllTicketsRequest getAllTicketsRequest) throws NoSuchIdException, NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        List<Flight> flights = flightService.getFlightsByPlanes(planeService.getListOfPlanes(airlineService.getAirline(id)));
        List<Ticket> tickets = flights.stream()
                .flatMap(flight -> ticketService.getAllTicketsByFlight(flight, getAllTicketsRequest.getSold()).stream()).toList();
        List<GetAllTicketsResponse> getTicketResponses = tickets.stream()
                .map(ticket -> new GetAllTicketsResponse(ticket.getId(), ticket.getPrice(), ticket.getCommission())).collect(Collectors.toList());
        return ResponseEntity.ok(getTicketResponses);
    }
}
