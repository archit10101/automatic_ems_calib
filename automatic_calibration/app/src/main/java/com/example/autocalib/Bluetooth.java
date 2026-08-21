package com.example.autocalib;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Bluetooth implements Serializable {
    BluetoothAdapter mBluetoothAdapter;

    BluetoothGatt mGatt;
    BluetoothGattService service;

    boolean connected = false;

    double RSSI = 0;

    Activity myContext;
    BluetoothGattCharacteristic intensityCharacteristic;
    BluetoothGattCharacteristic pulseWidthCharacteristic;
    BluetoothGattCharacteristic frequencyCharacteristic;
    BluetoothGattCharacteristic startCharacteristic;
    BluetoothDevice device;

    LinkedList<String> startQ = new LinkedList<>();
    BluetoothGattCallback bluetoothGattCallback;

    BroadcastReceiver mReceiver;
    Handler queueHandler;
    Runnable queueRunnable;

    @SuppressLint("MissingPermission")
    public Bluetooth(Activity context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        myContext = context;


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mBluetoothAdapter.startDiscovery();
        Log.d("Discovering devices ...", "Discovering devices ...");


    }


    public BluetoothDevice getDevice() {
        return device;
    }


    public void startAdd(String data) {
        myContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startQ.add(data);
                if (isConnected()){
                    Log.d("sending...",startCharacteristic.getUuid().toString());

                }else{
                    Toast.makeText(myContext.getApplication(), "You not connected!", Toast.LENGTH_SHORT).show();
                    Log.d("sending...",startCharacteristic.getUuid().toString());

                }
                runnableFunc();
            }
        });
    }
    ArrayList<Button> buttonName  = new ArrayList<>();;
    ArrayList<String> buttonAddress  = new ArrayList<>();;

    //Put your bluetooth device address here.
    public BroadcastReceiver createReciever(Context actCont, LinearLayout buttonCont, Button disconnect, Button refresh, TextView tv) {
        buttonCont.removeAllViews();
        buttonCont.setVisibility(View.VISIBLE);
        for (Button b:
             buttonName) {
            ViewGroup parent = (ViewGroup) b.getParent();
            if (parent != null) {
                parent.removeView(b);
            }
            buttonCont.addView(b);
        }

        if (mReceiver == null){
            mReceiver = new BroadcastReceiver() {
                @SuppressLint("MissingPermission")
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Log.d("address", foundDevice.getAddress() + ":" + foundDevice.getName());
                        Button button = new Button(actCont);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );
                        button.setLayoutParams(params);
                        if (foundDevice.getName() != null && !buttonAddress.contains(foundDevice.getAddress())){
                            buttonAddress.add(foundDevice.getAddress());
                            Log.d("names",foundDevice.getName()+" --1");
                            button.setText(foundDevice.getName());
                            Log.d("names",foundDevice.getName()+" --2");
                            Log.d("names",foundDevice.getName()+" --3");
                            button.setOnClickListener(v -> {
                                Log.d("here", "here");
                                mBluetoothAdapter.cancelDiscovery();
                                Log.d("Connecting...", "Connecting...");
                                device = foundDevice;
                                Log.d("test", "FOUND:" + device.getAddress());
                                mGatt = device.connectGatt(context.getApplicationContext(), false, bluetoothGattCallback, TRANSPORT_LE);
                                buttonCont.setVisibility(View.GONE);
                                disconnect.setVisibility(View.VISIBLE);
                                refresh.setVisibility(View.GONE);
                                tv.setVisibility(View.VISIBLE);
                                if (device.getName()==null){
                                    tv.setText("Connected to UNKNOWN");
                                }else{
                                    tv.setText("Connected to "+device.getName());
                                }
                            });
                            Log.d("names",foundDevice.getName()+" --4");
                            buttonName.add(button);
                            myContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    buttonCont.addView(button); // Add button to layout
                                    buttonCont.setVisibility(View.VISIBLE);
                                    buttonCont.invalidate();
                                    Log.d("names", foundDevice.getName() + " --6");
                                    Log.d("names", ""+button.getVisibility());

                                    Log.d("names", "numChild: "+buttonCont.getChildCount());

                                }
                            });
                        }
                    }
                }
            };

        }
        return mReceiver;
    }

    public double getDistanceFromRSSI() {
        return RSSI;
    }

    @SuppressLint("MissingPermission")
    public void startRssiRead() {
        if (mGatt != null) {
            mGatt.readRemoteRssi();
        }
    }

    public void runnableFunc() {
        queueHandler = new Handler();
        queueRunnable = new Runnable() {
            @SuppressLint("MissingPermission")
            public void run() {
                if (mGatt == null || startCharacteristic == null) {
                    queueHandler.postDelayed(this, 10);
                } else if (!startQ.isEmpty()) {
                    String start = startQ.pop();
                    startQ.clear();
                    Log.d("test", "popped: " + start + " at " + System.currentTimeMillis());
                    startCharacteristic.setValue(start);
                    mGatt.writeCharacteristic(startCharacteristic);
                    queueHandler.postDelayed(this, 10);
                } else {
                    queueHandler.postDelayed(this, 10);
                }
            }
        };
        queueRunnable.run();
    }

    @SuppressLint("MissingPermission")
    public void manualWrite(String start) {
        myContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startCharacteristic.setValue(start);
                mGatt.writeCharacteristic(startCharacteristic);
            }
        });

    }
    public boolean isConnected() {
        return connected;
    }

    @SuppressLint("MissingPermission")
    public void setmBluetoothAdapter(){
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mBluetoothAdapter.startDiscovery();
        Log.d("Discovering devices ...", "Discovering devices ...");
        ;

    }
    @SuppressLint("MissingPermission")
    public void disconnect() {
        if (mGatt != null) {
            manualWrite("disconnect");
            mGatt.disconnect();  // Disconnects the device.
            mGatt.close();       // Closes the GATT connection to clean up resources.
            mGatt = null;        // Sets the GATT object to null after closing.
            connected = false;   // Updates the connection status.
            Log.d("Bluetooth", "Device disconnected and resources released.");
        }
    }

    public void removeViewsbuttons(){
        buttonName.clear();
        buttonAddress.clear();
    }
    public void setBluetoothGattCallback(String devUuid, String uuidChar1) {
        bluetoothGattCallback = new BluetoothGattCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                    mGatt.readRemoteRssi();
                    connected = true;
                    startQ.clear();
                    myContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Connected", "connected");
                        }
                    });
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    connected = false;
                    myContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(" not Connected", "not connected");
                        }
                    });
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                String value = characteristic.getStringValue(0);
                Log.d("test", "Characteristic Written: " + value + " at " + System.currentTimeMillis());
            }
