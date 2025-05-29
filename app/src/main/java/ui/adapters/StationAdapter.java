package ui.adapters;

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

    private final List<Station> stations;
    private final OnStationClickListener listener;

    public StationAdapter(List<Station> stations, OnStationClickListener listener) {
        this.stations = stations;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Station station = stations.get(position);
        // Пример привязки данных
        holder.tvStationName.setText(station.getName());
        holder.tvStationAddress.setText(station.getAddress());
        holder.tvStatus.setText(station.getStatus());
        holder.tvWorkingHours.setText(station.getWorkingHours());

        // Установка иконки избранного
        int favoriteIcon = station.isFavorite() ?
                R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline;
        holder.btnFavorite.setImageResource(favoriteIcon);


        // Обработчики кликов
        holder.itemView.setOnClickListener(v -> {
            listener.onStationClick(station);
        });

        holder.btnFavorite.setOnClickListener(v -> {
            listener.onFavoriteClick(station);
        });

        holder.tvNav.setOnClickListener(v -> {
            listener.onRouteClick(station);
        });

        // установка случайного статуса
        String[] statuses = {"Свободно", "Занято", "Не в сети"};
        String randomStatus = statuses[new Random().nextInt(3)];
        holder.tvStatus.setText(randomStatus);

        // Привязка данных
       /* holder.tvName.setText(station.getName());
        holder.tvStatus.setText(station.getStatus());

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
        TextView tvStationName, tvStationAddress, tvWorkingHours, tvStatus;
        ImageButton btnFavorite;
        //Button btnRoute;

        public ViewHolder(View view) {
            super(view);
            //tvStatus = view.findViewById(R.id.tv_station_status);
           // btnRoute = view.findViewById(R.id.btn_route);
            tvStationName = itemView.findViewById(R.id.tv_station_name);
            tvStationAddress = itemView.findViewById(R.id.tv_station_address);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            tvWorkingHours =  itemView.findViewById(R.id.tv_working_hours);
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