package com.ynot.relief.Models;

public class LabModel {
    private String slno;
    private String name;

    public LabModel(String slno, String name) {
        this.slno = slno;
        this.name = name;
    }

    public String getSlno() {
        return slno;
    }

    public void setSlno(String slno) {
        this.slno = slno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
