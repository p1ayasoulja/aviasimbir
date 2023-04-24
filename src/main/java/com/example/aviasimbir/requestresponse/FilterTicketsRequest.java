package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

@Api("Запрос на показ выборки нужных билетов")
public class FilterTicketsRequest {
    @ApiModelProperty("Место вылета")
    private String departure;
    @ApiModelProperty("Место назначения")
    private String destination;
    @ApiModelProperty("Ранняя допустимая дата вылета")
    private LocalDate minDepartureDay;
    @ApiModelProperty("Поздняя допустивая дата вылета")
    private LocalDate maxDepartureDay;

    public FilterTicketsRequest(@JsonProperty("departure") String departure, @JsonProperty("destination") String destination,
                                @JsonProperty("MinDepartureDay") LocalDate minDepartureDay,
                                @JsonProperty("MaxDepartureDay") LocalDate maxDepartureDay) {
        this.departure = departure;
        this.destination = destination;
        this.minDepartureDay = minDepartureDay;
        this.maxDepartureDay = maxDepartureDay;

    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getMinDepartureDay() {
        return minDepartureDay;
    }

    public void setMinDepartureDay(LocalDate minDepartureDay) {
        minDepartureDay = minDepartureDay;
    }

    public LocalDate getMaxDepartureDay() {
        return maxDepartureDay;
    }

    public void setMaxDepartureDay(LocalDate maxDepartureDay) {
        maxDepartureDay = maxDepartureDay;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }
}
