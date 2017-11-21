package com.example.dotoan.musicrecommendation.Contruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOTOAN on 11/14/2017.
 */

public class ListC {
    int user1;
    double radius;
    List<ValueC> user2 = new ArrayList<ValueC>();

    public ListC() {
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

    public List<ValueC> getUser2() {
        return user2;
    }

    public void setUser2(List<ValueC> user2) {
        this.user2 = user2;
    }
}
