package com.example.phonesocketconnection;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ConnectActivity extends AppCompatActivity {
    Bluetooth bluetoothEMS;

    TextView connectedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }




        setContentView(R.layout.activity_connect);

        Singleton mySingleton = Singleton.getInstance(this);
        bluetoothEMS = mySingleton.getMyObject();

        LinearLayout buttonContainer = findViewById(R.id.buttonContainer);



        connectedText = findViewById(R.id.connectedTo);
        connectedText.setText("Not Connected");
        Button disconnectEMS = findViewById(R.id.disconnectButton);
        disconnectEMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothEMS.disconnect();
                disconnectEMS.setVisibility(View.GONE);
                connectedText.setVisibility(View.GONE);
                buttonContainer.setVisibility(View.VISIBLE);
                buttonContainer.removeAllViews();

                IntentFilter filterEMS = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(bluetoothEMS.createReciever(ConnectActivity.this, buttonContainer, disconnectEMS, connectedText), filterEMS);
                bluetoothEMS.runnableFunc();

            }
        });


        bluetoothEMS.setBluetoothGattCallback(
                "19b10000-e9f3-537e-4f6c-d104768a1214",
                "19b10004-e9f3-537e-4f6c-d104768a1214"
        );

        buttonContainer.removeAllViews();
        IntentFilter filterEMS = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothEMS.createReciever(this, buttonContainer, disconnectEMS, connectedText), filterEMS);
        bluetoothEMS.runnableFunc();


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1
            );
        }

        if (!bluetoothEMS.isConnected()){
            bluetoothEMS.setmBluetoothAdapter();
            connectedText.setText("Not Connected");

        }else{
            bluetoothEMS.startAdd("stim1000");
            connectedText.setText("Connected");
        }

        Button refresh = findViewById(R.id.refreshButton);

        Log.d("here", Boolean.toString(refresh == null));
        refresh.setOnClickListener(view -> {
            bluetoothEMS.setBluetoothGattCallback(
                    "19b10000-e9f3-537e-4f6c-d104768a1214",
                    "19b10004-e9f3-537e-4f6c-d104768a1214"
            );

            buttonContainer.removeAllViews();
            IntentFilter filtersEMS = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(bluetoothEMS.createReciever(this, buttonContainer, disconnectEMS, connectedText), filtersEMS);
            bluetoothEMS.runnableFunc();
            if (!bluetoothEMS.isConnected()){
                bluetoothEMS.setmBluetoothAdapter();
                connectedText.setText("Not Connected");

            }else{
                bluetoothEMS.startAdd("stim1000");
                connectedText.setText("Connected");
            }


        });
    }
}
