package ui.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import data.repositories.StationRepository;
import models.Station;

public class StationViewModel extends AndroidViewModel {
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

    public void toggleFavorite(int stationId) {
        repository.toggleFavorite(stationId);
    }

    public LiveData<Boolean> isFavorite(int stationId) {
        return repository.isFavorite(stationId);
    }

}