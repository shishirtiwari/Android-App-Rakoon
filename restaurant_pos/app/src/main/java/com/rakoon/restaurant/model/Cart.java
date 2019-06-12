package com.rakoon.restaurant.model;

import java.io.Serializable;

public class Cart implements Serializable {
    private int auoto_id;
    private String id, cat_id, cat_name, name, price, tax, photo, modification, description, userid, quantity,
            required_id, required_name, required_price, optional_id, optional_name, optional_price;

    public Cart() {
    }

    public Cart(int auoto_id, String id, String cat_id, String cat_name, String name, String price, String tax, String photo, String modification, String description, String userid, String quantity, String required_id, String required_name, String required_price, String optional_id, String optional_name, String optional_price) {
        this.auoto_id = auoto_id;
        this.id = id;
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.name = name;
        this.price = price;
        this.tax = tax;
        this.photo = photo;
        this.modification = modification;
        this.description = description;
        this.userid = userid;
        this.quantity = quantity;
        this.required_id = required_id;
        this.required_name = required_name;
        this.required_price = required_price;
        this.optional_id = optional_id;
        this.optional_name = optional_name;
        this.optional_price = optional_price;
    }

    public int getAuoto_id() {
        return auoto_id;
    }

    public void setAuoto_id(int auoto_id) {
        this.auoto_id = auoto_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
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

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRequired_id() {
        return required_id;
    }

    public void setRequired_id(String required_id) {
        this.required_id = required_id;
    }

    public String getRequired_name() {
        return required_name;
    }

    public void setRequired_name(String required_name) {
        this.required_name = required_name;
    }

    public String getRequired_price() {
        return required_price;
    }

    public void setRequired_price(String required_price) {
        this.required_price = required_price;
    }

    public String getOptional_id() {
        return optional_id;
    }

    public void setOptional_id(String optional_id) {
        this.optional_id = optional_id;
    }

    public String getOptional_name() {
        return optional_name;
    }

    public void setOptional_name(String optional_name) {
        this.optional_name = optional_name;
    }

    public String getOptional_price() {
        return optional_price;
    }

    public void setOptional_price(String optional_price) {
        this.optional_price = optional_price;
    }
}
