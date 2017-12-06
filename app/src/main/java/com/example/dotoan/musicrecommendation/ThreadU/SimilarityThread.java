package com.example.dotoan.musicrecommendation.ThreadU;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.dotoan.musicrecommendation.Contruct.Item;
import com.example.dotoan.musicrecommendation.SQLite.DBThreadU;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.dotoan.musicrecommendation.IntentService.IntentServiceDistance.context;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.cenTB;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.objTB;
import static com.example.dotoan.musicrecommendation.SQLite.DBThreadU.simTB;

/**
 * Created by DOTOAN on 11/29/2017.
 */

public class SimilarityThread extends Thread{

    static String threadname = "ThreadU.Similarity.";
    int uObj;

    public SimilarityThread(int uObj) {
        this.uObj = uObj;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        super.run();
        DBThreadU dbThreadU = new DBThreadU(context);

        List<Item> cen = dbThreadU.getAllNode(cenTB);
        List<Item> obj = dbThreadU.getAllNode(objTB);

        List<Double> cenVal = new ArrayList<Double>();
        List<Double> objVal = new ArrayList<Double>();
        List<Integer> cenPos = new ArrayList<Integer>();
        List<Integer> objPos = new ArrayList<Integer>();

        for (Item i: cen){
            cenPos.add(Integer.valueOf(i.getMid()));
            cenVal.add(Double.valueOf(i.getOrder()));
        }

        for (Item i: obj){
            objPos.add(Integer.valueOf(i.getMid()));
            objVal.add(Double.valueOf(i.getOrder()));
        }

        int [] cP = toIntArray(cenPos);
        int [] oP = toIntArray(objPos);
        double[] cV =  toDoubleArray(cenVal);
        double[] oV =  toDoubleArray(objVal);
        double c = similarityDistance(cV, cP, oV, oP);
        Log.e(threadname+"Similarity",c+" -- "+uObj);
        dbThreadU.addNode_forSim(simTB,uObj,c);
    }

    public int[] toIntArray(List<Integer> list) {
        int[] ret = new int[ list.size() ];
        int i = 0;
        for(Iterator<Integer> it = list.iterator();
            it.hasNext();
            ret[i++] = it.next() );
        return ret;
    }

    public double[] toDoubleArray(List<Double> list) {
        double[] ret = new double[ list.size() ];
        int i = 0;
        for(Iterator<Double> it = list.iterator();
            it.hasNext();
            ret[i++] = it.next() );
        return ret;
    }

    private double similarityDistance(double centVal[], int centPos[], double objVal[], int objPos[]) {
        double tag = 0.0, cenX = 0.0, cenY = 0.0, sol = 0.0;

        if (centPos.length >= objPos.length) {
            for (int i = 0; i < centPos.length; i++) {
                if (i < objPos.length) {
                    for (int j = 0; j < objPos.length; j++) {
                        if (centPos[i] == objPos[j]) {
                            tag = tag + (centVal[i] - objVal[j]) * (centVal[i] - objVal[j]);
                            break;
                        }

                        if (j == objPos.length - 1) {
                            cenX = cenX + centVal[i] * centVal[i];
                            cenY = cenY + objVal[i] * objVal[i];
                        }
                    }
                } else {
                    cenX = cenX + centVal[i] * centVal[i];
                }
                sol = tag + cenX + cenY;
            }
        } else {
            for (int i = 0; i < objPos.length; i++) {
                if (i < centPos.length) {
                    for (int j = 0; j < centPos.length; j++) {
                        if (centPos[j] == objPos[i]) {
                            tag = tag + (centVal[j] - objVal[i]) * (centVal[j] - objVal[i]);

                            break;
                        }

                        if (j == centPos.length - 1) {
                            cenX = cenX + centVal[i] * centVal[i];
                            cenY = cenY + objVal[i] * objVal[i];
                        }
                    }
                } else {
                    cenY = cenY + objVal[i] * objVal[i];
                }
                sol = tag + cenX + cenY;
            }
        }

        double s = Math.sqrt(sol);
        return (double) Math.round(s * 10000) / 10000;
    }
}
