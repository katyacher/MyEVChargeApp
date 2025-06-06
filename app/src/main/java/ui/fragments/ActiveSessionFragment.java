package ui.fragments;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

import android.os.Handler;

import data.repositories.StationRepository;
import models.Session;
import models.SessionManager;
import models.Station;

public class ActiveSessionFragment extends Fragment { //extends BottomSheetDialogFragment

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;
    private Session session;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("ActiveSession", "Creating fragment with args: " + getArguments());
        super.onCreate(savedInstanceState);
        // Для демо всегда используем станцию с ID 1
        Station station = StationRepository.getStationById(1);
        if (station == null) {
            Log.e("ActiveSession", "Demo station not found");
           // dismiss();
            return;
        }

        SessionManager sessionManager = SessionManager.getInstance();
        session = sessionManager.startNewSession(station);
        Log.d("ActiveSession", "Session started for station: " + station.getId());

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_active_session, container, false);

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
            // Завершаем сессию
            double powerConsumed = calculatePowerConsumed(); // powerConsumed = 15.7 (кВт*ч)
            session.endSession(powerConsumed);
            SessionManager.getInstance().stopCurrentSession(powerConsumed);// 15.7 кВт*ч
            //dismiss(); // Закрываем bottom sheet
            // Переходим на фрагмент деталей станции
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

            // Создаем Bundle с ID станции
            Bundle args = new Bundle();
            args.putInt("stationId", session.getStation().getId());

            // Выполняем переход
            navController.navigate(R.id.stationDetailsFragment, args);
        });

        return view;
    }


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
