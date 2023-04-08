package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.time.ZonedDateTime;

@Api("Ответ на запрос получения информации о рейсе")
public class FlightResponse {
    @ApiModelProperty(value = "Идентификатор рейса")
    private Long id;
    @ApiModelProperty(value = "Имя авиалинии рейса")
    private String airline;
    @ApiModelProperty(value = "Место вылета")
    private String departure;
    @ApiModelProperty(value = "Место назначения")
    private String destination;
    @ApiModelProperty(value = "Время вылета")
    private ZonedDateTime departureTime;
    @ApiModelProperty(value = "Время прилета")
    private ZonedDateTime arrivalTime;

    public FlightResponse(Long id, String airline, String departure, String destination, ZonedDateTime departureTime, ZonedDateTime arrivalTime) {
        this.id = id;
        this.airline = airline;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    @JsonProperty("airline")
    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    @JsonProperty("departure")
    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    @JsonProperty("destination")
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @JsonProperty("departureTime")
    public ZonedDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(ZonedDateTime departureTime) {
        this.departureTime = departureTime;
    }

    @JsonProperty("arrivalTime")
    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(ZonedDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
