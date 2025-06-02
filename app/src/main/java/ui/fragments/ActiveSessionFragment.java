package ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import models.Session;
import models.SessionManager;

public class ActiveSessionFragment extends BottomSheetDialogFragment {

    private Handler handler = new Handler();
    private Runnable updateRunnable;
    private Session session;
    private BottomSheetBehavior<View> bottomSheetBehavior;

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

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация BottomSheetBehavior
        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Настройка параметров BottomSheet
        bottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height));
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Добавление слушателя изменений состояния
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED:
                        // Полностью раскрыт
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // Свернут до peekHeight
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        // Полностью скрыт
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        // Пользователь тянет
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        // Анимация завершается
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Обработка анимации скольжения
            }
        });

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
            double powerConsumed = calculatePowerConsumed();
            session.endSession(powerConsumed);
            SessionManager.getInstance().stopCurrentSession(powerConsumed);
            dismiss(); // Закрываем фрагмент
        });
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

    // Методы для управления BottomSheet из других частей фрагмента
    public void expandBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void collapseBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void hideBottomSheet() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}