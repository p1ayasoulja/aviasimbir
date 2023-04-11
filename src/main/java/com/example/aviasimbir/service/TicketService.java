package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.repo.FlightRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import com.example.aviasimbir.repo.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TicketService {
    private final TicketRepository ticketRepository;
    private final LoggerRepository loggerRepository;
    private final EntityManager entityManager;
    private final FlightRepository flightRepository;
    private final TicketReservationService ticketReservationService;
    @Value("${ticket.fixedcommission}")
    private BigDecimal fixedcommission;
    @Value("${ticket.reservation.timeout}")
    private Long reservationTimeout;

    public TicketService(TicketRepository ticketRepository, LoggerRepository loggerRepository,
                         EntityManager entityManager, FlightRepository flightRepository, TicketReservationService ticketReservationService) {
        this.loggerRepository = loggerRepository;
        this.ticketRepository = ticketRepository;
        this.entityManager = entityManager;
        this.flightRepository = flightRepository;
        this.ticketReservationService = ticketReservationService;
    }

    /**
     * Получить билет по идентификатору
     *
     * @param id идентификатор билета
     * @return билет
     */
    public Optional<Ticket> findTicket(Long id) {
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
            BigDecimal hundred = BigDecimal.valueOf(100);
            priceUpdated = priceUpdated.multiply(BigDecimal.ONE.add(fixedcommission.divide(hundred, 3, RoundingMode.HALF_UP)));
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
        TypedQuery<BigDecimal> query = entityManager.createQuery(
                "SELECT (SUM(t.price)) / COUNT(t) FROM Ticket t WHERE t.sold = true AND t.commission = true",
                BigDecimal.class
        );
        BigDecimal result = query.getSingleResult();
        if (result == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal averageCommission = result.subtract(result.divide(BigDecimal.ONE.add(fixedcommission.divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP)), 3, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));

        log.info("IN getAverageCommissionOfSoldTickets - Average Commission : {} successfully counted", averageCommission);
        return averageCommission;
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
            price = price.multiply((BigDecimal.ONE.add(fixedcommission.divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP))));
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
     * Число проданных билетов на данных рейсах
     *
     * @param flights рейсы
     * @return число проднных билетов рейсов
     */
    public Long getAllSoldTicketsCountByFlights(List<Flight> flights) {
        Long sum = 0L;
        for (Flight flight : flights) {
            sum += getSoldTicketCountByFlight(flight);
        }
        log.info("IN getAllSoldTicketsCountByFlights - Sold tickets : {} successfully counted", sum);
        return sum;
    }

    /**
     * Подсчитать на какую сумму продано билетов на данном рейсе
     *
     * @param flight рейс билета
     * @return сумма
     */
    public BigDecimal getSoldTicketSumByFlight(Flight flight) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
                "SELECT SUM(t.price) FROM Ticket t WHERE t.flight = :flight AND t.sold = true",
                BigDecimal.class
        );
        query.setParameter("flight", flight);
        BigDecimal sum = query.getSingleResult();
        if (sum == null) {
            sum = BigDecimal.ZERO;
        }
        log.info("IN getSoldTicketSumByFlight - Sum of sold tickets : {} in {}", sum, flight);
        return sum;
    }

    /**
     * Получить на какую сумму было продано билетов
     *
     * @param flights рейсы
     * @return сумма заработка
     */
    public BigDecimal getTotalEarnedByFlights(List<Flight> flights) {
        BigDecimal earned = BigDecimal.ZERO;
        for (Flight flight : flights) {
            earned = earned.add(getSoldTicketSumByFlight(flight));
        }
        log.info("IN getTotalEarnedByFlights - Sum of sold tickets successfully counted : {}", earned);
        return earned;
    }

    /**
     * Продать билет
     *
     * @param ticket билет
     */
    public void sellTicket(Ticket ticket) {
        ticket.setSold(true);
        ticket.setReserved(true);
        log.info("IN sellTicket - Ticket successfully sold");
        ticketRepository.save(ticket);
    }

    /**
     * Забронировать билет
     *
     * @param ticket билет
     */
    public void reserveTicket(Ticket ticket) {
        if (!ticket.getReserved()) {
            ticket.setReserved(true);
            ticket.setReservedUntil(LocalDateTime.now().plusSeconds(reservationTimeout));
            log.info("IN reserveTicket - Ticket successfully reserved");
            ticketRepository.save(ticket);
            ticketReservationService.scheduleTicketReservation(ticket);
        } else log.info("IN reserveTicket - Ticket already reserved");
    }

    /**
     * Снять бронь с билета
     *
     * @param ticket билет
     */
    public void cancelTicketReserve(Ticket ticket) {
        if (ticket.getReserved() && ticket.getReservedUntil().isBefore(LocalDateTime.now())) {
            ticket.setReserved(false);
            ticket.setReservedUntil(null);
            ticketRepository.save(ticket);
        }
        log.info("IN cancelTicketReserve - Ticket reservation successfully cancelled");
    }

    /**
     * Получить список доступных билетов по рейсу
     *
     * @return список доступных билетов рейса
     */
    public List<Ticket> getAllAvailableTicketsByFlight(Flight flight) {
        TypedQuery<Ticket> query = entityManager.createQuery(
                "SELECT t FROM Ticket t WHERE t.flight = :flight AND t.reserved=false", Ticket.class);
        query.setParameter("flight", flight);
        log.info("IN getAllAvailableTicketsByFlight - Tickets successfully found");
        return query.getResultList();
    }

    /**
     * Возвращает список всех билетов, связанных с определенным рейсом.
     *
     * @param flight Рейс, для которого нужно получить билеты
     * @param sold   true, если нужно получить список проданных билетов, false - для списка доступных для продажи
     * @return Список билетов
     */
    public List<Ticket> getAllTicketsByFlight(Flight flight, Boolean sold) {
        TypedQuery<Ticket> query;
        if (sold) {
            query = entityManager.createQuery(
                    "SELECT t FROM Ticket t WHERE t.flight = :flight", Ticket.class);
        } else {
            query = entityManager.createQuery(
                    "SELECT t FROM Ticket t WHERE t.flight = :flight AND t.sold=false", Ticket.class);
        }
        query.setParameter("flight", flight);
        log.info("IN getAllTicketsByFlight - Tickets successfully found");
        return query.getResultList();
    }

    /**
     * Подсчитать на какую сумму было продано билетов
     *
     * @return сумма заработка
     */
    public BigDecimal getTotalEarned() {
        BigDecimal earned = BigDecimal.ZERO;
        List<Ticket> tickets = ticketRepository.findBySold();
        for (Ticket ticket : tickets) {
            earned = earned.add(ticket.getPrice());
        }
        log.info("IN getTotalEarned - Sum of sold tickets successfully counted : {}", earned);
        return earned;
    }

    /**
     * Подсчитать число проданных билетов
     *
     * @return число билетов
     */
    public Long getTicketsSoldCount() {
        List<Ticket> tickets = ticketRepository.findAll();
        log.info("IN ticketsFromKazan - Number of sold tickets from Kazan successfully found");
        return tickets.stream().filter(Ticket::getSold).count();
    }
}
