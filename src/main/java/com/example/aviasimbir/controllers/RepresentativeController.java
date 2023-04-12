package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.AirlineResponse;
import com.example.aviasimbir.requestresponse.GetAllTicketsRequest;
import com.example.aviasimbir.requestresponse.GetAllTicketsResponse;
import com.example.aviasimbir.requestresponse.UpdateAirlineRequest;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.PlaneService;
import com.example.aviasimbir.service.TicketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/representative")
//todo настроить проверку представителя
public class RepresentativeController {
    private final PlaneService planeService;
    private final AirlineService airlineService;
    private final TicketService ticketService;
    private final FlightService flightService;

    public RepresentativeController(PlaneService planeService, AirlineService airlineService, TicketService ticketService, FlightService flightService) {
        this.planeService = planeService;
        this.airlineService = airlineService;
        this.ticketService = ticketService;
        this.flightService = flightService;
    }

    @RequestMapping(value = "/{id}/planes", method = RequestMethod.GET)
    @ApiOperation("Получить число самолетов авиалинии")
    public ResponseEntity<Long> getNumberOfPlanes(@PathVariable("id") Long id) throws NoSuchIdException {
        return ResponseEntity.ok(planeService.getPlanesCountByAirlineId(id));
    }

    @RequestMapping(value = "/{id}/sold", method = RequestMethod.GET)
    @ApiOperation("Получить число проданных билетов авиалинии")
    public ResponseEntity<Long> getNumberOfSoldTickets(@PathVariable Long id) throws NoSuchIdException {
        long totalSoldTickets = ticketService.getAllSoldTicketsCountByFlights(
                flightService.getFlightsByPlanes(planeService.getListOfPlanes(airlineService.getAirline(id))));
        return ResponseEntity.ok(totalSoldTickets);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation("Обновить информацию об авиалинии")
    public ResponseEntity<AirlineResponse> updateAirline(@PathVariable("id") Long id,
                                                         @RequestBody UpdateAirlineRequest updateAirlineRequest) throws WrongArgumentException, NoSuchIdException {
        Airline airline = airlineService.updateAirline(id, updateAirlineRequest.getName());
        AirlineResponse airlineResponse = new AirlineResponse(airline.getId(), airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Удалить авиалинию")
    public void deleteAirline(@PathVariable("id") Long id) throws NoSuchIdException {
        airlineService.deleteAirline(id);
    }

    @RequestMapping(value = "/{id}/earnings", method = RequestMethod.GET)
    @ApiOperation("Подсчитать заработанную сумму")
    public ResponseEntity<BigDecimal> getTotalEarned(@PathVariable("id") Long id) throws NoSuchIdException {
        BigDecimal totalEarned = ticketService.getTotalEarnedByFlights
                (flightService.getFlightsByPlanes(planeService.getListOfPlanes(airlineService.getAirline(id))));
        return ResponseEntity.ok(totalEarned);
    }

    @RequestMapping(value = "/{airlineId}/tickets", method = RequestMethod.GET)
    @ApiOperation("Получить список всех билетов или список непроданных билетов в зависимости от запроса")
    public ResponseEntity<List<GetAllTicketsResponse>> getTickets(@PathVariable("airlineId") Long airlineId,
                                                                  @RequestBody GetAllTicketsRequest getAllTicketsRequest) throws NoSuchIdException {
        List<Flight> flights = flightService.getFlightsByPlanes(planeService.getListOfPlanes(airlineService.getAirline(airlineId)));
        List<Ticket> tickets = flights.stream()
                .flatMap(flight -> ticketService.getAllTicketsByFlight(flight, getAllTicketsRequest.getSold()).stream()).toList();
        List<GetAllTicketsResponse> getTicketResponses = tickets.stream()
                .map(ticket -> new GetAllTicketsResponse(ticket.getId(), ticket.getPrice(), ticket.getCommission())).collect(Collectors.toList());
        return ResponseEntity.ok(getTicketResponses);
    }
}
