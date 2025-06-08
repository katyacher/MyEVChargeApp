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
    }
    public void setFavorite(int stationId, boolean isFavorite) {
        executor.execute(() -> {
            stationDao.setFavorite(stationId, isFavorite);
        });
    }


    /*public void initializeStations() {
        executor.execute(() -> {
            // Получаем список станций напрямую (не LiveData)
            List<Station> stations = stationDao.getAllStationsSync();
            if (stations == null || stations.isEmpty()) {
                List<Station> initialStations = createInitialStations();
                for (Station station : initialStations) {
                    stationDao.insert(station);
                }
            }
        });
    }
    // Метод для создания начальных данных
    private static  List<Station> createInitialStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(
                1,
                "BRYANSKAYA4",
                "free",
                "Красноярск, ул. Брянская, 4",
                "Круглосуточно",
                56.020215, 92.875344,
                "Заправка Лукойл",
                50.0, // мощность 50 кВт
                5.5, // тариф 5.5 руб/кВт·ч
                false // isFavorite по умолчанию
        ));

        stations.add(new Station(
                2,
                "MYRA55",
                "Свободно",
                "Красноярск, ул. Мира, 55",
                "Круглосуточно",
                56.011812, 92.872829,
                "Парковка торгового центра",
                50.0, // мощность 50 кВт
                5.5, // тариф 5.5 руб/кВт·ч
                false // isFavorite по умолчанию
        ));

        stations.add(new Station(
                3,
                "PLANETA77",
                "Свободно",
                "Красноярск, пр. 9 Мая, 77",
                "Круглосуточно",
                56.0509171, 92.9044525,
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5, // тариф 5.5 руб/кВт·ч
                false // isFavorite по умолчанию
        ));

        stations.add(new Station(
                4,
                "PROFF64",
                "Не в сети",
                "Красноярск, ул.Профсоюзов, 64",
                "8:00-22:00",
                56.015243, 92.837723,
                "Заправка",
                50.0, // мощность 50 кВт
                5.5, // тариф 5.5 руб/кВт·ч
                false // isFavorite по умолчанию
        ));

        stations.add(new Station(
                5,
                "MICHURINA2",
                "Занято",
                "Красноярск, ул. Мичурина, 2Г",
                "8:00-22:00",
                56.013326, 92.959363,
                "Заправка",
                50.0, // мощность 50 кВт
                5.5, // тариф 5.5 руб/кВт·ч
                false // isFavorite по умолчанию
        ));
        return stations;
    }*/

}

