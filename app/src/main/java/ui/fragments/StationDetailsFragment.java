package ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import data.repositories.StationRepository;
import models.SessionManager;
import models.Station;

public class StationDetailsFragment extends BottomSheetDialogFragment {

    private int stationId;
    private Station station;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationId = getArguments().getInt("stationId");
            station = StationRepository.getStationById(stationId);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_details, container, false);

        // Заполнение данных станции
        TextView tvName = view.findViewById(R.id.tv_station_name);
        TextView tvAddress = view.findViewById(R.id.tv_station_address);
        TextView tvStatus = view.findViewById(R.id.tv_status);
        TextView tvPower = view.findViewById(R.id.tv_power);
        TextView tvTariff = view.findViewById(R.id.tv_tariff);
        TextView tvDescription = view.findViewById(R.id.tv_description);
        Button btnStartCharging = view.findViewById(R.id.btn_start_charging);

        tvName.setText(station.getName());
        tvAddress.setText(station.getAddress());
        tvStatus.setText(station.getStatus());
        tvPower.setText(String.format("%.1f кВт", station.getPower()));
        tvTariff.setText(String.format("%.1f руб/кВт·ч", station.getTariff()));
        tvDescription.setText(station.getLocationDescription());

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

        // Кнопка "Начать зарядку"
        btnStartCharging.setOnClickListener(v -> {
            SessionManager.getInstance().startSession(station);
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);
            Navigation.findNavController(v).navigate(R.id.action_details_to_session, args);
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
