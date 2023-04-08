package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@Api("Ответ на запрос получения информации о билете")
public class TicketResponse {
    @ApiModelProperty(value = "Место вылета")
    private String departure;
    @ApiModelProperty(value = "Место назначения")
    private String destination;
    @ApiModelProperty(value = "Цена билета")
    private BigDecimal price;
    @ApiModelProperty(value = "Флаг брони билета")
    private Boolean reserved;
    @ApiModelProperty(value = "Флаг продажи билета")
    private Boolean sold;

    public TicketResponse(String departure, String destination, BigDecimal price, Boolean reserved,
                          Boolean sold) {
        this.departure = departure;
        this.destination = destination;
        this.price = price;
        this.reserved = reserved;
        this.sold = sold;
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

    @JsonProperty("price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @JsonProperty("reserved")
    public Boolean getReserved() {
        return reserved;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    @JsonProperty("sold")
    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }
}
