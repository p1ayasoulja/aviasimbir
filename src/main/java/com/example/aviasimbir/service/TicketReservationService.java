package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Ticket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class TicketReservationService {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final TicketService ticketService;
    @Value("${ticket.reservation.timeout}")
    private int reservationTimeout;

    @Lazy
    public TicketReservationService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Установка таймера бронирования билета
     *
     * @param ticket билет
     */
    public void scheduleTicketReservation(Ticket ticket) {
        executorService.schedule(() -> {
            ticketService.cancelTicketReserve(ticket);
        }, reservationTimeout, TimeUnit.SECONDS);
    }
}
