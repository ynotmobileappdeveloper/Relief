package com.ynot.relief.Models;

public class MedicineViewModel {

    private String name;
    private String qty;

    private String morning;
    private String noon;
    private String night;
    private String after;
    private String before;
    private String nos;
    private String ml;
    private String grm;
    private String medi_count;
    private String medi_time;

    public MedicineViewModel(String name, String qty, String morning, String noon, String night, String after, String before, String nos, String ml, String grm, String medi_count, String medi_time) {
        this.name = name;
        this.qty = qty;
        this.morning = morning;
        this.noon = noon;
        this.night = night;
        this.after = after;
        this.before = before;
        this.nos = nos;
        this.ml = ml;
        this.grm = grm;
        this.medi_count = medi_count;
        this.medi_time = medi_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getMorning() {
        return morning;
    }

    public void setMorning(String morning) {
        this.morning = morning;
    }

    public String getNoon() {
        return noon;
    }

    public void setNoon(String noon) {
        this.noon = noon;
    }

    public String getNight() {
        return night;
    }

    public void setNight(String night) {
        this.night = night;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getNos() {
        return nos;
    }

    public void setNos(String nos) {
        this.nos = nos;
    }

    public String getMl() {
        return ml;
    }

    public void setMl(String ml) {
        this.ml = ml;
    }

    public String getGrm() {
        return grm;
    }

    public void setGrm(String grm) {
        this.grm = grm;
    }

    public String getMedi_count() {
        return medi_count;
    }

    public void setMedi_count(String medi_count) {
        this.medi_count = medi_count;
    }

    public String getMedi_time() {
        return medi_time;
    }

    public void setMedi_time(String medi_time) {
        this.medi_time = medi_time;
    }
}
