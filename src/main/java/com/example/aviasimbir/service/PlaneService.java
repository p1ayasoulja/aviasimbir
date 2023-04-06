package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.repo.PlaneRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlaneService {
    private final PlaneRepo planeRepo;

    public PlaneService(PlaneRepo planeRepo) {
        this.planeRepo = planeRepo;
    }
    /**
     * Получить самолет по идентификатору
     *
     * @param id   идентификатор самолета
     * @return самолет
     */
    public Optional<Plane> getPlane(Long id) {
        log.info("IN get - Plane: {} successfully found", id);
        return planeRepo.findById(id);
    }
    /**
     * Получить список самолетов
     *
     * @return список самолетов
     */
    public List<Plane> getAllPlanes() {
        log.info("IN get - List of: {} successfully found", "planes");
        return planeRepo.findAll();
    }
    /**
     * Создать самолет
     *
     * @param brand   бренд самолета
     * @param model модель самолета
     * @param seats число мест
     * @param airline авиалиния
     * @return самолет
     */
    public Plane createPlane(String brand, String model, int seats, Airline airline) {
        Plane plane = new Plane(brand, model, seats, airline);
        planeRepo.save(plane);
        log.info("IN create - Plane: {} successfully created", plane.getId());
        return plane;
    }
    /**
     * Удалить самолет
     *
     * @param id идентификатор самолета
     */
    public void deletePlane(Long id) {
        Optional<Plane> plane = planeRepo.findById(id);
        if (plane.isPresent()) {
            plane.get().setAirline(null);
            planeRepo.deleteById(plane.get().getId());
            log.info("IN delete - Plane: {} successfully deleted", id);
        }
    }
    /**
     * Посчитать число самолетов авиалинии
     *
     * @param airline авиалиния
     * @return число самолетов авиалинии
     */
    public Long getPlanesCount(Airline airline) {
        List<Plane> planes = planeRepo.findAll();
        log.info("IN planes - Planes of: {} successfully counted", airline.getName());
        return planes.stream().filter(plane -> plane.getAirline() == airline).count();

    }
    /**
     * Получить список самолетов авиалинии
     *
     * @param airline авиалиния
     * @return список самолетов авиалинии
     */
    public List<Plane> getListOfPlanes(Airline airline) {
        List<Plane> planes = planeRepo.findAll();
        log.info("IN getListOfPlanes - Planes of: {} successfully found", airline.getName());
        return planes.stream().filter(plane -> plane.getAirline().equals(airline)).collect(Collectors.toList());
    }
}

