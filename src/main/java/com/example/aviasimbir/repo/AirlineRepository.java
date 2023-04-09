package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<Airline, Long> {
}
