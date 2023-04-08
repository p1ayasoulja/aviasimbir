package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@Api("Запрос на создание билета")
public class CreateTicketRequest {
    @ApiModelProperty(value = "Цена билета")
    private BigDecimal price;
    @ApiModelProperty(value = "Флаг наличия коммиссии")
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
