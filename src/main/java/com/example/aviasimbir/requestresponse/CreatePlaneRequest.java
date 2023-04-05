package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePlaneRequest {
    private String brand;
    private String model;
    private Integer seats;
    private Long airline;

    public CreatePlaneRequest(@JsonProperty("brand") String brand, @JsonProperty("model") String model,
                              @JsonProperty("seats") Integer seats,
                              @JsonProperty("airline") Long airline) {
        this.brand = brand;
        this.model = model;
        this.seats = seats;
        this.airline = airline;
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

    public Long getAirline() {
        return airline;
    }

    public void setAirline(Long airline) {
        this.airline = airline;
    }
}
