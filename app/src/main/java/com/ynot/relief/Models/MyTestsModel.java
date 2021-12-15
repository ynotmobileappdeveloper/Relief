package com.ynot.relief.Models;

public class MyTestsModel {

    private String id;
    private String date;
    private String name;
    private String gender;
    private String age;
    private String tests;
    private String image;
    private String lab_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getTests() {
        return tests;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLab_status() {
        return lab_status;
    }

    public void setLab_status(String lab_status) {
        this.lab_status = lab_status;
    }

    public MyTestsModel(String id, String date, String name, String gender, String age, String tests, String image, String lab_status) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.tests = tests;
        this.image = image;
        this.lab_status = lab_status;
    }
}
