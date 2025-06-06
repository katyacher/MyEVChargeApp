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
    public long getDurationMinutes() {
        Date end = endTime != null ? endTime : new Date();
        return (end.getTime() - startTime.getTime()) / (60 * 1000);
        /*if (endTime == null) return 0;
        return (endTime.getTime() - startTime.getTime()) / (60 * 1000);*/
    }
    // Форматированная продолжительность (чч:мм)
    public String getFormattedDuration() {
        long minutes = getDurationMinutes();
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
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
    public Date getStartTime(){ return startTime; }
    public Date getEndTime(){ return endTime; }
    public double getCurrentPower() { return currentPower; }
    public double getTariff(){ return tariff; }
    public double getPowerConsumed() { return powerConsumed; }
    public void setCurrentPower(double power) { currentPower = power; }

}
