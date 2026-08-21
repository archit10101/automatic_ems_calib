package com.example.drummingapp;

import android.app.Activity;
import android.content.Context;

public class Singleton {

    private static Singleton instance;
    private Bluetooth ble;

    private int num;

    private String what = "ble";

    private Singleton(Activity act, String what) {
        if (what.equals("ble")){
            ble = new Bluetooth(act); // Initialize your non-serializable object here
        }
        this.what  = what;
    }

    public static Singleton getInstanceBLE(Activity act) {
        if (instance == null) {
            instance = new Singleton(act, "ble");
        }
        return instance;
    }



    public Bluetooth getMyBLEObject() {
        return ble;
    }



}
