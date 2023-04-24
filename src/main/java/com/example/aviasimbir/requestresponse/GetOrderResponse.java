package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Ответ на получение списка заказов")
public class GetOrderResponse {
    @ApiModelProperty("Идентификатор заказа")
    private Long id;
    @ApiModelProperty("Статус заказа")
    private String status;

    public GetOrderResponse(@JsonProperty("id") Long id, @JsonProperty("status") String status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
