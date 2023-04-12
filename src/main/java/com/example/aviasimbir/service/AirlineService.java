package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.exceptions.NoSuchIdException;
import com.example.aviasimbir.exceptions.WrongArgumentException;
import com.example.aviasimbir.repo.AirlineRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AirlineService {
    private final AirlineRepository airlineRepository;
    private final LoggerRepository loggerRepository;

    public AirlineService(AirlineRepository airlineRepository, LoggerRepository loggerRepository) {
        this.airlineRepository = airlineRepository;
        this.loggerRepository = loggerRepository;
    }

    /**
     * Получение сущности авиалинии по идентификатору
     *
     * @param id идентификатор авиалинии
     * @return авиалиния
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
     * Получение всех сущностей авиалиний
     *
     * @return список авиалиний
     */
    public List<Airline> getAllAirlines() {
        log.info("IN getAll - List of : {} successfully found", "airlines");
        return airlineRepository.findAll();
    }

    /**
     * Создать авиалинию
     *
     * @param name имя авиалинии
     * @return авиалиния
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
     * @param id   идентификатор авиалинии
     * @param name имя авиалинии
     * @return авиалиния
     */
    @Transactional
    public Airline updateAirline(Long id, String name) throws NoSuchIdException, WrongArgumentException {
        Optional<Airline> airline = airlineRepository.findById(id);
        if (airline.isPresent()) {
            if (name == null || name.trim().isEmpty()) {
                throw new WrongArgumentException("Airline name cannot be null or empty");
            } else {
                airline.get().setName(name);
                airlineRepository.save(airline.get());
                log.info("IN update - Airline: {} successfully updated", name);
                return airline.get();
            }
        } else {
            throw new NoSuchIdException("Airline with id " + id + " was not found");
        }
    }

    /**
     * Удалить авиалинию
     *
     * @param id - идентификатор авиалинии
     */
    @Transactional
    public void deleteAirline(Long id) throws NoSuchIdException {
        Optional<Airline> airline = airlineRepository.findById(id);
        if (airline.isPresent()) {
            airlineRepository.deleteById(airline.get().getId());
            log.info("IN delete - Airline: {} successfully deleted", id);
            loggerRepository.save(new Logger("Airline " + id + " was deleted", Instant.now()));
        } else throw new NoSuchIdException("Airline with id " + id + " was not found");
    }
}
