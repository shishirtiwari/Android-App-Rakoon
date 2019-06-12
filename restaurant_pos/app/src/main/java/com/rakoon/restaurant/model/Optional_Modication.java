package com.rakoon.restaurant.model;

import java.io.Serializable;

public class Optional_Modication implements Serializable {
    private String id, modification_status, name;
    private double price;
    private String photo;
    private int quantity;
    private boolean reruired;
    private String group;

    public Optional_Modication(String id, String modification_status, String name, double price, String photo, int quantity, boolean reruired, String group) {
        this.id = id;
        this.modification_status = modification_status;
        this.name = name;
        this.price = price;
        this.photo = photo;
        this.quantity = quantity;
        this.reruired = reruired;
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModification_status() {
        return modification_status;
    }

    public void setModification_status(String modification_status) {
        this.modification_status = modification_status;
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

    public boolean isReruired() {
        return reruired;
    }

    public void setReruired(boolean reruired) {
        this.reruired = reruired;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
