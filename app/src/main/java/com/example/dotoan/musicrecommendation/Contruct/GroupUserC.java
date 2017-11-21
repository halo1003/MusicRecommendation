package com.example.dotoan.musicrecommendation.Contruct;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DOTOAN on 11/21/2017.
 */

public class GroupUserC {
    double distance;
    int user;

    public GroupUserC() {
    }

    public GroupUserC(double distance, int user) {
        this.distance = distance;
        this.user = user;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("distance", distance);
        result.put("user", user);
        return result;
    }
}
