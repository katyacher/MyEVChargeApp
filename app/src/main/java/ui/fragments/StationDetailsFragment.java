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
public class StationDetailsFragment extends Fragment {
    private int stationId;
    private StationViewModel viewModel;
    private OnStartChargingListener listener;

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
        // Подписываемся на изменения статуса избранного
        viewModel.isFavorite(stationId).observe(getViewLifecycleOwner(), isFavorite -> {
            btnFavorite.setImageResource(isFavorite ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
            btnFavorite.setContentDescription(isFavorite ?
                    "Удалить из избранного" : "Добавить в избранное");
        });

        btnFavorite.setOnClickListener(v -> {
            viewModel.toggleFavorite(stationId);

            // Анимация кнопки
            v.animate()
                    .scaleX(0.8f).scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    ).start();
        });
    }
}

/*public class StationDetailsFragment extends Fragment {

    private int stationId;
    private Station station;
    private StationViewModel viewModel;
    private OnStartChargingListener listener;

    public interface OnStartChargingListener {
        void onStartCharging(int stationId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(StationViewModel.class);
        /*
        if (getArguments() != null) {
            stationId = getArguments().getInt("stationId");

            station = StationRepository.getStationById(stationId);
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

        if (station == null) {
            return view;
        }

        // Инициализация UI элементов
        TextView tvName = view.findViewById(R.id.tv_station_name);
        TextView tvAddress = view.findViewById(R.id.tv_station_address);
        TextView tvStatus = view.findViewById(R.id.tv_status);
        TextView tvPower = view.findViewById(R.id.tv_power);
        TextView tvTariff = view.findViewById(R.id.tv_tariff);
        TextView tvLocation = view.findViewById(R.id.tv_location);
        Button btnStartCharging = view.findViewById(R.id.btn_start_charging);
        ImageButton btnFavorite = view.findViewById(R.id.btn_favorite); // Добавляем кнопку избранного

        // Заполнение данных станции
        tvName.setText(station.getName());
        tvAddress.setText(station.getAddress());
        tvStatus.setText(station.getStatus());
        tvPower.setText(String.format("%.1f кВт", station.getPower()));
        tvTariff.setText(String.format("%.1f руб/кВт·ч", station.getTariff()));
        tvLocation.setText(station.getLocationDescription());

        // Настройка статуса
        setupStatusView(tvStatus);

        // Настройка доступности кнопки зарядки
        setupChargingButton(btnStartCharging);

        // Настройка кнопки избранного
        setupFavoriteButton(btnFavorite);

        // Обработчик клика по карте
        view.findViewById(R.id.map_container).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.mapFragment);
        });

        return view;
    }

    private void setupStatusView(TextView tvStatus) {
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
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupChargingButton(Button btnStartCharging) {
        boolean isAvailable = station.getStatus().equalsIgnoreCase("free")
                || station.getStatus().equalsIgnoreCase("свободно");
        btnStartCharging.setEnabled(isAvailable);
        btnStartCharging.setAlpha(isAvailable ? 1f : 0.5f);

        btnStartCharging.setOnClickListener(v -> {
            try {
                if (listener != null) {
                    listener.onStartCharging(stationId);
                }
            } catch (Exception e) {
                Log.e("StationDetails", "Start charging error", e);
                Toast.makeText(requireContext(), "Ошибка запуска зарядки", Toast.LENGTH_SHORT).show();
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

    private void setupFavoriteButton(ImageButton btnFavorite) {
        // Подписываемся на изменения статуса избранного
        viewModel.isFavorite(stationId).observe(getViewLifecycleOwner(), isFavorite -> {
            btnFavorite.setImageResource(isFavorite ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
        });

        btnFavorite.setOnClickListener(v -> {
            viewModel.toggleFavorite(stationId);
            Toast.makeText(requireContext(),
                    Boolean.TRUE.equals(viewModel.isFavorite(stationId).getValue()) ?
                            "Добавлено в избранное" : "Удалено из избранного",
                    Toast.LENGTH_SHORT).show();

            // Анимация кнопки
            v.animate()
                    .scaleX(0.8f).scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction(() ->
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
        });
    }
}
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
*/
   // @Override
  /*  public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            // Добавляем транзакцию в back stack
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.addToBackStack(null);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.e("StationDetails", "Can't show dialog", e);
        }
    }*/
/*
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
*/