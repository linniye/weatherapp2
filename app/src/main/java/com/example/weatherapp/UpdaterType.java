package com.example.weatherapp;

public enum UpdaterType {

    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    PPM("ppm");

    private final String tag;

    UpdaterType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
