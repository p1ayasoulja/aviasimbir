package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Ответ на запрос получения числа самолетов")
public class GetNumberOfPlanesResponse {
    @ApiModelProperty("Число самолетов")
    private Long numberOfPlanes;

    public GetNumberOfPlanesResponse(@JsonProperty("numberOfPlanes") Long numberOfPlanes) {
        this.numberOfPlanes = numberOfPlanes;
    }

    public Long getNumberOfPlanes() {
        return numberOfPlanes;
    }

    public void setNumberOfPlanes(Long numberOfPlanes) {
        this.numberOfPlanes = numberOfPlanes;
    }
}
