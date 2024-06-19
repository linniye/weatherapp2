package com.example.weatherapp.ui.Monitor;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.Weather.Weather;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MonitorFragment extends Fragment {

    private ImageButton imageButton2;
    private ImageButton imageButton3;
    public static Handler handler;
    private final static int ERROR_READ = 0; // used in bluetooth handler to identify message update
    int[] time = {9, 11, 13, 14, 15, 17, 18}; // Пример данных time
    int[] temperatures = {11, 14, 16, 13, 12, 11, 9}; // Пример данных температуры
    int[] humidities = {80, 77, 60, 65, 72, 75, 89}; // Пример данных влажности
    int[] ppm = {400, 650, 700, 650, 600, 550, 400};// Пример данных PPM

    public interface OnSettingsClickListener {
        void onSettingsClick();
    }

    private OnSettingsClickListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsClickListener) {
            mListener = (OnSettingsClickListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnSettingsClickListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor, container, false);
        View gauge1 = view.findViewById(R.id.gauge1);
        View gauge2 = view.findViewById(R.id.gauge2);
        View gauge3 = view.findViewById(R.id.gauge3);
        LineChart lineChart = view.findViewById(R.id.brain_chart3);
        LineChart lineChart2 = view.findViewById(R.id.brain_chart4);
        imageButton2 = view.findViewById(R.id.imageButton2);
        imageButton3 = view.findViewById(R.id.imageButton3);

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSettingsClick();
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Weather.class);
                startActivity(intent);

            }
        });
        ArcProgress arcProgress1 = view.findViewById(R.id.gauge1); // Получаем ссылку на второй виджет ArcProgress
        arcProgress1.setProgress(30);
        ArcProgress arcProgress2 = view.findViewById(R.id.gauge2); // Получаем ссылку на второй виджет ArcProgress
        arcProgress2.setProgress(30); // Устанавливаем значение 75 для второго виджета ArcProgress
        ArcProgress arcProgress3 = view.findViewById(R.id.gauge3); // Получаем ссылку на второй виджет ArcProgress
        arcProgress3.setProgress(414); // Устанавливаем значение 75 для второго виджета ArcProgress

        // Настройка lineChart
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setText("");
        lineChart.setExtraOffsets(10f, 20f, 10f, 20f);

// Создание LineDataSet для PPM
        LineDataSet ppmDataSet = new LineDataSet(null, "PPM");
        ppmDataSet.setColor(Color.BLUE);
        ppmDataSet.setCircleColor(Color.BLUE);
        ppmDataSet.setLineWidth(2f);
        ppmDataSet.setCircleRadius(4f);
        ppmDataSet.setValueTextColor(Color.BLUE);
        ppmDataSet.setValueTextSize(10f);

        List<Entry> ppmEntries = new ArrayList<>();
        for (int i = 0; i < time.length; i++) {
            ppmEntries.add(new Entry(time[i], ppm[i]));
        }
        ppmDataSet.setValues(ppmEntries);

        LineData ppmLineData = new LineData(ppmDataSet);
        lineChart.setData(ppmLineData); // Установка LineData для lineChart
        lineChart.invalidate();

// Настройка lineChart2
        lineChart2.setTouchEnabled(true);
        lineChart2.setDragEnabled(true);
        lineChart2.setScaleEnabled(true);
        lineChart2.setPinchZoom(false);
        lineChart2.setDrawGridBackground(false);
        lineChart2.getDescription().setText("");
        lineChart2.setExtraOffsets(10f, 20f, 10f, 20f);

// Создание LineDataSet для температуры
        LineDataSet temperatureDataSet = new LineDataSet(null, "Температура");
        temperatureDataSet.setColor(Color.RED);
        temperatureDataSet.setCircleColor(Color.RED);
        temperatureDataSet.setLineWidth(2f);
        temperatureDataSet.setCircleRadius(4f);
        temperatureDataSet.setValueTextColor(Color.RED);
        temperatureDataSet.setValueTextSize(10f);

// Создание LineDataSet для влажности
        LineDataSet humidityDataSet = new LineDataSet(null, "Влажность");
        humidityDataSet.setColor(Color.CYAN);
        humidityDataSet.setCircleColor(Color.CYAN);
        humidityDataSet.setLineWidth(2f);
        humidityDataSet.setCircleRadius(4f);
        humidityDataSet.setValueTextColor(Color.BLACK);
        humidityDataSet.setValueTextSize(10f);

        List<Entry> temperatureEntries = new ArrayList<>();
        List<Entry> humidityEntries = new ArrayList<>();

        for (int i = 0; i < time.length; i++) {
            temperatureEntries.add(new Entry(time[i], temperatures[i]));
            humidityEntries.add(new Entry(time[i], humidities[i]));
        }

        temperatureDataSet.setValues(temperatureEntries);
        humidityDataSet.setValues(humidityEntries);

        LineData lineData = new LineData(temperatureDataSet, humidityDataSet);
        lineChart2.setData(lineData); // Установка LineData для lineChart2
        lineChart2.invalidate();

        int sum = 0;
        for (int temperature : temperatures) {
            sum += temperature;
        }
        double averageTemperature = (double) sum / temperatures.length;
        TextView sensor1 = view.findViewById(R.id.sensor1);
        sensor1.setText(String.format("%.0f °C", averageTemperature));

        int sumhum = 0;
        for (int humidity : humidities) {
            sumhum += humidity;
        }
        double averageHumidity = (double) sumhum / humidities.length;
        TextView sensor2 = view.findViewById(R.id.sensor2);
        sensor2.setText(String.format("%.0f" + "%%", averageHumidity));

        int sumppm = 0;
        for (int ppms : ppm) {
            sumppm += ppms;
        }
        double averagePpm = (double) sumppm / ppm.length;
        TextView sensor3 = view.findViewById(R.id.sensor3);
        sensor3.setText(String.format("%.0f PPM", averagePpm));

        return view;

    }

}
