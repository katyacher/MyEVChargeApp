package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import ui.fragments.StationDetailsFragment;

public class MainActivity extends AppCompatActivity implements StationDetailsFragment.OnStartChargingListener {
    @Override
    protected void attachBaseContext(Context newBase) {
        // Устанавливаем язык перед созданием контекста
        super.attachBaseContext(updateBaseContextLocale(newBase));
    }
    private BottomNavigationView bottomNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Улучшаем производительность
        getWindow().setBackgroundDrawable(null);


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
    }

    @Override
    public void onStartCharging(int stationId) {
        Log.d("MainActivity", "Starting charging for station: " + stationId);
         try {

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);

            navController.navigate(R.id.action_details_to_session, args);
        } catch (Exception e) {
            Log.e("MainActivity", "Navigation error", e);
            Toast.makeText(this, "Ошибка перехода к зарядке", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Обработка изменений конфигурации
    }
    private Context updateBaseContextLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("AppSettings", MODE_PRIVATE);
        String language = prefs.getString("app_language", "ru");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }
    public static void restartActivity(Activity activity) {
        Intent intent = new Intent(activity, activity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}

