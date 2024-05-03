package com.example.weatherapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class Settings extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;
    ImageButton refreshButton = findViewById(R.id.refresh);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        ListView pairedDevicesListView = findViewById(R.id.devices_list_view);
        pairedDevicesListView.setAdapter(pairedDevicesArrayAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

        findViewById(R.id.switchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((SwitchCompat) view).isChecked()) {
                    if (ActivityCompat.checkSelfPermission(Settings.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    if (bluetoothAdapter.enable()) {
                        Toast.makeText(Settings.this, "Bluetooth включен", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Settings.this, "Не удалось включить Bluetooth", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (bluetoothAdapter.disable()) {
                        Toast.makeText(Settings.this, "Bluetooth выключен", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Settings.this, "Не удалось выключить Bluetooth", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        findViewById(R.id.disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(Settings.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                                Toast.makeText(Settings.this, "Устройство отключено", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(Settings.this, "Bluetooth не включен", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                pairedDevicesArrayAdapter.notifyDataSetChanged();
                devices.add(device);
                updateDeviceListView();
            }
        }
    };
    refreshButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Здесь вызываем метод для обновления списка устройств
            updateDeviceList();
        }
    });
    private void updateDeviceList() {
        // Проверяем, включен ли Bluetooth
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            return;
        }

        // Отключаем предыдущий BroadcastReceiver, если он был
        unregisterReceiver(bluetoothReceiver);

        // Создаем новый IntentFilter и регистрируем BroadcastReceiver для получения уведомлений о новых устройствах
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

        // Запускаем поиск новых устройств
        bluetoothAdapter.startDiscovery();
    }
    private void updateDeviceListView() {
        // Получаем ссылку на ListView
        ListView deviceListView = findViewById(R.id.devices_list_view);

        // Создаем адаптер для списка устройств
        ArrayAdapter<BluetoothDevice> adapter = new ArrayAdapter<>(this, android.R.layout.devices_list_view, devices);

        // Устанавливаем адаптер для ListView
        deviceListView.setAdapter(adapter);
    }
}