package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Запрос на обновление информации об авиалинии")
public class UpdateAirlineRequest {
    @ApiModelProperty(value = "Имя авиалинии")
    private String name;

    @JsonCreator
    public UpdateAirlineRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
