package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePlaneRequest {
    private String brand;
    private String model;
    private Integer seats;
    private Long airlineId;

    @JsonCreator
    public CreatePlaneRequest(@JsonProperty("brand") String brand, @JsonProperty("model") String model,
                              @JsonProperty("seats") Integer seats,
                              @JsonProperty("airlineId") Long airlineId) {
        this.brand = brand;
        this.model = model;
        this.seats = seats;
        this.airlineId = airlineId;
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

    public Long getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(Long airlineId) {
        this.airlineId = airlineId;
    }
}
