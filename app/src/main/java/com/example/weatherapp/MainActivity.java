package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.weatherapp.ui.Monitor.MonitorFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.weatherapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements MonitorFragment.OnSettingsClickListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
        @Override
        public void onSettingsClick() {
            openSettingsActivity();
        }

        public void openSettingsActivity() {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
    }
