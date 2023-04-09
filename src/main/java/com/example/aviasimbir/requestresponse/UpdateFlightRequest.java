package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class UpdateFlightRequest {
    private Long planeId;
    private String departure;
    private String destination;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;

    @JsonCreator
    public UpdateFlightRequest(@JsonProperty("planeId") Long planeId, @JsonProperty("departure") String departure,
                               @JsonProperty("destination") String destination,
                               @JsonProperty("departureTime") ZonedDateTime departureTime,
                               @JsonProperty("arrivalTime") ZonedDateTime arrivalTime) {
        this.planeId = planeId;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public Long getPlaneId() {
        return planeId;
    }

    public void setPlaneId(Long planeId) {
        this.planeId = planeId;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public ZonedDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(ZonedDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(ZonedDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
