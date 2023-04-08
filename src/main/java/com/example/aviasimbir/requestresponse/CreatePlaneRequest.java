package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Запрос на создание самолета")
public class CreatePlaneRequest {
    @ApiModelProperty(value = "Бренд самолета")
    private String brand;
    @ApiModelProperty(value = "Модель самолета")
    private String model;
    @ApiModelProperty(value = "Число мест в самолете")
    private Integer seats;
    @ApiModelProperty(value = "Идентификатор авиалинии самолета")
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
