package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@Api("Ответ на запрос получения средней коммиссии по проданным билетам")
public class GetCommissionInfo {
    @ApiModelProperty("Средняя коммиссия")
    private BigDecimal averageCommission;
    @ApiModelProperty("Сумма коммисии")
    private BigDecimal totalCommissionSum;

    public GetCommissionInfo(@JsonProperty("Average Commission") BigDecimal averageCommission,
                             @JsonProperty("Total Commission") BigDecimal totalCommissionSum) {
        this.averageCommission = averageCommission;
        this.totalCommissionSum = totalCommissionSum;
    }

    public BigDecimal getAverageCommission() {
        return averageCommission;
    }

    public void setAverageCommission(BigDecimal averageCommission) {
        this.averageCommission = averageCommission;
    }

    public BigDecimal getTotalCommissionSum() {
        return totalCommissionSum;
    }

    public void setTotalCommissionSum(BigDecimal totalCommissionSum) {
        this.totalCommissionSum = totalCommissionSum;
    }
}
