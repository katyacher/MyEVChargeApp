package data.db;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import models.Station;
@Dao
public interface StationDao {
    @Insert
    void insert(Station station);
    @Insert
    void insertAll(List<Station> stations);

    @Update
    void update(Station station);

    @Query("SELECT * FROM stations")
    LiveData<List<Station>> getAllStations();

    @Query("SELECT * FROM stations WHERE id = :id")
    Station getStationById(int id);

    @Query("SELECT * FROM stations WHERE isFavorite = 1")
    LiveData<List<Station>> getFavoriteStations();

    @Transaction
    @Query("UPDATE stations SET isFavorite = :isFavorite WHERE id = :stationId")
    void setFavorite(int stationId, boolean isFavorite);
    @Query("SELECT * FROM stations")
    List<Station> getAllStationsSync(); // Без LiveData для инициализации бд
    @Transaction
    default void toggleFavorite(int stationId) {
        Station station = getStationById(stationId);
        if (station != null) {
            boolean newStatus = !station.isFavorite();
            Log.d("StationDao", "Toggling favorite for station " + stationId +
                    " from " + station.isFavorite() + " to " + newStatus);
            setFavorite(stationId, !station.isFavorite());
        }
    }
    @Query("SELECT isFavorite FROM stations WHERE id = :stationId")
    LiveData<Boolean> isFavorite(int stationId);

    // Добавьте этот метод
    @Query("SELECT * FROM stations WHERE id = :stationId")
    LiveData<Station> getStationByIdLive(int stationId);
}
