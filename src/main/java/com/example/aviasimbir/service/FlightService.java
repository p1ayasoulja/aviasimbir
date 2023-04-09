package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.repo.FlightRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public Optional<Flight> getFlight(Long id) {
        log.info("IN getFlight - Flight: {} successfully found", id);
        return flightRepository.findById(id);
    }

    /**
     * Получение всех рейсов
     */
    public List<Flight> getAllFlights() {
        log.info("IN getAllFlights - List of {} successfully found", "flights");
        return flightRepository.findAll();
    }

    /**
     * Создание рейса
     *
     * @param plane         самолет рейса
     * @param departure     точка вылета
     * @param destination   точка прибытия
     * @param departureTime время вылета
     * @param arrivalTime   время прибытия
     * @return рейс
     */
    public Flight createFlight(Plane plane, String departure, String destination,
                               ZonedDateTime departureTime, ZonedDateTime arrivalTime) {
        Flight flight = new Flight(plane, departure, destination, departureTime, arrivalTime);
        flightRepository.save(flight);
        log.info("IN createFlight - Flight: {} successfully created", flight.getId());
        return flight;
    }

    /**
     * Обновление рейса
     *
     * @param id            идентификатор рейса
     * @param departureTime время вылета
     * @param arrivalTime   время прибытия
     * @param plane         самолет рейса
     * @param departure     точка вылета
     * @param destination   точка прибытия
     * @return рейс
     */
    public Optional<Flight> updateFlight(Long id, ZonedDateTime departureTime,
                                         ZonedDateTime arrivalTime, Plane plane,
                                         String departure, String destination) {
        Optional<Flight> optionalFlight = flightRepository.findById(id);
        if (optionalFlight.isEmpty()) {
            return Optional.empty();
        }
        Flight flight = optionalFlight.get();
        if (Objects.nonNull(departureTime)) {
            flight.setDepartureTime(departureTime);
        }
        if (Objects.nonNull(arrivalTime)) {
            flight.setArrivalTime(arrivalTime);
        }
        if (Objects.nonNull(plane)) {
            flight.setPlane(plane);
        }
        if (StringUtils.hasText(departure)) {
            flight.setDeparture(departure);
        }
        if (StringUtils.hasText(destination)) {
            flight.setDestination(destination);
        }
        if (!flightRepository.existsById(flight.getId())) {
            return Optional.empty();
        }
        flightRepository.save(flight);
        log.info("IN updateFlight - Flight: {} successfully updated", id);
        return optionalFlight;
    }

    /**
     * Удаление рейса
     *
     * @param id идентификатор рейса
     */

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
        log.info("IN deleteFlight - Flight: {} successfully deleted", id);
        loggerRepository.save(new Logger("Flight " + id + " was deleted", Instant.now()));
    }

    /**
     * Получение списка рейсов самолетов
     *
     * @param planes список самолетов
     * @return список рейсов самолета
     */
    public List<Flight> getFlightsByPlane(List<Plane> planes) {
        log.info("IN getFlightsByPlane - List of flights successfully found");
        List<Flight> flights = new ArrayList<>();
        planes.forEach(plane -> flights.addAll(flightRepository.findAllByPlane(plane)));
        return flights;
    }
}
