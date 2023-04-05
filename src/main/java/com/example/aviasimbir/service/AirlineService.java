package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.repo.AirlineRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirlineService {
    private final AirlineRepo airlineRepo;

    public AirlineService(AirlineRepo airlineRepo) {
        this.airlineRepo = airlineRepo;
    }

    public Optional<Airline> getAirline(Long id) {
        return airlineRepo.findById(id);
    }

    public List<Airline> getAll() {
        return airlineRepo.findAll();
    }

    public Airline create(String name) {
        Airline airline = new Airline(name);
        airlineRepo.save(airline);
        return airline;
    }

    public Optional<Airline> update(Long id, String name) {
        Optional<Airline> airline = airlineRepo.findById(id);
        if (airline.isPresent()) {
            if (!name.isEmpty()) {
                airline.get().setName(name);
                airlineRepo.save(airline.get());
            }
            return airline;
        } else return Optional.empty();
    }

    public void delete(Long id) {
        Optional<Airline> airline = airlineRepo.findById(id);
        if (airline.isPresent()) {
            airlineRepo.deleteById(airline.get().getId());
        }
    }
}
