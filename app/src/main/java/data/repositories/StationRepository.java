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
import data.db.FavoriteDao;
import data.db.StationDao;
import models.Station;

public class StationRepository {
    private StationDao stationDao;
    private FavoriteDao favoriteDao;
    private Executor executor;


    public StationRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        stationDao = db.stationDao();
        favoriteDao = db.favoriteDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Station>> getAllStations() {
        return  stationDao.getAllStations(); // возвращает LiveData
    }

    public LiveData<List<Station>> getFavoriteStations() {
        return stationDao.getFavoriteStations();  // возвращает LiveData
    }

    public Station getStationById(int id) {
        return stationDao.getStationById(id);
    }

    public void toggleFavorite(int stationId) {
        executor.execute(() -> {
            boolean isFavorite = favoriteDao.isFavorite(stationId) > 0;
            int count = favoriteDao.isFavorite(stationId);
            Log.d("StationRepository", "Checking favorite for stationId=" + stationId + ", count=" + count);
            if (isFavorite) {
                favoriteDao.deleteByStationId(stationId);
                Log.d("StationRepository", "Removed from favorites: " + stationId);
            } else {
                favoriteDao.insert(new FavoriteStation(stationId));
                Log.d("StationRepository", "Added to favorites: " + stationId);
            }
            // Принудительно обновляем данные
            stationDao.getAllStations(); // Для общего списка
            stationDao.getFavoriteStations(); // Для избранного
        });
    }

    public LiveData<Boolean> isFavorite(int stationId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        executor.execute(() -> {
            int count = favoriteDao.isFavorite(stationId);
            result.postValue(count > 0);
        });
        return result;
    }

    public void initializeStations() {
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

    private List<Station> createInitialStations() {
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
                5.5 // тариф 5.5 руб/кВт·ч
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
                5.5 // тариф 5.5 руб/кВт·ч
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
                5.5 // тариф 5.5 руб/кВт·ч
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
                5.5 // тариф 5.5 руб/кВт·ч
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
                5.5 // тариф 5.5 руб/кВт·ч
        ));
        return stations;
    }
    public void logAllFavorites() {
        executor.execute(() -> {
            List<FavoriteStation> favorites = favoriteDao.getAllFavorites();
            Log.d("StationRepository", "All favorites in DB: " + favorites);
        });
    }
   /* private static final List<Station> stations = new ArrayList<>();

    static {
        stations.add(new Station(
                1,
                "BRYANSKAYA4",
                "free",
                "Красноярск, ул. Брянская, 4",
                "Круглосуточно",
                56.012345, 92.987654,
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));

        stations.add(new Station(
                2,
                "MYRA55",
                "Свободно",
                "Красноярск, ул. Мира, 55",
                "Круглосуточно",
                56.012345, 92.987654,
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));

        stations.add(new Station(
                3,
                "PLANETA77",
                "Свободно",
                "Красноярск, пр. 9 Мая, 77",
                "Круглосуточно",
                56.012345, 92.987654,
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));

        stations.add(new Station(
                4,
                "PROFF64",
                "Не в сети",
                "Красноярск, ул.Профсоюзов, 64",
                "8:00-22:00",
                56.012345, 92.987654,
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));

        stations.add(new Station(
                5,
                "MICHURINA2",
                "Занято",
                "Красноярск, ул. Мичурина, 2Г",
                "8:00-22:00",
                56.012345, 92.987654,
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));
    }

    public static List<Station> getAllStations() {
        return new ArrayList<>(stations);
    }

    public static List<Station> getFavoriteStations() {
        List<Station> favorites = new ArrayList<>();
        for (Station station : stations) {
            if (station.isFavorite()) {
                favorites.add(station);
            }
        }
        return favorites;
    }
    public static Station getStationById(int id) {
        for (Station station : stations) {
            if (station.getId() == id) {
                return station;
            }
        }
        return null;
    }
    public static void toggleFavorite(int stationId) {
        for (Station station : stations) {
            if (station.getId() == stationId) {
                station.setFavorite(!station.isFavorite());
                break;
            }
        }
    }

    */
}

