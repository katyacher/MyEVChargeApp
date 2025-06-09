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

  /*  public LiveData<Station> getStationById(int stationId) {
        MutableLiveData<Station> result = new MutableLiveData<>();
        executor.execute(() -> {
            Station station = repository.getStationById(stationId);
            result.postValue(station);
        });
        return result;
    }*/
    /*public void toggleFavorite(int stationId) {
        repository.toggleFavorite(stationId).observeForever(station -> {
            if (station != null) {
                updateEvent.postValue(new Event<>(true));
                loadFavoriteStations(); // Явно перезагружаем список
            }
        });
    }*/

    public LiveData<Station> getStationById(int stationId) {
        return repository.getStationByIdLive(stationId); // Используем LiveData версию
    }

    public LiveData<Station> toggleFavorite(int stationId) {
        MutableLiveData<Station> result = new MutableLiveData<>();
        repository.toggleFavorite(stationId).observeForever(station -> {
            if (station != null) {
                result.postValue(station);
                updateEvent.postValue(new Event<>(true));
            }
        });
        return result;
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
    private final MutableLiveData<Station> selectedStation = new MutableLiveData<>();

    public void setSelectedStation(Station station) {
        selectedStation.postValue(station);
    }

    public LiveData<Station> getSelectedStation() {
        return selectedStation;
    }
}
