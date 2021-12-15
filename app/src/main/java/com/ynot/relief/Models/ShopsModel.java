package com.ynot.relief.Models;

public class ShopsModel {

    private String id;
    private String image;
    private String name;
    private String address;
    private String rating;
    private String distance;

    public ShopsModel(String id, String image, String name, String address, String rating, String distance) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
