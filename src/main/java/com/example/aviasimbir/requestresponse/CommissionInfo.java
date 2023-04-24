package com.example.aviasimbir.requestresponse;

import io.swagger.annotations.Api;

import java.math.BigDecimal;

@Api("Класс для показа информации о коммиссии")
public class CommissionInfo {
    private BigDecimal averageCommission;
    private BigDecimal totalCommission;

    public CommissionInfo(BigDecimal averageCommission, BigDecimal totalCommission) {
        this.averageCommission = averageCommission;
        this.totalCommission = totalCommission;
    }

    public BigDecimal getAverageCommission() {
        return averageCommission;
    }

    public void setAverageCommission(BigDecimal averageCommission) {
        this.averageCommission = averageCommission;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }
}
