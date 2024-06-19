package com.example.weatherapp;

import android.view.View;

import java.util.List;
import java.util.function.BiConsumer;

public class MessageCallback {

    private final View element;
    private final BiConsumer<View, List<? extends Number>> callback;

    public MessageCallback(View element, BiConsumer<View, List<? extends Number>> callback) {
        this.element = element;
        this.callback = callback;
    }

    public View getElement() {
        return element;
    }

    public BiConsumer<View, List<? extends Number>> getCallback() {
        return callback;
    }

}
