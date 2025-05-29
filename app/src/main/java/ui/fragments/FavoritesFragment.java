package ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import data.repositories.StationRepository;
import models.Station;
import ui.adapters.StationAdapter;

public class FavoritesFragment extends Fragment {
    private RecyclerView rvFavorites;
    private StationAdapter adapter;
    private ImageView ivFavorite;
    private boolean isFavorite = false; // Временная переменная для примера

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Надуваем макет фрагмента
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        rvFavorites = view.findViewById(R.id.rv_favorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        // Получите данные избранных станций (пример)
        List<Station> favoriteStations = getFavoriteStations();

        adapter = new StationAdapter(favoriteStations);
        rvFavorites.setAdapter(adapter);
        return view;
    }
    private List<Station> getFavoriteStations() {
        // Реализуйте получение избранных станций из БД или другого источника
        return StationRepository.getFavoriteStations();
    }

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
}