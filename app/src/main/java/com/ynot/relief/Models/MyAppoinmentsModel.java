package com.ynot.relief.Models;

import java.io.Serializable;

public class MyAppoinmentsModel implements Serializable {
   private  String id;
   private  String doc_name;
   private  String department;
   private  String name;
   private  String gender;
   private  String age;
   private  String op_type;
   private  String time;
   private  String date;
   private  String op_status;
   private  String doc_address;
   private  String doc_phone;

    public MyAppoinmentsModel(String id, String doc_name, String department, String name, String gender, String age, String op_type, String time, String date, String op_status, String doc_address, String doc_phone) {
        this.id = id;
        this.doc_name = doc_name;
        this.department = department;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.op_type = op_type;
        this.time = time;
        this.date = date;
        this.op_status = op_status;
        this.doc_address = doc_address;
        this.doc_phone = doc_phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public String getOp_type() {
        return op_type;
    }

    public void setOp_type(String op_type) {
        this.op_type = op_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOp_status() {
        return op_status;
    }

    public void setOp_status(String op_status) {
        this.op_status = op_status;
    }

    public String getDoc_address() {
        return doc_address;
    }

    public void setDoc_address(String doc_address) {
        this.doc_address = doc_address;
    }

    public String getDoc_phone() {
        return doc_phone;
    }

    public void setDoc_phone(String doc_phone) {
        this.doc_phone = doc_phone;
    }
}
