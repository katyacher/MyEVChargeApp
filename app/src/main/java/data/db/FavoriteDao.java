package data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    List<FavoriteStation> getAll();

    @Insert
    void insert(FavoriteStation station);

    @Delete
    void delete(FavoriteStation station);
}