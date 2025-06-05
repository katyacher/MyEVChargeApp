package ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvPhone = view.findViewById(R.id.tv_phone);

        RecyclerView rvHistory = view.findViewById(R.id.rv_history);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        SwitchCompat switchLanguage = view.findViewById(R.id.switch_language);
        String currentLang = getSavedLanguage();
        switchLanguage.setChecked("en".equals(currentLang));

        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String newLang = isChecked ? "en" : "ru";
            saveLanguagePreference(newLang);
            applyLocale(newLang);
        });
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

    private void applyLocale(String lang) {
        /*Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        Context context = requireContext().createConfigurationContext(config);
        context.getResources();*/

        saveLanguagePreference(lang);
        if (getActivity() != null) {
            MainActivity.restartActivity(getActivity());
        }
    }

    private void restartApp() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finishAffinity();
        }

        // Принудительный системный выход для полного обновления
        Runtime.getRuntime().exit(0);
    }
}