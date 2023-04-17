package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.*;
import com.example.aviasimbir.repo.FlightRepository;
import com.example.aviasimbir.repo.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class TicketService {
    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;
    private final TicketReservationService ticketReservationService;
    private final UserService userService;
    @Value("${ticket.fixedcommission}")
    private BigDecimal fixedcommission;
    @Value("${ticket.reservation.timeout}")
    private Long reservationTimeoutInMinutes;

    public TicketService(TicketRepository ticketRepository, FlightRepository flightRepository, TicketReservationService ticketReservationService, UserService userService) {
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
        this.ticketReservationService = ticketReservationService;
        this.userService = userService;
    }

    /**
     * Создать билет
     *
     * @param flightId   идентификатор рейса
     * @param price      цена билета
     * @param reserved   статус брони дилета
     * @param sold       статус доступности билета
     * @param commission статус наличия коммиссии
     * @return билет
     * @throws NoSuchIdException      ошибка неверного идентификатора
     * @throws WrongArgumentException ошибка неверно введенных данных
     */
    @Transactional
    public Ticket createTicket(Long flightId, BigDecimal price, Boolean reserved, Boolean sold, Boolean commission) throws WrongArgumentException, NoSuchIdException {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new NoSuchIdException("Flight with id " + flightId + " was not found"));
        if (flight == null || price.compareTo(BigDecimal.ZERO) <= 0 || reserved == null || sold == null || commission == null) {
            throw new WrongArgumentException("All fields must be filled");
        }
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
     * Подсчитать число билетов с отправлением из Казани
     *
     * @return число билетов
     */
    public Long getSoldTicketsFromKazanCount() {
        long number = ticketRepository.getTicketsByFlightDepartureAndSold("Kazan");
        log.info("IN getSoldTicketsFromKazanCount - Number of sold tickets from Kazan successfully counted");
        return number;
    }

    /**
     * Подсчитать среднюю коммиссию по проданным билетам
     *
     * @return средняя коммиссия по проданным билетам
     */
    public BigDecimal getAverageCommissionOfSoldTickets() {
        BigDecimal averageTicketPrice = ticketRepository.getAverageTicketPrice();
        BigDecimal averageCommission;
        if (averageTicketPrice.equals(BigDecimal.ZERO)) {
            averageCommission = BigDecimal.ZERO;
        } else {
            BigDecimal commissionPercentage = fixedcommission.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal divisor = BigDecimal.ONE.add(commissionPercentage);
            BigDecimal dividend = averageTicketPrice.divide(divisor, 3, RoundingMode.HALF_UP);
            averageCommission = averageTicketPrice.subtract(dividend).setScale(2, RoundingMode.HALF_UP);
        }
        log.info("IN getAverageCommissionOfSoldTickets - Average Commission : {} successfully counted", averageCommission);
        return averageCommission;
    }

    /**
     * Продать билет
     *
     * @param id идентификатор билета
     * @throws NoSuchIdException         ошибка неверного идентификатора
     * @throws PlaneAlreadyLeftException ошибка действия с билетом на уже улетевший рейс
     */
    @Transactional
    public void sellTicket(Long id) throws TicketSoldException, PlaneAlreadyLeftException, NoSuchIdException {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Ticket with id " + id + " was not found"));
        Flight flight = flightRepository.findById(ticket.getFlight().getId())
                .orElseThrow(() -> new NoSuchIdException("Flight was not found"));
        if (!flight.getDepartureTime().isAfter(ZonedDateTime.now())) {
            throw new PlaneAlreadyLeftException("Plane has already left airport");
        }
        if (ticket.getSold()) {
            throw new TicketSoldException("Ticket already sold");
        } else {
            ticket.setSold(true);
            ticket.setReserved(true);
            log.info("IN sellTicket - Ticket successfully sold");
            ticketRepository.save(ticket);
        }
    }

    /**
     * Забронировать билет
     *
     * @param id идентификатор билет
     * @throws NoSuchIdException         ошибка неверного идентификатора
     * @throws PlaneAlreadyLeftException ошибка действия с билетом на уже улетевший рейс
     * @throws TicketReservedException   ошибка бронирования билета
     */
    @Transactional
    public void reserveTicket(Long id) throws TicketReservedException, PlaneAlreadyLeftException, NoSuchIdException {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Ticket was not found"));
        Flight flight = flightRepository.findById(ticket.getFlight().getId())
                .orElseThrow(() -> new NoSuchIdException("Flight was not found"));
        if (!flight.getDepartureTime().isAfter(ZonedDateTime.now())) {
            throw new PlaneAlreadyLeftException("Plane has already left airport");
        }
        if (!ticket.getReserved()) {
            ticket.setReserved(true);
            ticket.setReservedUntil(LocalDateTime.now().plusMinutes(reservationTimeoutInMinutes));
            log.info("IN reserveTicket - Ticket successfully reserved until {}", ticket.getReservedUntil());
            ticketRepository.save(ticket);
            ticketReservationService.scheduleTicketReservation(ticket.getId());
        } else {
            throw new TicketReservedException("Ticket already reserved");
        }
    }

    /**
     * Снять бронь с билета
     *
     * @param id идентификатор билета
     * @throws NoSuchIdException ошибка неверного идентификатора
     */
    @Transactional
    public void cancelTicketReserve(Long id) throws NoSuchIdException {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Ticket with id " + id + " was not found"));
        if (ticket.getReserved() && ticket.getReservedUntil().isBefore(LocalDateTime.now())) {
            ticket.setReserved(false);
            ticket.setReservedUntil(null);
            ticketRepository.save(ticket);
            log.info("IN cancelTicketReserve - Ticket reservation cancelled");
        }
    }

    /**
     * Получить список доступных билетов по рейсу
     *
     * @param flightId идентификатор рейса
     * @return список доступных билетов рейса
     * @throws NoSuchIdException ошибка неверного идентификатора
     */
    public List<Ticket> getAllAvailableTicketsByFlight(Long flightId) throws NoSuchIdException {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new NoSuchIdException("Flight with id " + flightId + " was not found"));
        List<Ticket> tickets = ticketRepository.getTicketByFlightAndNotReserved(flight);
        log.info("IN getAllAvailableTicketsByFlight - Tickets successfully found");
        return tickets;
    }

    /**
     * Подсчитать на какую сумму было продано билетов
     *
     * @return сумма заработка
     */
    public BigDecimal getTotalEarned() {
        BigDecimal earned = ticketRepository.getTotalEarnedSum();
        log.info("IN getTotalEarned - Sum of sold tickets successfully counted : {}", earned);
        return earned;
    }

    /**
     * Подсчитать число проданных билетов
     *
     * @return число билетов
     */
    public Long getTicketsSoldCount() {
        long totalSold = ticketRepository.countBySold();
        log.info("IN getTicketsSoldCount - Number of sold tickets successfully counted");
        return totalSold;
    }

    /**
     * Получить список билетов(доступных или нет в зависимости от параметра)
     *
     * @param id       идентификатор авиалинии
     * @param sold     флаг продан или не продан билет
     * @param username никнейм пользователя
     * @return число билетов
     * @throws NotRepresentativeException ошибка доступа
     */
    public List<Ticket> findTicketsByAirline(Long id, Boolean sold, String username) throws NotRepresentativeException {
        if (userService.isRepresentativeOfThisAirline(username, id)) {
            List<Ticket> tickets = ticketRepository.getAllTicketsByAirlineAndSold(id, sold);
            log.info("IN findTicketsByAirline - Tickets successfully found");
            return tickets;
        } else {
            throw new NotRepresentativeException("User " + username + " is not a representative of airline with id " + id);
        }
    }
}
