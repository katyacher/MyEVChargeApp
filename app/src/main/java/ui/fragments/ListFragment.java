package ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ListFragment", "onCreateView called");
        try {
            View view = inflater.inflate(R.layout.fragment_list, container, false);
            // Настройка RecyclerView
            RecyclerView recyclerView = view.findViewById(R.id.rv_stations);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            // Получаем данные из репозитория через единый метод
            List<Station> stations = StationRepository.getAllStations();
            Log.d("ListFragment", "Loaded stations: " + stations.size());
            if (stations.isEmpty()) {
                Toast.makeText(requireContext(), "Нет доступных станций", Toast.LENGTH_SHORT).show();
                Log.w("ListFragment", "Stations list is empty");
            }
            // Используем общий адаптер
            StationAdapter adapter = new StationAdapter(stations, this);
            recyclerView.setAdapter(adapter);

            return view;
        } catch (Exception e) {
            Log.e("ListFragment", "Error in onCreateView", e);
            Toast.makeText(requireContext(), "Ошибка загрузки списка", Toast.LENGTH_SHORT).show();
            return null; // или fallback layout
        }
    }

    @Override
    public void onStationClick(Station station) {
        // Переход к детальной информации о станции
        Bundle args = new Bundle();
        args.putInt("stationId", station.getId());

        Navigation.findNavController(requireView())
                .navigate(R.id.action_list_to_details, args);
    }

    @Override
    public void onFavoriteClick(Station station) {
        StationRepository.toggleFavorite(station.getId());
        // Обновить отображение
        RecyclerView recyclerView = requireView().findViewById(R.id.rv_stations);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onRouteClick(Station station) {
        // Построение маршрута через Intent
        //Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
        //        station.getLatitude() + "," + station.getLongitude());
        //Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        //mapIntent.setPackage("com.google.android.apps.maps");
        //startActivity(mapIntent);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ListFragment", "onViewCreated");
    }
}