package com.example.dotoan.musicrecommendation.Contruct;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DOTOAN on 11/26/2017.
 */

public class RPush {
    String mid;
    int order;

    public RPush() {
    }

    public RPush(String mid, int order) {
        this.mid = mid;
        this.order = order;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(mid, order);
        return result;
    }
}
