package data.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import models.Station;

@Database(entities = {Station.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StationDao stationDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "ev_charge.db")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    //super.onCreate(db);
                                    // Запускаем инициализацию в фоновом потоке
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        //StationDao dao = getInstance(context).stationDao();
                                        //if (dao.getAllStationsSync().isEmpty()) {
                                        //    dao.insertAll(createInitialStations());
                                       // }
                                        // Вставляем начальные данные напрямую через SQL
                                        String sql = "INSERT INTO stations (id, name, status, address, workingHours, " +
                                                "latitude, longitude, locationDescription, power, tariff, isFavorite) " +
                                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                                        // Станция 1
                                        db.execSQL(sql, new Object[]{
                                                1,
                                                "BRYANSKAYA4",
                                                "free",
                                                "Красноярск, ул. Брянская, 4",
                                                "Круглосуточно",
                                                56.020215,
                                                92.875344,
                                                "Заправка Лукойл",
                                                50.0,
                                                5.5,
                                                0
                                        });

                                        // Станция 2
                                        db.execSQL(sql, new Object[]{
                                                2,
                                                "MYRA55",
                                                "Свободно",
                                                "Красноярск, ул. Мира, 55",
                                                "Круглосуточно",
                                                56.011812,
                                                92.872829,
                                                "Парковка торгового центра",
                                                50.0,
                                                5.5,
                                                0
                                        });

                                        // Станция 3
                                        db.execSQL(sql, new Object[]{
                                                3,
                                                "PLANETA77",
                                                "Свободно",
                                                "Красноярск, пр. 9 Мая, 77",
                                                "Круглосуточно",
                                                56.0509171,
                                                92.9044525,
                                                "Парковка торгового центра 'Планета'",
                                                50.0,
                                                5.5,
                                                0
                                        });

                                        // Станция 4
                                        db.execSQL(sql, new Object[]{
                                                4,
                                                "PROFF64",
                                                "Не в сети",
                                                "Красноярск, ул.Профсоюзов, 64",
                                                "8:00-22:00",
                                                56.015243,
                                                92.837723,
                                                "Заправка",
                                                50.0,
                                                5.5,
                                                0
                                        });

                                        // Станция 5
                                        db.execSQL(sql, new Object[]{
                                                5,
                                                "MICHURINA2",
                                                "Занято",
                                                "Красноярск, ул. Мичурина, 2Г",
                                                "8:00-22:00",
                                                56.013326,
                                                92.959363,
                                                "Заправка",
                                                50.0,
                                                5.5,
                                                0
                                        });
                                    });
                                }
                            }).addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Добавляем новый столбец isFavorite с значением по умолчанию false
            database.execSQL("ALTER TABLE stations ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0");
        }
    };
}