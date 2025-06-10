package ui.fragments;

import android.Manifest;

import android.content.Context;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.caverock.androidsvg.BuildConfig;
import com.example.myapplication.R;


import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;

import org.osmdroid.tileprovider.tilesource.XYTileSource;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;

import org.osmdroid.views.overlay.OverlayItem;

import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import data.api.ApiClient;
import data.api.CoinDeskApiService;
import data.api.CoinDeskResponse;
import models.Station;
import data.api.WeatherApiService;
import data.api.WeatherResponse;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ui.viewmodels.StationViewModel;

public class MapFragment extends Fragment implements MapEventsReceiver {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private StationViewModel viewModel;
    private ItemizedIconOverlay<OverlayItem> stationsOverlay;
    private ImageButton btnMyLocation;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Включение режима отладки OSMDroid
        Configuration.getInstance().setDebugMode(true); // Только для разработки
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Находим элементы вью
        mapView = view.findViewById(R.id.mapView);
        btnMyLocation = view.findViewById(R.id.btnMyLocation);
        // btnClearRoute = view.findViewById(R.id.btnClearRoute);

        // Инициализация карты
        initializeMap();
        //bitcoin
        fetchBitcoinPrice();

        // Настройка ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(StationViewModel.class);

        // Подписка на изменения списка станций
        viewModel.getAllStations().observe(getViewLifecycleOwner(), stations -> {
            if (stations != null && !stations.isEmpty()) {
                addStationsToMap(stations);
                zoomToStations(stations);
            }
        });

        // Кнопка "Мое местоположение"
        btnMyLocation.setOnClickListener(v -> {
            if (myLocationOverlay != null && myLocationOverlay.getMyLocation() != null) {
                mapView.getController().animateTo(myLocationOverlay.getMyLocation());
            }
        });
        // Кнопка "Очистка маршрута"
        // btnClearRoute.setOnClickListener(v -> clearRoute());

