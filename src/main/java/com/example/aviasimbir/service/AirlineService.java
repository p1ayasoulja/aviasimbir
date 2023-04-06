package com.example.aviasimbir.service;

import com.example.aviasimbir.entity.Airline;
import com.example.aviasimbir.repo.AirlineRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AirlineService {
    private final AirlineRepo airlineRepo;

    public AirlineService(AirlineRepo airlineRepo) {
        this.airlineRepo = airlineRepo;
    }

    /**
     * Получение сущности авиалинии
     *
     * @param id идентификатор авиалинии
     * @return авиалиния
     */
    public Optional<Airline> getAirline(Long id) {
        log.info("IN getAirline - Airline: {} successfully found", id);
        return airlineRepo.findById(id);
    }

    /**
     * Получение всех сущностей авиалиний
     *
     * @return список авиалиний
     */
    public List<Airline> getAllAirlines() {
        log.info("IN getAll - List of : {} successfully found", "airlines");
        return airlineRepo.findAll();
    }

    /**
     * Создать авиалинию
     *
     * @param name имя авиалинии
     * @return авиалиния
     */
    public Airline createAirline(String name) {
        Airline airline = new Airline(name);
        airlineRepo.save(airline);
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
        Optional<Airline> airline = airlineRepo.findById(id);
        if (airline.isPresent()) {
            if (!name.isEmpty()) {
                airline.get().setName(name);
                airlineRepo.save(airline.get());
            }
            log.info("IN update - Airline: {} successfully updated", name);
            return airline;
        } else {
            log.info("IN update - Airline: {} was not updated", name);
            return Optional.empty();
        }
    }

    /**
     * Удаление авиалинии
     *
     * @param id - идентификатор авиалинии
     */
    public void deleteAirline(Long id) {
        Optional<Airline> airline = airlineRepo.findById(id);
        if (airline.isPresent()) {
            airlineRepo.deleteById(airline.get().getId());
            log.info("IN delete - Airline: {} successfully deleted", id);
        }
    }
}
