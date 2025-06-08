package data.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;// для прототипа только один пользователь
import java.util.concurrent.Executors;//для многопользовательского приложения

import data.db.AppDatabase;
import data.db.FavoriteStation;
import data.db.StationDao;
import models.Station;

public class StationRepository {
    private StationDao stationDao;
    private Executor executor;

    public StationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        stationDao = db.stationDao();
        executor = Executors.newSingleThreadExecutor();
        // Проверяем инициализацию (опционально)
        executor.execute(() -> {
            List<Station> stations = stationDao.getAllStationsSync();
            Log.d("StationRepo", "Stations count: " + stations.size());
        });
    }

    public LiveData<List<Station>> getAllStations() {
        return stationDao.getAllStations(); // возвращает LiveData
    }

    public LiveData<List<Station>> getFavoriteStations() {
        return stationDao.getFavoriteStations();  // возвращает LiveData
    }

    public Station getStationById(int id) {
        return stationDao.getStationById(id);
    }
    public LiveData<Station> toggleFavorite(int stationId) {
        MutableLiveData<Station> result = new MutableLiveData<>();
        executor.execute(() -> {
            // Получаем станцию перед изменением
            Station station = stationDao.getStationById(stationId);
            if (station != null) {
                boolean newStatus = !station.isFavorite();
                stationDao.setFavorite(stationId, newStatus);

                // Явно получаем обновленную станцию
                Station updatedStation = stationDao.getStationById(stationId);
                result.postValue(updatedStation);

                Log.d("StationRepository", "Toggled favorite for station " + stationId +
                        " to " + newStatus);
            }
        });
        return result;
    }
    /*
    public  LiveData<Station> toggleFavorite(int stationId) {
        MutableLiveData<Station> result = new MutableLiveData<>();
        executor.execute(() -> {
            Station station = stationDao.getStationById(stationId);
            if (station != null) {
                boolean newFavoriteStatus = !station.isFavorite();
                stationDao.setFavorite(stationId, newFavoriteStatus);
                station.setFavorite(newFavoriteStatus);
                result.postValue(station);
                Log.d("StationRepository", "Toggled favorite for station " + stationId +
                        " to " + newFavoriteStatus);
            }
        });
        return result;
    } */
    public void setFavorite(int stationId, boolean isFavorite) {
        executor.execute(() -> {
            stationDao.setFavorite(stationId, isFavorite);
        });
    }
    public  LiveData<Station> getStationByIdLive(int stationId) {
        return stationDao.getStationByIdLive(stationId);  // возвращает LiveData
    }
}

