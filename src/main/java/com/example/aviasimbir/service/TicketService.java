package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.repo.LoggerRepo;
import com.example.aviasimbir.repo.TicketRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TicketService {
    private final TicketRepo ticketRepo;
    private final LoggerRepo loggerRepo;

    public TicketService(TicketRepo ticketRepo, LoggerRepo loggerRepo) {
        this.loggerRepo = loggerRepo;
        this.ticketRepo = ticketRepo;
    }

    /**
     * Получить билет по идентификатору
     *
     * @param id идентификатор билета
     * @return билет
     */
    public Optional<Ticket> getTicket(Long id) {
        log.info("IN getTicket - Ticket: {} successfully found", id);
        return ticketRepo.findById(id);
    }

    /**
     * Получить список билетов
     *
     * @return список билетов
     */
    public List<Ticket> getAllTickets() {
        log.info("IN getAllTickets - List of : {} successfully found", "tickets");
        return ticketRepo.findAll();
    }

    /**
     * Создать билет
     *
     * @param flight     рейс билета
     * @param price      цена билета
     * @param reserved   статус брони дилета
     * @param sold       статус доступности билета
     * @param commission статус наличия коммиссии
     * @return билет
     */
    public Ticket createTicket(Flight flight, Integer price, Boolean reserved, Boolean sold, Boolean commission) {
        Integer priceUpdated = price;
        if (commission) {
            priceUpdated = (int) (priceUpdated * 1.025);
        }
        Ticket ticket = new Ticket(flight, priceUpdated, reserved, sold, commission);
        ticketRepo.save(ticket);
        log.info("IN createTicket - Ticket: {} successfully created", ticket.getId());
        return ticket;
    }

    /**
     * Обновить билет
     *
     * @param id       идентификатор билета
     * @param price    цена билета
     * @param reserved статус брони дилета
     * @param sold     статус доступности билета
     * @return билет
     */
    public Optional<Ticket> updateTicket(Long id, Integer price, boolean reserved, boolean sold) {
        Optional<Ticket> ticket = ticketRepo.findById(id);
        if (ticket.isPresent()) {
            if (price > 0) {
                ticket.get().setPrice(price);
            }
            if (!sold) {
                ticket.get().setReserved(reserved);
            } else {
                ticket.get().setSold(true);
            }
            ticketRepo.save(ticket.get());
            log.info("IN updateTicket - Ticket: {} successfully updated", id);
            return ticket;
        } else {
            log.info("IN updateTicket - Ticket: {} was not updated", id);
            return Optional.empty();
        }
    }

    /**
     * Удалить билет
     *
     * @param id идентификатор билета
     */
    public void deleteTicket(Long id) {
        Optional<Ticket> ticket = ticketRepo.findById(id);
        if (ticket.isPresent()) {
            ticketRepo.deleteById(id);
            log.info("IN deleteTicket - Ticket: {} successfully deleted", id);
            loggerRepo.save(new Logger(ticket.get().toString() + " was deleted", LocalDateTime.now()));
        }
    }

    /**
     * Подсчитать число билетов с отправлением из Казани
     *
     * @return число билетов
     */
    public Long getTicketsToKazanCount() {
        List<Ticket> tickets = ticketRepo.findAll();
        log.info("IN ticketsToKazan - Tickets to {} successfully found", "Kazan");
        return tickets.stream().filter(ticket -> ticket.getFlight().getDeparture().equals("Kazan") && ticket.getSold()).count();
    }

    /**
     * Подсчитать среднюю коммиссию по проданным билетам
     *
     * @return средняя коммиссия по проданным билетам
     */
    public Long getAverageCommissionOfSoldTickets() {
        List<Ticket> tickets = ticketRepo.findAll();
        long price = tickets.stream().filter(Ticket::getCommission).mapToLong(Ticket::getPrice).sum();
        long sum = (long) ((long) (price) - ((price) / (1.025)));
        long numb = tickets.stream().filter(Ticket::getCommission).count();
        if (numb != 0) {
            log.info("IN getAverageCommissionOfSoldTickets - Average Commission: {} successfully counted", sum / numb);
            return sum / numb;
        } else return (long) 0;
    }

    /**
     * Подсчитать на какую сумму продано билетов на данном рейсе
     *
     * @param flight рейс билета
     * @return сумма всех проданных билетов по рейсу
     */
    public Long getSoldTicketCount(Flight flight) {
        long sum = 0;
        List<Ticket> tickets = ticketRepo.findByFlight(flight);
        for (Ticket ticket : tickets) {
            if (ticket.getSold()) {
                sum++;
            }
        }
        log.info("IN getSoldTicketCount - Tickets: {} successfully sold", sum);
        return sum;
    }
}
