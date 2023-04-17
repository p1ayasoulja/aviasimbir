package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@Api("Ответ на запрос получения заработанной суммы")
public class GetTotalEarnedResponse {
    @ApiModelProperty("Сумма заработка")
    private BigDecimal totalEarned;

    public GetTotalEarnedResponse(@JsonProperty("totalEarned") BigDecimal totalEarned) {
        this.totalEarned = totalEarned;
    }

    public BigDecimal getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(BigDecimal totalEarned) {
        this.totalEarned = totalEarned;
    }
}
