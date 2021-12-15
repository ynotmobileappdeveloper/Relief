package com.ynot.relief.Models;

public class OrderDetailModel {

    private String category_name;
    private String product_name;
    private String price;
    private String qty;
    private String image;

    public OrderDetailModel(String category_name, String product_name, String price, String qty, String image) {
        this.category_name = category_name;
        this.product_name = product_name;
        this.price = price;
        this.qty = qty;
        this.image = image;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
