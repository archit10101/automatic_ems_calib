package com.example.drummingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class demoActivity extends AppCompatActivity {
    private CircularSeekBar circle;

    TextView bpmDisplay;

    long pastTime = System.currentTimeMillis();
    long currentTime = System.currentTimeMillis();


    int bpm = 60;

    Button playButton;
    Bluetooth recievedBluetooth;
    ImageView exit;

    Timer timer;
    boolean playing = true;

    public static final String ACTION_UPDATE_TIMER = "com.example.action.UPDATE_TIMER";
    public static final String EXTRA_TIMER_VALUE = "extra_timer_value";

    private int timerValue = 0;

    int amt = 1;

    int total = 1;

    ImageView fullScreenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        Singleton mySingleton = Singleton.getInstanceBLE(this);
        recievedBluetooth = mySingleton.getMyBLEObject();
        fullScreenImageView = findViewById(R.id.fullScreenImageView);
        fullScreenImageView.setVisibility(View.INVISIBLE);


        SingletonBoolArray patternSingleton = SingletonBoolArray.getInstanceBLE(this);

        bpmDisplay = findViewById(R.id.bpmDisp);

        circle = findViewById(R.id.circularSeekBar);
        bpm=(int)(circle.getProgress()*1.50);
        bpmDisplay.setText(bpm+" bpm");

        playButton = findViewById(R.id.play);

        NumberPicker picker = findViewById(R.id.number_picker);
        String[] data = new String[]{"1","2","4","∞"};
        picker.setMinValue(0);
        picker.setMaxValue(data.length-1);
        picker.setDisplayedValues(data);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int a = picker.getValue();
                if (a == 0){
                    total = 100000;
                }else if (a == 1){
                    total = 1;
                }else if (a == 2){
                    total = 2;
                }else if (a == 3){
                    total = 4;
                }
            }
        });

        TimerTask task = new TimerTask() {
            public void run() {
                if (amt>0){
                    currentTime = System.currentTimeMillis();
                    if ((currentTime - pastTime )>= ((60.0/bpm)*1000)/2){
                        amt--;
                        if (!playing){//playing
                            int ind= patternSingleton.getInd();
                            timerValue++;
                            Log.d("happened","in demo");
                            broadcastTimerUpdate(ind);

                            if (patternSingleton.getMyBool()[ind]){
                                recievedBluetooth.startAdd("drum");
                            }
                        }
                        pastTime = System.currentTimeMillis();
                        if (amt == 0 && !playing){
//                            fullScreenImageView.setVisibility(View.VISIBLE);
                            playButton.setText("Play");
                            playing = true;
                            recievedBluetooth.startAdd("thumbsUp");

                        }
                    }
                }
            }
        };

        fullScreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreenImageView.setVisibility(View.INVISIBLE);
                playButton.setText("Play");
                playing = true;

            }
        });

        Button createButt = findViewById(R.id.createButton);

        createButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(demoActivity.this, CreateActivity.class);
                startActivityForResult(intent, 123); // Request code can be any integer
            }
        });
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 90);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing){
                    playing = false;
                    playButton.setText("Stop");
                    patternSingleton.resetInd();
                    amt = total*8;

                }else{
                    playing = true;
                    playButton.setText("Play");
                }
            }
        });


        circle.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                // Log the progress value whenever it changes
                Log.d("CircularSeekBar", "Progress: " + progress);
                bpm=(int)(circle.getProgress()*1.50);
                bpmDisplay.setText(bpm+" bpm");

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                // Handle when the user starts dragging the thumb (optional)
            }
        });


    }

    private void broadcastTimerUpdate(int value) {
        Intent intent = new Intent(ACTION_UPDATE_TIMER);
        intent.putExtra(EXTRA_TIMER_VALUE, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop all timers when activity is destroyed
        timer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getBooleanExtra("showImage", false)) {
                fullScreenImageView.setVisibility(View.INVISIBLE);
            }
        }
    }

}
