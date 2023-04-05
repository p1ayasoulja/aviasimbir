package com.example.aviasimbir.requestresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAirlineRequest {
    private String name;

    public CreateAirlineRequest(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
