package com.example.dotoan.musicrecommendation.Contruct;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DOTOAN on 11/20/2017.
 */

public class UserC {

    public String _id;
    public String id;
    List<HashMap<String,Integer>> t = new ArrayList<HashMap<String, Integer>>();

    public UserC() {
    }

    public List<HashMap<String, Integer>> getT() {
        return t;
    }

    public void setT(List<HashMap<String, Integer>> t) {
        this.t = t;
    }

    public UserC(String _id, String id, List<HashMap<String, Integer>> t) {

        this._id = _id;
        this.id = id;
        this.t = t;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("_id", _id);
        result.put("id", id);
        result.put("li_u",t);
        return result;
    }
}
