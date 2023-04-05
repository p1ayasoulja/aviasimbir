package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.repo.PlaneRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaneService {
    private final PlaneRepo planeRepo;

    public PlaneService(PlaneRepo planeRepo) {
        this.planeRepo = planeRepo;
    }

    public Optional<Plane> get(Long id) {
        return planeRepo.findById(id);
    }

    public List<Plane> getAll() {
        return planeRepo.findAll();
    }

    public Plane create(String brand, String model, int seats, Airline airline) {
        Plane plane = new Plane(brand, model, seats, airline);
        planeRepo.save(plane);
        return plane;
    }

    public void delete(Long id) {
        Optional<Plane> plane = planeRepo.findById(id);
        if (plane.isPresent()) {
            plane.get().setAirline(null);
            planeRepo.deleteById(plane.get().getId());
        }
    }
}

