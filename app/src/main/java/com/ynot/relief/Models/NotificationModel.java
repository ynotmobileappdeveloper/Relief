package com.ynot.relief.Models;

public class NotificationModel {

    private String id;
    private String date;
    private String op_date;
    private String op_time;
    private String medicine;
    private String lab_test;
    private String doc_name;

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

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

    public String getOp_date() {
        return op_date;
    }

    public void setOp_date(String op_date) {
        this.op_date = op_date;
    }

    public String getOp_time() {
        return op_time;
    }

    public void setOp_time(String op_time) {
        this.op_time = op_time;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getLab_test() {
        return lab_test;
    }

    public void setLab_test(String lab_test) {
        this.lab_test = lab_test;
    }
}
