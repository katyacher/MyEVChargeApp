package ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import models.Station;
import ui.viewmodels.StationViewModel;
public class StationDetailsFragment extends BaseStationFragment {
    private int stationId;
    private StationViewModel viewModel;
    private OnStartChargingListener listener;

    @Override
    public void onStationClick(Station station) {

    }

    @Override
    public void onFavoriteClick(Station station, int position) {

    }

    @Override
    public void onDeleteClick(Station station) {

    }

    public interface OnStartChargingListener {
        void onStartCharging(int stationId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем stationId из аргументов
        if (getArguments() != null) {
            stationId = getArguments().getInt("stationId");
        }

        viewModel = new ViewModelProvider(requireActivity()).get(StationViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnStartChargingListener) {
            listener = (OnStartChargingListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnStartChargingListener");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация UI элементов
        TextView tvName = view.findViewById(R.id.tv_station_name);
        TextView tvAddress = view.findViewById(R.id.tv_station_address);
        TextView tvStatus = view.findViewById(R.id.tv_status);
        TextView tvPower = view.findViewById(R.id.tv_power);
        TextView tvTariff = view.findViewById(R.id.tv_tariff);
        TextView tvLocation = view.findViewById(R.id.tv_location);
        Button btnStartCharging = view.findViewById(R.id.btn_start_charging);
        ImageButton btnFavorite = view.findViewById(R.id.btn_favorite);

        // Подписываемся на данные станции
        viewModel.getStationById(stationId).observe(getViewLifecycleOwner(), station -> {
            if (station == null) {
                Toast.makeText(requireContext(), "Станция не найдена", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
                return;
            }

            // Заполнение данных станции
            tvName.setText(station.getName());
            tvAddress.setText(station.getAddress());
            tvStatus.setText(station.getStatus());
            tvPower.setText(String.format("%.1f кВт", station.getPower()));
            tvTariff.setText(String.format("%.1f руб/кВт·ч", station.getTariff()));
            tvLocation.setText(station.getLocationDescription());

            // Настройка статуса
            setupStatusView(tvStatus, station.getStatus());

            // Настройка кнопки зарядки
            setupChargingButton(btnStartCharging, station.getStatus());

            // Настройка кнопки избранного
            setupFavoriteButton(btnFavorite, station.getId());
        });

        // Обработчик клика по карте
        view.findViewById(R.id.map_container).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);
            Navigation.findNavController(v).navigate(R.id.mapFragment, args);
        });
        // Добавляем кнопку "Маршрут"
        View tvNav = view.findViewById(R.id.tv_nav);
        tvNav.setOnClickListener(v -> {
            viewModel.getStationById(stationId).observe(getViewLifecycleOwner(), station -> {
                if (station != null) {
                    onRouteClick(station); // Вызов метода из BaseFragment
                }
            });
        });
    }

    private void setupStatusView(TextView tvStatus, String status) {
        switch (status.toLowerCase()) {
            case "free":
            case "свободно":
                tvStatus.setBackgroundResource(R.drawable.status_background_free);
                break;
            case "busy":
            case "занято":
                tvStatus.setBackgroundResource(R.drawable.status_background_busy);
                break;
            case "offline":
            case "не в сети":
                tvStatus.setBackgroundResource(R.drawable.status_background_offline);
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupChargingButton(Button btnStartCharging, String status) {
        boolean isAvailable = status.equalsIgnoreCase("free")
                || status.equalsIgnoreCase("свободно");
        btnStartCharging.setEnabled(isAvailable);
        btnStartCharging.setAlpha(isAvailable ? 1f : 0.5f);

        btnStartCharging.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStartCharging(stationId);
            }
        });

        btnStartCharging.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            }
            return false;
        });
    }

    private void setupFavoriteButton(ImageButton btnFavorite, int stationId) {
        // Подписываемся на изменения  станции
        viewModel.getStationById(stationId).observe(getViewLifecycleOwner(), station -> {
            if (station != null) {
                btnFavorite.setImageResource(station.isFavorite()
                        ? R.drawable.ic_favorite_filled
                        : R.drawable.ic_favorite_outline);
            }
        });

        btnFavorite.setOnClickListener(v -> {
            viewModel.toggleFavorite(stationId).observe(getViewLifecycleOwner(), updatedStation -> {
                if (updatedStation != null) {
                    // Анимация и мгновенное обновление
                    btnFavorite.setImageResource(updatedStation.isFavorite()
                            ? R.drawable.ic_favorite_filled
                            : R.drawable.ic_favorite_outline);

                    v.animate()
                            .scaleX(0.8f).scaleY(0.8f)
                            .setDuration(100)
                            .withEndAction(() ->
                                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                            ).start();
                }
            });

            /* Анимация кнопки
            v.animate()
                    .scaleX(0.8f).scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    ).start();*/
        });
    }
}

