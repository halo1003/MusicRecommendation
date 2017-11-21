package com.example.dotoan.musicrecommendation.Contruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOTOAN on 11/14/2017.
 */

public class FilterC {
    int user1;
    int size;
    List<ValueC> user2_per1 = new ArrayList<ValueC>();

    public List<ValueC> getUser2_per1() {
        return user2_per1;
    }

    public void setUser2_per1(List<ValueC> user2_per1) {
        this.user2_per1 = user2_per1;
    }

    public FilterC() {
    }

    public int getUser1() {
        return user1;
    }

    public void setUser1(int user1) {
        this.user1 = user1;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
