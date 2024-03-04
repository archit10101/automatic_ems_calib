package com.example.autocalib;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class startCalibActivity extends AppCompatActivity {

    ArrayList<String> fingers;
    Bluetooth recievedBluetooth;

    int fingerNum = 0;

    Button yesButt;

    Button noButt;

    TextView question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_calib);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Singleton mySingleton = Singleton.getInstance(this);
        recievedBluetooth = mySingleton.getMyObject();

        fingers = new ArrayList<>();
        fingers.add("thumb");
        fingers.add("index finger");
        fingers.add("middle finger");

        question = findViewById(R.id.question);

        yesButt = findViewById(R.id.yesButton);

        yesButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recievedBluetooth.startAdd("t"+(fingerNum+1)+": good");
                Toast.makeText(getApplicationContext(), "Muscle "+(fingerNum+1)+" is calibrated.", Toast.LENGTH_SHORT).show();

                fingerNum++;
                askQuest();
            }
        });
        noButt = findViewById(R.id.noButton);

        noButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recievedBluetooth.startAdd("t"+(fingerNum+1)+": more");
                askQuest();
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
        noButt.setEnabled(false);
        new CountDownTimer(500, 1000) { // 3000 milliseconds, tick every 1000 milliseconds
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (fingerNum>fingers.size()){
                    Toast.makeText(getApplicationContext(), "Fully Calibrated!", Toast.LENGTH_SHORT).show();
                    new CountDownTimer(1000, 1000) { // 3000 milliseconds, tick every 1000 milliseconds
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
                question.setText("Did your "+fingers.get(fingerNum)+" move?");
                yesButt.setEnabled(true);
                noButt.setEnabled(true);
            }
        }.start();
    }
}