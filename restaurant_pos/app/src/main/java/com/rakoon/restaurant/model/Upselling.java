package com.rakoon.restaurant.model;

import java.io.Serializable;

public class Upselling implements Serializable {
    private String id, name;
    private double price, tax;
    private String photo;
    private int quantity;
    boolean modification;
    String caregoryid,desc;

    public Upselling(String id, String name, double price, double tax, String photo, int quantity, boolean modification, String caregoryid, String desc) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.tax = tax;
        this.photo = photo;
        this.quantity = quantity;
        this.modification = modification;
        this.caregoryid = caregoryid;
        this.desc = desc;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isModification() {
        return modification;
    }

    public void setModification(boolean modification) {
        this.modification = modification;
    }

    public String getCaregoryid() {
        return caregoryid;
    }

    public void setCaregoryid(String caregoryid) {
        this.caregoryid = caregoryid;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
