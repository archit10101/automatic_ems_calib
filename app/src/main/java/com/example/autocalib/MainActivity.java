package com.example.autocalib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button startButton;

    Bluetooth bluetoothEMS;

    TextView connectedText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Singleton mySingleton = Singleton.getInstance(this);
        bluetoothEMS = mySingleton.getMyObject();

        connectedText = findViewById(R.id.connectText);
        connectedText.setText("Not Connected");
        Button rescanEMS = findViewById(R.id.connect);
        rescanEMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothEMS.isConnected()){
                    bluetoothEMS.setmBluetoothAdapter();
                    connectedText.setText("Not Connected");

                }else{
                    bluetoothEMS.startAdd("connected with beacon");
                    connectedText.setText("Connected");
                }
            }
        });

        bluetoothEMS.setBluetoothGattCallback(
                "19b10000-e9f3-537e-4f6c-d104768a1214",
                "19b10004-e9f3-537e-4f6c-d104768a1214"
        );

        IntentFilter filterEMS = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothEMS.createReciever("E7:96:1E:84:FD:75"), filterEMS);
        bluetoothEMS.runnableFunc();

        startButton = findViewById(R.id.start);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bluetoothEMS.isConnected()){

                    Intent intent = new Intent(MainActivity.this, startCalibActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(), "Not connected. Try reconnecting.", Toast.LENGTH_SHORT).show();
                    if (!bluetoothEMS.isConnected()){
                        bluetoothEMS.setmBluetoothAdapter();
                        connectedText.setText("Not Connected");

                    }else{
                        bluetoothEMS.startAdd("connected with beacon");
                        connectedText.setText("Connected");
                    }
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_SCAN
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_ADMIN
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                            android.Manifest.permission.BLUETOOTH_SCAN,
                            android.Manifest.permission.BLUETOOTH_ADMIN,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1
            );
        }

        if (!bluetoothEMS.isConnected()){
            bluetoothEMS.setmBluetoothAdapter();
            connectedText.setText("Not Connected");

        }else{
            bluetoothEMS.startAdd("connected with beacon");
            connectedText.setText("Connected");
        }



    }



}