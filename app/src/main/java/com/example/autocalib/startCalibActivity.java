package com.example.autocalib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class startCalibActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100010;

    private static final int REQUEST_CODE_OTHER = 100111;

    ArrayList<String> fingers;
    Bluetooth recievedBluetooth;

    int fingerNum = 0;

    Button yesButt;

    Button moreButt;

    Button nextButt;


    TextView question;

    int channel = 0;

    Integer[] fingersChannelVal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_calib);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        fingersChannelVal = new Integer[3];
        fingersChannelVal[0] = 0;
        fingersChannelVal[1] = 0;
        fingersChannelVal[2] = 0;

        Singleton mySingleton = Singleton.getInstance(this);
        recievedBluetooth = mySingleton.getMyObject();

        fingers = new ArrayList<>();
        fingers.add("thumb");
        fingers.add("index finger");
        fingers.add("middle finger");
        recievedBluetooth.startAdd("Start auto calibration.");

        question = findViewById(R.id.question);

        yesButt = findViewById(R.id.yesButton);

        yesButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(startCalibActivity.this, WhichActivity.class);
                startActivityForResult(intent, REQUEST_CODE_OTHER);
            }
        });
        moreButt = findViewById(R.id.moreButton);

        moreButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recievedBluetooth.startAdd("more");
                askQuest();
            }
        });

        nextButt = findViewById(R.id.nextButton);

        nextButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recievedBluetooth.startAdd("next");
                channel++;
                Log.d("array",channel+"");
                if (channel==12){
                    boolean done = true;
                    for (int i:fingersChannelVal) {
                        if (i == 0){
                            done = false;
                        }
                    }
                    if (done){
                        Intent intent = new Intent(startCalibActivity.this,doneActivity.class);
                        startActivityForResult(intent,REQUEST_CODE);
                    }else{
                        finish();
                    }
                }
            }
        });



    }

    public void askQuest(){
        if (!recievedBluetooth.isConnected()){
            Toast.makeText(getApplicationContext(), "Looks like the EMS is not connected. Try to reconnect.", Toast.LENGTH_SHORT).show();
            new CountDownTimer(1000, 1000) { // 3000 milliseconds, tick every 1000 milliseconds
                public void onTick(long millisUntilFinished) {

                }
                public void onFinish() {
                    finish();
                }
            }.start();

        }
        yesButt.setEnabled(false);
        moreButt.setEnabled(false);
        nextButt.setEnabled(false);
        new CountDownTimer(500, 1000) { // 3000 milliseconds, tick every 1000 milliseconds
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                yesButt.setEnabled(true);
                moreButt.setEnabled(true);
                nextButt.setEnabled(true);
            }
        }.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle if the user canceled the operation
            }
        }else if (requestCode == REQUEST_CODE_OTHER) {
            if (resultCode == 1) {
                fingersChannelVal[0] = channel+1;
            } else if (resultCode == 2) {
                fingersChannelVal[1] = channel+1;
            } else if (resultCode == 3) {
                fingersChannelVal[2] = channel+1;
            } else if (resultCode == RESULT_CANCELED) {
                // Handle if the user canceled the operation
            }
            Log.d("array", Arrays.toString(fingersChannelVal));
        }
    }

}