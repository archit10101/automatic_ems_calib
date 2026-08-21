package com.example.autocalib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class WhichActivity extends AppCompatActivity {

    Bluetooth recievedBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_which);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        String ans = getIntent().getStringExtra("ans");

        ArrayList<String> fingers = new ArrayList<>();
        if (ans.contains("1")){
            fingers.add("Thumb");
        }if (ans.contains("2")){
            fingers.add("Index");
        }if (ans.contains("3")){
            fingers.add("Middle");
        }if (ans.contains("4")){
            fingers.add("Wrist");
        }


        Singleton mySingleton = Singleton.getInstance(this);
        recievedBluetooth = mySingleton.getMyObject();
        Button t1Button = findViewById(R.id.t1Butt);

        if (fingers.size()>0){
            t1Button.setText(fingers.get(0));
            t1Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent();
                    setResult(1, resultIntent);
                    finish();
                }
            });

        }else{
            t1Button.setVisibility(View.GONE);
        }

        Button t2Button = findViewById(R.id.t2Butt);

        if (fingers.size()>1){
            t2Button.setText(fingers.get(1));
            t2Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent();
                    setResult(2, resultIntent);

                    finish();
                }
            });

        }else{
            t2Button.setVisibility(View.GONE);
        }

        Button t3Button = findViewById(R.id.t3Butt);

        if (fingers.size()>2){
            t3Button.setText(fingers.get(2));

            t3Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent();
                    setResult(3, resultIntent);

                    finish();
                }
            });

        }else{
            t3Button.setVisibility(View.GONE);
        }


        Button t4Button = findViewById(R.id.t4Butt);

        if (fingers.size()>3){
            t4Button.setText(fingers.get(3));

            t4Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resultIntent = new Intent();
                    setResult(4, resultIntent);

                    finish();
                }
            });

        }else{
            t4Button.setVisibility(View.GONE);
        }

    }
}