package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaneRepository extends JpaRepository<Plane, Long> {
    @Query("select count(p) from Plane p where p.airline.id = ?1")
    Long countByAirlineId(Long id);
}
