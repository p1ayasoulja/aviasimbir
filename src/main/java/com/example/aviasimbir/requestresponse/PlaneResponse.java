package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PlaneResponse {
    private Long id;
    private String brand;
    private String model;
    private int seats;
    private String airline;

    public PlaneResponse(Long id, String brand, String model,
                         int seats, String airline) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.seats = seats;
        this.airline = airline;
    }

    @JsonProperty("brand")
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @JsonProperty("seats")
    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    @JsonProperty("airline")
    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
