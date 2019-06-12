package com.rakoon.restaurant.model;

import java.io.Serializable;

public class Branch implements Serializable {
    private String id, name, open, close, logo, address;
    private String[] payment_type;
    private String phone;
    String open_lang,close_lang;

    public Branch() {
    }

    public Branch(String id, String name, String open, String close, String logo, String address, String[] payment_type, String phone, String open_lang, String close_lang) {
        this.id = id;
        this.name = name;
        this.open = open;
        this.close = close;
        this.logo = logo;
        this.address = address;
        this.payment_type = payment_type;
        this.phone = phone;
        this.open_lang = open_lang;
        this.close_lang = close_lang;
    }

    public String getOpen_lang() {
        return open_lang;
    }

    public void setOpen_lang(String open_lang) {
        this.open_lang = open_lang;
    }

    public String getClose_lang() {
        return close_lang;
    }

    public void setClose_lang(String close_lang) {
        this.close_lang = close_lang;
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

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String[] getArr() {
        return payment_type;
    }

    public void setArr(String[] payment_type) {
        this.payment_type = payment_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
