package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.FlightTimeException;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.*;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.TicketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flight")
public class FlightController {
    private final FlightService flightService;
    private final TicketService ticketService;

    public FlightController(FlightService flightService, TicketService ticketService) {
        this.flightService = flightService;
        this.ticketService = ticketService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation("Получить информацию о рейсе")
    public ResponseEntity<FlightResponse> getFlight(@PathVariable("id") Long id) throws NoSuchIdException {
        Flight flight = flightService.getFlight(id);
        FlightResponse flightResponse = new FlightResponse(flight.getId(), flight.getPlane().getAirline().getName(), flight.getDeparture(),
                flight.getDestination(), flight.getDepartureTime(), flight.getArrivalTime());
        return ResponseEntity.ok(flightResponse);

    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation("Создать рейс")
    public ResponseEntity<FlightResponse> createFlight(@RequestBody CreateFlightRequest createFlightRequest) throws WrongArgumentException, NoSuchIdException, FlightTimeException {
        Flight flight = flightService.createFlight(createFlightRequest.getPlaneId(), createFlightRequest.getDeparture(),
                createFlightRequest.getDestination(), createFlightRequest.getDepartureTime(),
                createFlightRequest.getArrivalTime(), createFlightRequest.getCommission(), createFlightRequest.getTicketPrice());
        FlightResponse flightResponse = new FlightResponse(flight.getId(), flight.getPlane().getAirline().getName(), flight.getDeparture(),
                flight.getDestination(), flight.getDepartureTime(), flight.getArrivalTime());
        return ResponseEntity.ok(flightResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ApiOperation("Обновить рейс")
    public ResponseEntity<FlightResponse> updateFlight(@PathVariable("id") Long id, @RequestBody UpdateFlightRequest updateFlightRequest) throws NoSuchIdException, WrongArgumentException, FlightTimeException {
        Flight flight = flightService.updateFlight(id, updateFlightRequest.getDepartureTime(), updateFlightRequest.getArrivalTime(),
                updateFlightRequest.getPlaneId(), updateFlightRequest.getDeparture(), updateFlightRequest.getDestination());
        FlightResponse flightResponse = new FlightResponse(flight.getId(), flight.getPlane().getAirline().getName(),
                flight.getDeparture(), flight.getDestination(), flight.getDepartureTime(),
                flight.getArrivalTime());
        return ResponseEntity.ok(flightResponse);
    }

    @RequestMapping(value = "/{id}/addticket", method = RequestMethod.POST)
    @ApiOperation("Добавить билет на рейс")
    public ResponseEntity<TicketResponse> createTicketForFlight(@PathVariable("id") Long id, @RequestBody CreateTicketRequest createTickerRequest) throws NoSuchIdException, WrongArgumentException {
        Ticket ticket = ticketService.createTicket(id, createTickerRequest.getPrice(),
                false, false, createTickerRequest.getCommission());
        TicketResponse ticketResponse = new TicketResponse(ticket.getFlight().getDeparture(), ticket.getFlight().getDestination(),
                ticket.getPrice(), ticket.getReserved(), ticket.getSold());
        return ResponseEntity.ok(ticketResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation("Удалить рейс")
    public void deleteFlight(@PathVariable("id") Long id) throws NoSuchIdException {
        flightService.deleteFlight(id);
    }

    @RequestMapping(value = "/{id}/tickets", method = RequestMethod.GET)
    @ApiOperation("Показать список досутпных билетов")
    public ResponseEntity<List<GetAllAvailableTicketsResponse>> getAllAvailableTickets(@PathVariable("id") Long id) throws NoSuchIdException {
        List<Ticket> tickets = ticketService.getAllAvailableTicketsByFlight(id);
        List<GetAllAvailableTicketsResponse> getAllAvailableTicketsResponse = tickets.stream()
                .map(ticket -> new GetAllAvailableTicketsResponse(ticket.getId(), ticket.getPrice())).collect(Collectors.toList());
        return ResponseEntity.ok(getAllAvailableTicketsResponse);
    }
}
