package com.example.myapplication;

import android.graphics.Point;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/*import java.util.List;
import models.Station; */


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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

        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_behavior));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Обработка изменений состояния
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Обработка анимации
            }
        });
    }
}