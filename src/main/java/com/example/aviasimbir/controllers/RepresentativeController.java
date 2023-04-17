package com.example.aviasimbir.controllers;

import com.example.aviasimbir.Jwt.JwtTokenProvider;
import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.NotRepresentativeException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.*;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.PlaneService;
import com.example.aviasimbir.service.TicketService;
import com.example.aviasimbir.service.UserService;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;


    public RepresentativeController(PlaneService planeService, AirlineService airlineService,
                                    TicketService ticketService,
                                    JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.planeService = planeService;
        this.airlineService = airlineService;
        this.ticketService = ticketService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @RequestMapping(value = "/{id}/planes", method = RequestMethod.GET)
    @ApiOperation("Получить число самолетов авиалинии")
    public ResponseEntity<GetNumberOfPlanesResponse> getNumberOfPlanes(@PathVariable("id") Long id,
                                                                       HttpServletRequest request) throws NoSuchIdException, NotRepresentativeException {
        if (userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id)) {
            GetNumberOfPlanesResponse getNumberOfPlanesResponse =
                    new GetNumberOfPlanesResponse(planeService.getPlanesCountByAirlineId(id));
            return ResponseEntity.ok(getNumberOfPlanesResponse);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(value = "/{id}/sold", method = RequestMethod.GET)
    @ApiOperation("Получить число проданных билетов авиалинии")
    public ResponseEntity<GetNumberOfSoldTicketsResponse> getNumberOfSoldTickets(@PathVariable Long id, HttpServletRequest request) throws NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        long totalSoldTickets = airlineService.getTotalSoldTickets(id);
        GetNumberOfSoldTicketsResponse getNumberOfSoldTicketsResponse = new GetNumberOfSoldTicketsResponse(totalSoldTickets);
        return ResponseEntity.ok(getNumberOfSoldTicketsResponse);
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
    public ResponseEntity<GetTotalEarnedResponse> getTotalEarned(@PathVariable("id") Long id, HttpServletRequest request) throws NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        BigDecimal totalEarned = airlineService.getTotalEarnedByAirline(id);
        GetTotalEarnedResponse getTotalEarnedResponse = new GetTotalEarnedResponse(totalEarned);
        return ResponseEntity.ok(getTotalEarnedResponse);
    }

    @RequestMapping(value = "/{id}/tickets", method = RequestMethod.GET)
    @ApiOperation("Получить список всех билетов или список непроданных билетов в зависимости от запроса")
    public ResponseEntity<List<GetAllTicketsResponse>> getTickets(@PathVariable("id") Long id, HttpServletRequest request,
                                                                  @RequestBody GetAllTicketsRequest getAllTicketsRequest) throws NotRepresentativeException {
        userService.isRepresentativeOfThisAirline(jwtTokenProvider.getUsernameByToken(jwtTokenProvider.resolveToken(request)), id);
        List<Ticket> tickets = ticketService.findTicketsByAirline(id, getAllTicketsRequest.getSold());
        List<GetAllTicketsResponse> getTicketResponses = tickets.stream()
                .map(ticket -> new GetAllTicketsResponse(ticket.getId(), ticket.getPrice(), ticket.getCommission())).collect(Collectors.toList());
        return ResponseEntity.ok(getTicketResponses);
    }

}
