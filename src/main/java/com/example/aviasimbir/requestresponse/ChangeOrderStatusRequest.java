package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Запрос на смену статуса заказа")
public class ChangeOrderStatusRequest {
    @ApiModelProperty("Статус заказа")
    private String status;

    public ChangeOrderStatusRequest(@JsonProperty("status") String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
