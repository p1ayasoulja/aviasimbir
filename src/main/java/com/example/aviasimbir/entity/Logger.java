package com.example.aviasimbir.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log")
public class Logger {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "time")
    private LocalDateTime localDateTime;

    public Logger() {
    }

    public Logger(String title, LocalDateTime localDateTime) {
        this.title = title;
        this.localDateTime = localDateTime;
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
