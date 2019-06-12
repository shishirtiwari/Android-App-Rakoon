package com.rakoon.restaurant.model;

import java.io.Serializable;

public class Area implements Serializable {
    private String id, name, delivery, branch_id;

    public Area() {
    }

    public Area(String id, String name, String delivery, String branch_id) {
        this.id = id;
        this.name = name;
        this.delivery = delivery;
        this.branch_id = branch_id;
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

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }
}
