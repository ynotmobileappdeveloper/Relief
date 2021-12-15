package com.ynot.relief.Models;

public class BillModel {
    private String items;
    private String products;
    private String qty;
    private String rate;
    private String discounts;
    private String item_id;
    private String delete;

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public BillModel(String items, String products, String qty, String rate, String discounts, String item_id, String delete) {
        this.items = items;
        this.products = products;
        this.qty = qty;
        this.rate = rate;
        this.discounts = discounts;
        this.item_id = item_id;
        this.delete = delete;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDiscounts() {
        return discounts;
    }

    public void setDiscounts(String discounts) {
        this.discounts = discounts;
    }
}
