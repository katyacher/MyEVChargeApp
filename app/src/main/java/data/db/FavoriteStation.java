package data.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class FavoriteStation {
    @PrimaryKey
    public String id;
    public String name;
    public String address;
}

