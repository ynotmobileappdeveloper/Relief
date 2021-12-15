package com.ynot.relief.Models;

public class MyordersModel {
    private String id;
    private String image;
    private String name;
    private String order;
    private String qty;
    private String pdate;
    private String ddate;
    private String price;
    private String order_status;
    private String payment_mode;
    private String delivery_mode;

    public MyordersModel(String id, String image, String name, String order, String qty, String pdate, String ddate, String price, String order_status, String payment_mode, String delivery_mode) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.order = order;
        this.qty = qty;
        this.pdate = pdate;
        this.ddate = ddate;
        this.price = price;
        this.order_status = order_status;
        this.payment_mode = payment_mode;
        this.delivery_mode = delivery_mode;
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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPdate() {
        return pdate;
    }

    public void setPdate(String pdate) {
        this.pdate = pdate;
    }

    public String getDdate() {
        return ddate;
    }

    public void setDdate(String ddate) {
        this.ddate = ddate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getDelivery_mode() {
        return delivery_mode;
    }

    public void setDelivery_mode(String delivery_mode) {
        this.delivery_mode = delivery_mode;
    }


}
