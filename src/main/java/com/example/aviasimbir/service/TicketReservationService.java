package com.example.aviasimbir.service;

import com.example.aviasimbir.exceptions.NoSuchIdException;
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
    private int reservationTimeoutInMinutes;

    @Lazy
    public TicketReservationService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Установка таймера бронирования билета
     *
     * @param id идентификатор билета
     */
    public void scheduleTicketReservation(Long id) {
        executorService.schedule(() -> {
            try {
                ticketService.cancelTicketReserve(id);
            } catch (NoSuchIdException e) {
                throw new RuntimeException(e);
            }
        }, reservationTimeoutInMinutes, TimeUnit.MINUTES);
    }
}
