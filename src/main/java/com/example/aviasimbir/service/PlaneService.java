package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.entity.Plane;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.NotRepresentativeException;
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
    private final UserService userService;
    private final AirlineRepository airlineRepository;

    public PlaneService(PlaneRepository planeRepository, LoggerRepository loggerRepository,
                        UserService userService, AirlineRepository airlineRepository) {
        this.planeRepository = planeRepository;
        this.loggerRepository = loggerRepository;
        this.userService = userService;
        this.airlineRepository = airlineRepository;
    }

    /**
     * Получить самолет по идентификатору
     *
     * @param id идентификатор самолета
     * @return самолет
     * @throws NoSuchIdException ошибка неверного идентификатора
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
     * @param brand     бренд самолета
     * @param model     модель самолета
     * @param seats     число мест
     * @param airlineId идентификатор авиалинии
     * @return самолет
     * @throws NoSuchIdException      ошибка неверного идентификатора
     * @throws WrongArgumentException ошибка неверно введенных данных
     */
    @Transactional
    public Plane createPlane(String brand, String model, int seats, Long airlineId) throws WrongArgumentException, NoSuchIdException {
        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new NoSuchIdException("Airline with id " + airlineId + " was not found"));
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
     * @throws NoSuchIdException ошибка неверного идентификатора
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
     * @param id       идентификатор авиалинии
     * @param username никнейм пользователя
     * @return число самолетов авиалинии
     * @throws NotRepresentativeException ошибка доступа
     */
    public Long getNumberOfPlanesForAirline(Long id, String username) throws NotRepresentativeException {
        if (userService.isRepresentativeOfThisAirline(username, id)) {
            log.info("IN getNumberOfPlanesForAirline - Number of planes for airline with id {} successfully counted", id);
            return planeRepository.countByAirlineId(id);
        } else {
            throw new NotRepresentativeException("User " + username + " is not a representative of airline with id " + id);
        }
    }
}

