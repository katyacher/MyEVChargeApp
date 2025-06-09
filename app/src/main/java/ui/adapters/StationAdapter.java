package ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import models.Station;
import ui.viewmodels.StationViewModel;
public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {
    private List<Station> stations;
    private final OnStationClickListener listener;
    private final boolean showDeleteIcon;

    // Убрали ViewModel и LifecycleOwner из адаптера
    public interface OnStationClickListener {
        void onStationClick(Station station);
        void onFavoriteClick(Station station, int position); // Добавили position
        void onDeleteClick(Station station);
        void onRouteClick(Station station);
    }

    public StationAdapter(List<Station> stations, OnStationClickListener listener, boolean showDeleteIcon) {
        this.stations = stations;
        this.listener = listener;
        this.showDeleteIcon = showDeleteIcon;
    }
    // ViewHolder класс
    public static class StationViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationName, tvStationAddress, tvWorkingHours, tvStatus;
        ImageButton btnFavorite;
        View tvRoute;

        public StationViewHolder(View view) {
            super(view);
            tvStationName = view.findViewById(R.id.tv_station_name);
            tvStationAddress = view.findViewById(R.id.tv_station_address);
            tvWorkingHours = view.findViewById(R.id.tv_working_hours);
            //tvStatus = view.findViewById(R.id.tv_status);
            btnFavorite = view.findViewById(R.id.btn_favorite);
            tvRoute = view.findViewById(R.id.tv_nav);//tv_nav
        }
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_station, parent, false);
        return new StationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        Station station = stations.get(position);

        holder.tvStationName.setText(station.getName());
        holder.tvStationAddress.setText(station.getAddress());
        holder.tvWorkingHours.setText(station.getWorkingHours());

        // Установка иконки на основе текущего состояния станции
        holder.btnFavorite.setImageResource(
                showDeleteIcon ? R.drawable.ic_delete :
                        station.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline
        );

        holder.itemView.setOnClickListener(v -> listener.onStationClick(station));

        holder.btnFavorite.setOnClickListener(v -> {
            if (showDeleteIcon) {
                listener.onDeleteClick(station);
            } else {
                listener.onFavoriteClick(station, position); // Передаем позицию

                // Анимация без принудительного обновления
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100)
                        .withEndAction(() ->
                                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                        ).start();
            }
        });
        /*Обработчик клика по кнопке маршрута
        if (holder.tvRoute != null) {
            holder.tvRoute.setOnClickListener(v -> listener.onRouteClick(station));
        }*/
        holder.tvRoute.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRouteClick(station);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    // Остальные методы без изменений
    // ...

    // Новый метод для обновления с DiffUtil
    public void updateStations(List<Station> newStations) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new StationDiffCallback(stations, newStations));
        this.stations = new ArrayList<>(newStations);
        diffResult.dispatchUpdatesTo(this);
    }

    // DiffUtil callback класс
    private static class StationDiffCallback extends DiffUtil.Callback {
        private final List<Station> oldStations, newStations;

        public StationDiffCallback(List<Station> oldStations, List<Station> newStations) {
            this.oldStations = oldStations;
            this.newStations = newStations;
        }

        @Override
        public int getOldListSize() { return oldStations.size(); }

        @Override
        public int getNewListSize() { return newStations.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldStations.get(oldPos).getId() == newStations.get(newPos).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            Station oldStation = oldStations.get(oldPos);
            Station newStation = newStations.get(newPos);
            return oldStation.equals(newStation);
        }
    }
}
