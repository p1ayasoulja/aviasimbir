package com.example.aviasimbir.controllers;

import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.PlaneAlreadyLeftException;
import com.example.aviasimbir.exceptions.TicketReservedException;
import com.example.aviasimbir.requestresponse.GetStatisticOfSoldTicketsResponse;
import com.example.aviasimbir.service.TicketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @RequestMapping(value = "/averagecommission", method = RequestMethod.GET)
    @ApiOperation("Получить среднюю коммиссию по проданным билетам")
    public ResponseEntity<BigDecimal> getAverageCommission() {
        return ResponseEntity.ok(ticketService.getAverageCommissionOfSoldTickets());
    }

    @RequestMapping(value = "/kazan", method = RequestMethod.GET)
    @ApiOperation("Получить число проданных билетов с точкой отправления Казань")
    public ResponseEntity<Long> getSoldTicketsFromKazanCount() {
        return ResponseEntity.ok(ticketService.getTicketsFromKazanCount());
    }

    @RequestMapping(value = "/soldstatistic", method = RequestMethod.GET)
    @ApiOperation("Получить статистику по проданным билетам")
    public ResponseEntity<GetStatisticOfSoldTicketsResponse> getStatisticOfSoldTickets() {
        BigDecimal averagecommission = ticketService.getAverageCommissionOfSoldTickets();
        Long fromKazan = ticketService.getTicketsFromKazanCount();
        BigDecimal totalEarned = ticketService.getTotalEarned();
        Long totalSold = ticketService.getTicketsSoldCount();
        GetStatisticOfSoldTicketsResponse getStatisticOfSoldTicketsResponse = new GetStatisticOfSoldTicketsResponse(totalSold, fromKazan, averagecommission, totalEarned);
        return ResponseEntity.ok(getStatisticOfSoldTicketsResponse);
    }

    @RequestMapping(value = "/{id}/reserve", method = RequestMethod.PATCH)
    @ApiOperation("Забронировать билет")
    public ResponseEntity<Object> reserveTicket(@PathVariable("id") Long id) throws NoSuchIdException, TicketReservedException, PlaneAlreadyLeftException {
        Ticket ticket = ticketService.getTicket(id);
        ticketService.reserveTicket(ticket);
        return ResponseEntity.ok().build();
    }
}
