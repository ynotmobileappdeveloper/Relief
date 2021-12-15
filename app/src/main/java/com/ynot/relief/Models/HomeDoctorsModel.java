package com.ynot.relief.Models;

public class HomeDoctorsModel {
    private String id;
    private String name;
    private String image;
    private String role;
    private String patient;
    private String exp;
    private String rating;

    public HomeDoctorsModel(String id, String name, String image, String role, String patient, String exp, String rating) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.role = role;
        this.patient = patient;
        this.exp = exp;
        this.rating = rating;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
