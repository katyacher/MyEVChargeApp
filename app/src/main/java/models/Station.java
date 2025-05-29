package models;
public class Station {
    private int id;
    private String name;
    private String address;
    private String workingHours;
    private double latitude;
    private double longitude;
    private String status;
    private boolean isFavorite; // Добавлено поле для избранного
    private String locationDescription; // Для подробного описания
    private double power; // Мощность станции (кВт)
    private double tariff; // Тариф (руб/кВт·ч)

    // Конструктор
    public Station(int id, String name, String status, String address,
                   String workingHours, String locationDescription,
                   double power, double tariff) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.address = address;
        this.workingHours = workingHours;
        this.locationDescription = locationDescription;
        this.power = power;
        this.tariff = tariff;
        this.isFavorite = false; // По умолчанию не в избранном
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


/*
public enum Status {
        FREE, BUSY, OFFLINE
    }

    private Status status;

    // Геттер/сеттер для enum
    public Status getStatus() {
        return status;
    }

    public String getStatusString() {
        switch(status) {
            case FREE: return "Свободно";
            case BUSY: return "Занято";
            case OFFLINE: return "Не в сети";
            default: return "";
        }
    }
 */