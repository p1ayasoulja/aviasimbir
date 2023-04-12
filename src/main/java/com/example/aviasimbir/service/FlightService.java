package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.exceptions.FlightTimeException;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.repo.FlightRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FlightService {
    private final FlightRepository flightRepository;
    private final LoggerRepository loggerRepository;

    public FlightService(FlightRepository flightRepository, LoggerRepository loggerRepository) {
        this.flightRepository = flightRepository;
        this.loggerRepository = loggerRepository;
    }

    /**
     * Получение сущности рейса по идентификатору
     *
     * @param id идентификатор рейса
     * @return рейс
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
     * Получение всех рейсов
     */
    public List<Flight> getAllFlights() {
        log.info("IN getAllFlights - List of {} successfully found", "flights");
        return flightRepository.findAll();
    }

    /**
     * Создать рейс
     *
     * @param plane         самолет рейса
     * @param departure     точка вылета
     * @param destination   точка прибытия
     * @param departureTime время вылета
     * @param arrivalTime   время прибытия
     * @return рейс
     */
    @Transactional
    public Flight createFlight(Plane plane, String departure, String destination,
                               ZonedDateTime departureTime, ZonedDateTime arrivalTime) throws WrongArgumentException, FlightTimeException {
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
                    log.info("IN createFlight - Flight: {} successfully created", flight.getId());
                    return flight;
                }
            }
        }
    }

    /**
     * Обновить рейс
     *
     * @param id            идентификатор рейса
     * @param departureTime время вылета
     * @param arrivalTime   время прибытия
     * @param plane         самолет рейса
     * @param departure     точка вылета
     * @param destination   точка прибытия
     * @return рейс
     */
    @Transactional
    public Flight updateFlight(Long id, ZonedDateTime departureTime,
                               ZonedDateTime arrivalTime, Plane plane,
                               String departure, String destination) throws WrongArgumentException, NoSuchIdException {
        Optional<Flight> optionalFlight = flightRepository.findById(id);
        if (optionalFlight.isPresent()) {
            if (plane == null || departure == null || departure.trim().isEmpty() || departureTime == null || arrivalTime == null || destination == null || destination.trim().isEmpty()) {
                throw new WrongArgumentException("All fields must be filled");
            } else {
                Flight flight = optionalFlight.get();
                flight.setPlane(plane);
                flight.setDepartureTime(departureTime);
                flight.setArrivalTime(arrivalTime);
                flight.setDeparture(departure);
                flight.setDestination(destination);
                flightRepository.save(flight);
                log.info("IN updateFlight - Flight: {} successfully updated", id);
                return flight;
            }
        } else {
            throw new NoSuchIdException("Flight with id " + id + " was not found");
        }
    }

    /**
     * Удалить рейс
     *
     * @param id идентификатор рейса
     */
    @Transactional
    public void deleteFlight(Long id) throws NoSuchIdException {
        if (flightRepository.findById(id).isPresent()) {


            flightRepository.deleteById(id);
            log.info("IN deleteFlight - Flight: {} successfully deleted", id);
            loggerRepository.save(new Logger("Flight " + id + " was deleted", Instant.now()));
        } else {
            throw new NoSuchIdException("Flight with id " + id + " was not found");
        }

    }

    /**
     * Получить список рейсов самолетов
     *
     * @param planes список самолетов
     * @return список рейсов самолетов
     */
    public List<Flight> getFlightsByPlanes(List<Plane> planes) {
        log.info("IN getFlightsByPlane - List of flights successfully found");
        List<Flight> flights = new ArrayList<>();
        planes.forEach(plane -> flights.addAll(flightRepository.findAllByPlane(plane)));
        return flights;
    }

    /**
     * Получение списка рейсов самолета
     *
     * @param plane самолет
     * @return список рейсов самолета
     */
    public List<Flight> getFlightsByPlane(Plane plane) {
        log.info("IN getFlightsByPlane - List of flights successfully found");
        return flightRepository.findAllByPlane(plane);
    }
}
