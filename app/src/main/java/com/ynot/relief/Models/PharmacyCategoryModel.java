package com.ynot.relief.Models;

public class PharmacyCategoryModel {
   private int image;
   private String since;
   private String name;
   private String address;
   private String open_status;
   private String open;
   private String phone;

    public PharmacyCategoryModel(int image, String since, String name, String address, String open_status, String open, String phone) {
        this.image = image;
        this.since = since;
        this.name = name;
        this.address = address;
        this.open_status = open_status;
        this.open = open;
        this.phone = phone;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
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

    public String getOpen_status() {
        return open_status;
    }

    public void setOpen_status(String open_status) {
        this.open_status = open_status;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
