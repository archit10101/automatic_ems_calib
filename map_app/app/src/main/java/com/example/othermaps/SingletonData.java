package com.example.othermaps;

import android.util.Log;

public class SingletonData {

    private static SingletonData instance;

    int left;

    int right;

    int done;

    public SingletonData() {
        left = 5;
        right = 5;
        done = 3;
    }

    public static SingletonData getInstance() {
        if (instance == null) {
            instance = new SingletonData();
        }
        return instance;
    }


    public int getright() {
        return right;
    }

    public void setright(int right) {
        Log.d("right",right+"");

        this.right = right;
    }

    public void setdone(int done) {
        Log.d("done",done+"");

        this.done = done;
    }

    public void setleft(int left) {
        Log.d("left",left+"");

        this.left = left;
    }

    public int getleft() {

        return left;
    }

    public int getdone() {
        return done;
    }
}
