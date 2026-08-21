package com.example.othermaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LaunchScreenActivity extends AppCompatActivity {
    Bluetooth bluetoothEMS;
    TextView connectedText;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Singleton mySingleton = Singleton.getInstanceBLE(this);
        bluetoothEMS = mySingleton.getMyBLEObject();


        connectedText = findViewById(R.id.connectedText);
        connectedText.setText("Not Connected");


        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothEMS.isConnected()){
                    Intent intent = new Intent(LaunchScreenActivity.this, SearchPlaceScreen.class);
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

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchScreenActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire();


        Button connect = findViewById(R.id.connectButton);
        connect.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onPause() {
        super.onPause();

        // Release the wake lock when activity is paused
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release the wake lock when activity is paused
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Re-acquire the wake lock when activity is resumed
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

}