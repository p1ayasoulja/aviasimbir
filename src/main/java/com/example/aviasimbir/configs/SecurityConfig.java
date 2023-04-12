package com.example.aviasimbir.configs;

import com.example.aviasimbir.Jwt.JwtConfigurer;
import com.example.aviasimbir.Jwt.JwtTokenProvider;
import com.example.aviasimbir.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String[] CASHIER_ENDPOINTS = {"/cashier/{id}/sell", "/cashier/{id}/cancelreservation"};
    private static final String[] REPRESENTATIVE_ENDPOINTS = {"/representative/**"};
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth", "/registration", "/flight/{id}/tickets").permitAll()
                .antMatchers(CASHIER_ENDPOINTS).hasAuthority(Role.CASHIER.name())
                .antMatchers(REPRESENTATIVE_ENDPOINTS).hasAuthority(Role.REPRESENTATIVE.name())
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}
