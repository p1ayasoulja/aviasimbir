package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.NotRepresentativeException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.repo.AirlineRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class AirlineService {
    private final AirlineRepository airlineRepository;
    private final LoggerRepository loggerRepository;
    private final UserService userService;

    public AirlineService(AirlineRepository airlineRepository, LoggerRepository loggerRepository, UserService userService) {
        this.airlineRepository = airlineRepository;
        this.loggerRepository = loggerRepository;
        this.userService = userService;
    }

    /**
     * Получение сущности авиалинии по идентификатору
     *
     * @param id идентификатор авиалинии
     * @return авиалиния
     * @throws NoSuchIdException ошибка неверного идентификатора
     */
    public Airline getAirline(Long id) throws NoSuchIdException {
        Optional<Airline> airlineOptional = airlineRepository.findById(id);
        if (airlineOptional.isPresent()) {
            log.info("IN getAirline - Airline: {} successfully found", id);
            return airlineOptional.get();
        } else {
            throw new NoSuchIdException("Airline with id " + id + " was not found");
        }
    }

    /**
     * Создать авиалинию
     *
     * @param name имя авиалинии
     * @return авиалиния
     * @throws WrongArgumentException ошибка неверно введенных данных
     */
    @Transactional
    public Airline createAirline(String name) throws WrongArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new WrongArgumentException("Airline name cannot be null or empty");
        }
        Airline airline = new Airline(name);
        airlineRepository.save(airline);
        log.info("IN create - Airline: {} successfully created", name);
        return airline;
    }

    /**
     * Обновить авиалинию
     *
     * @param id       идентификатор авиалинии
     * @param name     имя авиалинииё
     * @param username никнейм пользователя
     * @return авиалиния
     * @throws NoSuchIdException          ошибка неверного идентификатора
     * @throws WrongArgumentException     ошибка неверно введенных данных
     * @throws NotRepresentativeException ошибка доступа
     */
    @Transactional
    public Airline updateAirline(Long id, String name, String username) throws NoSuchIdException, WrongArgumentException, NotRepresentativeException {
        if (!userService.isRepresentativeOfThisAirline(username, id)) {
            throw new NotRepresentativeException("User " + username + " is not a representative of airline with id " + id);
        }
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Airline with id " + id + " was not found"));
        if (name == null || name.trim().isEmpty()) {
            throw new WrongArgumentException("Airline name cannot be null or empty");
        }
        airline.setName(name);
        airlineRepository.save(airline);
        log.info("IN update - Airline: {} successfully updated", name);
        return airline;
    }

    /**
     * Удалить авиалинию
     *
     * @param username никнейм пользователя
     * @param id       - идентификатор авиалинии
     * @throws NoSuchIdException          ошибка неверного идентификатора
     * @throws NotRepresentativeException ошибка доступа
     */
    @Transactional
    public void deleteAirline(Long id, String username) throws NoSuchIdException, NotRepresentativeException {
        if (!userService.isRepresentativeOfThisAirline(username, id)) {
            throw new NotRepresentativeException("User " + username + " is not a representative of airline with id " + id);
        }
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Airline with id " + id + " was not found"));
        airlineRepository.delete(airline);
        log.info("IN delete - Airline: {} successfully deleted", id);
        loggerRepository.save(new Logger("Airline " + id + " was deleted", Instant.now()));
    }

    /**
     * Посчитать число проданных билетов
     *
     * @param id       идентификатор авиалинии
     * @param username никнейм пользователя
     * @return число проданных билетов авиалинии
     * @throws NotRepresentativeException ошибка доступа
     */
    public Long getTotalSoldTickets(Long id, String username) throws NotRepresentativeException {
        if (!userService.isRepresentativeOfThisAirline(username, id)) {
            throw new NotRepresentativeException("User " + username + " is not a representative of airline with id " + id);
        }
        log.info("IN getTotalSoldTickets - Total sold tickets counted");
        return airlineRepository.getTotalSoldTicketsByAirline(id);
    }

    /**
     * Посчитать на какую сумму было продано билетов
     *
     * @param id       идентификатор авиалинии
     * @param username никнейм пользователя
     * @return число проданных билетов авиалинии
     * @throws NotRepresentativeException ошибка доступа
     */
    public BigDecimal getTotalEarnedByAirline(Long id, String username) throws NotRepresentativeException {
        if (!userService.isRepresentativeOfThisAirline(username, id)) {
            throw new NotRepresentativeException("User " + username + " is not a representative of airline with id " + id);
        }
        log.info("IN getTotalEarnedByAirline - Total earned sum counted");
        return airlineRepository.getTotalEarnedByAirline(id);
    }
}
