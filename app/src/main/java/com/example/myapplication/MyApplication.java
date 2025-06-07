package com.example.myapplication;

import android.app.Application;
import data.repositories.StationRepository;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Инициализация базы данных
        StationRepository repository = new StationRepository(this);
        repository.initializeStations();
    }
}
