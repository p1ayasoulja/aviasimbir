package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.repo.FlightRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {
    private final FlightRepo flightRepo;

    public FlightService(FlightRepo flightRepo) {
        this.flightRepo = flightRepo;
    }

    public Optional<Flight> get(Long id) {
        return flightRepo.findById(id);
    }

    public List<Flight> getAll() {
        return flightRepo.findAll();
    }

    public Flight create(Plane plane, String departure, String destination,
                         LocalDateTime departureTime, LocalDateTime arrivalTime) {
        Flight flight = new Flight(plane, departure, destination, departureTime, arrivalTime);
        flightRepo.save(flight);
        return flight;
    }

    public Optional<Flight> update(Long id, LocalDateTime departureTime,
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
            flightRepo.save(flight.get());
            return flight;
        } else return Optional.empty();
    }

    public void delete(Long id) {
        flightRepo.deleteById(id);
    }

    public void setPlaneNull(Long id) {
        List<Flight> flight = flightRepo.findAll();
        flight.forEach(flight1 -> {
            if (flight1.getPlane().getId().equals(id)) {
                flight1.setPlane(null);
            }
        });
    }

    public List<Flight> flights(Plane plane) {
        List<Flight> flights = flightRepo.findAll();
        return flights.stream().filter(flight -> flight.getPlane().equals(plane)).collect(Collectors.toList());
    }
}
