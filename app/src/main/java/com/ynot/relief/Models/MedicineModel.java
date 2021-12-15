package com.ynot.relief.Models;

public class MedicineModel {
    private String id;
    private String image;
    private String company;
    private String name;
    private String price;
    private String fav_status;

    public MedicineModel(String id, String image, String company, String name, String price, String fav_status) {
        this.id = id;
        this.image = image;
        this.company = company;
        this.name = name;
        this.price = price;
        this.fav_status = fav_status;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFav_status() {
        return fav_status;
    }

    public void setFav_status(String fav_status) {
        this.fav_status = fav_status;
    }
}
