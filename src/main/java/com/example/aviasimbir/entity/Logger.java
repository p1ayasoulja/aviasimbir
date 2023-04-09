package com.example.aviasimbir.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "log")
public class Logger {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "time")
    private Instant instant;

    public Logger() {
    }

    public Logger(String title, Instant instant) {
        this.title = title;
        this.instant = instant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
