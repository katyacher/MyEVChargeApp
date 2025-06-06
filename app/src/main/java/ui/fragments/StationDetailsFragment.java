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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import data.repositories.StationRepository;
import models.Station;
public class StationDetailsFragment extends Fragment {

    private int stationId;
    private Station station;
    private OnStartChargingListener listener;
    public interface OnStartChargingListener {
        void onStartCharging(int stationId);
    }

   // @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            // Добавляем транзакцию в back stack
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.addToBackStack(null);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.e("StationDetails", "Can't show dialog", e);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("StationDetails", "Fragment created with stationId: " + stationId);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationId = getArguments().getInt("stationId");
            station = StationRepository.getStationById(stationId);
        } else {
            //dismiss(); // Закрываем, если нет аргументов
        }
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_details, container, false);
        // Обработчик клика по карте
        view.findViewById(R.id.map_container).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.mapFragment);
        });

        // Проверка на null станции
        if (station == null) {
            //dismiss(); // Закрываем bottom sheet
            return view; // Возвращаем пустое view
        }

        // Заполнение данных станции
        TextView tvName = view.findViewById(R.id.tv_station_name);
        TextView tvAddress = view.findViewById(R.id.tv_station_address);
        TextView tvStatus = view.findViewById(R.id.tv_status);
        TextView tvPower = view.findViewById(R.id.tv_power);
        TextView tvTariff = view.findViewById(R.id.tv_tariff);
        TextView tvLocation = view.findViewById(R.id.tv_location);
        // Инициализация UI элементов
        Button btnStartCharging = view.findViewById(R.id.btn_start_charging);

        tvName.setText(station.getName());
        tvAddress.setText(station.getAddress());
        tvStatus.setText(station.getStatus());
        tvPower.setText(String.format("%.1f кВт", station.getPower()));
        tvTariff.setText(String.format("%.1f руб/кВт·ч", station.getTariff()));
        tvLocation.setText(station.getLocationDescription());

        // Настройка цвета статуса в зависимости от состояния
        switch (station.getStatus().toLowerCase()) {
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
        // Проверка доступности станции для зарядки
        boolean isAvailable = station.getStatus().equalsIgnoreCase("free")
                || station.getStatus().equalsIgnoreCase("свободно");
        btnStartCharging.setEnabled(isAvailable);
        btnStartCharging.setAlpha(isAvailable ? 1f : 0.5f); //визуальный эффект, если станция не доступна для зарядки

        // Кнопка "Начать зарядку"
        btnStartCharging.setOnClickListener(v -> {
            try {
                if (listener != null) {
                    listener.onStartCharging(stationId);
                } else {
                    Log.e("StationDetails", "Listener is null");
                }
                //dismiss();
            } catch (Exception e) {
                Log.e("StationDetails", "Start charging error", e);
                Toast.makeText(requireContext(), "Ошибка запуска зарядки", Toast.LENGTH_SHORT).show();
            }
        });
        //Анимация при нажатии кнопки
        btnStartCharging.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            }
            return false;
        });

        return view;
    }




}
