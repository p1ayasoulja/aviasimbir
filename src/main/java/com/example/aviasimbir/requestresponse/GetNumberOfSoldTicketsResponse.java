package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Ответ на запрос получения числа проданных билетов")
public class GetNumberOfSoldTicketsResponse {
    @ApiModelProperty("Число проданных билетов")
    private Long numberOfSoldTickets;

    public GetNumberOfSoldTicketsResponse(@JsonProperty("numberOfSoldTickets") Long numberOfSoldTickets) {
        this.numberOfSoldTickets = numberOfSoldTickets;
    }

    public Long getNumberOfSoldTickets() {
        return numberOfSoldTickets;
    }

    public void setNumberOfSoldTickets(Long numberOfSoldTickets) {
        this.numberOfSoldTickets = numberOfSoldTickets;
    }
}
