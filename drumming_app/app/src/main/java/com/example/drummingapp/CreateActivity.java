package com.example.drummingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class CreateActivity extends AppCompatActivity {

    private TextView[] boxes = new TextView[8]; // Array to hold TextViews for boxes
    SingletonBoolArray patternSingleton;

    private ImageView[] checks = new ImageView[8];

    private int previous;

    private BroadcastReceiver timerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(demoActivity.ACTION_UPDATE_TIMER)) {
                int timerValue = intent.getIntExtra(demoActivity.EXTRA_TIMER_VALUE, 0);
                Log.d(timerValue+"","in create class");
                updateUI(timerValue);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        patternSingleton = SingletonBoolArray.getInstanceBLE(this);

        IntentFilter filter = new IntentFilter(demoActivity.ACTION_UPDATE_TIMER);
        LocalBroadcastManager.getInstance(this).registerReceiver(timerReceiver, filter);

        // Initialize TextViews for boxes
        boxes[0] = findViewById(R.id.box1);
        boxes[1] = findViewById(R.id.box2);
        boxes[2] = findViewById(R.id.box3);
        boxes[3] = findViewById(R.id.box4);
        boxes[4] = findViewById(R.id.box5);
        boxes[5] = findViewById(R.id.box6);
        boxes[6] = findViewById(R.id.box7);
        boxes[7] = findViewById(R.id.box8);


        checks[0] = findViewById(R.id.on1);
        checks[1] = findViewById(R.id.on2);
        checks[2] = findViewById(R.id.on3);
        checks[3] = findViewById(R.id.on4);
        checks[4] = findViewById(R.id.on5);
        checks[5] = findViewById(R.id.on6);
        checks[6] = findViewById(R.id.on7);
        checks[7] = findViewById(R.id.on8);
        // Initialize box states
        for (int i = 0; i < 8; i++) {
            TextView textView = boxes[i]; // Set initial state
            int index = i;
            if (patternSingleton.getMyBool()[index]) {
                textView.setBackgroundColor(getResources().getColor(R.color.purple));
                patternSingleton.getMyBool()[index] = true;
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.grey));
                patternSingleton.getMyBool()[index] = false;
            }
        }

        // Set click listeners for each box
        for (int i = 0; i < 8; i++) {
            final int index = i;
            boxes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleBoxState(index, boxes[index]);
                }
            });
        }

        // Button to send back the boolean array
        findViewById(R.id.buttonSendback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("showImage", true);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
    private void updateUI(int timer) {
        checks[timer].setImageResource(R.drawable.onpurple);
        Log.d("d",timer+"");
        checks[previous].setImageResource(R.drawable.offpurple);
        previous = timer;

    }
    private void toggleBoxState(int index, TextView textView) {
        if (patternSingleton.getMyBool()[index]) {
            textView.setBackgroundColor(getResources().getColor(R.color.grey));
            patternSingleton.getMyBool()[index] = false;
        } else {
            textView.setBackgroundColor(getResources().getColor(R.color.purple));
            patternSingleton.getMyBool()[index] = true;
        }
    }
}
