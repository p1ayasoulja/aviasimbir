package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.repo.LoggerRepository;
import com.example.aviasimbir.repo.PlaneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlaneService {
    private final PlaneRepository planeRepository;
    private final LoggerRepository loggerRepository;

    public PlaneService(PlaneRepository planeRepository, LoggerRepository loggerRepository) {
        this.planeRepository = planeRepository;
        this.loggerRepository = loggerRepository;
    }

    /**
     * Получить самолет по идентификатору
     *
     * @param id идентификатор самолета
     * @return самолет
     */
    public Optional<Plane> findPlane(Long id) {
        log.info("IN getPlane - Plane: {} successfully found", id);
        return planeRepository.findById(id);
    }

    /**
     * Получить список самолетов
     *
     * @return список самолетов
     */
    public List<Plane> getAllPlanes() {
        log.info("IN getAllPlanes - List of: {} successfully found", "planes");
        return planeRepository.findAll();
    }

    /**
     * Создать самолет
     *
     * @param brand   бренд самолета
     * @param model   модель самолета
     * @param seats   число мест
     * @param airline авиалиния
     * @return самолет
     */
    public Plane createPlane(String brand, String model, int seats, Airline airline) {
        Plane plane = new Plane(brand, model, seats, airline);
        planeRepository.save(plane);
        log.info("IN createPlane - Plane: {} successfully created", plane.getId());
        return plane;
    }

    /**
     * Удалить самолет
     *
     * @param id идентификатор самолета
     */
    public void deletePlane(Long id) {
        Optional<Plane> plane = planeRepository.findById(id);
        if (plane.isPresent()) {
            plane.get().setAirline(null);
            planeRepository.deleteById(plane.get().getId());
            log.info("IN deletePlane - Plane: {} successfully deleted", id);
            loggerRepository.save(new Logger("Plane " + id + " was deleted", Instant.now()));
        }
    }

    /**
     * Получить список самолетов авиалинии
     *
     * @param airline авиалиния
     * @return список самолетов авиалинии
     */
    public List<Plane> getListOfPlanes(Airline airline) {
        log.info("IN getListOfPlanes - Planes of: {} successfully found", airline.getName());
        return planeRepository.findAllByAirline(airline);
    }

    /**
     * Посчитать число самолетов авиалинии
     *
     * @param id идентификатор авиалинии
     * @return число самолетов авиалинии
     */
    public Long getPlanesCountByAirlineId(Long id) {
        log.info("IN getPlanesCount - Planes successfully counted");
        return planeRepository.countByAirlineId(id);
    }
}

