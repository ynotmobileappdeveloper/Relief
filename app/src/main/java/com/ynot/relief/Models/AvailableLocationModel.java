package com.ynot.relief.Models;

public class AvailableLocationModel {
    private String store_id;
    private String location;
    private String lat;
    private String lon;

    public AvailableLocationModel(String store_id, String location, String lat, String lon) {
        this.store_id = store_id;
        this.location = location;
        this.lat = lat;
        this.lon = lon;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
