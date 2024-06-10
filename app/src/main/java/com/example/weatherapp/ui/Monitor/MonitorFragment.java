package com.example.weatherapp.ui.Monitor;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.MainActivity;
import com.example.weatherapp.R;
import com.example.weatherapp.Settings;
import com.example.weatherapp.ui.Weather.Weather;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MonitorFragment extends Fragment {

    private ImageButton imageButton2;
    private ImageButton imageButton3;
    public static Handler handler;
    private final static int ERROR_READ = 0; // used in bluetooth handler to identify message update
    int[] time = {9, 11, 13, 14, 15, 17, 18}; // Пример данных time
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
        imageButton2 = view.findViewById(R.id.imageButton2);
        imageButton3 = view.findViewById(R.id.imageButton3);

        Bundle args = getArguments();
        if (args != null) {
            float humidity = args.getFloat("humidity", 0.0f);
            float temperature = args.getFloat("temperature", 0.0f);
            float ppm = args.getFloat("ppm", 0.0f);

            // Используйте данные для обновления gauge1, gauge2, gauge3 и создания LineChart
        }

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
        //Using a handler to update the interface in case of an error connecting to the BT device
        //My idea is to show handler vs RxAndroid
//        handler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//
//                    case ERROR_READ:
//                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
//                        ArcProgress arcProgress1 = view.findViewById(R.id.gauge1); // Получаем ссылку на первый виджет ArcProgress
//                        gauge1.setText(arduinoMsg);
//                        break;
//                }
//            }
//        };
//
//        @Override
//        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//            super.onViewCreated(view, savedInstanceState);
//
//            Bundle args = getArguments();
//            if (args != null) {
//                float humidity = args.getFloat("humidity");
//                float temperature = args.getFloat("temperature");
//                float ppm = args.getFloat("ppm");
//
//                gauge1.setValue(humidity);
//                gauge2.setValue(temperature);
//                gauge3.setValue(ppm);
//            }
//        }
//////
//            ArcProgress arcProgress2 = view.findViewById(R.id.gauge2); // Получаем ссылку на второй виджет ArcProgress
//            arcProgress2.setProgress(30); // Устанавливаем значение 75 для второго виджета ArcProgress
//            ArcProgress arcProgress3 = view.findViewById(R.id.gauge3); // Получаем ссылку на второй виджет ArcProgress
//            arcProgress3.setProgress(414); // Устанавливаем значение 75 для второго виджета ArcProgress

        // Настройка LineChart
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setText("");
        lineChart.setExtraOffsets(10f, 20f, 10f, 20f);

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

// Создание LineDataSet для PPM
        LineDataSet ppmDataSet = new LineDataSet(null, "PPM");
        ppmDataSet.setColor(Color.BLUE);
        ppmDataSet.setCircleColor(Color.BLUE);
        ppmDataSet.setLineWidth(2f);
        ppmDataSet.setCircleRadius(4f);
        ppmDataSet.setValueTextColor(Color.BLUE);
        ppmDataSet.setValueTextSize(10f);

        List<Entry> temperatureEntries = new ArrayList<>();
        List<Entry> humidityEntries = new ArrayList<>();
        List<Entry> ppmEntries = new ArrayList<>();

//        for (int i = 0; i < time.length; i++) {
//            temperatureEntries.add(new Entry(time[i], temperatures[i]));
//            humidityEntries.add(new Entry(time[i], humidities[i]));
//            ppmEntries.add(new Entry(time[i], ppm[i]));
//        }
//
//        temperatureDataSet.setValues(temperatureEntries);
//        humidityDataSet.setValues(humidityEntries);
//        ppmDataSet.setValues(ppmEntries);
//
//        LineData lineData = new LineData(temperatureDataSet, humidityDataSet, ppmDataSet);
//        lineChart.setData(lineData); // Установка LineData для LineChart
//        lineChart.notifyDataSetChanged();
//        lineChart.invalidate();
//        int sum = 0;
//        for (int temperature : temperatures) {
//            sum += temperature;
//        }
//        double averageTemperature = (double) sum / temperatures.length;
//        TextView sensor1 = view.findViewById(R.id.sensor1);
//        sensor1.setText(String.format("%.0f °C", averageTemperature));
//
//        int sumhum = 0;
//        for (int humidity : humidities) {
//            sumhum += humidity;
//        } double averageHumidity = (double) sumhum / humidities.length;
//        TextView sensor2 = view.findViewById(R.id.sensor2);
//        sensor2.setText(String.format("%.0f" + "%%", averageHumidity));
//
//        int sumppm = 0;
//        for (int ppms : ppm) {
//            sumppm += ppms;
//        } double averagePpm = (double) sumppm / ppm.length;
//        TextView sensor3 = view.findViewById(R.id.sensor3);
//        sensor3.setText(String.format("%.0f PPM", averagePpm));

        return view;

    }

    }
