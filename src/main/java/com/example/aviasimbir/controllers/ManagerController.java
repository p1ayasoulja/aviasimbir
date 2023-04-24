package com.example.aviasimbir.controllers;

import com.example.aviasimbir.Jwt.JwtUser;
import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.FlightTimeException;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.NotRepresentativeException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.*;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.TicketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final AirlineService airlineService;
    private final FlightService flightService;
    private final TicketService ticketService;

    public ManagerController(AirlineService airlineService, FlightService flightService, TicketService ticketService) {
        this.airlineService = airlineService;
        this.flightService = flightService;
        this.ticketService = ticketService;
    }

    @RequestMapping(value = "/airline", method = RequestMethod.POST)
    @ApiOperation("Создать авиалинию")
    public ResponseEntity<AirlineResponse> createAirline(@RequestBody CreateAirlineRequest createAirlineRequest) throws WrongArgumentException {
        Airline airline = airlineService.createAirline(createAirlineRequest.getName());
        AirlineResponse airlineResponse = new AirlineResponse(airline.getId(), airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }


    @RequestMapping(value = "/airline/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Удалить авиалинию")
    public void deleteAirline(@PathVariable("id") Long id, @AuthenticationPrincipal JwtUser jwtUser) throws NoSuchIdException, NotRepresentativeException {
        String username = jwtUser.getUsername();
        airlineService.deleteAirline(id, username);
    }


    @RequestMapping(value = "/flight", method = RequestMethod.POST)
    @ApiOperation("Создать рейс")
    public ResponseEntity<FlightResponse> createFlight(@RequestBody CreateFlightRequest createFlightRequest) throws WrongArgumentException, NoSuchIdException, FlightTimeException {
        Flight flight = flightService.createFlight(createFlightRequest.getPlaneId(), createFlightRequest.getDeparture(),
                createFlightRequest.getDestination(), createFlightRequest.getDepartureTime(),
                createFlightRequest.getArrivalTime(), createFlightRequest.getCommission(), createFlightRequest.getTicketPrice());
        FlightResponse flightResponse = new FlightResponse(flight.getId(), flight.getPlane().getAirline().getName(), flight.getDeparture(),
                flight.getDestination(), flight.getDepartureTime(), flight.getArrivalTime());
        return ResponseEntity.ok(flightResponse);
    }

    @RequestMapping(value = "/flight/{id}", method = RequestMethod.PATCH)
    @ApiOperation("Обновить рейс")
    public ResponseEntity<FlightResponse> updateFlight(@PathVariable("id") Long id, @RequestBody UpdateFlightRequest updateFlightRequest) throws NoSuchIdException, WrongArgumentException, FlightTimeException {
        Flight flight = flightService.updateFlight(id, updateFlightRequest.getDepartureTime(), updateFlightRequest.getArrivalTime(),
                updateFlightRequest.getPlaneId(), updateFlightRequest.getDeparture(), updateFlightRequest.getDestination());
        FlightResponse flightResponse = new FlightResponse(flight.getId(), flight.getPlane().getAirline().getName(),
                flight.getDeparture(), flight.getDestination(), flight.getDepartureTime(),
                flight.getArrivalTime());
        return ResponseEntity.ok(flightResponse);
    }

    @RequestMapping(value = "/flight/{id}/addticket", method = RequestMethod.POST)
    @ApiOperation("Добавить билет на рейс")
    public ResponseEntity<TicketResponse> createTicketForFlight(@PathVariable("id") Long id, @RequestBody CreateTicketRequest createTickerRequest) throws NoSuchIdException, WrongArgumentException {
        Ticket ticket = ticketService.createTicket(id, createTickerRequest.getPrice(),
                false, false, createTickerRequest.getCommission());
        TicketResponse ticketResponse = new TicketResponse(ticket.getFlight().getDeparture(), ticket.getFlight().getDestination(),
                ticket.getPrice(), ticket.getReserved(), ticket.getSold());
        return ResponseEntity.ok(ticketResponse);
    }

    @RequestMapping(value = "/flight/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Удалить рейс")
    public void deleteFlight(@PathVariable("id") Long id) throws NoSuchIdException {
        flightService.deleteFlight(id);
    }

    @RequestMapping(value = "/tickets/commission", method = RequestMethod.GET)
    @ApiOperation("Получить среднюю коммиссию по проданным билетам")
    public ResponseEntity<GetCommissionInfo> getCommissionInfo() {
        GetCommissionInfo getCommissionInfo =
                new GetCommissionInfo(ticketService.getCommissionInfo().getAverageCommission(), ticketService.getCommissionInfo().getTotalCommission());
        return ResponseEntity.ok(getCommissionInfo);
    }

    @RequestMapping(value = "/tickets/{departure}", method = RequestMethod.GET)
    @ApiOperation("Получить число проданных билетов с указанной точкой отправления")
    public ResponseEntity<GetSoldTicketsFromKazanCountResponse> getSoldTicketsFromCount(@PathVariable("departure") String departure) {
        GetSoldTicketsFromKazanCountResponse getSoldTicketsFromKazanCountResponse =
                new GetSoldTicketsFromKazanCountResponse(ticketService.getSoldTicketsFromCount(departure));
        return ResponseEntity.ok(getSoldTicketsFromKazanCountResponse);
    }

    @RequestMapping(value = "/tickets/soldstatistic", method = RequestMethod.GET)
    @ApiOperation("Получить статистику по проданным билетам")
    public ResponseEntity<GetStatisticOfSoldTicketsResponse> getStatisticOfSoldTickets() {
        BigDecimal averagecommission = ticketService.getCommissionInfo().getAverageCommission();
        Long fromKazan = ticketService.getSoldTicketsFromCount("Kazan");
        BigDecimal totalEarned = ticketService.getTotalEarned();
        Long totalSold = ticketService.getTicketsSoldCount();
        GetStatisticOfSoldTicketsResponse getStatisticOfSoldTicketsResponse = new GetStatisticOfSoldTicketsResponse(totalSold, fromKazan, averagecommission, totalEarned);
        return ResponseEntity.ok(getStatisticOfSoldTicketsResponse);
    }
}
