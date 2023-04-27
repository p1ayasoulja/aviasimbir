package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.*;
import com.example.aviasimbir.exceptions.*;
import com.example.aviasimbir.requestresponse.*;
import com.example.aviasimbir.service.*;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CustomerControllerTest {
    private final AirlineService airlineService = Mockito.mock(AirlineService.class);
    private final OrderService orderService = Mockito.mock(OrderService.class);
    private final TicketService ticketService = Mockito.mock(TicketService.class);
    private final FlightService flightService = Mockito.mock(FlightService.class);
    private final PlaneService planeService = Mockito.mock(PlaneService.class);
    private final CustomerController customerController = new CustomerController(airlineService, flightService,
            ticketService, planeService, orderService);

    @Test
    public void testGetAirline() throws NoSuchIdException {
        Airline airline = new Airline("testAirline");
        when(airlineService.getAirline(1L)).thenReturn(airline);
        ResponseEntity<AirlineResponse> response = customerController.getAirline(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(airline.getId(), Objects.requireNonNull(response.getBody()).getId());
        Assertions.assertEquals(airline.getName(), Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void testGetAirlineNoSuchId() throws NoSuchIdException {
        when(airlineService.getAirline(1L)).thenThrow(new NoSuchIdException("Airline with id 1 was not found"));
        try {
            customerController.getAirline(1L);
            Assertions.fail("Expected NoSuchIdException to be thrown");
        } catch (NoSuchIdException e) {
            Assertions.assertEquals("Airline with id 1 was not found", e.getMessage());
        }
    }

    @Test
    public void testGetAirlineThrowsNoSuchIdException() throws NoSuchIdException {
        Long id = 1L;
        when(airlineService.getAirline(id)).thenThrow(new NoSuchIdException("Airline with id " + id + " was not found"));
        assertThrows(NoSuchIdException.class, () -> customerController.getAirline(id));
    }

    @Test
    public void testGetFlight() throws NoSuchIdException {
        Flight flight = new Flight(new Plane("Brand", "Model", 20, new Airline("testAirline")), "Departure", "Destination",
                ZonedDateTime.now(), ZonedDateTime.now().plusHours(1));
        when(flightService.getFlight(1L)).thenReturn(flight);
        ResponseEntity<FlightResponse> response = customerController.getFlight(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(flight.getId(), Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    public void testGetFlightNoSuchId() throws NoSuchIdException {
        Long id = 1L;
        Mockito.doThrow(new NoSuchIdException("Flight with id " + id + " was not found")).when(flightService).getFlight(id);
        assertThrows(NoSuchIdException.class, () -> customerController.getFlight(id));
    }

    @Test
    public void testGetFlightThrowsNoSuchIdException() throws NoSuchIdException {
        Long id = 1L;
        when(flightService.getFlight(id)).thenThrow(new NoSuchIdException("Flight with id " + id + " was not found"));
        assertThrows(NoSuchIdException.class, () -> customerController.getFlight(id));
    }

    @Test
    public void testGetAllAvailableTicketsSuccess() throws NoSuchIdException {
        Long flightId = 1L;
        List<Ticket> tickets = Arrays.asList(new Ticket(1L, BigDecimal.TEN, false), new Ticket(2L, BigDecimal.TEN, false));
        when(ticketService.getAllAvailableTicketsByFlight(flightId)).thenReturn(tickets);
        ResponseEntity<List<GetAllAvailableTicketsResponse>> response = customerController.getAllAvailableTickets(flightId);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(tickets.size(), Objects.requireNonNull(response.getBody()).size());
        Assertions.assertEquals(tickets.get(0).getId(), response.getBody().get(0).getId());
        Assertions.assertEquals(tickets.get(1).getPrice(), response.getBody().get(1).getPrice());
    }

    @Test
    public void testGetAllAvailableTicketsEmptyList() throws NoSuchIdException {
        Long flightId = 1L;
        List<Ticket> tickets = Collections.emptyList();
        when(ticketService.getAllAvailableTicketsByFlight(flightId)).thenReturn(tickets);

        ResponseEntity<List<GetAllAvailableTicketsResponse>> response = customerController.getAllAvailableTickets(flightId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(0, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void testGetAllAvailableTicketsForNonExistingFlight() throws NoSuchIdException {
        when(ticketService.getAllAvailableTicketsByFlight(1L)).thenThrow(NoSuchIdException.class);
        assertThrows(NoSuchIdException.class, () -> customerController.getAllAvailableTickets(1L));
    }

    @Test
    public void testGetPlane() throws NoSuchIdException {
        Airline airline = new Airline("testAirline");
        Plane plane = new Plane("Boeing", "737", 150, airline);
        plane.setId(1L);
        airline.setId(1L);
        when(planeService.getPlane(1L)).thenReturn(plane);
        ResponseEntity<PlaneResponse> response = customerController.getPlane(1L);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1L, response.getBody().getId());
        Assertions.assertEquals("Boeing", response.getBody().getBrand());
        Assertions.assertEquals("737", response.getBody().getModel());
        Assertions.assertEquals(150, response.getBody().getSeats());
        Assertions.assertEquals("testAirline", response.getBody().getAirline());
    }

    @Test
    public void testGetPlaneNoSuchIdException() throws NoSuchIdException {
        when(planeService.getPlane(1L)).thenThrow(new NoSuchIdException("Plane not found"));
        assertThrows(NoSuchIdException.class, () -> customerController.getPlane(1L));
    }

    @Test
    public void testGetPlaneThrowsNoSuchIdException() throws NoSuchIdException {
        Long id = 1L;
        when(planeService.getPlane(id)).thenThrow(new NoSuchIdException("Plane with id " + id + " was not found"));
        assertThrows(NoSuchIdException.class, () -> customerController.getPlane(id));
    }

    @Test
    public void testFilterTicketsSuccess() throws Exception {
        FilterTicketsRequest filterRequest = new FilterTicketsRequest("Moscow", "London", LocalDate.parse("2023-06-01"), LocalDate.parse("2023-06-30"));
        List<Ticket> filteredTickets = new ArrayList<>();
        filteredTickets.add(new Ticket(1L, BigDecimal.TEN, false));
        filteredTickets.add(new Ticket(2L, BigDecimal.TEN, false));
        when(ticketService.getTicketsFilter(eq("Moscow"), eq("London"), eq(LocalDate.parse("2023-06-01")), eq(LocalDate.parse("2023-06-30"))))
                .thenReturn(filteredTickets);
        ResponseEntity<List<GetAllAvailableTicketsResponse>> responseEntity = customerController.filterTickets(filterRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).size()).isEqualTo(2);
        assertThat(responseEntity.getBody().get(0).getId()).isEqualTo(1L);
        assertThat(responseEntity.getBody().get(0).getPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(responseEntity.getBody().get(1).getId()).isEqualTo(2L);
        assertThat(responseEntity.getBody().get(1).getPrice()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void testFilterTicketsEmptyResult() throws Exception {
        FilterTicketsRequest filterRequest = new FilterTicketsRequest("Moscow", "London", LocalDate.parse("2023-06-01"), LocalDate.parse("2023-06-30"));
        when(ticketService.getTicketsFilter(eq("Moscow"), eq("London"), eq(LocalDate.parse("2023-06-01")), eq(LocalDate.parse("2023-06-30"))))
                .thenReturn(new ArrayList<>());
        ResponseEntity<List<GetAllAvailableTicketsResponse>> responseEntity = customerController.filterTickets(filterRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).size()).isEqualTo(0);
    }

    @Test
    public void testFilterTicketsWrongArgumentException() throws WrongArgumentException {
        FilterTicketsRequest filterRequest = new FilterTicketsRequest("Moscow", "London", LocalDate.parse("2024-06-01"), LocalDate.parse("2023-06-30"));
        when(ticketService.getTicketsFilter("Moscow", "London", LocalDate.parse("2024-06-01"), LocalDate.parse("2023-06-30")))
                .thenThrow(new WrongArgumentException("Some of fields are wrong or not present"));
        assertThrows(WrongArgumentException.class, () -> customerController.filterTickets(filterRequest));
    }


    @Test
    public void testCreateOrderSuccessful() throws TicketReservedException, WrongPromocodeException, CreateOrderException, PlaneAlreadyLeftException {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket(1L, BigDecimal.TEN, false));
        tickets.add(new Ticket(2L, BigDecimal.TEN, false));

        Promocode promocode = new Promocode("PROMO123", ZonedDateTime.now().minusDays(1), 10L, 10L);
        List<Long> ticketIds = tickets.stream().map(Ticket::getId).collect(Collectors.toList());
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(promocode.getCode(), ticketIds);

        when(orderService.createOrder(eq(ticketIds), eq(promocode.getCode()))).thenReturn(new Order(1L, BigDecimal.TEN, ZonedDateTime.now().plusHours(1), Order.Status.NEW));

        ResponseEntity<CreateOrderResponse> response = customerController.createOrder(createOrderRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CreateOrderResponse createOrderResponse = response.getBody();
        assertNotNull(createOrderResponse);
        assertNotNull(createOrderResponse.getId());
        assertTrue(createOrderResponse.getId() > 0);
    }

    @Test
    public void testCreateOrderWrongPromocodeException() throws CreateOrderException, TicketReservedException, WrongPromocodeException, PlaneAlreadyLeftException {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket(1L, BigDecimal.TEN, false));
        tickets.add(new Ticket(2L, BigDecimal.TEN, false));

        Promocode promocode = new Promocode(null, ZonedDateTime.now().minusDays(1), 10L, 10L);

        List<Long> ticketIds = tickets.stream().map(Ticket::getId).collect(Collectors.toList());
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(promocode.getCode(), ticketIds);
        when(orderService.createOrder(ticketIds, promocode.getCode())).thenThrow(new WrongPromocodeException("Promocode is expired or not available"));
        assertThrows(WrongPromocodeException.class, () -> customerController.createOrder(createOrderRequest));
    }


}