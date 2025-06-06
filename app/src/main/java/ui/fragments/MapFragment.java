package ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.R;

public class MapFragment extends Fragment {
    private ImageView mapPlaceholder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapPlaceholder = view.findViewById(R.id.map_placeholder);
        mapPlaceholder.setOnClickListener(v -> {
            // Для демо открываем станцию с ID 1
            showStationDetails(1);
        });

        return view;
    }

    private void showStationDetails(int stationId) {
        try {
            NavController navController = Navigation.findNavController(requireView());
            Bundle args = new Bundle();
            args.putInt("stationId", stationId);
            navController.navigate(R.id.action_map_to_details, args);
        } catch (Exception e) {
            Log.e("MapFragment", "Navigation error", e);
            Toast.makeText(requireContext(), "Ошибка открытия деталей станции", Toast.LENGTH_SHORT).show();
        }
    }
}
