package com.example.aviasimbir.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Airline {
    @Id
    @GeneratedValue(generator = "airline_id_generator")
    @SequenceGenerator(name = "airline_id_generator", sequenceName = "airline_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "airline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Plane> planes;

    public Airline() {
    }

    public Airline(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Airline " + name;
    }
}