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
import com.diploma.aerodent.ui.appointments.SelectAppointmentDialogFragment;
import com.diploma.aerodent.ui.patients.AddPatientFragment;
import com.diploma.aerodent.ui.patients.PatientsFragment;
import com.diploma.aerodent.ui.calendar.CalendarFragment;
import com.diploma.aerodent.ui.photos.PhotoViewModel;
import com.diploma.aerodent.util.CameraHelper;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private PhotoViewModel photoViewModel;
    private CameraHelper cameraHelper;
    
    private TextView textTotalPatients, textTodaysAppts, textActiveTreatments, textAppointmentsLeft;
    private RecyclerView recyclerSchedule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        photoViewModel = new ViewModelProvider(requireActivity()).get(PhotoViewModel.class);
        cameraHelper = new CameraHelper(this, photoViewModel);
        cameraHelper.setShowSuccessToast(true);

        if (savedInstanceState != null) {
            cameraHelper.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        cameraHelper.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Bind UI Elements
        textTotalPatients = root.findViewById(R.id.text_total_patients);
        textTodaysAppts = root.findViewById(R.id.text_todays_appointments);
        textActiveTreatments = root.findViewById(R.id.text_active_treatments);
        textAppointmentsLeft = root.findViewById(R.id.text_appointments_left);
        recyclerSchedule = root.findViewById(R.id.recycler_home_schedule);

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
        root.findViewById(R.id.card_total_patients).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new PatientsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        root.findViewById(R.id.card_todays_appointments).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new CalendarFragment())
                    .addToBackStack(null)
                    .commit();
        });

        root.findViewById(R.id.card_appointments_left).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new CalendarFragment())
                    .addToBackStack(null)
                    .commit();
        });

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

        root.findViewById(R.id.btn_quick_take_photo).setOnClickListener(v -> showAppointmentSelection());

        return root;
    }

    private void showAppointmentSelection() {
        SelectAppointmentDialogFragment dialog = new SelectAppointmentDialogFragment();
        dialog.setOnAppointmentSelectedListener(appointment -> {
            cameraHelper.takePhoto(appointment.getPatientId(), appointment.getId(), null);
        });
        dialog.show(getParentFragmentManager(), "SelectAppointmentDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textTotalPatients = null;
        textTodaysAppts = null;
        textActiveTreatments = null;
        textAppointmentsLeft = null;
        recyclerSchedule = null;
    }
}
