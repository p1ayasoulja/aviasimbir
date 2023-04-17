package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.repo.AirlineRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import com.example.aviasimbir.repo.PlaneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
public class PlaneService {
    private final PlaneRepository planeRepository;
    private final LoggerRepository loggerRepository;
    private final AirlineRepository airlineRepository;

    public PlaneService(PlaneRepository planeRepository, LoggerRepository loggerRepository, AirlineRepository airlineRepository) {
        this.planeRepository = planeRepository;
        this.loggerRepository = loggerRepository;
        this.airlineRepository = airlineRepository;
    }

    /**
     * Получить самолет по идентификатору
     *
     * @param id идентификатор самолета
     * @return самолет
     */
    public Plane getPlane(Long id) throws NoSuchIdException {
        if (planeRepository.findById(id).isPresent()) {
            log.info("IN getPlane - Plane: {} successfully found", id);
            return planeRepository.findById(id).get();
        } else {
            throw new NoSuchIdException("Plane with id " + id + " was not found");
        }
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
    @Transactional
    public Plane createPlane(String brand, String model, int seats, Airline airline) throws WrongArgumentException {
        if (brand == null || brand.trim().isEmpty() || model == null || model.trim().isEmpty() || seats <= 0 || airline == null) {
            throw new WrongArgumentException("All fields must be filled");
        }
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
    @Transactional
    public void deletePlane(Long id) throws NoSuchIdException {
        if (planeRepository.findById(id).isPresent()) {
            planeRepository.deleteById(id);
            log.info("IN deletePlane - Plane: {} successfully deleted", id);
            loggerRepository.save(new Logger("Plane " + id + " was deleted", Instant.now()));
        } else {
            throw new NoSuchIdException("Plane with id " + id + " was not found");
        }
    }

    /**
     * Посчитать число самолетов авиалинии
     *
     * @param id идентификатор авиалинии
     * @return число самолетов авиалинии
     */
    public Long getPlanesCountByAirlineId(Long id) throws NoSuchIdException {
        if (airlineRepository.findById(id).isPresent()) {
            log.info("IN getPlanesCount - Planes successfully counted : {}", planeRepository.countByAirlineId(id));
            return planeRepository.countByAirlineId(id);
        } else {
            throw new NoSuchIdException("Airline with id " + id + " was not found");
        }
    }
}

