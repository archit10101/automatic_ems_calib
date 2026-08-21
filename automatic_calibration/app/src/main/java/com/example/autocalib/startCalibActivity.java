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

    int intensity = 0;


    int channel = 0;

    Integer[] fingersChannelVal;
    Integer[] fingersIntensityVal;
    int indX = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_calib);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        String ans = intent.getStringExtra("which");



        if (ans.length()>0){
            fingersChannelVal = new Integer[ans.length()];
            fingersIntensityVal = new Integer[ans.length()];

        }

        for (int i = 0;i<ans.length();i++){
            fingersChannelVal[i] = 0;
            fingersIntensityVal[i] = 0;
        }

        Singleton mySingleton = Singleton.getInstance(this);
        recievedBluetooth = mySingleton.getMyObject();

        fingers = new ArrayList<>();
        if (ans.contains("1")){
            fingers.add("thumbSet");
        }
        if (ans.contains("2")){
            fingers.add("indSet");
        }
        if (ans.contains("3")){
            fingers.add("middleSet");

        }
        if (ans.contains("4")){
            fingers.add("wristSet");
        }

        question = findViewById(R.id.question);

        yesButt = findViewById(R.id.yesButton);

        yesButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(startCalibActivity.this, WhichActivity.class);
                intent.putExtra("ans",ans);
                startActivityForResult(intent, REQUEST_CODE_OTHER);
            }
        });
        moreButt = findViewById(R.id.moreButton);

        moreButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intensity+=1;
                recievedBluetooth.startAdd("int"+intensity);

                new CountDownTimer(500, 500) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        recievedBluetooth.startAdd("stim1000");

                    }
                }.start();

                Log.d("ble","ch: "+channel+" int: "+intensity);
                askQuest();
            }
        });

        nextButt = findViewById(R.id.nextButton);


        nextButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                channel++;
                if (channel==12){
                    boolean done = true;
                    for (int i:fingersChannelVal) {
                        if (i == 0){
                            done = false;
                        }
                    }
                    if (done){
                        for (int i = 0 ;i<fingers.size();i++){

                            Log.d("ble",fingers.get(i)+fingersChannelVal[i]+","+fingersIntensityVal[i]);


                        }
                        indX = 0;
                        new CountDownTimer(500L *fingers.size(), 500) { // 3000 milliseconds, tick every 1000 milliseconds
                            public void onTick(long millisUntilFinished) {
                                recievedBluetooth.startAdd(fingers.get(indX)+fingersChannelVal[indX]+","+fingersIntensityVal[indX]);
                                indX+=1;
                            }
                            public void onFinish() {
                                finish();
                            }
                        }.start();
                        Intent intent = new Intent(startCalibActivity.this,doneActivity.class);
                        startActivityForResult(intent,REQUEST_CODE);
                    }else{
                        Toast.makeText(getApplicationContext(), "You need to adjust and recalibrate.", Toast.LENGTH_SHORT).show();
                        new CountDownTimer(1000, 1000) { // 3000 milliseconds, tick every 1000 milliseconds
                            public void onTick(long millisUntilFinished) {

                            }
                            public void onFinish() {
                                finish();
                            }
                        }.start();
                    }
                }else{
                    recievedBluetooth.startAdd("setch"+(channel+1));
                    intensity = 1;
                    new CountDownTimer(500, 500) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            recievedBluetooth.startAdd("int"+intensity);
                            new CountDownTimer(500, 500) {
                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    recievedBluetooth.startAdd("stim1000");

                                }
                            }.start();
                        }
                    }.start();



                    Log.d("ble","ch: "+channel+" int: "+intensity);
                }
            }
        });



    }

    public void askQuest(){
//        if (!recievedBluetooth.isConnected()){
//            Toast.makeText(getApplicationContext(), "Looks like the EMS is not connected. Try to reconnect.", Toast.LENGTH_SHORT).show();
//            new CountDownTimer(1000, 1000) {
//                public void onTick(long millisUntilFinished) {
//
//                }
//                public void onFinish() {
//                    finish();
//                }
//            }.start();
//
//        }
        yesButt.setEnabled(false);
        moreButt.setEnabled(false);
        nextButt.setEnabled(false);
        new CountDownTimer(500, 500) {
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
                recievedBluetooth.startAdd("Calibration Done");
                finish();
            }
        }else if (requestCode == REQUEST_CODE_OTHER) {
            if (resultCode == 1) {
                fingersChannelVal[0] = channel+1;
                fingersIntensityVal[0] = intensity;
            } else if (resultCode == 2) {
                fingersChannelVal[1] = channel+1;
                fingersIntensityVal[1] = intensity;

            } else if (resultCode == 3) {
                fingersChannelVal[2] = channel+1;
                fingersIntensityVal[2] = intensity;

            } else if (resultCode == 4) {
                fingersChannelVal[3] = channel+1;
                fingersIntensityVal[3] = intensity;

            } else if (resultCode == RESULT_CANCELED) {
                // Handle if the user canceled the operation
            }
            Log.d("channels: ", Arrays.toString(fingersChannelVal));
            Log.d("intensities: ", Arrays.toString(fingersChannelVal));

            channel++;
            if (channel==12){
                boolean done = true;
                for (int i:fingersChannelVal) {
                    if (i == 0){
                        done = false;
                    }
                }
                if (done){
                    for (int i = 0 ;i<fingers.size();i++){
                        recievedBluetooth.startAdd(fingers.get(i)+fingersChannelVal[i]+","+fingersIntensityVal[i]);
                        Log.d("ble",fingers.get(i)+fingersChannelVal[i]+","+fingersIntensityVal[i]);

                    }
                    Intent intent = new Intent(startCalibActivity.this,doneActivity.class);
                    startActivityForResult(intent,REQUEST_CODE);
                }else{
                    Toast.makeText(getApplicationContext(), "You need to adjust and recalibrate.", Toast.LENGTH_SHORT).show();
                    new CountDownTimer(1000, 1000) { // 3000 milliseconds, tick every 1000 milliseconds
                        public void onTick(long millisUntilFinished) {

                        }
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }else{
                recievedBluetooth.startAdd("setch"+(channel+1));
                intensity = 1;
                new CountDownTimer(500, 500) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        recievedBluetooth.startAdd("int"+intensity);
                        new CountDownTimer(500, 500) {
                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                recievedBluetooth.startAdd("stim1000");

                            }
                        }.start();
                    }
                }.start();



                Log.d("ble","ch: "+channel+" int: "+intensity);
            }
        }
    }

}