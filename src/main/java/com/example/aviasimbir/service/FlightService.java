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
import java.util.Optional;
import java.util.stream.Stream;

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
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Flight with id " + id + " was not found"));

        if (Stream.of(plane, departure, departureTime, arrivalTime, destination)
                .anyMatch(field -> field == null || field.toString().trim().isEmpty())) {
            throw new WrongArgumentException("All fields must be filled");
        }

        flight.setPlane(plane);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setDeparture(departure);
        flight.setDestination(destination);
        flightRepository.save(flight);
        log.info("IN updateFlight - Flight: {} successfully updated", id);
        return flight;
    }

    /**
     * Удалить рейс
     *
     * @param id идентификатор рейса
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
