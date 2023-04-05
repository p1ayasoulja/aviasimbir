package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketResponse {

    private String departure;
    private String destination;
    private Integer price;
    private Boolean reserved;
    private Boolean sold;

    public TicketResponse(@JsonProperty("departure") String departure, @JsonProperty("destination") String destination,
                          @JsonProperty("price") Integer price, @JsonProperty("reserved") Boolean reserved,
                          @JsonProperty("sold") Boolean sold) {
        this.departure = departure;
        this.destination = destination;
        this.price = price;
        this.reserved = reserved;
        this.sold = sold;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getReserved() {
        return reserved;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }
}
