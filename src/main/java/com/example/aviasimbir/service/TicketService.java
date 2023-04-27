package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.*;
import com.example.aviasimbir.repo.AirlineRepository;
import com.example.aviasimbir.repo.FlightRepository;
import com.example.aviasimbir.repo.TicketRepository;
import com.example.aviasimbir.requestresponse.CommissionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class TicketService {
    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final UserService userService;
    @Value("${ticket.fixedcommission}")
    private BigDecimal fixedcommission;

    public TicketService(TicketRepository ticketRepository, FlightRepository flightRepository,
                         UserService userService, AirlineRepository airlineRepository) {
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
        this.userService = userService;
        this.airlineRepository = airlineRepository;
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
        if (price.compareTo(BigDecimal.ZERO) <= 0 || reserved == null || sold == null || commission == null) {
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
     * Подсчитать число билетов с заданной точкой отправления
     *
     * @return число билетов
     */
    public Long getSoldTicketsFromCount(String departure) {
        long number = ticketRepository.getTicketsByFlightDepartureAndSold(departure);
        log.info("IN getSoldTicketsFromCount - Number of sold tickets from {} successfully counted", departure);
        return number;
    }

    /**
     * Получить информацию о коммиссии по проданным билетам
     *
     * @return информация о коммиссии по проданным билетам
     */
    public CommissionInfo getCommissionInfo() {
        BigDecimal averageTicketPrice = ticketRepository.getAverageTicketPrice();
        BigDecimal averageCommission;
        BigDecimal totalCommission;
        if (averageTicketPrice == null || averageTicketPrice.compareTo(BigDecimal.ZERO) <= 0) {
            averageCommission = BigDecimal.ZERO;
            totalCommission = BigDecimal.ZERO;
        } else {
            BigDecimal commissionPercentage = fixedcommission.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal divisor = BigDecimal.ONE.add(commissionPercentage);
            BigDecimal dividend = averageTicketPrice.divide(divisor, 3, RoundingMode.HALF_UP);
            averageCommission = averageTicketPrice.subtract(dividend).setScale(2, RoundingMode.HALF_UP);
            totalCommission = averageCommission.multiply(BigDecimal.valueOf(ticketRepository.countBySoldAndCommission()));
        }
        log.info("IN getCommissionInfo - Average Commission : {} and Total Commission : {} successfully counted",
                averageCommission, totalCommission);
        return new CommissionInfo(averageCommission, totalCommission);
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
    public List<Ticket> getTicketsByAirline(Long id, Boolean sold, String username) throws NotRepresentativeException, NoSuchIdException {

        if (!userService.isRepresentativeOfThisAirline(username, id)) {
            throw new NotRepresentativeException("User " + username + " is not a representative of airline with id " + id);
        }
        if (airlineRepository.findById(id).isEmpty()) {
            throw new NoSuchIdException("Plane with id " + id + " was not found");
        }

        List<Ticket> tickets = ticketRepository.getAllTicketsByAirlineAndSold(id, sold);
        log.info("IN getTicketsByAirline - Tickets successfully found");
        return tickets;
    }

    /**
     * @param departure       место вылета
     * @param destination     место назначения
     * @param MinDepartureDay раняя допустимая дата вылета
     * @param MaxDepartureDay поздняя допустимая дата вылета
     * @return список билетов, подходящих под условия
     * @throws WrongArgumentException ошибка неправильных или невведенных данных
     */
    public List<Ticket> getTicketsFilter(String departure, String destination, LocalDate MinDepartureDay, LocalDate MaxDepartureDay) throws WrongArgumentException {
        if (departure == null || departure.trim().isEmpty() || destination == null || destination.trim().isEmpty() ||
                MinDepartureDay == null || MaxDepartureDay == null || MinDepartureDay.isAfter(MaxDepartureDay) ||
                MaxDepartureDay.isBefore(MinDepartureDay)) {
            throw new WrongArgumentException("Some of fields are wrong or not present");
        }
        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        ZonedDateTime MinZonedDateTime = MinDepartureDay.atStartOfDay(zoneId);
        ZonedDateTime MaxZonedDateTime = MaxDepartureDay.atStartOfDay(zoneId);
        return ticketRepository.getTicketsByFlightDestinationAndFlightDepartureAndFlightDepartureTime(departure, destination,
                MinZonedDateTime, MaxZonedDateTime);
    }
}
