package com.example.myapplication;

import android.graphics.Point;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import ui.fragments.StationDetailsFragment;

public class MainActivity extends AppCompatActivity implements StationDetailsFragment.OnStartChargingListener {

    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        // Получаем NavHostFragment и NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found in layout");
        }
        NavController navController = navHostFragment.getNavController();
        // Настройка нижнего меню
        NavigationUI.setupWithNavController(bottomNav, navController);
        // Скрывать меню на некоторых экранах
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.stationDetailsFragment || id == R.id.activeSessionFragment) {
                bottomNav.setVisibility(View.GONE);
            } else {
                bottomNav.setVisibility(View.VISIBLE);
            }
        });

        /*List<Station> stations = getStationsFromAPI(); // Заглушка
        for (Station station : stations) {
            Point point = new Point(station.getLatitude(), station.getLongitude());
            mapView.getMap().getMapObjects().addPlacemark(point);
        }*/
    }
    // Реализация интерфейса OnStartChargingListener
    @Override
    public void onStartCharging(int stationId) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);
            navController.navigate(R.id.action_details_to_session, args);
        }
    }

    // упращенная реализация через NavController
    /*
    @Override
    public void onStartCharging(int stationId) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putInt("stationId", stationId);
        navController.navigate(R.id.action_details_to_session, args);
    } */
}

