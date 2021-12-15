package com.ynot.relief.Models;

public class PTestMode {
    private String id;
    private String name;
    private String gender;
    private String age;
    private String title;
    private String medicine;
    private String test;
    private String date;

    public PTestMode(String id, String name, String gender, String age, String title, String medicine, String test, String date) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.title = title;
        this.medicine = medicine;
        this.test = test;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
