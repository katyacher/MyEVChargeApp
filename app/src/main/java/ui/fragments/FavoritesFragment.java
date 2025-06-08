package ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import models.Station;
import ui.adapters.StationAdapter;
import ui.viewmodels.StationViewModel;

public class FavoritesFragment extends Fragment implements StationAdapter.OnStationClickListener {
    private StationViewModel viewModel;
    private StationAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.rv_stations);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация адаптера с флагом showDeleteIcon = true
        adapter = new StationAdapter(
                new ArrayList<>(),
                this,
                true // Показывать иконку удаления
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Подписываемся на изменения списка избранных станций
        viewModel.getFavoriteStations().observe(getViewLifecycleOwner(), stations -> {
            if (stations == null || stations.isEmpty()) {
                showEmptyState();
            } else {
                showStationsList(stations);
            }
        });
        viewModel.getUpdateEvent().observe(getViewLifecycleOwner(), event -> {
            if (event != null && event.getContentIfNotHandled() != null) {
                viewModel.loadFavoriteStations();
            }
        });
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }

    private void showStationsList(List<Station> stations) {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        adapter.updateStations(stations);
    }

    @Override
    public void onStationClick(Station station) {
        navigateToStationDetails(station.getId());
    }

    @Override
    public void onFavoriteClick(Station station, int position) {
        // Не используется в этом фрагменте
    }

    @Override
    public void onDeleteClick(Station station) {
        viewModel.toggleFavorite(station.getId());
       Toast.makeText(requireContext(), "Удалено из избранного", Toast.LENGTH_SHORT).show();
        // viewModel.toggleFavorite(station.getId()).observe(getViewLifecycleOwner(), updatedStation -> {
            // Список автоматически обновится через LiveData в getFavoriteStations()
       // });
    }

    @Override
    public void onRouteClick(Station station) {
        // Реализация построения маршрута
    }

    private void navigateToStationDetails(int stationId) {
        Bundle args = new Bundle();
        args.putInt("stationId", stationId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_favorites_to_details, args);
    }
}
/*public class FavoritesFragment extends Fragment implements StationAdapter.OnStationClickListener {

    private StationViewModel viewModel;
    private StationAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.rv_stations);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_stations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Инициализация адаптера с флагом showDeleteIcon = true
        adapter = new StationAdapter(
                new ArrayList<>(),
                this,
                false, // Показывать иконку удаления
                getViewLifecycleOwner(),
                viewModel
        );

        recyclerView.setAdapter(adapter);

        // Подписываемся на изменения списка избранных станций
        viewModel.getFavoriteStations().observe(getViewLifecycleOwner(), stations -> {
            if (stations == null || stations.isEmpty()) {
                Log.d("FavoriteFragment", "No favorite stations");
                showEmptyState();
            } else {
                //showStationsList(stations);
                adapter.updateStations(stations);
            }
        });
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
    }

    private void showStationsList(List<Station> stations) {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        adapter.updateStations(stations);
    }

    @Override
    public void onStationClick(Station station) {
        navigateToStationDetails(station.getId());
    }

    @Override
    public void onFavoriteClick(Station station, int position) {

    }

    @Override
    public void onDeleteClick(Station station) {
        viewModel.toggleFavorite(station.getId());
        Toast.makeText(requireContext(), "Удалено из избранного", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteClick(Station station) {
        // Не используется в этом фрагменте (только для списка всех станций)
    }

    @Override
    public void onRouteClick(Station station) {
        // Реализация построения маршрута
    }

    private void navigateToStationDetails(int stationId) {
        Bundle args = new Bundle();
        args.putInt("stationId", stationId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_favorites_to_details, args);
    }
}
package ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;


import java.util.ArrayList;
import java.util.List;

import data.repositories.StationRepository;
import models.Station;
import ui.adapters.StationAdapter;
import ui.viewmodels.StationViewModel;

public class FavoritesFragment extends Fragment implements StationAdapter.OnStationClickListener {
    private StationViewModel viewModel;
    private RecyclerView recyclerView;
    private TextView tvEmptyState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StationViewModel.class);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // макет фрагмента
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = view.findViewById(R.id.rv_favorites);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        StationAdapter adapter = new StationAdapter(
                new ArrayList<>(),
                this,
                false, // showDeleteIcon
                getViewLifecycleOwner(), // Передаем LifecycleOwner
                viewModel
        );
        recyclerView.setAdapter(adapter);
        viewModel.getFavoriteStations().observe(getViewLifecycleOwner(), stations -> {
            adapter.updateStations(stations);
        });
        // Получите данные избранных станций из репозитория через единый метод
       // List<Station> favoriteStations = StationRepository.getFavoriteStations();
       // updateUI(favoriteStations);
        return view;
    }
    private void updateUI(List<Station> favoriteStations) {
        if (favoriteStations.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
            // Используем общий адаптер
            StationAdapter adapter = new StationAdapter(favoriteStations, this);
            recyclerView.setAdapter(adapter);
        }
    }


    @Override
    public void onStationClick(Station station) {
        // Переход к детальной информации о станции
        Bundle args = new Bundle();
        args.putInt("stationId", station.getId());

        Navigation.findNavController(requireView())
                .navigate(R.id.action_favorites_to_details, args);
    }
    /*
    @Override
    public void onFavoriteClick(Station station) {
        StationRepository.toggleFavorite(station.getId());
        // Обновляем список избранных
        List<Station> updatedFavorites = StationRepository.getFavoriteStations();
        updateUI(updatedFavorites);
    }
    @Override
    public void onFavoriteClick(Station station) {
        viewModel.toggleFavorite(station.getId());
    }
    @Override
    public void onDeleteClick(Station station) {
        viewModel.toggleFavorite(station.getId());
    }


    @Override
    public void onRouteClick(Station station) {
        // Построение маршрута через Intent
    }
}
*/