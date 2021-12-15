package com.ynot.relief.Models;

public class MedicalModel {
    private String id;
    private int image;

    public MedicalModel(String id, int image) {
        this.id = id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
