package com.diploma.aerodent.ui.appointments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.ui.home.HomeAppointmentAdapter;
import com.diploma.aerodent.ui.home.HomeViewModel;

public class SelectAppointmentDialogFragment extends DialogFragment {

    public interface OnAppointmentSelectedListener {
        void onAppointmentSelected(Appointment appointment);
    }

    private OnAppointmentSelectedListener listener;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;

    public void setOnAppointmentSelectedListener(OnAppointmentSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_select_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        recyclerView = view.findViewById(R.id.recycler_select_appointment);
        HomeAppointmentAdapter adapter = new HomeAppointmentAdapter();
        adapter.setOnAppointmentClickListener(appointment -> {
            if (listener != null) {
                listener.onAppointmentSelected(appointment);
            }
            dismiss();
        });
        recyclerView.setAdapter(adapter);

        homeViewModel.getTodaysAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) {
                adapter.setAppointments(appointments);
            }
        });

        homeViewModel.getAllPatients().observe(getViewLifecycleOwner(), patients -> {
            if (patients != null) {
                adapter.setPatients(patients);
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
    }
}
