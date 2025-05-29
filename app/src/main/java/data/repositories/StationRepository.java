package data.repositories;

import java.util.ArrayList;
import java.util.List;

import models.Station;

public class StationRepository {
    private static final List<Station> stations = new ArrayList<>();


    static {
        stations.add(new Station(
                1,
                "BRYANSKAYA4",
                "free",
                "Красноярск, ул. Брянская, 4",
                "Круглосуточно",
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));

        stations.add(new Station(
                2,
                "MYRA55",
                "Свободно",
                "Красноярск, ул. Мира, 55",
                "Круглосуточно",
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));

        stations.add(new Station(
                3,
                "PLANETA77",
                "free",
                "Красноярск, пр. 9 Мая, 77",
                "Круглосуточно",
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));

        stations.add(new Station(
                4,
                "PROFF64",
                "offline",
                "Красноярск, ул.Профсоюзов, 64",
                "8:00-22:00",
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));

        stations.add(new Station(
                5,
                "MICHURINA2",
                "Занято",
                "Красноярск, ул. Мичурина, 2Г",
                "8:00-22:00",
                "Парковка торгового центра 'Планета'",
                50.0, // мощность 50 кВт
                5.5 // тариф 5.5 руб/кВт·ч
        ));
    }

    public static List<Station> getAllStations() {
        return new ArrayList<>(stations);
    }

    public static List<Station> getFavoriteStations() {
        List<Station> favorites = new ArrayList<>();
        for (Station station : stations) {
            if (station.isFavorite()) {
                favorites.add(station);
            }
        }
        return favorites;
    }

    public static void toggleFavorite(int stationId) {
        for (Station station : stations) {
            if (station.getId() == stationId) {
                station.setFavorite(!station.isFavorite());
                break;
            }
        }
    }
}

