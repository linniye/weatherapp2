package com.example.weatherapp.ui.Monitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.weatherapp.R;

public class DataAdapter extends BaseAdapter {
    private Context context;

    private int[] temperatures;
    private int[] humidities;
    private int[] ppm;
    private int[]  time;

    public DataAdapter(Context context, int[] temperatures, int[] humidities, int[] ppm, int [] time) {
        this.context = context;
        this.temperatures = temperatures;
        this.humidities = humidities;
        this.ppm = ppm;
        this.time = time;
    }

    @Override
    public int getCount() {
        return temperatures.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listviewdata, parent, false);
        }

        TextView timeTextView = convertView.findViewById(R.id.mT1);
        TextView temperatureTextView = convertView.findViewById(R.id.mT2);
        TextView humidityTextView = convertView.findViewById(R.id.mT3);
        TextView ppmTextView = convertView.findViewById(R.id.mT4);

        timeTextView.setText(String.valueOf(time[position]));
        temperatureTextView.setText(temperatures[position] + "°C");
        humidityTextView.setText(humidities[position] + "%");
        ppmTextView.setText(ppm[position] + " PPM");

        return convertView;
    }
    }