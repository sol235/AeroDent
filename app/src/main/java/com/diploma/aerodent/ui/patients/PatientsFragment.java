package com.diploma.aerodent.ui.patients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;

public class PatientsFragment extends Fragment {

    private PatientViewModel patientViewModel;
    private PatientListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patients, container, false);

        RecyclerView recyclerPatients = root.findViewById(R.id.recycler_patients);
        adapter = new PatientListAdapter();
        recyclerPatients.setAdapter(adapter);

        adapter.setOnPatientClickListener(patient -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, PatientDetailFragment.newInstance(patient.getId()))
                    .addToBackStack(null)
                    .commit();
        });

        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);
        patientViewModel.getAllPatients().observe(getViewLifecycleOwner(), patients -> {
            if (patients != null) {
                adapter.setPatients(patients);
            }
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
}
