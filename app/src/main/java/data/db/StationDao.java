package data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
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

    @Query("UPDATE stations SET isFavorite = :isFavorite WHERE id = :stationId")
    void setFavorite(int stationId, boolean isFavorite);
    @Query("SELECT * FROM stations")
    List<Station> getAllStationsSync(); // Без LiveData для инициализации бд
}
