package com.example.weatherapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.weatherapp.ui.Monitor.MonitorFragment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class Settings extends AppCompatActivity {
    // Global variables we will use in the
    private static final String TAG = "linniye";
    private static final int REQUEST_ENABLE_BT = 1;
    //We will use a Handler to get the BT Connection statys
    public static Handler handler;
    private final static int ERROR_READ = 0; // used in bluetooth handler to identify message update
    BluetoothDevice arduinoBTModule = null;
    UUID arduinoUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //We declare a default UUID to create the global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //Intances of BT Manager and BT Adapter needed to work with BT in Android.
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        //Intances of the Android UI elements that will will use during the execution of the APP
        TextView btDevices = findViewById(R.id.btDevices);
        btDevices.setTextColor(getResources().getColor(R.color.black));
        Button connectToDevice = findViewById(R.id.connectToDevice);
        Button seachDevices = findViewById(R.id.seachDevices);
        ImageButton clearValues = findViewById(R.id.refresh);
        Log.d(MainActivity.TAG, "Begin Execution");

        // Set a listener event on a button to clear the texts
        clearValues.setOnClickListener(view -> btDevices.setText(""));

        connectToDevice.setOnClickListener(view -> {
            if (arduinoBTModule != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    MainActivity.READER = new MessageReader(MainActivity.UPDATER, arduinoBTModule.createRfcommSocketToServiceRecord(arduinoUUID));
                    MainActivity.READER.startReader();
                } catch (IOException ignored) {
                }
            }
        });

        //Display all the linked BT Devices
        seachDevices.setOnClickListener(view -> {
            try {
                //Check if the phone supports BT
                if (bluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    Log.d(MainActivity.TAG, "Device doesn't support Bluetooth");
                } else {
                    Log.d(MainActivity.TAG, "Device support Bluetooth");
                    //Check BT enabled. If disabled, we ask the user to enable BT
                    if (!bluetoothAdapter.isEnabled()) {
                        Log.d(MainActivity.TAG, "Bluetooth is disabled");
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(MainActivity.TAG, "We don't BT Permissions");
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            Log.d(MainActivity.TAG, "Bluetooth is enabled now");
                        } else {
                            Log.d(MainActivity.TAG, "We have BT Permissions");
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            Log.d(MainActivity.TAG, "Bluetooth is enabled now");
                        }
                    } else {
                        if (ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                            return;
                        }
                        Log.d(MainActivity.TAG, "Bluetooth is enabled");
                    }
                    String btDevicesString = "";

                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                    if (!pairedDevices.isEmpty()) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            Log.d(MainActivity.TAG, "deviceName:" + deviceName);
                            Log.d(MainActivity.TAG, "deviceHardwareAddress:" + deviceHardwareAddress);
                            //We append all devices to a String that we will display in the UI
                            btDevicesString = btDevicesString + deviceName + " || " + deviceHardwareAddress + "\n";
                            //If we find the HC 05 device (the Arduino BT module)
                            //We assign the device value to the Global variable BluetoothDevice
                            //We enable the button "Connect to HC 05 device"
                            if (deviceName.equals("HC-05")) {
                                Log.d(MainActivity.TAG, "HC-05 found");
                                arduinoUUID = device.getUuids()[0].getUuid();
                                arduinoBTModule = device;

                                connectToDevice.setEnabled(true);
                                MonitorFragment monitorFragment = new MonitorFragment();

                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_monitor, monitorFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                            btDevices.setText(btDevicesString);
                        }
                    }
                }
                Log.d(MainActivity.TAG, "Button Pressed");
            } catch (Exception exception) {
                btDevices.setText(Arrays.toString(exception.getStackTrace()));
            }
        });
    }

}