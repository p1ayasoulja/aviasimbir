package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@Api("Ответ на запрос получения средней коммиссии по проданным билетам")
public class GetAverageCommissionResponse {
    @ApiModelProperty("Средняя коммиссия")
    private BigDecimal averageCommission;

    public GetAverageCommissionResponse(@JsonProperty("Average Commission") BigDecimal averageCommission) {
        this.averageCommission = averageCommission;
    }

    public BigDecimal getAverageCommission() {
        return averageCommission;
    }

    public void setAverageCommission(BigDecimal averageCommission) {
        this.averageCommission = averageCommission;
    }
}
