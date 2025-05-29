package ui.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.util.List;
import java.util.Locale;

import models.Session;
import models.SessionManager;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Заполнение данных пользователя
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvPhone = view.findViewById(R.id.tv_phone);

        // История сессий
        RecyclerView rvHistory = view.findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Session> history = SessionManager.getInstance().getSessionHistory();
        // Адаптер для истории

        // Переключение языка
        Switch switchLanguage = view.findViewById(R.id.switch_language);
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Смена языка приложения
            setLocale(isChecked ? "en" : "ru");
        });

        return view;
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        //Перезапуск активности для применения изменений
        Intent refresh = new Intent(getContext(), MainActivity.class);
        startActivity(refresh);
        getActivity().finish();
    }
}
