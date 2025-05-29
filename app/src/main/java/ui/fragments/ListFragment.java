package ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import data.repositories.StationRepository;
import models.Station;
import ui.adapters.StationAdapter;

public class ListFragment extends Fragment implements StationAdapter.OnStationClickListener  {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        // Настройка RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rv_stations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Получаем данные из репозитория через единый метод
        List<Station> stations = StationRepository.getAllStations();
        // Используем общий адаптер
        recyclerView.setAdapter(new StationAdapter(stations, this));

        return view;
    }

    // 4. Получаем данные из репозитория
    private List<Station> getAllStations() {
        return StationRepository.getAllStations();
    }

    @Override
    public void onStationClick(Station station) {
        // Переход к детальной информации о станции
        Bundle args = new Bundle();
        args.putInt("station_id", station.getId());

        Navigation.findNavController(requireView())
                .navigate(R.id.action_list_to_stationDetails, args);
    }

    @Override
    public void onFavoriteClick(Station station) {
        StationRepository.toggleFavorite(station.getId());
        // Обновить отображение
        RecyclerView recyclerView = requireView().findViewById(R.id.rv_stations);
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}