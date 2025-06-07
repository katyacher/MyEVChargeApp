package data.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import models.Station;
//c одним пользователем
@Entity(tableName = "favorites",
        foreignKeys = @ForeignKey(entity = Station.class,
                parentColumns = "id",
                childColumns = "stationId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("stationId")})
public class FavoriteStation {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "stationId")
    private final int stationId;

    public FavoriteStation(int stationId) {
        this.stationId = stationId;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStationId() { return stationId; }
}

