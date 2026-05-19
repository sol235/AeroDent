package com.diploma.aerodent.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.diploma.aerodent.R;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.card_user_data).setOnClickListener(
                v -> Toast.makeText(getContext(), R.string.settings_user_data, Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.card_payments).setOnClickListener(
                v -> Toast.makeText(getContext(), R.string.settings_payments, Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.card_reports).setOnClickListener(
                v -> Toast.makeText(getContext(), R.string.settings_reports, Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.card_nzok).setOnClickListener(
                v -> Toast.makeText(getContext(), R.string.settings_nzok, Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btn_logout).setOnClickListener(
                v -> Toast.makeText(getContext(), R.string.settings_logout, Toast.LENGTH_SHORT).show());
    }
}
