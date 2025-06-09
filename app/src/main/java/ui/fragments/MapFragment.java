package ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.caverock.androidsvg.BuildConfig;
import com.example.myapplication.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import models.Station;
import ui.viewmodels.StationViewModel;

public class MapFragment extends Fragment implements MapEventsReceiver {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private StationViewModel viewModel;
    private ItemizedIconOverlay<OverlayItem> stationsOverlay;
    private ImageButton btnMyLocation;

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

        // Инициализация карты
        initializeMap();

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
        mapView.setBuiltInZoomControls(false); // Лучше использовать жесты
        mapView.setMultiTouchControls(true);

        // Установка начального масштаба и положения
        mapView.getController().setZoom(12.0);
        mapView.getController().setCenter(new GeoPoint(56.010563, 92.852572)); // Красноярск по умолчанию

        requestPermissionsIfNecessary();

        myLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.setOptionsMenuEnabled(true);
        mapView.getOverlays().add(myLocationOverlay);

        mapView.getOverlays().add(new MapEventsOverlay(this));
    }

    private void addStationsToMap(List<Station> stations) {
        // Удаляем старые маркеры
        if (stationsOverlay != null) {
            mapView.getOverlays().remove(stationsOverlay);
        }

        List<OverlayItem> items = new ArrayList<>();
        Drawable stationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_charger_marker);

        for (Station station : stations) {
            GeoPoint point = new GeoPoint(station.getLatitude(), station.getLongitude());
            OverlayItem item = new OverlayItem(station.getName(), station.getAddress(), point);
            if (stationIcon != null) {
                item.setMarker(stationIcon);
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

    private void requestPermissionsIfNecessary() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
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
        super.onDestroyView();
        // Очищаем ссылки на вью
        mapView = null;
        btnMyLocation = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myLocationOverlay.enableMyLocation();
            } else {
                Toast.makeText(requireContext(), "Для работы карты требуются разрешения", Toast.LENGTH_LONG).show();
            }
        }
    }
}/* View Binding
package ui.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentMapBinding;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import models.Station;
import ui.viewmodels.StationViewModel;

public class MapFragment extends Fragment implements MapEventsReceiver {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private FragmentMapBinding binding;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private StationViewModel viewModel;
    private ItemizedIconOverlay<OverlayItem> stationsOverlay;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация карты
        initializeMap();

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
        binding.btnMyLocation.setOnClickListener(v -> {
            if (myLocationOverlay != null && myLocationOverlay.getMyLocation() != null) {
                mapView.getController().animateTo(myLocationOverlay.getMyLocation());
            }
        });
    }

    private void initializeMap() {
        // Конфигурация OSMDroid
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        mapView = binding.mapView;
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Проверка и запрос разрешений
        requestPermissionsIfNecessary();

        // Добавление слоя текущего местоположения
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        // Добавление обработчика кликов по карте
        mapView.getOverlays().add(new MapEventsOverlay(this));
    }

    private void addStationsToMap(List<Station> stations) {
        // Удаляем старые маркеры
        if (stationsOverlay != null) {
            mapView.getOverlays().remove(stationsOverlay);
        }

        List<OverlayItem> items = new ArrayList<>();
        Drawable stationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_charger_marker);

        for (Station station : stations) {
            GeoPoint point = new GeoPoint(station.getLatitude(), station.getLongitude());
            OverlayItem item = new OverlayItem(station.getName(), station.getAddress(), point);
            item.setMarker(stationIcon);
            items.add(item);
        }

        stationsOverlay = new ItemizedIconOverlay<>(items, (index, marker) -> {
            Station station = stations.get(index);
            viewModel.setSelectedStation(station);
            navigateToStationDetails(station.getId());
            return true;
        }, requireContext());

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

    private void requestPermissionsIfNecessary() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
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
        super.onDestroyView();
        binding = null;
    }
}*/
/*package ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

public class MapFragment extends Fragment {
    private ImageView mapPlaceholder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapPlaceholder = view.findViewById(R.id.map_placeholder);
        mapPlaceholder.setOnClickListener(v -> {
            // Для демо открываем станцию с ID 1
            showStationDetails(1);
        });

        return view;
    }

    private void showStationDetails(int stationId) {
        try {
            NavController navController = Navigation.findNavController(requireView());
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);
            navController.navigate(R.id.action_map_to_details, args);
        } catch (Exception e) {
            Log.e("MapFragment", "Navigation error", e);
            Toast.makeText(requireContext(), "Ошибка открытия деталей станции", Toast.LENGTH_SHORT).show();
        }
    }
}
*/