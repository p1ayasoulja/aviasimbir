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

import java.math.BigDecimal;
import java.time.Instant;
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
     * @param id - идентификатор авиалинии
     */
    @Transactional
    public void deleteAirline(Long id) throws NoSuchIdException {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new NoSuchIdException("Airline with id " + id + " was not found"));
        airlineRepository.delete(airline);
        log.info("IN delete - Airline: {} successfully deleted", id);
        loggerRepository.save(new Logger("Airline " + id + " was deleted", Instant.now()));
    }

    /**
     * @param id идентификатор авиалинии
     * @return число проданных билетов авиалинии
     */
    public Long getTotalSoldTickets(Long id) {
        return airlineRepository.getTotalSoldTicketsByAirline(id);
    }

    /**
     * @param id идентификатор авиалинии
     * @return число проданных билетов авиалинии
     */
    public BigDecimal getTotalEarnedByAirline(Long id) {
        return airlineRepository.getTotalEarnedByAirline(id);
    }
}
