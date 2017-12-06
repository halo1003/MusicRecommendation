package com.example.dotoan.musicrecommendation.Contruct;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DOTOAN on 11/19/2017.
 */

public class MusicC implements Parcelable {
    public int _id;
    public String mid;
    public String aname;
    public String mname;
    public String trackid;

    public MusicC() {
    }

    public MusicC(int _id, String mid, String aname, String mname, String trackid) {
        this._id = _id;
        this.mid = mid;
        this.aname = aname;
        this.mname = mname;
        this.trackid = trackid;
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

    public String getTrackid() {
        return trackid;
    }

    public void setTrackid(String trackid) {
        this.trackid = trackid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("_id", _id);
        result.put("mid", mid);
        result.put("aname",aname);
        result.put("mname",mname);
        result.put("trackid",trackid);
        return result;
    }

    public MusicC(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.mid = data[0];
        this.aname = data[1];
        this.mname = data[2];
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.mid,
                this.aname,
                this.mname});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MusicC createFromParcel(Parcel in) {
            return new MusicC(in);
        }

        public MusicC[] newArray(int size) {
            return new MusicC[size];
        }
    };
}
