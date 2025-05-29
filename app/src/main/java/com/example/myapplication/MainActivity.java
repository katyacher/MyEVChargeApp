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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
       /* Button button = findViewById(R.id.button);
        button.setOnClickListener(view -> {
            Toast.makeText(this, "Привет от Java!", Toast.LENGTH_SHORT).show();
        });*/

        bottomNav = findViewById(R.id.bottom_navigation);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNav, navController);

        /*List<Station> stations = getStationsFromAPI(); // Заглушка
        for (Station station : stations) {
            Point point = new Point(station.getLatitude(), station.getLongitude());
            mapView.getMap().getMapObjects().addPlacemark(point);
        }*/
    }
}