package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.entity.Logger;
import com.example.aviasimbir.repo.AirlineRepository;
import com.example.aviasimbir.repo.LoggerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public Optional<Airline> findAirline(Long id) {
        log.info("IN getAirline - Airline: {} successfully found", id);
        return airlineRepository.findById(id);
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
    public Airline createAirline(String name) {
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
    public Optional<Airline> updateAirline(Long id, String name) {
        Optional<Airline> airline = airlineRepository.findById(id);
        if (airline.isPresent()) {
            if (!name.isEmpty()) {
                airline.get().setName(name);
                airlineRepository.save(airline.get());
            }
            log.info("IN update - Airline: {} successfully updated", name);
            return airline;
        } else {
            log.info("IN update - Airline: {} was not updated", name);
            return Optional.empty();
        }
    }

    /**
     * Удалить авиалинию
     *
     * @param id - идентификатор авиалинии
     */
    public void deleteAirline(Long id) {
        Optional<Airline> airline = airlineRepository.findById(id);
        if (airline.isPresent()) {
            airlineRepository.deleteById(airline.get().getId());
            log.info("IN delete - Airline: {} successfully deleted", id);
            loggerRepository.save(new Logger("Airline " + id + " was deleted", Instant.now()));
        }
    }
}
