package com.rakoon.restaurant.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UpsellingCategory implements Serializable {
    private String cate_name;
    private ArrayList<Upselling> items;

    public UpsellingCategory(String cate_name, ArrayList<Upselling> items) {
        this.cate_name = cate_name;
        this.items = items;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public ArrayList<Upselling> getItems() {
        return items;
    }

    public void setItems(ArrayList<Upselling> items) {
        this.items = items;
    }
}
