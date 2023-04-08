package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Запрос на получение всех билетов в зависимости от условия")
public class GetAllTicketsRequest {
    @ApiModelProperty(value = "Флаг показа проданных билетов")
    private Boolean sold;

    @JsonCreator
    public GetAllTicketsRequest(@JsonProperty("sold") Boolean sold) {
        this.sold = sold;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }
}
