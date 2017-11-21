package com.example.dotoan.musicrecommendation.Contruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOTOAN on 11/10/2017.
 */

public class Node {
    int ID;
    int user_1;
    int user_2;
    double distance;
    int max;
    List<ValueC> arrayList = new ArrayList<ValueC>();

    public Node() {
    }

    public List<ValueC> getArrayList() {
        return arrayList;
    }

    public void setArrayList(List<ValueC> arrayList) {
        this.arrayList = arrayList;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Node(int user_1, int user_2, double distance) {
        this.user_1 = user_1;
        this.user_2 = user_2;
        this.distance = distance;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUser_1() {
        return user_1;
    }

    public void setUser_1(int user_1) {
        this.user_1 = user_1;
    }

    public int getUser_2() {
        return user_2;
    }

    public void setUser_2(int user_2) {
        this.user_2 = user_2;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
