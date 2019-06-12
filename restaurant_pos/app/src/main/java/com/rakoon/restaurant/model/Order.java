package com.rakoon.restaurant.model;

import java.io.Serializable;

public class Order implements Serializable {
    private String order_id, order_title, date, total_price;

    public Order() {
    }

    public Order(String order_id, String order_title, String date, String total_price) {
        this.order_id = order_id;
        this.order_title = order_title;
        this.date = date;
        this.total_price = total_price;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_title() {
        return order_title;
    }

    public void setOrder_title(String order_title) {
        this.order_title = order_title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }
}
