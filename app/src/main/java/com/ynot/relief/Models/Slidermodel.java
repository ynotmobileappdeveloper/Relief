package com.ynot.relief.Models;

public class Slidermodel {
    private int images;
    private String title;

    public Slidermodel(int images, String title) {
        this.images = images;
        this.title = title;
    }

    public int getImages() {
        return images;
    }

    public void setImages(int images) {
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
