package ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

public class FavoritesFragment extends Fragment implements StationAdapter.OnStationClickListener {
  //  private RecyclerView rvFavorites;
  //  private StationAdapter adapter;
  //  private ImageView ivFavorite;
  //  private boolean isFavorite = false; // Временная переменная для примера
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

    /*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация элементов после создания View
        ivFavorite = view.findViewById(R.id.iv_favorite);

        // Проверка состояния (пример)
        isFavorite = checkFavoriteStatus(); // Ваш метод проверки состояния

        // Установка слушателя
        ivFavorite.setOnClickListener(v -> {
            isFavorite = !isFavorite; // Инвертируем состояние
            updateFavoriteIcon();
        });

        // Первоначальная настройка иконки
        updateFavoriteIcon();
    }

    private boolean checkFavoriteStatus() {
        // Реализуйте проверку состояния избранного (из БД или SharedPreferences)
        return false; // Заглушка
    }

    private void updateFavoriteIcon() {
        int iconRes = isFavorite ?
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline;
        int colorRes = isFavorite ?
                R.color.primary : R.color.gray;

        ivFavorite.setImageResource(iconRes);
        ivFavorite.setColorFilter(ContextCompat.getColor(requireContext(), colorRes));
    }
*/

    @Override
    public void onStationClick(Station station) {
        // Переход к детальной информации о станции
        Bundle args = new Bundle();
        args.putInt("stationId", station.getId());// "station_id"

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
        //Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
        //        station.getLatitude() + "," + station.getLongitude());
        //Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        //mapIntent.setPackage("com.google.android.apps.maps");
        //startActivity(mapIntent);
    }
}