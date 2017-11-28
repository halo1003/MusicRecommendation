package com.example.dotoan.musicrecommendation.Contruct;

/**
 * Created by DOTOAN on 11/27/2017.
 */

public class itemC {
    String id;
    String mid;
    String name;
    String Artic;

    public itemC() {
    }

    public itemC(String id, String mid, String name, String artic) {
        this.id = id;
        this.mid = mid;
        this.name = name;
        Artic = artic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtic() {
        return Artic;
    }

    public void setArtic(String artic) {
        Artic = artic;
    }
}
