package com.ynot.relief.Models;

public class PharmacyModel {
    private  String id;
    private  String name;
    private  int  image;
    private  String  address;
    private  String  time;
    private  String  phone;
    private  String  whatsapp;

    public PharmacyModel(String id, String name, int image, String address, String time, String phone, String whatsapp) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.address = address;
        this.time = time;
        this.phone = phone;
        this.whatsapp = whatsapp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }
}
