package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class UpdateFlightRequest {
    private Long plane_id;
    private String departure;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    public UpdateFlightRequest(@JsonProperty("plane") Long plane_id, @JsonProperty("departure") String departure,
                               @JsonProperty("destination") String destination,
                               @JsonProperty("departureTime") LocalDateTime departureTime,
                               @JsonProperty("arrivalTime") LocalDateTime arrivalTime) {
        this.plane_id = plane_id;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public Long getPlane_id() {
        return plane_id;
    }

    public void setPlane_id(Long plane_id) {
        this.plane_id = plane_id;
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

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
