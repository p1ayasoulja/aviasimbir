package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateTicketRequest {
    private Integer price;
    private Boolean commission;
    @JsonCreator
    public CreateTicketRequest(@JsonProperty("price") Integer price, @JsonProperty("commission") Boolean commission) {
        this.price = price;
        this.commission = commission;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getCommission() {
        return commission;
    }

    public void setCommission(Boolean commission) {
        this.commission = commission;
    }
}
