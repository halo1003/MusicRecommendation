package com.example.dotoan.musicrecommendation.Contruct;

/**
 * Created by DOTOAN on 11/27/2017.
 */

public class Mdetail {
    String trackid;
    String mid;
    String aname;
    String mname;

    public Mdetail() {
    }

    public Mdetail(String trackid, String mid, String aname, String mname) {
        this.trackid = trackid;
        this.mid = mid;
        this.aname = aname;
        this.mname = mname;
    }

    public String getTrackid() {
        return trackid;
    }

    public void setTrackid(String trackid) {
        this.trackid = trackid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }
}
