package com.example.weatherapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class MessageReader {

    private final BluetoothSocket socket;

    private final Handler messageHandler;
    private final MessageUpdater updater;

    public MessageReader(MessageUpdater updater, BluetoothSocket socket) {
        this.updater = updater;
        this.socket = socket;

        this.messageHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Map<UpdaterType, MessageRepository<? extends Number>> vaults = updater.getVaults();
                updater.processMessage(((String) msg.obj));
                updater.getCallbacks().forEach((type, callbacks) -> {
                    MessageRepository<? extends Number> messageRepository = vaults.get(type);
                    for (MessageCallback callback : callbacks) {
                        callback.getCallback().accept(callback.getElement(), messageRepository.getVault());
                    }
                });
            }
        };
    }

    public void startReader() {
        Thread thread = new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytes = 0; // bytes returned from read()
                int numberOfReadings = 0; //to control the number of readings from the Arduino

                // Keep listening to the InputStream until an exception occurs.
                // We just want to get 1 temperature readings from the Arduino
                while (numberOfReadings < 1) {
                    try {
                        buffer[bytes] = (byte) inputStream.read();
                        String readMessage;
                        // If I detect a "\n" means I already read a full measurement
                        if (buffer[bytes] == '\n') {
                            readMessage = new String(buffer, 0, bytes);
                            Log.i(MainActivity.TAG, readMessage);

                            Message msg = messageHandler.obtainMessage();
                            msg.obj = readMessage;
                            messageHandler.sendMessage(msg);

                            bytes = 0;
                            numberOfReadings++;
                        } else {
                            bytes++;
                        }

                    } catch (IOException e) {
                        Log.d(MainActivity.TAG, "Input stream was disconnected", e);
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("Bluetooth", "Error while reading", e);
            }
        });
        thread.start();
    }

}
