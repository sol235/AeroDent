package com.diploma.aerodent.ui.patients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Patient;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PatientDetailFragment extends Fragment {

    private static final String ARG_PATIENT_ID = "patient_id";
    private PatientDetailViewModel viewModel;
    private HistoryTimelineAdapter historyAdapter;
    private SimpleDateFormat dobFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public static PatientDetailFragment newInstance(int patientId) {
        PatientDetailFragment fragment = new PatientDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_detail, container, false);

        int patientId = -1;
        if (getArguments() != null) {
            patientId = getArguments().getInt(ARG_PATIENT_ID);
        }

        viewModel = new ViewModelProvider(this).get(PatientDetailViewModel.class);
        viewModel.setPatientId(patientId);

        setupToolbar(root);
        setupRecyclerView(root);
        observeViewModel(root);

        return root;
    }

    private void setupToolbar(View root) {
        root.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        root.findViewById(R.id.btn_edit_patient).setOnClickListener(v -> {
            int patientId = -1;
            if (getArguments() != null) {
                patientId = getArguments().getInt(ARG_PATIENT_ID);
            }
            if (patientId != -1) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, AddPatientFragment.newInstance(patientId))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setupRecyclerView(View root) {
        RecyclerView recyclerHistory = root.findViewById(R.id.recycler_history);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryTimelineAdapter();
        recyclerHistory.setAdapter(historyAdapter);
    }

    private void observeViewModel(View root) {
        TextView textNameHeader = root.findViewById(R.id.text_patient_name_header);
        TextView textNameProfile = root.findViewById(R.id.text_patient_name_profile);
        TextView textInitials = root.findViewById(R.id.text_avatar_initials);
        TextView textIds = root.findViewById(R.id.text_patient_ids);
        TextView textDob = root.findViewById(R.id.text_patient_dob);
        TextView textPhone = root.findViewById(R.id.text_phone);
        TextView textEmail = root.findViewById(R.id.text_email);

        viewModel.getPatient().observe(getViewLifecycleOwner(), patient -> {
            if (patient != null) {
                String fullName = patient.getFirstName() + " " + patient.getLastName();
                textNameHeader.setText(fullName);
                textNameProfile.setText(fullName);

                String initials = "";
                if (patient.getFirstName() != null && !patient.getFirstName().isEmpty()) {
                    initials += patient.getFirstName().substring(0, 1);
                }
                if (patient.getLastName() != null && !patient.getLastName().isEmpty()) {
                    initials += patient.getLastName().substring(0, 1);
                }
                textInitials.setText(initials.toUpperCase());

                String egn = patient.getEgn() != null ? patient.getEgn() : "---";
                String nhif = patient.getNhifNumber() != null ? patient.getNhifNumber() : "---";
                textIds.setText(getString(R.string.patient_details_egn, egn) + " • " + getString(R.string.patient_details_nhif, nhif));

                if (patient.getDateOfBirth() != null) {
                    textDob.setText(getString(R.string.patient_details_dob, dobFormat.format(patient.getDateOfBirth())));
                    textDob.setVisibility(View.VISIBLE);
                } else {
                    textDob.setVisibility(View.GONE);
                }

                textPhone.setText(patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "---");
                textEmail.setText(patient.getEmail() != null ? patient.getEmail() : "---");
            }
        });

        viewModel.getHistoryItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                historyAdapter.setItems(items);
            }
        });
    }
}
