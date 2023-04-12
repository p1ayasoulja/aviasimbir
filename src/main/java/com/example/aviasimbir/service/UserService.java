package com.example.aviasimbir.service;


import com.example.aviasimbir.Jwt.JwtUser;
import com.example.aviasimbir.Jwt.JwtUserFactory;
import com.example.aviasimbir.entity.Role;
import com.example.aviasimbir.entity.User;
import com.example.aviasimbir.exceptions.RegisterUserException;
import com.example.aviasimbir.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    /**
     * Регистрация пользователя
     *
     * @param username имя пользователя
     * @param password пароль пользователя
     * @return новый пользователь
     */
    @Transactional
    public User register(String username, String password) throws RegisterUserException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new RegisterUserException("Username and password cannot be null or empty");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.CUSTOMER);
        user.setAirline(null);
        User registeredUser = userRepository.save(user);

        log.info("IN register - user: {} successfully registered  with role {}", registeredUser.getUsername(), registeredUser.getRole());

        return registeredUser;
    }

    /**
     * Поиск пользователя по никнейму
     *
     * @param username имя пользователя
     * @return пользователь
     */
    public Optional<User> findByUsername(String username) {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            log.info("IN findByUsername - user : {} was not found", username);
            return userOpt;
        }
        return userOpt;
    }

    /**
     * Загрузка пользователя
     *
     * @param username имя пользователя
     * @return JWT-пользователь
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Cant find user with username :" + username);
        }
        JwtUser jwtUser = JwtUserFactory.create(userOpt.get());
        log.info("IN loadByUsername - user with: {} loaded with Role {}", username, userOpt.get().getRole());
        return jwtUser;
    }
}
