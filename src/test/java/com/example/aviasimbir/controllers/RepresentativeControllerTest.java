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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class RepresentativeControllerTest {
    private final AirlineService airlineService = mock(AirlineService.class);
    private final TicketService ticketService = mock(TicketService.class);
    private final PlaneService planeService = mock(PlaneService.class);
    private final JwtUser jwtUser = mock(JwtUser.class);
    private final RepresentativeController representativeController = new RepresentativeController(planeService, airlineService, ticketService);

    @Test
    public void testGetNumberOfPlanesSuccess() throws Exception {
        long planesCount = 5L;
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        when(planeService.getAirlinePlanesCount(1L, "testUser")).thenReturn(planesCount);

        ResponseEntity<GetNumberOfPlanesResponse> responseEntity = representativeController.getNumberOfPlanes(1L, jwtUser);
        GetNumberOfPlanesResponse response = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
        assertEquals(planesCount, response.getNumberOfPlanes());
    }

    @Test
    public void testGetNumberOfPlanesThrowsNotRepresentativeException() throws NotRepresentativeException {
        Long airlineId = 1L;
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        doThrow(new NotRepresentativeException("User " + jwtUser.getUsername() + " is not a representative of airline with id " + airlineId)).when(planeService).getAirlinePlanesCount(airlineId, jwtUser.getUsername());
        assertThrows(NotRepresentativeException.class, () -> representativeController.getNumberOfPlanes(airlineId, jwtUser));
    }

    @Test
    public void testGetNumberOfPlanesWithError() throws Exception {
        when(jwtUser.getUsername()).thenReturn("testUser");
        when(planeService.getAirlinePlanesCount(1L, "testUser")).thenThrow(new RuntimeException("Internal Server Error"));
        assertThrows(RuntimeException.class, () -> representativeController.getNumberOfPlanes(1L, jwtUser));
    }

    @Test
    public void testNumberOfSoldTicketsSuccess() throws Exception {
        long totalSoldTickets = 5L;
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        when(airlineService.getTotalSoldTickets(1L, "testUser")).thenReturn(totalSoldTickets);

        ResponseEntity<GetNumberOfSoldTicketsResponse> responseEntity = representativeController.getNumberOfSoldTickets(1L, jwtUser);
        GetNumberOfSoldTicketsResponse response = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
        assertEquals(totalSoldTickets, response.getNumberOfSoldTickets());
    }

    @Test
    public void testNumberOfSoldTicketsThrowsNotRepresentativeException() throws NotRepresentativeException {
        Long airlineId = 1L;
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        doThrow(new NotRepresentativeException("User " + jwtUser.getUsername() + " is not a representative of airline with id " + airlineId)).when(airlineService).getTotalSoldTickets(airlineId, jwtUser.getUsername());
        assertThrows(NotRepresentativeException.class, () -> representativeController.getNumberOfSoldTickets(airlineId, jwtUser));
    }

    @Test
    public void testNumberOfSoldTicketsWithError() throws Exception {
        when(jwtUser.getUsername()).thenReturn("testUser");
        when(airlineService.getTotalSoldTickets(1L, "testUser")).thenThrow(new RuntimeException("Internal Server Error"));
        assertThrows(RuntimeException.class, () -> representativeController.getNumberOfSoldTickets(1L, jwtUser));
    }

    @Test
    public void testTotalEarnedSuccess() throws Exception {
        BigDecimal totalEarned = BigDecimal.valueOf(1000L);
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        when(airlineService.getTotalEarnedByAirline(1L, "testUser")).thenReturn(totalEarned);

        ResponseEntity<GetTotalEarnedResponse> responseEntity = representativeController.getTotalEarned(1L, jwtUser);
        GetTotalEarnedResponse response = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
        assertEquals(totalEarned, response.getTotalEarned());
    }

    @Test
    public void testTotalEarnedThrowsNotRepresentativeException() throws NotRepresentativeException {
        Long airlineId = 1L;
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        doThrow(new NotRepresentativeException("User " + jwtUser.getUsername() + " is not a representative of airline with id " + airlineId)).when(airlineService).getTotalEarnedByAirline(airlineId, jwtUser.getUsername());
        assertThrows(NotRepresentativeException.class, () -> representativeController.getTotalEarned(airlineId, jwtUser));
    }

    @Test
    public void testTotalEarnedWithError() throws Exception {
        when(jwtUser.getUsername()).thenReturn("testUser");
        when(airlineService.getTotalEarnedByAirline(1L, "testUser")).thenThrow(new RuntimeException("Internal Server Error"));
        assertThrows(RuntimeException.class, () -> representativeController.getTotalEarned(1L, jwtUser));
    }

    @Test
    public void testDeletePlaneSuccess() throws NoSuchIdException, NotRepresentativeException {
        Long planeId = 1L;
        Long airlineId = 1L;
        String username = "testUser";
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());
        representativeController.deletePlane(planeId, jwtUser, airlineId);
        verify(planeService, times(1)).deletePlane(username, planeId, airlineId);
    }

    @Test
    public void testDeletePlaneThrowsNoSuchIdException() throws NoSuchIdException, NotRepresentativeException {
        Long airlineId = 1L;
        Long planeId = 1L;
        String username = "testUser";
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());

        doThrow(new NoSuchIdException("Plane with id " + planeId + " was not found")).when(planeService).deletePlane(username, airlineId, planeId);
        assertThrows(NoSuchIdException.class, () -> representativeController.deletePlane(airlineId, jwtUser, planeId));
    }

    @Test
    public void testDeletePlaneThrowsNotRepresentativeException() throws NoSuchIdException, NotRepresentativeException {
        Long airlineId = 1L;
        Long planeId = 1L;
        String username = "testUser";
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());

        doThrow(new NotRepresentativeException("User " + username + " is not a representative of airline with id " + airlineId)).when(planeService).deletePlane(username, airlineId, planeId);

        assertThrows(NotRepresentativeException.class, () -> representativeController.deletePlane(planeId, jwtUser, airlineId));
    }

    @Test
    public void testUpdateAirlineSuccess() throws Exception {
        long id = 1L;
        Airline airline = new Airline("updatedAirlineName");
        airline.setId(id);
        String username = "testUser";
        String name = airline.getName();
        UpdateAirlineRequest updateAirlineRequest = new UpdateAirlineRequest(name);
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());
        when(airlineService.updateAirline(id, name, username)).thenReturn(airline);

        ResponseEntity<AirlineResponse> responseEntity = representativeController.updateAirline(id, jwtUser, updateAirlineRequest);
        AirlineResponse response = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(name, response.getName());
    }

    @Test
    public void testUpdateAirlineWithInvalidId() throws Exception {
        long id = 0L;
        String username = "testUser";
        String name = "updatedAirlineName";
        UpdateAirlineRequest updateAirlineRequest = new UpdateAirlineRequest(name);
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());
        doThrow(new NoSuchIdException("Airline with id " + id + " was not found")).when(airlineService).updateAirline(id, name, username);

        assertThrows(NoSuchIdException.class, () -> representativeController.updateAirline(id, jwtUser, updateAirlineRequest));
    }

    @Test
    public void testUpdateAirlineWithUnauthorizedUser() throws Exception {
        long id = 1L;
        String username = "testUser";
        String name = "updatedName";
        UpdateAirlineRequest updateAirlineRequest = new UpdateAirlineRequest(name);
        JwtUser jwtUser = new JwtUser(1L, username, "password", Collections.emptyList());
        doThrow(new NotRepresentativeException("User " + username + " is not a representative of airline with id " + id)).when(airlineService).updateAirline(id, name, username);

        assertThrows(NotRepresentativeException.class, () -> representativeController.updateAirline(id, jwtUser, updateAirlineRequest));
    }

    @Test
    public void testGetAllTicketsSuccess() throws Exception {
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        GetAllTicketsRequest getAllTicketsRequest = new GetAllTicketsRequest(false);
        List<Ticket> tickets = Arrays.asList(new Ticket(1L, BigDecimal.TEN, false), new Ticket(2L, BigDecimal.TEN, true));
        when(ticketService.getTicketsByAirline(1L, false, "testUser")).thenReturn(tickets);

        ResponseEntity<List<GetAllTicketsResponse>> responseEntity = representativeController.getTickets(1L, jwtUser, getAllTicketsRequest);
        List<GetAllTicketsResponse> response = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(BigDecimal.TEN, response.get(0).getPrice());
        assertEquals(2L, response.get(1).getId());
        assertEquals(BigDecimal.TEN, response.get(1).getPrice());
    }

    @Test
    public void testGetUnsoldTicketsSuccess() throws Exception {
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        GetAllTicketsRequest getAllTicketsRequest = new GetAllTicketsRequest(true);
        List<Ticket> tickets = List.of(new Ticket(1L, BigDecimal.TEN, true));
        when(ticketService.getTicketsByAirline(1L, true, "testUser")).thenReturn(tickets);

        ResponseEntity<List<GetAllTicketsResponse>> responseEntity = representativeController.getTickets(1L, jwtUser, getAllTicketsRequest);
        List<GetAllTicketsResponse> response = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(BigDecimal.TEN, response.get(0).getPrice());
    }

    @Test
    public void testGetAllTicketsWithInvalidAirlineId() throws NotRepresentativeException, NoSuchIdException {
        JwtUser jwtUser = new JwtUser(1L, "testUser", "password", Collections.emptyList());
        GetAllTicketsRequest getAllTicketsRequest = new GetAllTicketsRequest(true);
        when(ticketService.getTicketsByAirline(1L, true, "testUser")).thenThrow(NoSuchIdException.class);
        assertThrows(NoSuchIdException.class, () -> representativeController.getTickets(1L, jwtUser, getAllTicketsRequest));
    }

    @Test
    public void testCreatePlaneSuccess() throws NotRepresentativeException, WrongArgumentException, NoSuchIdException {
        CreatePlaneRequest createPlaneRequest = new CreatePlaneRequest("Boeing", "777", 300, 1L);
        Airline airline = new Airline("testAirline");
        airline.setId(1L);
        when(jwtUser.getUsername()).thenReturn("testUser");
        Plane plane = new Plane("Boeing", "777", 300, airline);
        plane.setId(1L);
        when(planeService.createPlane("testUser", 1L, "Boeing", "777", 300, 1L)).thenReturn(plane);

        ResponseEntity<PlaneResponse> responseEntity = representativeController.createPlane(createPlaneRequest, jwtUser, 1L);
        PlaneResponse response = responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Boeing", response.getBrand());
        assertEquals("777", response.getModel());
        assertEquals(300, response.getSeats());
        assertEquals("testAirline", response.getAirline());
    }

    @Test
    public void testCreatePlaneWithIncorrectAirlineId() throws Exception {
        CreatePlaneRequest createPlaneRequest = new CreatePlaneRequest("Boeing", "777", 300, 2L);
        when(jwtUser.getUsername()).thenReturn("testUser");
        when(planeService.createPlane("testUser", 2L, "Boeing", "777", 300, 2L)).thenThrow(new NoSuchIdException("Airline with ID 2 does not exist."));

        assertThrows(NoSuchIdException.class, () -> representativeController.createPlane(createPlaneRequest, jwtUser, 2L));
    }

    @Test
    public void testCreatePlaneWithIncorrectSeats() throws NotRepresentativeException, WrongArgumentException, NoSuchIdException {
        CreatePlaneRequest createPlaneRequest = new CreatePlaneRequest("Boeing", "777", -2, 1L);
        when(jwtUser.getUsername()).thenReturn("testUser");

        when(planeService.createPlane(eq("testUser"), eq(1L), eq("Boeing"), eq("777"), eq(-2), eq(1L)))
                .thenThrow(new WrongArgumentException("All fields must be filled"));
        assertThrows(WrongArgumentException.class, () -> representativeController.createPlane(createPlaneRequest, jwtUser, 1L));
    }


}