package com.example.aviasimbir.entity;

import javax.persistence.*;

@Entity
public class Plane {
    @Id
    @GeneratedValue(generator = "plane_id_generator")
    @SequenceGenerator(name = "plane_id_generator", sequenceName = "plane_id_seq", allocationSize = 1)
    private Long id;
    @Column(name = "brand")
    private String brand;
    @Column(name = "model")
    private String model;
    @Column(name = "seats")
    private int seats;

    @ManyToOne
    @JoinColumn(name = "airline")
    private Airline airline;
    @OneToOne(mappedBy = "plane", cascade = CascadeType.ALL)
    private Flight flight;

    public Plane() {
    }

    public Plane(String brand, String model, int seats, Airline airline) {
        this.brand = brand;
        this.model = model;
        this.seats = seats;
        this.airline = airline;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    @Override
    public String toString() {
        return "Plane with " +
                "id = " + id;
    }
}