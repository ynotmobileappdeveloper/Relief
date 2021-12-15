package com.ynot.relief.Models;

public class AddressModel {

    private String id;
    private String house;
    private String address;
    private String pin;
    private String lat;
    private String lon;
    private String status;
    private String google_location;
    private String store_id;

    public AddressModel(String id, String house, String address, String pin, String lat, String lon, String status, String google_location, String store_id) {
        this.id = id;
        this.house = house;
        this.address = address;
        this.pin = pin;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
        this.google_location = google_location;
        this.store_id = store_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGoogle_location() {
        return google_location;
    }

    public void setGoogle_location(String google_location) {
        this.google_location = google_location;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
}
