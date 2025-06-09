package com.example.myapplication;

import android.app.Application;
import android.preference.PreferenceManager;

import org.osmdroid.config.Configuration;

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

        // Инициализация OSMDroid
        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        Configuration.getInstance().setUserAgentValue(getPackageName());
    }
    @Override
    public void onTerminate() {
        // Сохраняем настройки OSMDroid перед завершением приложения
        Configuration.getInstance().save(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        super.onTerminate();
    }
    @Override
    public void onTrimMemory(int level) {
        // Сохраняем настройки OSMDroid перед завершением приложения для современных версий Android
        if (level == TRIM_MEMORY_COMPLETE) {
            Configuration.getInstance().save(
                    getApplicationContext(),
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
            );
        }
        super.onTrimMemory(level);
    }
}
