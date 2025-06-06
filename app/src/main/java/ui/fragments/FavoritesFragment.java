package ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;


import java.util.List;

import data.repositories.StationRepository;
import models.Station;
import ui.adapters.StationAdapter;

public class FavoritesFragment extends Fragment implements StationAdapter.OnStationClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyState;

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
        // Получите данные избранных станций из репозитория через единый метод
        List<Station> favoriteStations = StationRepository.getFavoriteStations();
        updateUI(favoriteStations);

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

    @Override
    public void onFavoriteClick(Station station) {
        StationRepository.toggleFavorite(station.getId());
        // Обновляем список избранных
        List<Station> updatedFavorites = StationRepository.getFavoriteStations();
        updateUI(updatedFavorites);
    }

    @Override
    public void onRouteClick(Station station) {
        // Построение маршрута через Intent
    }
}