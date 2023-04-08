package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("Ответ на запрос получения информации о авиалинии")
public class AirlineResponse {
    @ApiModelProperty(value = "Идентификатор авиалинии")
    private Long id;
    @ApiModelProperty(value = "Имя авиалинии")
    private String name;

    public AirlineResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
