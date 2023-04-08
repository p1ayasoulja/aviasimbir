package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Ответ на запрос получения информации о самолете")

public class PlaneResponse {
    @ApiModelProperty(value = "Идентификатор самолета")
    private Long id;
    @ApiModelProperty(value = "Бренд самолета")
    private String brand;
    @ApiModelProperty(value = "Модель самолета")
    private String model;
    @ApiModelProperty(value = "Число мест в самолете")
    private int seats;
    @ApiModelProperty(value = "Авиалиния самолета")
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
