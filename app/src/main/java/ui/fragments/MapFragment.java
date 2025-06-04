package ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.yandex.mapkit.mapview.MapView;

import data.repositories.StationRepository;
import models.Station;
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
/*
public class MapFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
    private void showStationDetails(int stationId) {
        Bundle args = new Bundle();
        args.putInt("stationId", stationId);
        Navigation.findNavController(requireView()).navigate(R.id.action_map_to_details, args);
    }

} /*implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Добавление меток станций
        for (Station station : StationRepository.getAllStations()) {
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(station.getLatitude(), station.getLongitude()))
                    .title(station.getName())
                    .snippet(station.getAddress()));
            marker.setTag(station.getId());
        }

        // Обработка клика по метке
        googleMap.setOnMarkerClickListener(marker -> {
            int stationId = (int) marker.getTag();
            showStationDetails(stationId);
            return true;
        });
    }

    private void showStationDetails(int stationId) {
        Bundle args = new Bundle();
        args.putInt("stationId", stationId);
        Navigation.findNavController(requireView()).navigate(R.id.action_map_to_details, args);
    }

    // Обработка жизненного цикла карты
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    // ... остальные методы жизненного цикла
}
*/