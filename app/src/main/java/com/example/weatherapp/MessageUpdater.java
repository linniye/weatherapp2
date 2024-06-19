package com.example.weatherapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class MessageUpdater {

    private final Map<UpdaterType, Collection<MessageCallback>> callbacks = new EnumMap<>(UpdaterType.class);
    private final Map<UpdaterType, MessageRepository<? extends Number>> vaults = new EnumMap<>(UpdaterType.class);

    public void processMessage(String message) {
        String[] historyValues = message.split(",");

        MessageRepository<Float> temperature = vaults.getOrDefault(UpdaterType.TEMPERATURE, new MessageRepository());
        MessageRepository<Float> humidity = vaults.getOrDefault(UpdaterType.HUMIDITY, new MessageRepository());
        MessageRepository<Integer> ppm = vaults.getOrDefault(UpdaterType.PPM, new MessageRepository());

        temperature.clear();
        humidity.clear();
        ppm.clear();

        for (String historyValue : historyValues) {
            if (historyValue.isEmpty()) {
                continue;
            }

            String[] split = historyValue.split("#");
            if (split.length != 3) {
                continue;
            }

            temperature.add(Float.parseFloat(split[0]));
            humidity.add(Float.parseFloat(split[1]));
            ppm.add(Integer.parseInt(split[2]));
        }
    }

    public void addCallback(UpdaterType updaterType, MessageCallback callback) {
        callbacks.getOrDefault(updaterType, new ArrayList<>()).add(callback);
    }

    public Map<UpdaterType, Collection<MessageCallback>> getCallbacks() {
        return callbacks;
    }

    public Map<UpdaterType, MessageRepository<? extends Number>> getVaults() {
        return vaults;
    }
}
