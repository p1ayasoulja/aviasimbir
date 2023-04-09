package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CreateTicketRequest {
    private BigDecimal price;
    private Boolean commission;

    @JsonCreator
    public CreateTicketRequest(@JsonProperty("price") BigDecimal price, @JsonProperty("commission") Boolean commission) {
        this.price = price;
        this.commission = commission;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getCommission() {
        return commission;
    }

    public void setCommission(Boolean commission) {
        this.commission = commission;
    }
}
