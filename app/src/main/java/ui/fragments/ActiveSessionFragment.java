package ui.fragments;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.os.Handler;

import data.repositories.StationRepository;
import models.Session;
import models.SessionManager;
import models.Station;

public class ActiveSessionFragment extends BottomSheetDialogFragment {

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;
    private Session session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/* (getArguments() != null) {
            int stationId = getArguments().getInt("stationId");
            // Здесь можно получить данные станции, если нужно
        }*/

        // Инициализация сессии (для демо-режима)
        SessionManager sessionManager = SessionManager.getInstance();
        session = sessionManager.getCurrentSession();
        if (session == null && getArguments() != null) {
            int stationId = getArguments().getInt("stationId");
            Station station = StationRepository.getStationById(stationId);
            if (station != null) {
                session = sessionManager.startNewSession(station);
            }
        }

        // Если все равно нет сессии (например, нет аргументов), создаем демо-сессию
        if (session == null) {
            Station demoStation = StationRepository.getStationById(1); // Первая станция
            session = sessionManager.startNewSession(demoStation);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);

       // if (getArguments() != null) {
       //    int stationId = getArguments().getInt("stationId");
            // Загрузите данные станции по ID
       // }
        View view = inflater.inflate(R.layout.fragment_active_session, container, false);
       /* session = SessionManager.getInstance().getCurrentSession();

        if (session == null) {
            dismiss(); // Закрываем фрагмент, если сессии нет
            return view;
        }

        // Инициализация UI
        TextView tvStationName = view.findViewById(R.id.tv_station_name);
        TextView tvSessionId = view.findViewById(R.id.tv_session_id);
        TextView tvDuration = view.findViewById(R.id.tv_duration);
        TextView tvCurrentPower = view.findViewById(R.id.tv_current_power);
        TextView tvPowerConsumed = view.findViewById(R.id.tv_power_consumed);
        TextView tvTariff = view.findViewById(R.id.tv_tariff);
        TextView tvTotalCost = view.findViewById(R.id.tv_total_cost);
        Button btnStop = view.findViewById(R.id.btn_stop);

        // Установка статических данных
        tvStationName.setText(session.getStation().getName());
        tvSessionId.setText("Номер сессии: " + session.getId());
        tvTariff.setText(String.format("%.1f руб/кВт·ч", session.getTariff()));
*/
        // Инициализация UI
        TextView tvStationName = view.findViewById(R.id.tv_station_name);
        Button btnStop = view.findViewById(R.id.btn_stop);

        tvStationName.setText(session.getStation().getName());
        // Обновление данных каждую секунду
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                //updateSessionUI(tvDuration, tvCurrentPower, tvPowerConsumed, tvTotalCost);
                updateSessionUI(view);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateRunnable);

        // Остановка зарядки
        btnStop.setOnClickListener(v -> {
            //  здесь будет запрос к API
            double powerConsumed = calculatePowerConsumed(); // powerConsumed = 15.7 (кВт*ч)
            session.endSession(powerConsumed);
            SessionManager.getInstance().stopCurrentSession(powerConsumed);// 15.7 кВт*ч
            dismiss(); // Закрываем фрагмент
        });

        return view;
    }

   /* private void updateSessionUI(TextView tvDuration, TextView tvCurrentPower,
                                 TextView tvPowerConsumed, TextView tvTotalCost) {
        // Обновление длительности
        tvDuration.setText(session.getFormattedDuration());

        // В демо-режиме: случайные значения
        double currentPower = 30 + Math.random() * 20; // 30-50 кВт
        double powerConsumed = currentPower * session.getDurationMinutes() / 60.0;

        tvCurrentPower.setText(String.format("%.1f кВт", currentPower));
        tvPowerConsumed.setText(String.format("%.1f кВт·ч", powerConsumed));
        tvTotalCost.setText(String.format("%.1f руб", powerConsumed * session.getTariff()));
    }
*/
    private double calculatePowerConsumed() {
        // логика расчета
        return 15.7; // Пример
    }

   private void updateSessionUI(View view) {
       TextView tvDuration = view.findViewById(R.id.tv_duration);
       TextView tvCurrentPower = view.findViewById(R.id.tv_current_power);
       TextView tvPowerConsumed = view.findViewById(R.id.tv_power_consumed);
       TextView tvTotalCost = view.findViewById(R.id.tv_total_cost);

       // Демо-данные
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
}
