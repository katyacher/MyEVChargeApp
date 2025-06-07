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

    @Update
    void update(Station station);

    @Query("SELECT * FROM stations")
    LiveData<List<Station>> getAllStations(); // Возвращаем LiveData

    @Query("SELECT * FROM stations WHERE id = :id")
    Station getStationById(int id);

    @Query("SELECT s.* FROM stations s INNER JOIN favorites f ON s.id = f.stationId")
    LiveData<List<Station>> getFavoriteStations(); // Возвращаем LiveData
    @Query("SELECT * FROM stations")
    List<Station> getAllStationsSync(); // Без LiveData для инициализации бд
}
