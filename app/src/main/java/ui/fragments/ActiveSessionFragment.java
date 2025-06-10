package ui.fragments;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

import android.os.Handler;

import models.Session;
import models.SessionManager;
import models.Station;
import ui.viewmodels.StationViewModel;

public class ActiveSessionFragment extends BaseStationFragment {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;
    private Session session;
    private StationViewModel viewModel;
    private ImageButton btnFavorite;
    private int stationId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StationViewModel.class);

        // Получаем ID станции из аргументов или используем дефолтный (1)
        stationId = getArguments() != null ? getArguments().getInt("stationId", 1) : 1;

        // Получаем станцию через ViewModel
        viewModel.getStationById(stationId).observe(this, station -> {
            if (station != null) {
                session = SessionManager.getInstance().startNewSession(station);
                Log.d("ActiveSession", "Session started for station: " + station.getId());
            } else {
                Log.e("ActiveSession", "Station not found");
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация UI
        TextView tvStationName = view.findViewById(R.id.tv_station_name);
        Button btnStop = view.findViewById(R.id.btn_stop);
        btnFavorite = view.findViewById(R.id.btn_favorite);

        // Устанавливаем данные станции, когда они будут доступны
        viewModel.getStationById(stationId).observe(getViewLifecycleOwner(), station -> {
            if (station != null) {
                tvStationName.setText(station.getName());
                setupFavoriteButton(station.getId());
            }
        });

        // Добавляем кнопку "Маршрут"
        View tvNav = view.findViewById(R.id.tv_nav);
        tvNav.setOnClickListener(v -> {
            if (session != null) {
                onRouteClick(session.getStation()); // Используем станцию из сессии
            }
        });

        // Запуск обновления UI
        startSessionUpdates(view);

        // Обработчик остановки зарядки
        btnStop.setOnClickListener(v -> stopChargingAndNavigate());
    }

    private void setupFavoriteButton(int stationId) {
        // Подписываемся на изменения статуса избранного
        viewModel.isFavorite(stationId).observe(getViewLifecycleOwner(), isFavorite -> {
            btnFavorite.setImageResource(isFavorite ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
            btnFavorite.setContentDescription(isFavorite ?
                    "Удалить из избранного" : "Добавить в избранное");
        });

        // Обработчик клика по кнопке избранного
        btnFavorite.setOnClickListener(v -> {
            viewModel.toggleFavorite(stationId); //(session.getStation().getId())

            // Анимация кнопки
            v.animate()
                    .scaleX(0.8f).scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    ).start();
        });
    }

    private void startSessionUpdates(View view) {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (session != null) {
                    updateSessionUI(view);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateRunnable);
    }

    private void stopChargingAndNavigate() {
        if (session != null) {
            double powerConsumed = calculatePowerConsumed();
            session.endSession(powerConsumed);
            SessionManager.getInstance().stopCurrentSession(powerConsumed);

            // Переход к деталям станции
            NavController navController = Navigation.findNavController(requireView());
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);
            navController.navigate(R.id.stationDetailsFragment, args);
        }
    }

    private double calculatePowerConsumed() {
        // Примерная логика расчета
        return session != null ? 15.7 * (session.getDurationMinutes() / 60.0) : 0;
    }

    private void updateSessionUI(View view) {
        TextView tvDuration = view.findViewById(R.id.tv_duration);
        TextView tvCurrentPower = view.findViewById(R.id.tv_current_power);
        TextView tvPowerConsumed = view.findViewById(R.id.tv_power_consumed);
        TextView tvTotalCost = view.findViewById(R.id.tv_total_cost);

        double currentPower = 30 + Math.random() * 20;
        double powerConsumed = currentPower * session.getDurationMinutes() / 60.0;

        tvDuration.setText(session.getFormattedDuration());
        tvCurrentPower.setText(String.format("%.1f кВт", currentPower));
        tvPowerConsumed.setText(String.format("%.1f кВт·ч", powerConsumed));
        tvTotalCost.setText(String.format("%.1f руб", powerConsumed * session.getTariff()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateRunnable);
    }

    @Override
    public void onStationClick(Station station) {

    }

    @Override
    public void onFavoriteClick(Station station, int position) {

    }

    @Override
    public void onDeleteClick(Station station) {

    }
}
