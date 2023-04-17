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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class TicketService {
    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;
    private final TicketReservationService ticketReservationService;
    @Value("${ticket.fixedcommission}")
    private BigDecimal fixedcommission;
    @Value("${ticket.reservation.timeout}")
    private Long reservationTimeout;

    public TicketService(TicketRepository ticketRepository, FlightRepository flightRepository, TicketReservationService ticketReservationService) {
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
        this.ticketReservationService = ticketReservationService;
    }

    /**
     * Получить билет по идентификатору
     *
     * @param id идентификатор билета
     * @return билет
     */
    public Ticket getTicket(Long id) throws NoSuchIdException {
        if (ticketRepository.findById(id).isPresent()) {
            log.info("IN getTicket - Ticket: {} successfully found", id);
            return ticketRepository.findById(id).get();
        } else throw new NoSuchIdException("Ticket with id " + id + " was not found");
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
    @Transactional
    public Ticket createTicket(Flight flight, BigDecimal price, Boolean reserved, Boolean sold, Boolean commission) throws WrongArgumentException {
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
        log.info("IN ticketsFromKazan - Number of sold tickets from Kazan successfully found");
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
     * Создать билеты для только что созданного рейса
     *
     * @param id         идентификатор рейса
     * @param commission флаг наличия коммисии
     * @param seats      число билетов
     * @param price      цена билетов
     */
    public void createTicketsForCreatedFlight(Long id, Boolean commission, Integer seats, BigDecimal price) throws NoSuchIdException, WrongArgumentException {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Flight with id " + id + " was not found"));
        if (commission == null || price.compareTo(BigDecimal.ZERO) <= 0 || seats <= 0) {
            throw new WrongArgumentException("All fields must be filled");
        }
        if (commission) {
            price = price.multiply((BigDecimal.ONE.add
                    (fixedcommission.divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP))));
        }
        BigDecimal finalPrice = price;
        List<Ticket> tickets = IntStream.range(0, seats / 2)
                .mapToObj(i -> new Ticket(flight, finalPrice, false, false, commission))
                .collect(Collectors.toList());
        ticketRepository.saveAll(tickets);
    }

    /**
     * Продать билет
     *
     * @param ticket билет
     */
    @Transactional
    public void sellTicket(Ticket ticket) throws TicketSoldException, PlaneAlreadyLeftException, NoSuchIdException {
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
     * @param ticket билет
     */
    @Transactional
    public void reserveTicket(Ticket ticket) throws TicketReservedException, PlaneAlreadyLeftException, NoSuchIdException {
        Flight flight = flightRepository.findById(ticket.getFlight().getId())
                .orElseThrow(() -> new NoSuchIdException("Flight was not found"));
        if (!flight.getDepartureTime().isAfter(ZonedDateTime.now())) {
            throw new PlaneAlreadyLeftException("Plane has already left airport");
        }
        if (!ticket.getReserved()) {
            ticket.setReserved(true);
            ticket.setReservedUntil(LocalDateTime.now().plusSeconds(reservationTimeout));
            log.info("IN reserveTicket - Ticket successfully reserved");
            ticketRepository.save(ticket);
            ticketReservationService.scheduleTicketReservation(ticket);
        } else {
            throw new TicketReservedException("Ticket already reserved");
        }
    }

    /**
     * Снять бронь с билета
     *
     * @param ticket билет
     */
    @Transactional
    public void cancelTicketReserve(Ticket ticket) {
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
     * @return список доступных билетов рейса
     */
    public List<Ticket> getAllAvailableTicketsByFlight(Flight flight) {
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

    /**
     * Подсчитать число проданных билетов
     *
     * @return число билетов
     */
    public List<Ticket> findTicketsByAirline(Long id, Boolean sold) {
        List<Ticket> tickets = ticketRepository.getAllTicketsByAirlineAndSold(id, sold);
        log.info("IN ticketsFromKazan - Number of sold tickets from Kazan successfully found");
        return tickets;
    }
}
