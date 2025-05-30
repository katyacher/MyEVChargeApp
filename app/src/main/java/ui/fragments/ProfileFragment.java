package ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private static final String PREF_LANGUAGE = "app_language";
    private static final String DEFAULT_LANGUAGE = "ru";

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
        // TODO: Создать и установить адаптер для истории

        // Переключение языка
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchLanguage = view.findViewById(R.id.switch_language);

        // Восстановление состояния переключателя
        String currentLang = getSavedLanguage();
        switchLanguage.setChecked("en".equals(currentLang));

        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newLang = isChecked ? "en" : "ru";
            saveLanguagePreference(newLang);
            setLocale(newLang);
        });

        return view;
    }

    private String getSavedLanguage() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        return prefs.getString(PREF_LANGUAGE, DEFAULT_LANGUAGE);
    }

    private void saveLanguagePreference(String lang) {
        SharedPreferences.Editor editor = requireContext()
                .getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
                .edit();
        editor.putString(PREF_LANGUAGE, lang);
        editor.apply();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        Context context = requireContext();
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        // Перезапуск активности
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finishAffinity();
    }
}
