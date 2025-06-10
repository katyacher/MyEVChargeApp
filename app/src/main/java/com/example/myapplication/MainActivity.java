package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.util.GeoPoint;

import java.util.Locale;

import ui.fragments.StationDetailsFragment;

public class MainActivity extends AppCompatActivity implements StationDetailsFragment.OnStartChargingListener {
    // Добавьте константу для запроса разрешений
    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    // Добавьте поле для хранения отложенного запроса
    private Pair<GeoPoint, String> pendingRouteRequest;

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

    public void openYandexMapsRoute(GeoPoint destination, String destinationName) {
        try {
            // Проверяем разрешение еще раз (на всякий случай)
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                pendingRouteRequest = new Pair<>(destination, destinationName);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
                return;
            }

            // Формируем URI для Яндекс.Карт
            String uri;
            if (hasLocationPermission()) {
                // Маршрут от текущего местоположения
                uri = String.format(Locale.US,
                        "yandexmaps://build_route_on_map/?lat_to=%f&lon_to=%f",
                        destination.getLatitude(),
                        destination.getLongitude());
            } else {
                // Только точка назначения
                uri = String.format(Locale.US,
                        "yandexmaps://maps.yandex.ru/?pt=%f,%f&z=14",
                        destination.getLongitude(),
                        destination.getLatitude());
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Проверяем, установлены ли Яндекс.Карты
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Если Яндекс.Карты не установлены, открываем в браузере
                openYandexMapsInBrowser(destination);
            }
        } catch (Exception e) {
            Log.e("MAPS", "Error opening Yandex Maps", e);
            Toast.makeText(this, "Ошибка при открытии Яндекс.Карт", Toast.LENGTH_SHORT).show();
        }
    }

    private void openYandexMapsInBrowser(GeoPoint destination) {
        String webUrl = String.format(Locale.US,
                "https://yandex.ru/maps/?pt=%f,%f&z=14&rtt=auto",
                destination.getLongitude(),
                destination.getLatitude());

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        startActivity(browserIntent);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // Обработка результата запроса разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Если разрешение получено, выполняем отложенный запрос
                if (pendingRouteRequest != null) {
                    openYandexMapsRoute(pendingRouteRequest.first, pendingRouteRequest.second);
                    pendingRouteRequest = null;
                }
            } else {
                Toast.makeText(this,
                        "Для построения маршрута необходимо разрешение на доступ к местоположению",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}

