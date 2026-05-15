package com.diploma.aerodent.ui.appointments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.ui.patients.PatientViewModel;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppointmentDetailFragment extends Fragment {

    private static final String ARG_APPOINTMENT_ID = "appointment_id";

    private AppointmentViewModel viewModel;
    private PatientViewModel patientViewModel;
    private int appointmentId;
    private Appointment currentAppointment;

    private TextView textPatientName;
    private TextView textDate;
    private TextView textTime;
    private TextView textTreatment;
    private TextView textStatus;
    private MaterialCardView cardStatus;
    private TextView textNotes;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static AppointmentDetailFragment newInstance(int appointmentId) {
        AppointmentDetailFragment fragment = new AppointmentDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_APPOINTMENT_ID, appointmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointmentId = getArguments().getInt(ARG_APPOINTMENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        initViews(view);
        observeData();
    }

    private void initViews(View view) {
        textPatientName = view.findViewById(R.id.text_patient_name);
        textDate = view.findViewById(R.id.text_date);
        textTime = view.findViewById(R.id.text_time);
        textTreatment = view.findViewById(R.id.text_treatment);
        textStatus = view.findViewById(R.id.text_status);
        cardStatus = view.findViewById(R.id.card_status);
        textNotes = view.findViewById(R.id.text_notes);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btn_delete_appointment).setOnClickListener(v -> showDeleteConfirmationDialog());
        
        ImageView btnEdit = view.findViewById(R.id.btn_edit_appointment);
        btnEdit.setOnClickListener(v -> {
            if (currentAppointment != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, AddAppointmentFragment.newInstanceForEdit(currentAppointment.getId()))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void observeData() {
        viewModel.getAppointmentById(appointmentId).observe(getViewLifecycleOwner(), appointment -> {
            if (appointment != null) {
                currentAppointment = appointment;
                bindAppointmentData(appointment);
                
                patientViewModel.getPatientById(appointment.getPatientId()).observe(getViewLifecycleOwner(), patient -> {
                    if (patient != null) {
                        bindPatientData(patient);
                    }
                });
            }
        });
    }

    private void bindAppointmentData(Appointment appointment) {
        if (appointment.getDateTime() != null) {
            textDate.setText(dateFormat.format(appointment.getDateTime()));
            textTime.setText(timeFormat.format(appointment.getDateTime()));
        }

        textTreatment.setText(appointment.getTreatmentType() != null ? appointment.getTreatmentType() : getString(R.string.unknown));
        textNotes.setText(appointment.getNotes() != null && !appointment.getNotes().isEmpty() ? appointment.getNotes() : "-");

        String status = appointment.getStatus() != null ? appointment.getStatus() : Appointment.STATUS_SCHEDULED;
        textStatus.setText(status);

        if (Appointment.STATUS_COMPLETED.equals(status)) {
            cardStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chip_green_bg));
            textStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_green_text));
        } else if (Appointment.STATUS_CANCELLED.equals(status)) {
            cardStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chip_red_bg));
            textStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_red_text));
        } else { // SCHEDULED
            cardStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chip_orange_bg));
            textStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_orange_text));
        }
    }

    private void bindPatientData(Patient patient) {
        String fullName = (patient.getFirstName() != null ? patient.getFirstName() : "") + " " +
                         (patient.getLastName() != null ? patient.getLastName() : "");
        textPatientName.setText(fullName.trim());
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_appointment_title)
                .setMessage(R.string.delete_appointment_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteAppointment())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteAppointment() {
        if (currentAppointment != null) {
            viewModel.delete(currentAppointment);
            Toast.makeText(requireContext(), R.string.appointment_deleted_success, Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }
}
