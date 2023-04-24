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
    private static final String[] CASHIER_ENDPOINTS = {"/cashier/**", "/swagger-ui/**"};
    private static final String[] REPRESENTATIVE_ENDPOINTS = {"/representative/**", "/swagger-ui/**"};
    private static final String[] MANAGER_ENDPOINTS = {"/manager/**", "/swagger-ui/**", "/representative/**"};
    private static final String CUSTOMER_ENDPOINT = "/customer/**";
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
                .antMatchers("/auth", "/registration", "/customer/flight/{id}/tickets", "/customer/filter").permitAll()
                .antMatchers(CUSTOMER_ENDPOINT).authenticated()
                .antMatchers(CASHIER_ENDPOINTS).hasAuthority(Role.CASHIER.name())
                .antMatchers(REPRESENTATIVE_ENDPOINTS).hasAuthority(Role.REPRESENTATIVE.name())
                .antMatchers(MANAGER_ENDPOINTS).hasAuthority(Role.MANAGER.name())
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}
