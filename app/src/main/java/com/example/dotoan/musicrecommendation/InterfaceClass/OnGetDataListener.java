package com.example.dotoan.musicrecommendation.InterfaceClass;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by DOTOAN on 11/20/2017.
 */

public interface OnGetDataListener {
    void onSuccess(DataSnapshot dataSnapshot);
    void onStart();
    void onFailure();
}
