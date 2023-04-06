package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AirlineResponse {
    private String name;

    public AirlineResponse(String name) {
        this.name = name;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
