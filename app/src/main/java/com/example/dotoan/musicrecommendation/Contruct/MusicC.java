package com.example.dotoan.musicrecommendation.Contruct;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DOTOAN on 11/19/2017.
 */

public class MusicC {
    public int _id;
    public String mid;

    public MusicC() {
    }

    public MusicC(int _id, String mid) {

        this._id = _id;
        this.mid = mid;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("_id", _id);
        result.put("mid", mid);

        return result;
    }
}