//            @Override
//            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
//                super.onReadRemoteRssi(gatt, rssi, status);
//                if (status == BluetoothGatt.GATT_SUCCESS) {
////                    Log.d("RSSI", "RSSI Value: " + rssi);
//                    // Handle the RSSI value as needed
//                } else {
//                    Log.e("RSSI", "Error reading RSSI: " + status);
//                }
//
//                // Schedule the next RSSI read after a delay (adjust as needed)
//                queueHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startRssiRead();
//                        RSSI = rssi;
//                    }
//                }, 100); // 1000 milliseconds delay for the next RSSI read
//            }
            @SuppressLint("MissingPermission")
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                final List<BluetoothGattService> services = gatt.getServices();
                Log.d("debugginging", "1");

                for (BluetoothGattService s : services) {
                    String uuid = s.getUuid().toString();
                    Log.d("debugginging", "2");
                    if (uuid.equals(devUuid)) {
                        Log.d("debugginging", "3");
                        service = s;
                        for (BluetoothGattCharacteristic mCharacteristic : s.getCharacteristics()) {
                            Log.d("debugginging", "4");

                            if (mCharacteristic.getUuid().toString().equals(uuidChar1)) {
                                startCharacteristic = mCharacteristic;
                                Log.d("debugginging", "5");

                                Log.d("Start ", "Start characteristic established");
                            }else{
                                Log.d("Start ", mCharacteristic.getUuid().toString());

                            }
                        }
                    }
                }
            }
        };

    }
}
