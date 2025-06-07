package ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

import models.Station;
import ui.viewmodels.StationViewModel;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {
    private List<Station> stations;
    private final OnStationClickListener listener;
    private final boolean showDeleteIcon; // Флаг для отображения корзины вместо сердечка
    private final LifecycleOwner lifecycleOwner; // Добавляем LifecycleOwner
    private StationViewModel viewModel; // Добавляем ViewModel
    public interface OnStationClickListener {
        void onStationClick(Station station);
        void onFavoriteClick(Station station);
        void onDeleteClick(Station station);
        void onRouteClick(Station station);
    }

    // Конструктор с флагом showDeleteIcon
    public StationAdapter(List<Station> stations, OnStationClickListener listener, boolean showDeleteIcon,  LifecycleOwner lifecycleOwner,StationViewModel viewModel) {
        this.stations = stations;
        this.listener = listener;
        this.showDeleteIcon = showDeleteIcon;
        this.lifecycleOwner = lifecycleOwner;
        this.viewModel = viewModel;
    }

    // ViewHolder класс
    public static class StationViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationName, tvStationAddress, tvWorkingHours, tvStatus;
        ImageButton btnFavorite;
        View btnRoute;

        public StationViewHolder(View view) {
            super(view);
            tvStationName = view.findViewById(R.id.tv_station_name);
            tvStationAddress = view.findViewById(R.id.tv_station_address);
            tvWorkingHours = view.findViewById(R.id.tv_working_hours);
            //tvStatus = view.findViewById(R.id.tv_status);
            btnFavorite = view.findViewById(R.id.btn_favorite);
           //btnRoute = view.findViewById(R.id.btn_route);//tv_nav
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

        // Установка данных станции
        holder.tvStationName.setText(station.getName());
        holder.tvStationAddress.setText(station.getAddress());
        holder.tvWorkingHours.setText(station.getWorkingHours());
        //holder.tvStatus.setText(station.getStatus());

        // Установка иконки в зависимости от режима (избранное/удаление)
        if (showDeleteIcon) {
            // Режим избранного - показываем иконку удаления
            holder.btnFavorite.setImageResource(R.drawable.ic_delete);
            //  holder.btnFavorite.setOnClickListener(v -> listener.onDeleteClick(station));
        } else {
            // Запрашиваем статус избранного через ViewModel
            // Режим общего списка - показываем сердечко  // Устанавливаем начальное состояние сердечка
            viewModel.isFavorite(station.getId()).observe(lifecycleOwner, isFavorite -> {
                holder.btnFavorite.setImageResource(isFavorite ?
                        R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
            });
           // holder.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(station));
        }

        // Обработчик клика по всей карточке
        holder.itemView.setOnClickListener(v -> listener.onStationClick(station));

        // Обработчик клика по кнопке избранного/удаления
        holder.btnFavorite.setOnClickListener(v -> {
            if (showDeleteIcon) {
                listener.onDeleteClick(station);
            } else {
                listener.onFavoriteClick(station);
                // Анимация и мгновенное обновление
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100)
                        .withEndAction(() -> {
                            v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                            // Принудительно обновляем текущую позицию
                            notifyItemChanged(position);
                        }).start();
            }
        });
        // Обработчик клика по кнопке маршрута
        if (holder.btnRoute != null) {
            holder.btnRoute.setOnClickListener(v -> listener.onRouteClick(station));
        }
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    // Метод для обновления списка станций
    public void updateStations(List<Station> newStations) {
        this.stations = newStations;
        notifyDataSetChanged();
    }
}
/*package ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

import models.Station;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {
    private List<Station> stations;
    private final OnStationClickListener listener;
    private final boolean showDeleteIcon; // Флаг для отображения корзины вместо сердечка

    public interface OnStationClickListener {
        void onStationClick(Station station);
        void onFavoriteClick(Station station);
        void onDeleteClick(Station station);
        void onRouteClick(Station station);
    }

    // Конструктор с флагом showDeleteIcon
    public StationAdapter(List<Station> stations, OnStationClickListener listener, boolean showDeleteIcon) {
        this.stations = stations;
        this.listener = listener;
        this.showDeleteIcon = showDeleteIcon;
    }

    // ViewHolder класс
    public static class StationViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationName, tvStationAddress, tvWorkingHours, tvStatus;
        ImageButton btnFavorite;
        View btnRoute;

        public StationViewHolder(View view) {
            super(view);
            tvStationName = view.findViewById(R.id.tv_station_name);
            tvStationAddress = view.findViewById(R.id.tv_station_address);
            tvWorkingHours = view.findViewById(R.id.tv_working_hours);
            tvStatus = view.findViewById(R.id.tv_status);
            btnFavorite = view.findViewById(R.id.btn_favorite);
             btnRoute = view.findViewById(R.id.btn_route);
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

        // Установка данных станции
        holder.tvStationName.setText(station.getName());
        holder.tvStationAddress.setText(station.getAddress());
        holder.tvWorkingHours.setText(station.getWorkingHours());
        holder.tvStatus.setText(station.getStatus());

        // Установка иконки в зависимости от режима (избранное/удаление)
        if (showDeleteIcon) {
            holder.btnFavorite.setImageResource(R.drawable.ic_delete);
        } else {
            holder.btnFavorite.setImageResource(station.isFavorite() ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline);
        }

        // Обработчик клика по всей карточке
        holder.itemView.setOnClickListener(v -> listener.onStationClick(station));

        // Обработчик клика по кнопке избранного/удаления
        holder.btnFavorite.setOnClickListener(v -> {
            if (showDeleteIcon) {
                listener.onDeleteClick(station);
            } else {
                listener.onFavoriteClick(station);
                // Анимация при нажатии
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).withEndAction(() ->
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100)).start();
            }
        });

        // Обработчик клика по кнопке маршрута
        if (holder.btnRoute != null) {
            holder.btnRoute.setOnClickListener(v -> listener.onRouteClick(station));
        }
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    // Метод для обновления списка станций
    public void updateStations(List<Station> newStations) {
        this.stations = newStations;
        notifyDataSetChanged();
    }
}

*/