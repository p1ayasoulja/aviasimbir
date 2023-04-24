package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.Promocode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromocodeRepository extends JpaRepository<Promocode, Long> {
    @Query("select p from Promocode p where p.code = ?1")
    Optional<Promocode> findByCode(String code);
}
