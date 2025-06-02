package ui.fragments;

import android.os.Bundle;
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

import models.Session;
import models.SessionManager;

public class ActiveSessionFragment extends BottomSheetDialogFragment {

    private Handler handler = new Handler();
    private Runnable updateRunnable;
    private Session session;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_session, container, false);
        session = SessionManager.getInstance().getCurrentSession();

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

        // Обновление данных каждую секунду
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateSessionUI(tvDuration, tvCurrentPower, tvPowerConsumed, tvTotalCost);
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

    private void updateSessionUI(TextView tvDuration, TextView tvCurrentPower,
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

    private double calculatePowerConsumed() {
        // логика расчета
        return 15.7; // Пример
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateRunnable);
    }
}
