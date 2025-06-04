package ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;
import java.util.Random;

import models.Station;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    public interface OnStationClickListener {
        void onStationClick(Station station);
        void onFavoriteClick(Station station);
        void onRouteClick(Station station);
    }

    private List<Station> stations;
    private final OnStationClickListener listener;

    public StationAdapter(List<Station> stations, OnStationClickListener listener) {
        this.stations = stations;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateStations(List<Station> newStations) {
        this.stations = newStations;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Station station = stations.get(position);
        if (station == null) {
            Log.w("StationAdapter", "Station at position " + position + " is null");
            return;
        }
        //  привязка данных
        holder.tvStationName.setText(station.getName());
        holder.tvStationAddress.setText(station.getAddress());
        holder.tvWorkingHours.setText(station.getWorkingHours());
        // Проверка статуса
        /*String status = station.getStatus();
        if (status == null) {
            status = "Неизвестно";
            Log.w("StationAdapter", "Null status for station: " + station.getName());
        }
        holder.tvStatus.setText(status);*/

        // Установка иконки избранного
        int favoriteIcon = station.isFavorite() ?
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline;
        holder.btnFavorite.setImageResource(favoriteIcon);


        // Обработчики кликов

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onStationClick(station);
        });
        // Обработчики кликов с проверками
        if (holder.btnFavorite != null) {
            holder.btnFavorite.setOnClickListener(v -> {
                if (listener != null) listener.onFavoriteClick(station);
            });
        }

        if (holder.tvNav != null) {  // если используется
            holder.tvNav.setOnClickListener(v -> {
                if (listener != null) listener.onRouteClick(station);
            });
        }

        // установка случайного статуса
        //String[] statuses = {"Свободно", "Занято", "Не в сети"};
        //String randomStatus = statuses[new Random().nextInt(3)];
        //holder.tvStatus.setText(randomStatus);


       /*
        holder.btnRoute.setOnClickListener(v -> {
            // Обработка клика на кнопке маршрута
            // station.getLatitude()/getLongitude()
        });

        // Обработчики кликов
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onStationClick(station);
        });
        // Обработчик клика на кнопке избранного
        holder.btnFavorite.setOnClickListener(v -> {
            if (listener != null) listener.onFavoriteClick(station);
            // Логика добавления/удаления из избранного
        });
        // Для анимации переключения избранного:
        holder.btnFavorite.setOnClickListener(v -> {
            boolean isFavorite = !station.isFavorite();
            station.setFavorite(isFavorite);

            int iconRes = isFavorite ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline;

            holder.btnFavorite.setImageResource(iconRes);
            notifyItemChanged(position);
        });   */
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View tvNav;
        TextView tvStationName, tvStationAddress, tvWorkingHours; // tvStatus
        ImageButton btnFavorite;
        //Button btnRoute;

        public ViewHolder(View view) {
            super(view);
            // Находим все View
            tvStationName = view.findViewById(R.id.tv_station_name);
            tvStationAddress = view.findViewById(R.id.tv_station_address);
            tvWorkingHours = view.findViewById(R.id.tv_working_hours);
            btnFavorite = view.findViewById(R.id.btn_favorite);
            //tvStatus = view.findViewById(R.id.tv_status); // нет в item_station.xml
            // btnRoute = view.findViewById(R.id.btn_route); // для реализации карты
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_station, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }
}