        viewModel.getRouteEndPoint().observe(getViewLifecycleOwner(), endPoint -> {
            if (endPoint != null && myLocationOverlay != null && myLocationOverlay.getMyLocation() != null) {
                GeoPoint startPoint = myLocationOverlay.getMyLocation();
                String stationName = viewModel.getRouteStationName().getValue();
                //buildRoute(startPoint, endPoint, stationName != null ? stationName : "Станция");

                // Очищаем точку маршрута после построения
                viewModel.clearRouteEndPoint();
            }
        });
    }

    private void initializeMap() {
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().load(requireContext(),
                requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        //mapView.setTileSource(TileSourceFactory.MAPNIK);
        // Заменяем стандартный TileSource на кастомный с обработкой URL
        mapView.setTileSource(new XYTileSource("Mapnik",
                0, 19, 256, ".png",
                new String[]{"https://a.tile.openstreetmap.org/"},
                "© OpenStreetMap contributors") {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex) + "/"
                        + MapTileIndex.getX(pMapTileIndex) + "/"
                        + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding;
            }
        });
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        mapView.setMultiTouchControls(true); // Лучше использовать жесты

        mapView.getOverlays().add(new MapEventsOverlay(this));
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView) {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                super.onLocationChanged(location, source);
                if (location != null) {
                    fetchWeather(location.getLatitude(), location.getLongitude());
                }
            }
        };

        //myLocationOverlay = new MyLocationNewOverlay(
        //  new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setOptionsMenuEnabled(true);
        mapView.getOverlays().add(myLocationOverlay);

        // Установка начального масштаба и положения
        mapView.getController().setZoom(19.0);
        mapView.getController().setCenter(new GeoPoint(56.010563, 92.852572)); // Красноярск по умолчанию

        requestPermissionsIfNecessary();

    }

    private void addStationsToMap(List<Station> stations) {
        // Удаляем старые маркеры
        if (stationsOverlay != null) {
            mapView.getOverlays().remove(stationsOverlay);
        }

        List<OverlayItem> items = new ArrayList<>();
        //Drawable stationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_charger_marker);

        for (Station station : stations) {
            GeoPoint point = new GeoPoint(station.getLatitude(), station.getLongitude());
            OverlayItem item = new OverlayItem(station.getName(), station.getAddress(), point);
            /* if (stationIcon != null) {
                item.setMarker(stationIcon);
            }*/
            // Устанавливаем иконку в зависимости от статуса
            switch (station.getStatus()) {
                case "busy":
                case "Занято":
                    item.setMarker(ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_busy));
                    break;
                case "free":
                case "Свободно":
                    item.setMarker(ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_free));
                    break;
                case "offline":
                default:
                    item.setMarker(ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_offline));
            }
            items.add(item);
        }

        stationsOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        Station station = stations.get(index);
                        viewModel.setSelectedStation(station);
                        navigateToStationDetails(station.getId());
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        return false;
                    }
                },
                requireContext().getApplicationContext());

        mapView.getOverlays().add(stationsOverlay);
        mapView.invalidate(); // Обновляем карту

    }

    private void zoomToStations(List<Station> stations) {
        if (stations.isEmpty()) return;

        // Центрируем карту на первой станции
        GeoPoint firstStation = new GeoPoint(stations.get(0).getLatitude(), stations.get(0).getLongitude());
        mapView.getController().setZoom(12.0);
        mapView.getController().setCenter(firstStation);
    }



    private void navigateToStationDetails(int stationId) {
        try {
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);
            Navigation.findNavController(requireView()).navigate(R.id.action_map_to_details, args);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ошибка навигации", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        // Обработка клика по карте
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        // Обработка долгого нажатия
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mapView != null) {
            mapView.onDetach();
            mapView = null;
            btnMyLocation = null;
        }

        super.onDestroyView();
        // Очищаем ссылки на вью
        mapView = null;
        btnMyLocation = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown(); // Завершаем фоновые задачи
        handler.removeCallbacksAndMessages(null); // Удаляем все callback-и
        //routeExecutor.shutdownNow();
    }


    private void showProgress(boolean show) {
        requireView().findViewById(R.id.routeProgress).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)requireContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    /*private void fetchWeather(double lat, double lon) {
        if (!isOnline()) {
            Log.e("WeatherAPI", "No internet connection");
            return;
        }


        // Безопасное получение ключа
        // String apiKey = BuildConfig.WEATHER_API_KEY;
        String apiKey = "WEATHER_API_KEY";

        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = service.getCurrentWeather(lat, lon, "metric", apiKey);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateWeatherUI(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Error fetching weather", t);
            }
        });
    }
*/
    private void fetchWeather(double lat, double lon) {
        if (!isOnline()) {
            Log.e("WeatherAPI", "No internet connection");
            return;
        }

        executor.execute(() -> {
            try {
                String apiKey = "WEATHER_API_KEY";
                WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
                Response<WeatherResponse> response = service.getCurrentWeather(lat, lon, "metric", apiKey).execute();

                if (response.isSuccessful() && response.body() != null) {
                    handler.post(() -> updateWeatherUI(response.body()));
                }
            } catch (IOException e) {
                Log.e("WeatherAPI", "Error fetching weather", e);
            }
        });
    }
    private void updateWeatherUI(WeatherResponse weather) {
        TextView tempText = getView().findViewById(R.id.temperatureText);
        TextView descText = getView().findViewById(R.id.weatherDescription);
        ImageView iconView = getView().findViewById(R.id.weatherIcon);

        tempText.setText(String.format(Locale.getDefault(), "%.1f°C", weather.getMain().getTemperature()));
        descText.setText(weather.getWeather().get(0).getDescription());

        // Загрузка иконки погоды
        String iconUrl = "https://openweathermap.org/img/wn/" +
                weather.getWeather().get(0).getIcon() + "@2x.png";

        Glide.with(this)
                .load(iconUrl)
                .into(iconView);
    }

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                if (fineLocationGranted != null && fineLocationGranted) {
                    myLocationOverlay.enableMyLocation();
                } else {
                    Toast.makeText(requireContext(), "Для работы карты требуются разрешения", Toast.LENGTH_LONG).show();
                }
            });

    private void requestPermissionsIfNecessary() {
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
    // bitcoin
    private void fetchBitcoinPrice() {
        if (!isOnline()) {
            return;
        }

        executor.execute(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.coindesk.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            CoinDeskApiService service = retrofit.create(CoinDeskApiService.class);
            try {
                Response<CoinDeskResponse> response = service.getCurrentBitcoinPrice().execute();
                if (response.isSuccessful() && response.body() != null) {
                    String price = response.body().getBpi().getUsd().getRate();
                    handler.post(() -> updateBitcoinPriceUI(price)); // Возвращаемся в UI-поток
                }
            } catch (IOException e) {
                Log.e("CoinDeskAPI", "Error in background", e);
            }
        });
    }

    private void updateBitcoinPriceUI(String price) {
        // Предположим, что у вас есть TextView в разметке (добавьте его в fragment_map.xml)
        TextView bitcoinPriceText = getView().findViewById(R.id.bitcoinPriceText);
        if (bitcoinPriceText != null) {
            bitcoinPriceText.setText("BTC: $" + price);
        }
    }
}



