package com.example.aviasimbir.repo;

import com.example.aviasimbir.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);

    @Query("select (count(u) > 0) from User u where u.username = ?1")
    Boolean existsUserByUsername(String username);

}
