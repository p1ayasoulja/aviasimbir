package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.repo.FlightRepo;
import com.example.aviasimbir.repo.LoggerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FlightService {
    private final FlightRepo flightRepo;
    private final LoggerRepo loggerRepo;

    public FlightService(FlightRepo flightRepo, LoggerRepo loggerRepo) {
        this.flightRepo = flightRepo;
        this.loggerRepo = loggerRepo;
    }

    /**
     * Получение сущности рейса по идентификатору
     *
     * @param id идентификатор рейса
     * @return рейс
     */
    public Optional<Flight> getFlight(Long id) {
        log.info("IN getFlight - Flight: {} successfully found", id);
        return flightRepo.findById(id);
    }

    /**
     * Получение всех рейсов
     */
    public List<Flight> getAllFlights() {
        log.info("IN getAllFlights - List of {} successfully found", "flights");
        return flightRepo.findAll();
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
                               LocalDateTime departureTime, LocalDateTime arrivalTime) {
        Flight flight = new Flight(plane, departure, destination, departureTime, arrivalTime);
        flightRepo.save(flight);
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
    public Optional<Flight> updateFlight(Long id, LocalDateTime departureTime,
                                         LocalDateTime arrivalTime, Plane plane, String departure, String destination) {
        Optional<Flight> flight = flightRepo.findById(id);
        if (flight.isPresent()) {
            if (departureTime != null) {
                flight.get().setDepartureTime(departureTime);
            }
            if (arrivalTime != null) {
                flight.get().setArrivalTime(arrivalTime);
            }
            if (plane != null) {
                flight.get().setPlane(plane);
            }
            if (!departure.isEmpty()) {
                flight.get().setDeparture(departure);
            }
            if (!destination.isEmpty()) {
                flight.get().setDestination(destination);
            }
            log.info("IN updateFlight - Flight: {} successfully updated", id);
            flightRepo.save(flight.get());
            return flight;
        } else return Optional.empty();
    }

    /**
     * Удаление рейса
     *
     * @param id идентификатор рейса
     */

    public void deleteFlight(Long id) {
        Optional<Flight> flight = flightRepo.findById(id);
        if (flight.isPresent()) {
            flight.get().setPlane(null);
            flightRepo.deleteById(flight.get().getId());
            log.info("IN deleteFlight - Flight: {} successfully deleted", id);
            loggerRepo.save(new Logger(flight.get().toString() + " was deleted", LocalDateTime.now()));
        }
    }

    /**
     * Обнуление значений самолета одинаковых с данным
     *
     * @param id идентификатор самолета
     */
    public void setPlaneFieldToNull(Long id) {
        List<Flight> flight = flightRepo.findAll();
        flight.forEach(flight1 -> {
            if (flight1.getPlane().getId().equals(id)) {
                flight1.setPlane(null);
            }
        });
        log.info("IN setPlaneFieldToNull - PlaneField of flights by {} successfully updated to null", "plane");
    }

    /**
     * Показ списка рейсов самолета
     *
     * @param plane самолет рейса
     * @return список рейсов самолета
     */
    public List<Flight> getFlightsByPlane(Plane plane) {
        List<Flight> flights = flightRepo.findAll();
        log.info("IN getFlightsByPlane - List of : {} successfully updated", "flights");
        return flights.stream().filter(flight -> flight.getPlane().equals(plane)).collect(Collectors.toList());
    }
}
