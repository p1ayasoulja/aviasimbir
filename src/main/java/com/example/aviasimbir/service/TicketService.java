package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.repo.FlightRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import com.example.aviasimbir.repo.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TicketService {
    private final TicketRepository ticketRepository;
    private final LoggerRepository loggerRepository;
    private final EntityManager entityManager;
    private final FlightRepository flightRepository;

    public TicketService(TicketRepository ticketRepository, LoggerRepository loggerRepository,
                         EntityManager entityManager, FlightRepository flightRepository) {
        this.loggerRepository = loggerRepository;
        this.ticketRepository = ticketRepository;
        this.entityManager = entityManager;
        this.flightRepository = flightRepository;
    }

    /**
     * Получить билет по идентификатору
     *
     * @param id идентификатор билета
     * @return билет
     */
    public Optional<Ticket> getTicket(Long id) {
        log.info("IN getTicket - Ticket: {} successfully found", id);
        return ticketRepository.findById(id);
    }

    /**
     * Получить список билетов
     *
     * @return список билетов
     */
    public List<Ticket> getAllTickets() {
        log.info("IN getAllTickets - List of : {} successfully found", "tickets");
        return ticketRepository.findAll();
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
    public Ticket createTicket(Flight flight, BigDecimal price, Boolean reserved, Boolean sold, Boolean commission) {
        BigDecimal priceUpdated = price;
        if (commission) {
            priceUpdated = priceUpdated.multiply(BigDecimal.valueOf(1.025));
        }
        Ticket ticket = new Ticket(flight, priceUpdated, reserved, sold, commission);
        ticketRepository.save(ticket);
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
    public Optional<Ticket> updateTicket(Long id, BigDecimal price, boolean reserved, boolean sold) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            if (price.compareTo(BigDecimal.ZERO) > 0) {
                ticket.get().setPrice(price);
            }
            if (!sold) {
                ticket.get().setReserved(reserved);
            } else {
                ticket.get().setSold(true);
            }
            ticketRepository.save(ticket.get());
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
        ticketRepository.deleteById(id);
        loggerRepository.save(new Logger("Ticket " + id + " was deleted", Instant.now()));

    }

    /**
     * Подсчитать число билетов с отправлением из Казани
     *
     * @return число билетов
     */
    public Long getTicketsFromKazanCount() {
        List<Ticket> tickets = ticketRepository.findAll();
        log.info("IN ticketsFromKazan - Number of sold tickets from Kazan successfully found");
        return tickets.stream().filter(ticket -> ticket.getFlight().getDeparture().equals("Kazan") && ticket.getSold()).count();
    }

    /**
     * Подсчитать среднюю коммиссию по проданным билетам
     *
     * @return средняя коммиссия по проданным билетам
     */
    public BigDecimal getAverageCommissionOfSoldTickets() {
        TypedQuery<Double> query = entityManager.createQuery(
                "SELECT (SUM(t.price-(t.price / 1.025)) / COUNT(t)) FROM Ticket t WHERE t.sold = true AND t.commission = true",
                Double.class
        );
        Double result = query.getSingleResult();
        if (result == null) {
            log.warn("IN getAverageCommissionOfSoldTickets - Average Commission is null");
            return null;
        }
        BigDecimal resultBigDecimal = BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP);
        log.info("IN getAverageCommissionOfSoldTickets - Average Commission : {} successfully counted", resultBigDecimal);
        return resultBigDecimal;
    }

    /**
     * Создать билеты для только что созданного рейса
     *
     * @param id         идентификатор рейса
     * @param commission флаг наличия коммисии
     * @param seats      число билетов
     * @param price      цена билетов
     */
    public void createTicketsForCreatedFlight(Long id, Boolean commission, Integer seats, BigDecimal price) {
        if (commission) {
            price = price.multiply(BigDecimal.valueOf(1.025));
        }
        for (int i = 0; i < seats / 2; i++) {
            ticketRepository.save(new Ticket(flightRepository.findById(id).get(), price, false, false, commission));
        }
    }

    /**
     * Подсчитать сколько продано билетов на данном рейсе
     *
     * @param flight рейс билета
     * @return число проданных билетов по рейсу
     */
    public Long getSoldTicketCountByFlight(Flight flight) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(t) FROM Ticket t WHERE t.flight = :flight AND t.sold = true",
                Long.class
        );
        query.setParameter("flight", flight);
        Long count = query.getSingleResult();
        log.info("IN getSoldTicketCountByFlight - Tickets: {} successfully sold on {}", count, flight);
        return count;
    }

    /**
     * Подсчитать сколько продано билетов на данных рейсах
     *
     * @param flights рейсы
     * @return число проднных билетов рейсов
     */
    public Long getAllSoldTicketsCountByFlights(List<Flight> flights) {
        Long sum = 0L;
        for (Flight flight : flights) {
            sum += getSoldTicketCountByFlight(flight);
        }
        return sum;
    }
}
