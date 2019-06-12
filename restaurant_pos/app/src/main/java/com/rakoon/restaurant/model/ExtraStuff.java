package com.rakoon.restaurant.model;

import java.io.Serializable;

public class ExtraStuff implements Serializable {
    private String id, name, price, photo, modification, quantity;
    boolean status;


    public ExtraStuff(String id, String name, String price, String photo, String modification, String quantity, boolean status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.photo = photo;
        this.modification = modification;
        this.quantity = quantity;
        this.status = status;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
