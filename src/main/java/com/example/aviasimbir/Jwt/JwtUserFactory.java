package com.example.aviasimbir.Jwt;

import com.example.aviasimbir.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public final class JwtUserFactory {
    public static JwtUser create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
        return new JwtUser(user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
