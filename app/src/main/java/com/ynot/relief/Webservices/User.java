package com.ynot.relief.Webservices;

public class User {
    private String id;
    private String name;
    private String mob;
    private String email;

    public User(String id, String name, String mob, String email) {
        this.id = id;
        this.name = name;
        this.mob = mob;
        this.email = email;
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

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
