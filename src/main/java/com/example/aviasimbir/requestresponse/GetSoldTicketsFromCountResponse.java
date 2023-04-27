package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Ответ на запрос получения числа проданных билетов с точкой отпрвления Казань")
public class GetSoldTicketsFromCountResponse {
    @ApiModelProperty("Число проданных билетов с точкой отправления Казань")
    private Long SoldTicketsFromCount;

    public GetSoldTicketsFromCountResponse(@JsonProperty("soldTicketsFromKazan") Long soldTicketsFromKazanCount) {
        SoldTicketsFromCount = soldTicketsFromKazanCount;
    }

    public Long getSoldTicketsFromCount() {
        return SoldTicketsFromCount;
    }

    public void setSoldTicketsFromCount(Long soldTicketsFromCount) {
        SoldTicketsFromCount = soldTicketsFromCount;
    }
}
