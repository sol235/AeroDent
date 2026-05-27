package com.diploma.aerodent.ui.patients;

import com.diploma.aerodent.AeroDentApplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;

public class PatientsFragment extends Fragment {

    private PatientViewModel patientViewModel;
    private PatientListAdapter adapter;
    private RecyclerView recyclerPatients;
    private EditText editSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patients, container, false);

        recyclerPatients = root.findViewById(R.id.recycler_patients);
        adapter = new PatientListAdapter();
        recyclerPatients.setAdapter(adapter);

        adapter.setOnPatientClickListener(patient -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, PatientDetailFragment.newInstance(patient.getId()))
                    .addToBackStack(null)
                    .commit();
        });

        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        patientViewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(PatientViewModel.class);
        patientViewModel.getSearchResults().observe(getViewLifecycleOwner(), patients -> {
            if (patients != null) {
                adapter.setPatients(patients);
            }
        });

        editSearch = root.findViewById(R.id.edit_search);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (patientViewModel != null) {
                    patientViewModel.setSearchQuery(s != null ? s.toString() : "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        root.findViewById(R.id.fab_add_patient).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new AddPatientFragment())
                    .addToBackStack(null)
                    .commit();
        });

        root.findViewById(R.id.icon_add_patient_top).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new AddPatientFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerPatients = null;
        editSearch = null;
    }
}
