package com.ynot.relief.Models;

public class CartModel {

    private String id;
    private String image;
    private String name;
    private String price;
    private String brand;
    private String product_id;
    private String out_of_stock;

    public CartModel(String id, String image, String name, String price, String brand, String product_id, String out_of_stock) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.product_id = product_id;
        this.out_of_stock = out_of_stock;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getOut_of_stock() {
        return out_of_stock;
    }

    public void setOut_of_stock(String out_of_stock) {
        this.out_of_stock = out_of_stock;
    }
}
