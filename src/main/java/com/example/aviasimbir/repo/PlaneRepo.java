package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Plane;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaneRepo extends JpaRepository<Plane, Long> {
}
