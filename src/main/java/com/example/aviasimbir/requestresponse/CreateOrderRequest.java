package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@Api("Запрос на бронирование билета")
public class CreateOrderRequest {
    @ApiModelProperty("Промокод")
    private String promocode;
    private List<Long> tickets_id;

    public CreateOrderRequest(@JsonProperty("promocode") String promocode, @JsonProperty("tickets_id") List<Long> tickets_id) {
        this.promocode = promocode;
        this.tickets_id = tickets_id;
    }

    public List<Long> getTickets_id() {
        return tickets_id;
    }

    public void setTickets_id(List<Long> tickets_id) {
        this.tickets_id = tickets_id;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }
}
