package com.example.drummingapp;

import android.app.Activity;
import android.util.Log;

public class SingletonBoolArray {

    private static SingletonBoolArray instance;

    private Boolean[] boolArr;

    int ind;


    private SingletonBoolArray(Activity act) {
        boolArr = new Boolean[8]; // Initialize your object here
        for (int i = 0;i<8;i++){
            boolArr[i] = false;
        }
        boolArr[0] = true;
        ind = 7;
    }

    public static SingletonBoolArray getInstanceBLE(Activity act) {
        if (instance == null) {
            instance = new SingletonBoolArray(act);
        }
        return instance;
    }

    public int getInd() {
        ind+=1;
        ind = ind%boolArr.length;
        Log.d("j", ind+" index");
        return ind;
    }

    public void resetInd() {
        ind = 7;
    }

    public Boolean[] getMyBool() {
        return boolArr;
    }



}
