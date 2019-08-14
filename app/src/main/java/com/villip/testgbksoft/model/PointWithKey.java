package com.villip.testgbksoft.model;

public class PointWithKey {
    public String name;
    public String latitude;
    public String longitude;
    public String key;

    public PointWithKey(String name, String latitude, String longitude, String key) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.key = key;
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

    public String getKey() {
        return key;
    }
}
