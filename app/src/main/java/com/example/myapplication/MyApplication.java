package com.example.myapplication;

import android.app.Application;

import data.db.AppDatabase;
import data.repositories.StationRepository;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Инициализация базы данных через репозиторий
        //StationRepository repository = new StationRepository(this);
        //repository.initializeStations();
        // Просто инициализируем БД - данные добавятся автоматически через callback
        AppDatabase.getInstance(this);
    }
}
