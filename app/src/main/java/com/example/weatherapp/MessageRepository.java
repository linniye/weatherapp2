package com.example.weatherapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessageRepository<T extends Number> {

    private final Collection<T> vault = new ArrayList<>();

    public List<? extends Number> getVault() {
        return (List<? extends Number>) vault;
    }

    public void add(T number) {
        vault.add(number);
    }

    public void clear() {
        vault.clear();
    }

}
