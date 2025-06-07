package data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    void insert(FavoriteStation favorite);

    @Delete
    void delete(FavoriteStation favorite);

    @Query("DELETE FROM favorites WHERE stationId = :stationId")
    void deleteByStationId(int stationId);

    @Query("SELECT COUNT(*) FROM favorites WHERE stationId = :stationId")
    int isFavorite(int stationId);

    @Query("SELECT * FROM favorites")
    List<FavoriteStation> getAllFavorites();

}