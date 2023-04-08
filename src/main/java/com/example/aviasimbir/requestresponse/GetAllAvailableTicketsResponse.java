package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;

@ApiOperation("Ответ на получение списка доступных билетов")
public class GetAllAvailableTicketsResponse {
    @ApiModelProperty(value = "Идентификатор билета")
    private Long id;
    @ApiModelProperty(value = "Цена билета")
    private BigDecimal price;

    public GetAllAvailableTicketsResponse(@JsonProperty("id") Long id,
                                          @JsonProperty("price") BigDecimal price) {
        this.id = id;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
