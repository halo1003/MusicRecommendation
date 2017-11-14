package com.example.dotoan.musicrecommendation.Contruct;

/**
 * Created by DOTOAN on 11/10/2017.
 */

public class DistanceC{
    private int current_user;
    private int[] cenPos;
    private double[] cenVal;

    public int getCurrent_user() {
        return current_user;
    }

    public void setCurrent_user(int current_user) {
        this.current_user = current_user;
    }

    public int[] getCenPos() {
        return cenPos;
    }

    public void setCenPos(int[] cenPos) {
        this.cenPos = cenPos;
    }

    public double[] getCenVal() {
        return cenVal;
    }

    public void setCenVal(double[] cenVal) {
        this.cenVal = cenVal;
    }

    public DistanceC(int current_user, int[] cenPos, double[] cenVal) {

        this.current_user = current_user;
        this.cenPos = cenPos;
        this.cenVal = cenVal;
    }
}
