package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.entity.Ticket;
import com.example.aviasimbir.exceptions.FlightTimeException;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.repo.FlightRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import com.example.aviasimbir.repo.PlaneRepository;
import com.example.aviasimbir.repo.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Slf4j
public class FlightService {
    private final FlightRepository flightRepository;
    private final LoggerRepository loggerRepository;
    private final PlaneRepository planeRepository;
    private final TicketRepository ticketRepository;
    @Value("${ticket.fixedcommission}")
    private BigDecimal fixedcommission;

    public FlightService(FlightRepository flightRepository, LoggerRepository loggerRepository,
                         PlaneRepository planeRepository, TicketRepository ticketRepository) {
        this.flightRepository = flightRepository;
        this.loggerRepository = loggerRepository;
        this.planeRepository = planeRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Получение сущности рейса по идентификатору
     *
     * @param id идентификатор рейса
     * @return рейс
     * @throws NoSuchIdException ошибка неверного идентификатора
     */
    public Flight getFlight(Long id) throws NoSuchIdException {
        Optional<Flight> flightOptional = flightRepository.findById(id);
        if (flightOptional.isPresent()) {
            log.info("IN getFlight - Flight: {} successfully found", id);
            return flightOptional.get();
        } else {
            throw new NoSuchIdException("Flight with id " + id + " was not found");
        }
    }

    /**
     * Создать рейс
     *
     * @param planeId       идентификатор самолета
     * @param departure     точка вылета
     * @param destination   точка прибытия
     * @param departureTime время вылета
     * @param arrivalTime   время прибытия
     * @return рейс
     * @throws NoSuchIdException      ошибка неверного идентификатора
     * @throws WrongArgumentException ошибка неверно введенных данных
     * @throws FlightTimeException    ошибка неправильных данных времени
     */
    @Transactional
    public Flight createFlight(Long planeId, String departure, String destination,
                               ZonedDateTime departureTime, ZonedDateTime arrivalTime, Boolean commission, BigDecimal ticketPrice) throws WrongArgumentException, FlightTimeException, NoSuchIdException {
        Plane plane = planeRepository.findById(planeId).orElseThrow(() -> new NoSuchIdException("Cant find flight with id = " + planeId));
        if (plane == null || departure == null || departure.trim().isEmpty() || departureTime == null || arrivalTime == null || destination == null || destination.trim().isEmpty()) {
            throw new WrongArgumentException("All fields must be filled");
        } else {
            if (departureTime.isAfter(arrivalTime)) {
                throw new FlightTimeException("Departure time cannot be after arrival time");
            } else {
                if (departureTime.isBefore(ZonedDateTime.now())) {
                    throw new FlightTimeException("Departure time cannot be before current time");
                } else {
                    Flight flight = new Flight(plane, departure, destination, departureTime, arrivalTime);
                    flightRepository.save(flight);
                    createTicketsForCreatedFlight(flight.getId(), commission, plane.getSeats(), ticketPrice);
                    log.info("IN createFlight - Flight: {} successfully created", flight.getId());
                    return flight;
                }
            }
        }
    }

    /**
     * Создать билеты для только что созданного рейса
     *
     * @param id         идентификатор рейса
     * @param commission флаг наличия коммисии
     * @param seats      число билетов
     * @param price      цена билетов
     * @throws NoSuchIdException      ошибка неверного идентификатора
     * @throws WrongArgumentException ошибка неверно введенных данных
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
     * Обновить рейс
     *
     * @param id            идентификатор рейса
     * @param departureTime время вылета
     * @param arrivalTime   время прибытия
     * @param planeId       идентификатор самолета
     * @param departure     точка вылета
     * @param destination   точка прибытия
     * @return рейс
     * @throws NoSuchIdException      ошибка неверного идентификатора
     * @throws WrongArgumentException ошибка неверно введенных данных
     * @throws FlightTimeException    ошибка неправильных данных времени
     */
    @Transactional
    public Flight updateFlight(Long id, ZonedDateTime departureTime,
                               ZonedDateTime arrivalTime, Long planeId,
                               String departure, String destination) throws WrongArgumentException, NoSuchIdException, FlightTimeException {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Flight with id " + id + " was not found"));
        Plane plane = planeRepository.findById(planeId)
                .orElseThrow(() -> new NoSuchIdException("Plane with id " + planeId + " was not found"));
        if (Stream.of(plane, departure, departureTime, arrivalTime, destination)
                .anyMatch(field -> field == null || field.toString().trim().isEmpty())) {
            throw new WrongArgumentException("All fields must be filled");
        }
        if (departureTime.isAfter(arrivalTime)) {
            throw new FlightTimeException("Departure time cannot be after arrival time");
        } else {
            if (departureTime.isBefore(ZonedDateTime.now())) {
                throw new FlightTimeException("Departure time cannot be before current time");
            } else {
                flight.setPlane(plane);
                flight.setDepartureTime(departureTime);
                flight.setArrivalTime(arrivalTime);
                flight.setDeparture(departure);
                flight.setDestination(destination);
                flightRepository.save(flight);
                log.info("IN updateFlight - Flight: {} successfully updated", id);
                return flight;
            }
        }
    }

    /**
     * Удалить рейс
     *
     * @param id идентификатор рейса
     * @throws NoSuchIdException ошибка неверного идентификатора
     */
    @Transactional
    public void deleteFlight(Long id) throws NoSuchIdException {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Flight with id " + id + " was not found"));
        flightRepository.delete(flight);
        log.info("IN deleteFlight - Flight: {} successfully deleted", id);
        loggerRepository.save(new Logger("Flight " + id + " was deleted", Instant.now()));
    }
}
