package com.diploma.aerodent.ui.user;

import com.diploma.aerodent.AeroDentApplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.model.DentalSpecialty;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.appbar.MaterialToolbar;

public class SetupAdminFragment extends Fragment {

    private AuthViewModel viewModel;

    private MaterialToolbar toolbar;
    private TextInputEditText nameEditText;
    private TextInputLayout roleInputLayout;
    private TextInputEditText pinEditText;
    private TextInputEditText pinConfirmEditText;
    private TextInputEditText uinEditText;
    private AutoCompleteTextView specialtyAutoComplete;
    private TextInputEditText rziEditText;
    private Button saveButton;


    private DentalSpecialty selectedSpecialty = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_admin, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        nameEditText = view.findViewById(R.id.nameEditText);
        roleInputLayout = view.findViewById(R.id.roleInputLayout);
        pinEditText = view.findViewById(R.id.pinEditText);
        pinConfirmEditText = view.findViewById(R.id.pinConfirmEditText);
        uinEditText = view.findViewById(R.id.uinEditText);
        specialtyAutoComplete = view.findViewById(R.id.specialtyAutoComplete);
        rziEditText = view.findViewById(R.id.rziEditText);
        saveButton = view.findViewById(R.id.saveButton);

        setupSpecialtyDropdown();
        setupMode();
        
        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(AuthViewModel.class);
        
        viewModel.getActionComplete().observe(getViewLifecycleOwner(), complete -> {
            if (complete != null && complete) {
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = 
                    requireActivity().findViewById(R.id.bottomNavigationView);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                }
            }
        });
        
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        saveButton.setOnClickListener(v -> saveUser());

        return view;
    }

    private void setupMode() {
        toolbar.setTitle(R.string.app_name);
        roleInputLayout.setVisibility(View.GONE);
    }

    private void setupSpecialtyDropdown() {
        DentalSpecialty[] specialties = DentalSpecialty.values();
        String[] specialtyNames = new String[specialties.length];
        for (int i = 0; i < specialties.length; i++) {
            specialtyNames[i] = getString(specialties[i].getDisplayName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_custom, specialtyNames);
        specialtyAutoComplete.setAdapter(adapter);

        specialtyAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedSpecialty = specialties[position];
        });
    }

    private void saveUser() {
        String name = String.valueOf(nameEditText.getText()).trim();
        String pin = String.valueOf(pinEditText.getText());
        String pinConfirm = String.valueOf(pinConfirmEditText.getText());
        String uin = String.valueOf(uinEditText.getText()).trim();
        String rzi = String.valueOf(rziEditText.getText()).trim();

        viewModel.createInitialAdmin(name, pin, pinConfirm, uin, selectedSpecialty, rzi);
    }
}
