package models;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "stations")
public class Station {
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name = "workingHours")
    private String workingHours;
    @ColumnInfo(name = "latitude")
    private double latitude;
    @ColumnInfo(name = "longitude")
    private double longitude;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "isFavorite", defaultValue = "false")
    private boolean isFavorite; // Добавлено поле для избранного // определяется через таблицу favorites
    @ColumnInfo(name = "locationDescription")
    private String locationDescription; // Для подробного описания
    @ColumnInfo(name = "power")
    private double power; // Мощность станции (кВт)
    @ColumnInfo(name = "tariff")
    private double tariff; // Тариф (руб/кВт·ч)

    // Конструктор
    public Station(int id, String name, String status, String address,
                   String workingHours, double latitude, double longitude, String locationDescription,
                   double power, double tariff,  boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.address = address;
        this.workingHours = workingHours;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationDescription = locationDescription;
        this.power = power;
        this.tariff = tariff;
        this.isFavorite = isFavorite; // По умолчанию не в избранном
    }

    // Геттеры для всех полей
    public int getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getAddress() { return address; }
    public String getWorkingHours() { return workingHours; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public String getLocationDescription() { return locationDescription; }
    public double getPower() { return power; }
    public double getTariff() { return tariff; }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}


