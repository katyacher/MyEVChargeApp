package ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import models.Station;
import ui.adapters.StationAdapter;
import ui.viewmodels.StationViewModel;
public class ListFragment extends Fragment implements StationAdapter.OnStationClickListener {
    private StationViewModel viewModel;
    private StationAdapter adapter;
    // Добавьте константу для запроса разрешений
   // private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private ActivityResultLauncher<String> requestPermissionLauncher;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StationViewModel.class);
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_stations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Инициализация адаптера
        adapter = new StationAdapter(
                new ArrayList<>(),
                this,
                false // Не показывать иконку удаления
        );
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Подписываемся на изменения списка станций
        viewModel.getAllStations().observe(getViewLifecycleOwner(), stations -> {
            if (stations == null || stations.isEmpty()) {
                Toast.makeText(requireContext(), "Нет доступных станций", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.updateStations(stations);
        });
    }

    @Override
    public void onStationClick(Station station) {
        navigateToStationDetails(station.getId());
    }

    @Override
    public void onFavoriteClick(Station station, int position) {
        viewModel.toggleFavorite(station.getId());
        //  добавить анимацию для сердечка
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onDeleteClick(Station station) {
        // Не используется в этом фрагменте
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
            // Запрашиваем разрешение новым способом
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    private void navigateToStationDetails(int stationId) {
        Bundle args = new Bundle();
        args.putInt("stationId", stationId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_list_to_details, args);
    }
}

