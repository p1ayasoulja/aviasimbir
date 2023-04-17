package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Ответ на запрос получения числа проданных билетов с точкой отпрвления Казань")
public class GetSoldTicketsFromKazanCountResponse {
    @ApiModelProperty("Число проданных билетов с точкой отправления Казань")
    private Long SoldTicketsFromKazanCount;

    public GetSoldTicketsFromKazanCountResponse(@JsonProperty("soldTicketsFromKazan") Long soldTicketsFromKazanCount) {
        SoldTicketsFromKazanCount = soldTicketsFromKazanCount;
    }

    public Long getSoldTicketsFromKazanCount() {
        return SoldTicketsFromKazanCount;
    }

    public void setSoldTicketsFromKazanCount(Long soldTicketsFromKazanCount) {
        SoldTicketsFromKazanCount = soldTicketsFromKazanCount;
    }
}
