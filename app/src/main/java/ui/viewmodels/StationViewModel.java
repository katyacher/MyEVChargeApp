package ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import data.repositories.StationRepository;
import models.Station;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import data.repositories.StationRepository;
import models.Station;
import utils.Event;

public class StationViewModel extends AndroidViewModel {
    private final StationRepository repository;
    private final Executor executor;
    private final LiveData<List<Station>> allStations;
   // private final LiveData<List<Station>> favoriteStations;
   private final MutableLiveData<List<Station>> favoriteStations = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> updateEvent = new MutableLiveData<>();

    public StationViewModel(Application application) {
        super(application);
        repository = new StationRepository(application);
        executor = Executors.newSingleThreadExecutor();
        allStations = repository.getAllStations();
        //favoriteStations = repository.getFavoriteStations();
        loadFavoriteStations();
    }
    public void loadFavoriteStations() {
        repository.getFavoriteStations().observeForever(stations -> {
            favoriteStations.postValue(stations);
        });
    }

    public LiveData<List<Station>> getAllStations() {
        return allStations;
    }

    public LiveData<List<Station>> getFavoriteStations() {
        return favoriteStations;
    }

    public LiveData<Station> getStationById(int stationId) {
        MutableLiveData<Station> result = new MutableLiveData<>();
        executor.execute(() -> {
            Station station = repository.getStationById(stationId);
            result.postValue(station);
        });
        return result;
    }
    public void toggleFavorite(int stationId) {
        repository.toggleFavorite(stationId).observeForever(station -> {
            if (station != null) {
                updateEvent.postValue(new Event<>(true));
                loadFavoriteStations(); // Явно перезагружаем список
            }
        });
    }

    /*public LiveData<Station> toggleFavorite(int stationId) {
        //return repository.toggleFavorite(stationId);
        MutableLiveData<Station> result = new MutableLiveData<>();
        executor.execute(() -> {
            Station station = repository.getStationById(stationId);
            if (station != null) {
                boolean newStatus = !station.isFavorite();
                repository.setFavorite(stationId, newStatus);
                station.setFavorite(newStatus);
                result.postValue(station);
                updateEvent.postValue(new Event<>(true));
                Log.d("StationViewModel", "Toggled favorite for station " + stationId + " to " + newStatus);
            }
        });
        return result;
    }
*/

    public LiveData<Boolean> isFavorite(int stationId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        executor.execute(() -> {
            Station station = repository.getStationById(stationId);
            result.postValue(station != null && station.isFavorite());
        });
        return result;
    }

    public LiveData<Event<Boolean>> getUpdateEvent() {
        return updateEvent;
    }
}
 /*public void toggleFavorite(int stationId) {
        executor.execute(() -> {
            // Получаем текущее состояние станции
            Station station = repository.getStationById(stationId);
            if (station != null) {
                // Инвертируем статус избранного
                boolean newFavoriteStatus = !station.isFavorite();
                repository.setFavorite(stationId, newFavoriteStatus);

                // Можно добавить логирование
                // Log.d("StationViewModel", "Toggled favorite for station " + stationId + " to " + newFavoriteStatus);
            }
        });
    }*/
/*public class StationViewModel extends AndroidViewModel {
    private final StationRepository repository;
    private final LiveData<List<Station>> allStations;
    private final LiveData<List<Station>> favoriteStations;


    public StationViewModel(Application application) {
        super(application);
        repository = new StationRepository(application);
        allStations = repository.getAllStations();
        favoriteStations = repository.getFavoriteStations();
        //repository.initializeStations(); // Инициализация начальных данных
    }

    public LiveData<List<Station>> getAllStations() {
        return allStations;
    }

    public LiveData<List<Station>> getFavoriteStations() {
        return favoriteStations;
    }

    public Station getStationById(int id) {
        return repository.getStationById(id);
    }
    public LiveData<Station> getStationById(int stationId) {
        MutableLiveData<Station> result = new MutableLiveData<>();
        executor.execute(() -> {
            Station station = repository.getStationById(stationId);
            result.postValue(station);
        });
        return result;
    }

    public void toggleFavorite(int stationId) {
        repository.toggleFavorite(stationId);
    }

    public LiveData<Boolean> isFavorite(int stationId) {
        return repository.isFavorite(stationId);
    }

}*/