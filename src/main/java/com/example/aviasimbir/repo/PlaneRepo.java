package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaneRepo extends JpaRepository<Plane, Long> {
}
