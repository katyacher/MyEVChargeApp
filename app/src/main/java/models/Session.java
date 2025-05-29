package models;
import java.util.Date;

public class Session {
    private String id;
    private Station station;
    private Date startTime;
    private Date endTime;
    private double powerConsumed; // кВт·ч
    private double currentPower; // текущая мощность (кВт)
    private double tariff;

    public Session(String id, Station station) {
        this.id = id;
        this.station = station;
        this.startTime = new Date();
        this.tariff = station.getTariff();
    }

    // Расчет продолжительности в минутах
    public long getDuration() {
        if (endTime == null) return 0;
        return (endTime.getTime() - startTime.getTime()) / (60 * 1000);
    }

    // Расчет стоимости
    public double getTotalCost() {
        return powerConsumed * tariff;
    }

    // Завершение сессии
    public void endSession(double powerConsumed) {
        this.endTime = new Date();
        this.powerConsumed = powerConsumed;
    }

    // Геттеры
    public String getId() { return id; }
    public Station getStation() { return station; }
    public double getCurrentPower() { return currentPower; }
    public void setCurrentPower(double power) { currentPower = power; }
    // ... остальные геттеры
}
