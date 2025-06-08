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
