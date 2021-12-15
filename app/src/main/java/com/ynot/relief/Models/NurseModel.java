package com.ynot.relief.Models;

public class NurseModel {
    private String id;
    private String image;
    private String name;
    private String service;
    private String fav_status;
    private String offer;
    private String price;

    public NurseModel(String id, String image, String name, String service, String fav_status, String offer, String price) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.service = service;
        this.fav_status = fav_status;
        this.offer = offer;
        this.price = price;
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getFav_status() {
        return fav_status;
    }

    public void setFav_status(String fav_status) {
        this.fav_status = fav_status;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
