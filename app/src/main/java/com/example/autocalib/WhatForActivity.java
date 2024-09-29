package com.example.autocalib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WhatForActivity extends AppCompatActivity {

    Bluetooth recievedBluetooth;

    String send = "";

    boolean[] options = new boolean[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what_for);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        options[0] = false;
        options[1] = false;
        options[2] = false;
        options[3] = false;

        Singleton mySingleton = Singleton.getInstance(this);
        recievedBluetooth = mySingleton.getMyObject();

        Button t1Button = findViewById(R.id.t1Butt);
        t1Button.setBackgroundColor(Color.parseColor("#000000"));

        t1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!options[0]){
                    options[0] = true;
                    t1Button.setBackgroundColor(Color.parseColor("#03c2fc"));

                }else{
                    options[0] = false;
                    t1Button.setBackgroundColor(Color.parseColor("#000000"));

                }
            }
        });

        Button t2Button = findViewById(R.id.t2Butt);
        t2Button.setBackgroundColor(Color.parseColor("#000000"));

        t2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!options[1]){
                    options[1] = true;
                    t2Button.setBackgroundColor(Color.parseColor("#03c2fc"));

                }else{
                    options[1] = false;
                    t2Button.setBackgroundColor(Color.parseColor("#000000"));

                }
            }
        });

        Button t3Button = findViewById(R.id.t3Butt);
        t3Button.setBackgroundColor(Color.parseColor("#000000"));

        t3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!options[2]){
                    options[2] = true;
                    t3Button.setBackgroundColor(Color.parseColor("#03c2fc"));

                }else{
                    options[2] = false;
                    t3Button.setBackgroundColor(Color.parseColor("#000000"));

                }
            }
        });


        Button t4Button = findViewById(R.id.t4Butt);
        t4Button.setBackgroundColor(Color.parseColor("#000000"));

        t4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!options[3]){
                    options[3] = true;
                    t4Button.setBackgroundColor(Color.parseColor("#03c2fc"));

                }else{
                    options[3] = false;
                    t4Button.setBackgroundColor(Color.parseColor("#000000"));

                }
            }
        });

        Button doneButton = findViewById(R.id.doneButt);
        doneButton.setOnClickListener(v -> {
            Intent intent= new Intent(WhatForActivity.this,startCalibActivity.class);
            String ans = "";
            for (int i = 0;i<4;i++){
                if (options[i]){
                    ans+=(i+1);
                }
            }
            if (ans.equals("")){
                Toast.makeText(WhatForActivity.this,"Select at least one.",Toast.LENGTH_SHORT).show();

            }else{
                intent.putExtra("which",ans);
                startActivity(intent);
                finish();
            }

        });

    }
}