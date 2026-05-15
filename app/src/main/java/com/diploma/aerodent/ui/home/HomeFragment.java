package com.diploma.aerodent.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.ui.appointments.AddAppointmentFragment;
import com.diploma.aerodent.ui.appointments.AppointmentDetailFragment;
import com.diploma.aerodent.ui.patients.AddPatientFragment;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Bind UI Elements
        TextView textTotalPatients = root.findViewById(R.id.text_total_patients);
        TextView textTodaysAppts = root.findViewById(R.id.text_todays_appointments);
        TextView textActiveTreatments = root.findViewById(R.id.text_active_treatments);
        TextView textAppointmentsLeft = root.findViewById(R.id.text_appointments_left);

        // Observe Data
        homeViewModel.getTotalPatientsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) textTotalPatients.setText(String.valueOf(count));
        });

        homeViewModel.getTodaysAppointmentsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) textTodaysAppts.setText(String.valueOf(count));
        });

        homeViewModel.getActiveTreatmentsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                textActiveTreatments.setText(String.valueOf(count));
            }
        });

        homeViewModel.getAppointmentsLeftCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                textAppointmentsLeft.setText(String.valueOf(count));
            }
        });

        // Schedule RecyclerView Setup
        RecyclerView recyclerSchedule = root.findViewById(R.id.recycler_home_schedule);
        HomeAppointmentAdapter scheduleAdapter = new HomeAppointmentAdapter();
        scheduleAdapter.setOnAppointmentClickListener(appointment -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, AppointmentDetailFragment.newInstance(appointment.getId()))
                    .addToBackStack(null)
                    .commit();
        });
        recyclerSchedule.setAdapter(scheduleAdapter);

        homeViewModel.getTodaysAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) {
                scheduleAdapter.setAppointments(appointments);
            }
        });

        homeViewModel.getAllPatients().observe(getViewLifecycleOwner(), patients -> {
            if (patients != null) {
                scheduleAdapter.setPatients(patients);
            }
        });

        // Quick Actions
        root.findViewById(R.id.btn_quick_new_patient).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new AddPatientFragment())
                    .addToBackStack(null)
                    .commit();
        });

        root.findViewById(R.id.btn_quick_new_appointment).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new AddAppointmentFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return root;
    }
}
