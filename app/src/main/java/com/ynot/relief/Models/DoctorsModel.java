package com.ynot.relief.Models;

public class DoctorsModel {
    private String id;
    private String image;
    private String name;
    private String qualification;
    private String time;
    private String location;
    private String rating;

    public DoctorsModel(String id, String image, String name, String qualification, String time, String location, String rating) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.qualification = qualification;
        this.time = time;
        this.location = location;
        this.rating = rating;
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

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
