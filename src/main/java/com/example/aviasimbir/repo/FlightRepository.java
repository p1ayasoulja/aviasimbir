package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Flight;
import com.example.aviasimbir.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByPlaneId(Long id);


    List<Flight> findAllByPlane(Plane plane);
}
