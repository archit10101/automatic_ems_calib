package com.example.autocalib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WhichActivity extends AppCompatActivity {

    Bluetooth recievedBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_which);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        Singleton mySingleton = Singleton.getInstance(this);
        recievedBluetooth = mySingleton.getMyObject();

        Button t1Button = findViewById(R.id.t1Butt);
        t1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recievedBluetooth.startAdd("t1:good");
                Intent resultIntent = new Intent();
                setResult(1, resultIntent);

                finish();
            }
        });

        Button t2Button = findViewById(R.id.t2Butt);
        t2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recievedBluetooth.startAdd("t2:good");
                Intent resultIntent = new Intent();
                setResult(2, resultIntent);

                finish();
            }
        });

        Button t3Button = findViewById(R.id.t3Butt);
        t3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recievedBluetooth.startAdd("t3:good");
                Intent resultIntent = new Intent();
                setResult(3, resultIntent);

                finish();
            }
        });

    }
}