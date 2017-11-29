package com.example.dotoan.musicrecommendation.Contruct;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DOTOAN on 11/26/2017.
 */

public class Item {

    int id;
    String mid;
    String order;
    double dis;

    public Item() {
    }

    public Item(int id, String mid, String order, double dis) {
        this.id = id;
        this.mid = mid;
        this.order = order;
        this.dis = dis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }
}
