package com.ynot.relief.Models;

public class MediProduct {
    private String id;
    private String name;
    private String need_pres;

    public MediProduct(String id, String name, String need_pres) {
        this.id = id;
        this.name = name;
        this.need_pres = need_pres;
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

    public String getNeed_pres() {
        return need_pres;
    }

    public void setNeed_pres(String need_pres) {
        this.need_pres = need_pres;
    }
}
