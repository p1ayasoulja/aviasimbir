package com.example.aviasimbir.requestresponse;

import com.example.aviasimbir.entity.Flight;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;

public class CreateTickerRequest {
    private Integer price;
    private Boolean commission;

    public CreateTickerRequest(@JsonProperty("price") Integer price,@JsonProperty("commission") Boolean commission) {
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
