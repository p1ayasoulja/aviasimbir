package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Order;
import com.example.aviasimbir.exceptions.BadStatusException;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.PlaneAlreadyLeftException;
import com.example.aviasimbir.exceptions.TicketSoldException;
import com.example.aviasimbir.requestresponse.ChangeOrderStatusRequest;
import com.example.aviasimbir.requestresponse.GetOrderResponse;
import com.example.aviasimbir.service.OrderService;
import com.example.aviasimbir.service.TicketService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class CashierControllerTest {

    private final TicketService ticketService = Mockito.mock(TicketService.class);
    private final OrderService orderService = Mockito.mock(OrderService.class);
    private final CashierController cashierController = new CashierController(ticketService, orderService);

    @Test
    public void testSellTicket() throws NoSuchIdException, TicketSoldException, PlaneAlreadyLeftException {
        Long ticketId = 1L;
        Mockito.doNothing().when(ticketService).sellTicket(ticketId);

        ResponseEntity<Object> response = cashierController.sellTicket(ticketId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(ticketService, Mockito.times(1)).sellTicket(ticketId);
    }

    @Test
    public void testSellTicketNoSuchId() throws NoSuchIdException, TicketSoldException, PlaneAlreadyLeftException {
        Long ticketId = 1L;
        Mockito.doThrow(new NoSuchIdException("Ticket with id " + ticketId + " not found")).when(ticketService).sellTicket(ticketId);

        Assertions.assertThrows(NoSuchIdException.class, () -> cashierController.sellTicket(ticketId));
        Mockito.verify(ticketService, Mockito.times(1)).sellTicket(ticketId);
    }

    @Test
    public void testSellTicketPlaneAlreadyLeft() throws NoSuchIdException, TicketSoldException, PlaneAlreadyLeftException {
        Long ticketId = 1L;
        Mockito.doThrow(new PlaneAlreadyLeftException("Plane has already left airport")).when(ticketService).sellTicket(ticketId);

        Assertions.assertThrows(PlaneAlreadyLeftException.class, () -> cashierController.sellTicket(ticketId));
        Mockito.verify(ticketService, Mockito.times(1)).sellTicket(ticketId);
    }

    @Test
    public void testChangeOrderStatusNoSuchId() throws BadStatusException, NoSuchIdException {
        Long nonExistingOrderId = 999L;
        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(Order.Status.CANCELLED.name());

        Mockito.doThrow(NoSuchIdException.class).when(orderService).changeOrderStatus(nonExistingOrderId, request.getStatus());

        Assertions.assertThrows(NoSuchIdException.class, () -> cashierController.changeOrderStatus(nonExistingOrderId, request));
    }

    @Test
    public void testChangeOrderStatusBadStatus() throws BadStatusException, NoSuchIdException {
        Long orderId = 123L;
        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(null);

        OrderService orderServiceMock = Mockito.mock(OrderService.class);
        Mockito.doThrow(BadStatusException.class).when(orderServiceMock).changeOrderStatus(orderId, request.getStatus());

        CashierController controller = new CashierController(null, orderServiceMock);

        Assertions.assertThrows(BadStatusException.class, () -> controller.changeOrderStatus(orderId, request));
    }

    @Test
    public void testChangeOrderStatusAccepted() throws BadStatusException, NoSuchIdException {
        Long orderId = 123L;
        ChangeOrderStatusRequest request = new ChangeOrderStatusRequest(Order.Status.CANCELLED.name());
        Mockito.doNothing().when(orderService).changeOrderStatus(orderId, request.getStatus());
        ResponseEntity<Object> response = cashierController.changeOrderStatus(orderId, request);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void testGetOrdersReturnsEmptyList() {
        when(orderService.getAll()).thenReturn(Collections.emptyList());
        ResponseEntity<List<GetOrderResponse>> responseEntity = cashierController.getOrders();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(Objects.requireNonNull(responseEntity.getBody()).isEmpty());
    }

    @Test
    void testGetOrdersReturnsNonEmptyList() {
        List<Order> orderList = Arrays.asList(
                new Order(1L, Order.Status.NEW),
                new Order(2L, Order.Status.NEW),
                new Order(3L, Order.Status.NEW)
        );
        when(orderService.getAll()).thenReturn(orderList);
        ResponseEntity<List<GetOrderResponse>> responseEntity = cashierController.getOrders();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(3, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(1L, responseEntity.getBody().get(0).getId());
        assertEquals(Order.Status.NEW.name(), responseEntity.getBody().get(0).getStatus());
    }
}