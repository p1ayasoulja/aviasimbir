package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@Api("Ответ на запрос покупки билетов")
public class CreateOrderResponse {
    @ApiModelProperty("Идентификатор заказа")
    private Long id;
    @ApiModelProperty("Сумма заказа")
    private BigDecimal totalPrice;
    @ApiModelProperty("Статус заказа")
    private String status;

    public CreateOrderResponse(@JsonProperty("id") Long id, @JsonProperty("totalPrice") BigDecimal totalPrice,
                               @JsonProperty("status") String status) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
