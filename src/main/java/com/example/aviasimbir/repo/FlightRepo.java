package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepo extends JpaRepository<Flight, Long> {
}
