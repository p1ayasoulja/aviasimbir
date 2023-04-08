package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@Api("Ответ на запрос по получению всех билеты")
public class GetAllTicketsResponse {
    @ApiModelProperty(value = "Идентификатор билета")
    private Long id;
    @ApiModelProperty(value = "Цена билета")
    private BigDecimal price;
    @ApiModelProperty(value = "Флаг брони билета")
    private Boolean commission;

    public GetAllTicketsResponse(@JsonProperty("id") Long id,
                                 @JsonProperty("price") BigDecimal price, @JsonProperty("commission") Boolean commission) {
        this.id = id;
        this.price = price;
        this.commission = commission;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @JsonProperty("commission")
    public Boolean getCommission() {
        return commission;
    }

    public void setCommission(Boolean commission) {
        this.commission = commission;
    }
}

