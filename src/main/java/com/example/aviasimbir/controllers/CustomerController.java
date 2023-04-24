package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.*;
import com.example.aviasimbir.exceptions.*;
import com.example.aviasimbir.requestresponse.*;
import com.example.aviasimbir.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final AirlineService airlineService;
    private final FlightService flightService;
    private final TicketService ticketService;
    private final PlaneService planeService;
    private final OrderService orderService;

    public CustomerController(AirlineService airlineService, FlightService flightService, TicketService ticketService, PlaneService planeService, OrderService orderService) {
        this.airlineService = airlineService;
        this.flightService = flightService;
        this.ticketService = ticketService;
        this.planeService = planeService;
        this.orderService = orderService;
    }

    @RequestMapping(value = "/airline/{id}", method = RequestMethod.GET)
    @ApiOperation("Получение информации об авиалинии")
    public ResponseEntity<AirlineResponse> getAirline(@PathVariable("id") Long id) throws NoSuchIdException {
        Airline airline = airlineService.getAirline(id);
        AirlineResponse airlineResponse = new AirlineResponse(airline.getId(), airline.getName());
        return ResponseEntity.ok(airlineResponse);
    }

    @RequestMapping(value = "/flight/{id}", method = RequestMethod.GET)
    @ApiOperation("Получить информацию о рейсе")
    public ResponseEntity<FlightResponse> getFlight(@PathVariable("id") Long id) throws NoSuchIdException {
        Flight flight = flightService.getFlight(id);
        FlightResponse flightResponse = new FlightResponse(flight.getId(), flight.getPlane().getAirline().getName(), flight.getDeparture(),
                flight.getDestination(), flight.getDepartureTime(), flight.getArrivalTime());
        return ResponseEntity.ok(flightResponse);
    }

    @RequestMapping(value = "/flight/{id}/tickets", method = RequestMethod.GET)
    @ApiOperation("Показать список досутпных билетов рейса")
    public ResponseEntity<List<GetAllAvailableTicketsResponse>> getAllAvailableTickets(@PathVariable("id") Long id) throws NoSuchIdException {
        List<Ticket> tickets = ticketService.getAllAvailableTicketsByFlight(id);
        List<GetAllAvailableTicketsResponse> getAllAvailableTicketsResponse = tickets.stream()
                .map(ticket -> new GetAllAvailableTicketsResponse(ticket.getId(), ticket.getPrice())).collect(Collectors.toList());
        return ResponseEntity.ok(getAllAvailableTicketsResponse);
    }

    @RequestMapping(value = "/plane/{id}", method = RequestMethod.GET)
    @ApiOperation("Получить информацию о самолете")
    public ResponseEntity<PlaneResponse> getPlane(@PathVariable("id") Long id) throws NoSuchIdException {
        Plane plane = planeService.getPlane(id);
        PlaneResponse getPlaneResponse = new PlaneResponse(plane.getId(), plane.getBrand(), plane.getModel(),
                plane.getSeats(), plane.getAirline().getName());
        return ResponseEntity.ok(getPlaneResponse);
    }

    @RequestMapping(value = "/filter", method = RequestMethod.GET)
    @ApiOperation("Отфильровать билеты по месту вылета, месту назначения и дате вылета")
    public ResponseEntity<List<GetAllAvailableTicketsResponse>> filterTickets(@RequestBody FilterTicketsRequest filterTicketsRequest) throws WrongArgumentException {
        List<GetAllAvailableTicketsResponse> getAllAvailableTicketsResponses = ticketService.getTicketsFilter(filterTicketsRequest.getDeparture(),
                        filterTicketsRequest.getDestination(), filterTicketsRequest.getMinDepartureDay(), filterTicketsRequest.getMaxDepartureDay())
                .stream()
                .map(ticket -> new GetAllAvailableTicketsResponse(ticket.getId(), ticket.getPrice()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(getAllAvailableTicketsResponses);
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ApiOperation("Создать заказ")
    public ResponseEntity<Object> createOrder(@RequestBody CreateOrderRequest createOrderRequest) throws TicketReservedException, WrongPromocodeException, CreateOrderException, PlaneAlreadyLeftException {
        Order order = orderService.createOrder(createOrderRequest.getTickets_id(), createOrderRequest.getPromocode());
        CreateOrderResponse createOrderResponse = new CreateOrderResponse(order.getId(), order.getTotalPrice(), order.getStatus().name());
        return ResponseEntity.ok(createOrderResponse);
    }
}
