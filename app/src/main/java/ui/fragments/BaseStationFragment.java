package ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;

import org.osmdroid.util.GeoPoint;

import models.Station;
import ui.adapters.StationAdapter;

public abstract class BaseStationFragment extends Fragment
        implements StationAdapter.OnStationClickListener {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermissionLauncher();
    }

    private void initPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(requireContext(),
                                "Разрешение получено. Нажмите на маршрут еще раз",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Для построения маршрута необходимо разрешение",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onRouteClick(Station station) {
        GeoPoint destination = new GeoPoint(station.getLatitude(), station.getLongitude());

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openYandexMapsRoute(destination, station.getName());
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
}