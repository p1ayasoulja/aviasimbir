package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class CreateFlightRequest {
    private Long planeId;
    private String departure;
    private String destination;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;
    private BigDecimal ticketPrice;
    private Boolean commission;

    @JsonCreator
    public CreateFlightRequest(@JsonProperty("planeId") Long planeId, @JsonProperty("departure") String departure,
                               @JsonProperty("destination") String destination,
                               @JsonProperty("departureTime") ZonedDateTime departureTime,
                               @JsonProperty("arrivalTime") ZonedDateTime arrivalTime,
                               @JsonProperty("ticketPrice") BigDecimal ticketPrice,
                               @JsonProperty("commission") Boolean commission) {
        this.planeId = planeId;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.ticketPrice = ticketPrice;
        this.commission = commission;
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

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Boolean getCommission() {
        return commission;
    }

    public void setCommission(Boolean commission) {
        this.commission = commission;
    }
}
