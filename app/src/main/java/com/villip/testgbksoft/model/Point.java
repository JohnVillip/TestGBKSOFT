package com.villip.testgbksoft.model;

public class Point {
    public String name;
    public String latitude;
    public String longitude;

    public Point() {
    }

    public Point(String name, String latitude, String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
