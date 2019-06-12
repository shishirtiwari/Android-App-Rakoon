package com.rakoon.restaurant.model;

import java.io.Serializable;

public class ItemDetail implements Serializable {
    private String item_id,categoryid,name,desc;
    private double price,tax;
    private String photo;

    public ItemDetail(String item_id, String categoryid, String name, String desc, double price, double tax, String photo) {
        this.item_id = item_id;
        this.categoryid = categoryid;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.tax = tax;
        this.photo = photo;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
}
