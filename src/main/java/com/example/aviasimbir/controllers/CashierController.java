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
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cashier")
public class CashierController {
    private final TicketService ticketService;
    private final OrderService orderService;

    public CashierController(TicketService ticketService, OrderService orderService) {
        this.ticketService = ticketService;
        this.orderService = orderService;
    }

    @RequestMapping(value = "/{id}/sell", method = RequestMethod.PUT)
    @ApiOperation("Продать билет")
    public ResponseEntity<Object> sellTicket(@PathVariable("id") Long id) throws NoSuchIdException, TicketSoldException, PlaneAlreadyLeftException {
        ticketService.sellTicket(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/order/{id}", method = RequestMethod.PUT)
    @ApiOperation("Поменять статус заказа")
    public ResponseEntity<Object> changeOrderStatus(@PathVariable("id") Long id, @RequestBody ChangeOrderStatusRequest changeOrderStatusRequest) throws BadStatusException, NoSuchIdException {
        orderService.changeOrderStatus(id, changeOrderStatusRequest.getStatus());
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    @ApiOperation("Получить список всех заказов")
    public ResponseEntity<List<GetOrderResponse>> getOrders() {
        List<Order> orderList = orderService.getAll();
        List<GetOrderResponse> getOrderResponses = orderList.stream()
                .map(order -> new GetOrderResponse(order.getId(), order.getStatus().name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(getOrderResponses);
    }
}
