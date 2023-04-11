package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@Api("Ответ на запрос получения статистики проданных билетов")
public class GetStatisticOfSoldTicketsResponse {
    @ApiModelProperty("totalSoldCount")
    private Long totalSold;
    @ApiModelProperty("totalEarned")
    private BigDecimal totalEarned;
    @ApiModelProperty("soldFromKazanCount")
    private Long fromKazan;
    @ApiModelProperty("averageCommission")
    private BigDecimal averageCommission;

    public GetStatisticOfSoldTicketsResponse(@JsonProperty("totalSoldCount") Long totalSold,
                                             @JsonProperty("soldFromKazanCount") Long fromKazan,
                                             @JsonProperty("averageCommission") BigDecimal averageCommission,
                                             @JsonProperty("totalEarned") BigDecimal totalEarned) {
        this.totalSold = totalSold;
        this.fromKazan = fromKazan;
        this.averageCommission = averageCommission;
        this.totalEarned = totalEarned;
    }

    public Long getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Long totalSold) {
        this.totalSold = totalSold;
    }

    public Long getFromKazan() {
        return fromKazan;
    }

    public void setFromKazan(Long fromKazan) {
        this.fromKazan = fromKazan;
    }

    public BigDecimal getAverageCommission() {
        return averageCommission;
    }

    public void setAverageCommission(BigDecimal averageCommission) {
        this.averageCommission = averageCommission;
    }

    public BigDecimal getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(BigDecimal totalEarned) {
        this.totalEarned = totalEarned;
    }
}
