package com.example.aviasimbir.controllers;

import com.example.aviasimbir.Jwt.JwtUser;
import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.FlightTimeException;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.NotRepresentativeException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.requestresponse.*;
import com.example.aviasimbir.service.AirlineService;
import com.example.aviasimbir.service.FlightService;
import com.example.aviasimbir.service.TicketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ManagerControllerTest {

    private final AirlineService airlineService = Mockito.mock(AirlineService.class);
    private final TicketService ticketService = Mockito.mock(TicketService.class);
    private final FlightService flightService = Mockito.mock(FlightService.class);
    private final ManagerController managerController = new ManagerController(airlineService, flightService, ticketService);

    @Test
    public void testCreateAirline() throws WrongArgumentException {
        CreateAirlineRequest request = new CreateAirlineRequest("testAirline");
        Airline airline = new Airline("testAirline");
        when(airlineService.createAirline(request.getName())).thenReturn(airline);

        ResponseEntity<AirlineResponse> response = managerController.createAirline(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(airline.getId(), response.getBody().getId());
        assertEquals(airline.getName(), response.getBody().getName());
    }

    @Test
    public void testCreateAirlineThrowsWrongArgumentException() throws WrongArgumentException {
        CreateAirlineRequest request = new CreateAirlineRequest("");
        doThrow(new WrongArgumentException("Airline name cannot be empty")).when(airlineService).createAirline(request.getName());

        assertThrows(WrongArgumentException.class, () -> managerController.createAirline(request));
    }

    @Test
    public void testCreateAirlineThrowsException() throws Exception {
        CreateAirlineRequest request = new CreateAirlineRequest("testAirline");
        doThrow(new RuntimeException("Something went wrong")).when(airlineService).createAirline(request.getName());

        assertThrows(RuntimeException.class, () -> managerController.createAirline(request));
    }

    @Test
    public void testDeleteAirlineSuccess() throws NoSuchIdException, NotRepresentativeException {
        Long airlineId = 1L;
        String username = "testUser";
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());
        managerController.deleteAirline(airlineId, jwtUser);
        verify(airlineService, times(1)).deleteAirline(airlineId, username);
    }

    @Test
    public void testDeleteAirlineThrowsNoSuchIdException() throws NoSuchIdException, NotRepresentativeException {
        Long airlineId = 1L;
        String username = "testUser";
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());

        doThrow(new NoSuchIdException("Airline with id " + airlineId + " was not found")).when(airlineService).deleteAirline(airlineId, username);
        assertThrows(NoSuchIdException.class, () -> managerController.deleteAirline(airlineId, jwtUser));
    }

    @Test
    public void testDeleteAirlineThrowsNotRepresentativeException() throws NoSuchIdException, NotRepresentativeException {
        Long airlineId = 1L;
        String username = "testUser";
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());

        doThrow(new NotRepresentativeException("User " + username + " is not a representative of airline with id " + airlineId)).when(airlineService).deleteAirline(airlineId, username);

        assertThrows(NotRepresentativeException.class, () -> managerController.deleteAirline(airlineId, jwtUser));
    }

    @Test
    public void testCreateFlightSuccess() throws WrongArgumentException, NoSuchIdException, FlightTimeException {
        CreateFlightRequest createFlightRequest = new CreateFlightRequest(1L, "Moscow", "Paris",
                ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusHours(3), BigDecimal.TEN, false);
        Flight flight = new Flight();
        Airline airline = new Airline("testAirline");
        Plane plane = new Plane("Brand", "Model", 20, airline);
        flight.setPlane(plane);
        flight.setDeparture(createFlightRequest.getDeparture());
        flight.setDestination(createFlightRequest.getDestination());
        flight.setDepartureTime(createFlightRequest.getDepartureTime());
        flight.setArrivalTime(createFlightRequest.getArrivalTime());
        when(flightService.createFlight(createFlightRequest.getPlaneId(), createFlightRequest.getDeparture(),
                createFlightRequest.getDestination(), createFlightRequest.getDepartureTime(),
                createFlightRequest.getArrivalTime(), createFlightRequest.getCommission(),
                createFlightRequest.getTicketPrice())).thenReturn(flight);
        FlightResponse expectedFlightResponse = new FlightResponse(flight.getId(), airline.getName(),
                flight.getDeparture(), flight.getDestination(), flight.getDepartureTime(), flight.getArrivalTime());
        ResponseEntity<FlightResponse> responseEntity = managerController.createFlight(createFlightRequest);
        FlightResponse actualFlightResponse = responseEntity.getBody();

        assertEquals(expectedFlightResponse.getId(), Objects.requireNonNull(actualFlightResponse).getId());
        assertEquals(expectedFlightResponse.getAirline(), actualFlightResponse.getAirline());
        assertEquals(expectedFlightResponse.getDeparture(), actualFlightResponse.getDeparture());
        assertEquals(expectedFlightResponse.getDestination(), actualFlightResponse.getDestination());
        assertEquals(expectedFlightResponse.getDepartureTime(), actualFlightResponse.getDepartureTime());
        assertEquals(expectedFlightResponse.getArrivalTime(), actualFlightResponse.getArrivalTime());
    }

    @Test
    public void testCreateFlightThrowsWrongArgumentException() throws WrongArgumentException, NoSuchIdException, FlightTimeException {
        CreateFlightRequest createFlightRequest = new CreateFlightRequest(1L, "Moscow", "Moscow",
                ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusHours(3), BigDecimal.TEN, false);
        when(flightService.createFlight(createFlightRequest.getPlaneId(), createFlightRequest.getDeparture(),
                createFlightRequest.getDestination(), createFlightRequest.getDepartureTime(),
                createFlightRequest.getArrivalTime(), createFlightRequest.getCommission(),
                createFlightRequest.getTicketPrice())).thenThrow(new WrongArgumentException("Destination cannot be the same as departure"));
        assertThrows(WrongArgumentException.class, () -> managerController.createFlight(createFlightRequest));
    }

    @Test
    public void testCreateFlightThrowsNoSuchIdException() throws WrongArgumentException, NoSuchIdException, FlightTimeException {
        CreateFlightRequest createFlightRequest = new CreateFlightRequest(1L, "Moscow", "Paris",
                ZonedDateTime.now(), ZonedDateTime.now().plusHours(3), BigDecimal.TEN, false);
        when(flightService.createFlight(createFlightRequest.getPlaneId(), createFlightRequest.getDeparture(),
                createFlightRequest.getDestination(), createFlightRequest.getDepartureTime(),
                createFlightRequest.getArrivalTime(), createFlightRequest.getCommission(),
                createFlightRequest.getTicketPrice())).thenThrow(new NoSuchIdException("Plane with id " + createFlightRequest.getPlaneId() + " not found"));
        assertThrows(NoSuchIdException.class, () -> managerController.createFlight(createFlightRequest));
    }

    @Test
    public void testUpdateFlightSuccess() throws NoSuchIdException, WrongArgumentException, FlightTimeException {
        Airline airline = new Airline("testAirline");
        Plane plane = new Plane("Brand", "Model", 20, airline);
        UpdateFlightRequest updateFlightRequest = new UpdateFlightRequest(plane.getId(), "Moscow", "Berlin", ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusHours(3));
        Flight flight = new Flight(plane, "Moscow", "Paris",
                ZonedDateTime.now().plusHours(1), ZonedDateTime.now().plusHours(3));
        when(flightService.updateFlight(updateFlightRequest.getPlaneId(), updateFlightRequest.getDepartureTime(),
                updateFlightRequest.getArrivalTime(), updateFlightRequest.getPlaneId(),
                updateFlightRequest.getDeparture(), updateFlightRequest.getDestination())).thenReturn(flight); FlightResponse expectedResponse = new FlightResponse(flight.getId(), airline.getName(),
                flight.getDeparture(), flight.getDestination(),
                flight.getDepartureTime(), flight.getArrivalTime());
        ResponseEntity<FlightResponse> responseEntity = managerController.updateFlight(flight.getId(), updateFlightRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse.getAirline(), Objects.requireNonNull(responseEntity.getBody()).getAirline());
        assertEquals(expectedResponse.getDeparture(), Objects.requireNonNull(responseEntity.getBody()).getDeparture());
        assertEquals(expectedResponse.getDestination(), Objects.requireNonNull(responseEntity.getBody()).getDestination());
    }

    @Test
    public void testUpdateFlightWrongDepartureTime() throws NoSuchIdException, WrongArgumentException, FlightTimeException {
        Long id = 1L;
        UpdateFlightRequest updateFlightRequest = new UpdateFlightRequest(1L, "Moscow", "Berlin", ZonedDateTime.now().plusHours(5), ZonedDateTime.now().plusHours(3));
        when(flightService.updateFlight(id, updateFlightRequest.getDepartureTime(),
                updateFlightRequest.getArrivalTime(), updateFlightRequest.getPlaneId(),
                updateFlightRequest.getDeparture(), updateFlightRequest.getDestination()))
                .thenThrow(new FlightTimeException("Departure time cannot be after arrival time"));
        assertThrows(FlightTimeException.class, () -> managerController.updateFlight(id, updateFlightRequest));
    }

    @Test
    public void testUpdateFlightWrongPlaneId() throws NoSuchIdException, WrongArgumentException, FlightTimeException {
        Long id = 1L;
        UpdateFlightRequest updateFlightRequest = new UpdateFlightRequest(-1L, "Moscow", "Berlin", ZonedDateTime.now().plusHours(5), ZonedDateTime.now().plusHours(3));
        when(flightService.updateFlight(id, updateFlightRequest.getDepartureTime(),
                updateFlightRequest.getArrivalTime(), updateFlightRequest.getPlaneId(),
                updateFlightRequest.getDeparture(), updateFlightRequest.getDestination()))
                .thenThrow(new WrongArgumentException("Некорректный номер самолета"));
        assertThrows(WrongArgumentException.class, () -> managerController.updateFlight(id, updateFlightRequest));
    }

    @Test
    public void testCreateTicketForFlightSuccess() throws NoSuchIdException, WrongArgumentException {
        CreateTicketRequest createTicketRequest = new CreateTicketRequest(BigDecimal.TEN, false);
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setDeparture("Moscow");
        flight.setDestination("Paris");
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setFlight(flight);
        ticket.setPrice(createTicketRequest.getPrice());
        ticket.setCommission(createTicketRequest.getCommission());

        when(ticketService.createTicket(flight.getId(), createTicketRequest.getPrice(), false, false, createTicketRequest.getCommission()))
                .thenReturn(ticket);

        ResponseEntity<TicketResponse> responseEntity = managerController.createTicketForFlight(flight.getId(), createTicketRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        TicketResponse actualResponse = responseEntity.getBody();
        assertEquals(flight.getDeparture(), actualResponse.getDeparture());
        assertEquals(flight.getDestination(), actualResponse.getDestination());
        assertEquals(createTicketRequest.getPrice(), actualResponse.getPrice());
    }


    @Test
    public void testCreateTicketForFlightNoSuchIdException() throws NoSuchIdException, WrongArgumentException {
        CreateTicketRequest createTicketRequest = new CreateTicketRequest(BigDecimal.TEN, false);
        Long id = 1L;
        when(ticketService.createTicket(id, createTicketRequest.getPrice(),
                false, false, createTicketRequest.getCommission())).thenThrow(new NoSuchIdException("No such flight with id: " + id));
        assertThrows(NoSuchIdException.class, () -> managerController.createTicketForFlight(id, createTicketRequest));
    }

    @Test
    public void testCreateTicketForFlightZeroPrice() throws NoSuchIdException, WrongArgumentException {
        CreateTicketRequest createTicketRequest = new CreateTicketRequest(BigDecimal.ZERO, false);
        Long id = 1L;
        when(ticketService.createTicket(id, createTicketRequest.getPrice(),
                false, false, createTicketRequest.getCommission())).thenThrow(new WrongArgumentException("All fields must be filled"));
        assertThrows(WrongArgumentException.class, () -> managerController.createTicketForFlight(id, createTicketRequest));
    }

    ///////
    @Test
    public void testDeleteFlightSuccess() throws NoSuchIdException {
        Long flightId = 1L;
        managerController.deleteFlight(flightId);
        verify(flightService, times(1)).deleteFlight(flightId);
    }

    @Test
    public void testDeleteFlightThrowsNoSuchIdException() throws NoSuchIdException {
        Long flightId = 1L;
        doThrow(new NoSuchIdException("Flight with id " + flightId + " was not found")).when(flightService).deleteFlight(flightId);
        assertThrows(NoSuchIdException.class, () -> managerController.deleteFlight(flightId));
    }

    @Test
    public void testDeleteFlightWithInvalidId() throws NoSuchIdException {
        Long flightId = null;
        doThrow(NoSuchIdException.class).when(flightService).deleteFlight(flightId);
        assertThrows(NoSuchIdException.class, () -> managerController.deleteFlight(flightId));
        verify(flightService, times(1)).deleteFlight(flightId);
    }

    @Test
    public void testGetSoldTicketsFromCountNotFound() {
        String departure = "Paris";
        when(ticketService.getSoldTicketsFromCount(departure)).thenReturn(0L);
        ResponseEntity<GetSoldTicketsFromCountResponse> responseEntity = managerController.getSoldTicketsFromCount(departure);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetSoldTicketsFromCountSuccess() {
        String departure = "Kazan";
        int soldTicketsCount = 5;
        when(ticketService.getSoldTicketsFromCount(departure)).thenReturn(Long.valueOf(soldTicketsCount));

        ResponseEntity<GetSoldTicketsFromCountResponse> responseEntity = managerController.getSoldTicketsFromCount(departure);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        GetSoldTicketsFromCountResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(soldTicketsCount, response.getSoldTicketsFromCount());
    }

    @Test
    public void testGetSoldTicketsFromCountEmptyDeparture() {
        String departure = "";
        Long soldTicketsCount = 0L;
        when(ticketService.getSoldTicketsFromCount(departure)).thenReturn(soldTicketsCount);

        ResponseEntity<GetSoldTicketsFromCountResponse> responseEntity = managerController.getSoldTicketsFromCount(departure);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        GetSoldTicketsFromCountResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(soldTicketsCount, response.getSoldTicketsFromCount());
    }

    @Test
    public void testGetStatisticOfSoldTicketsSuccess() {
        BigDecimal averageCommission = BigDecimal.valueOf(12.5);
        Long fromKazan = 10L;
        BigDecimal totalEarned = BigDecimal.valueOf(50000);
        Long totalSold = 2000L;
        when(ticketService.getCommissionInfo()).thenReturn(new CommissionInfo(averageCommission, BigDecimal.ZERO));
        when(ticketService.getSoldTicketsFromCount("Kazan")).thenReturn(fromKazan);
        when(ticketService.getTotalEarned()).thenReturn(totalEarned);
        when(ticketService.getTicketsSoldCount()).thenReturn(totalSold);

        ResponseEntity<GetStatisticOfSoldTicketsResponse> responseEntity = managerController.getStatisticOfSoldTickets();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        GetStatisticOfSoldTicketsResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(fromKazan, response.getFromKazan());
        assertEquals(totalEarned, response.getTotalEarned());
        assertEquals(totalSold, response.getTotalSold());
        assertEquals(averageCommission, response.getAverageCommission());
    }

    @Test
    public void testGetStatisticOfSoldTicketsNoTicketsSold() {
        BigDecimal averageCommission = BigDecimal.ZERO;
        Long fromKazan = 0L;
        BigDecimal totalEarned = BigDecimal.ZERO;
        Long totalSold = 0L;
        when(ticketService.getCommissionInfo()).thenReturn(new CommissionInfo(averageCommission, BigDecimal.ZERO));
        when(ticketService.getSoldTicketsFromCount("Kazan")).thenReturn(fromKazan);
        when(ticketService.getTotalEarned()).thenReturn(totalEarned);
        when(ticketService.getTicketsSoldCount()).thenReturn(totalSold);

        ResponseEntity<GetStatisticOfSoldTicketsResponse> responseEntity = managerController.getStatisticOfSoldTickets();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        GetStatisticOfSoldTicketsResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(fromKazan, response.getFromKazan());
        assertEquals(totalEarned, response.getTotalEarned());
        assertEquals(totalSold, response.getTotalSold());
        assertEquals(averageCommission, response.getAverageCommission());
    }

    @Test
    public void testGetStatisticOfSoldTicketsOnlyFromKazan() {
        BigDecimal averageCommission = BigDecimal.valueOf(5.5);
        Long fromKazan = 1000L;
        BigDecimal totalEarned = BigDecimal.valueOf(5500);
        Long totalSold = 1000L;
        when(ticketService.getCommissionInfo()).thenReturn(new CommissionInfo(averageCommission, BigDecimal.ZERO));
        when(ticketService.getSoldTicketsFromCount("Kazan")).thenReturn(fromKazan);
        when(ticketService.getTotalEarned()).thenReturn(totalEarned);
        when(ticketService.getTicketsSoldCount()).thenReturn(totalSold);

        ResponseEntity<GetStatisticOfSoldTicketsResponse> responseEntity = managerController.getStatisticOfSoldTickets();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        GetStatisticOfSoldTicketsResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(fromKazan, response.getFromKazan());
        assertEquals(totalEarned, response.getTotalEarned());
        assertEquals(totalSold, response.getTotalSold());
        assertEquals(averageCommission, response.getAverageCommission());
    }

    @Test
    public void testGetCommissionInfoSuccess() {
        BigDecimal averageCommission = new BigDecimal("10.00");
        BigDecimal totalCommission = new BigDecimal("100.00");
        GetCommissionInfo expected = new GetCommissionInfo(averageCommission, totalCommission);
        when(ticketService.getCommissionInfo()).thenReturn(new CommissionInfo(averageCommission, totalCommission));

        ResponseEntity<GetCommissionInfo> response = managerController.getCommissionInfo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GetCommissionInfo actual = response.getBody();
        assertNotNull(actual);
        assertEquals(expected.getAverageCommission(), actual.getAverageCommission());
        assertEquals(expected.getTotalCommissionSum(), actual.getTotalCommissionSum());
    }

    @Test
    public void testGetCommissionInfoNoTicketsSold() {
        CommissionInfo expected = new CommissionInfo(BigDecimal.ZERO, BigDecimal.ZERO);
        when(ticketService.getCommissionInfo()).thenReturn(expected);

        ResponseEntity<GetCommissionInfo> response = managerController.getCommissionInfo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GetCommissionInfo actual = response.getBody();
        assertNotNull(actual);
        assertEquals(expected.getAverageCommission(), actual.getAverageCommission());
        assertEquals(expected.getTotalCommission(), actual.getTotalCommissionSum());
    }

    @Test
    public void testGetCommissionInfoInvalidCommission() {
        BigDecimal averageCommission = BigDecimal.ZERO;
        BigDecimal totalCommission = new BigDecimal("100.00");
        CommissionInfo expected = new CommissionInfo(averageCommission, totalCommission);
        when(ticketService.getCommissionInfo()).thenReturn(expected);

        ResponseEntity<GetCommissionInfo> response = managerController.getCommissionInfo();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        GetCommissionInfo actual = response.getBody();
        assertNotNull(actual);
        assertEquals(BigDecimal.ZERO, actual.getAverageCommission());
        assertEquals(expected.getTotalCommission(), actual.getTotalCommissionSum());
    }


}