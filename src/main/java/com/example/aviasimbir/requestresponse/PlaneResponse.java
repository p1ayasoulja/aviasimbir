package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PlaneResponse {
    private String brand;
    private String model;
    private int seats;
    private String airline;

    public PlaneResponse(@JsonProperty("brand") String brand, @JsonProperty("model") String model,
                         @JsonProperty("seats")int seats,
                         @JsonProperty("airline")String airline) {
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

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }
}
