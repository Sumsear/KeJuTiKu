package com.example.hp.keju.http;

public enum RequestMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    RequestMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
