package com.example.dotoan.musicrecommendation.Contruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOTOAN on 11/14/2017.
 */

public class ListC {
    int user1;
    double radius;
    List<Integer> user2 = new ArrayList<Integer>();

    public ListC() {
    }

    public ListC(List<Integer> user, int user1, double radius) {
        this.user1 = user1;
        this.user2 = user2;
        this.radius = radius;
    }

    public int getUser1() {
        return user1;
    }

    public void setUser1(int user1) {
        this.user1 = user1;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<Integer> getUser2() {
        return user2;
    }

    public void setUser2(List<Integer> user2) {
        this.user2 = user2;
    }
}
