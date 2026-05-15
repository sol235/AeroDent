package com.diploma.aerodent.ui.appointments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.util.NameUtils;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddAppointmentFragment extends Fragment {

    private static final String ARG_PATIENT_ID = "patient_id";
    private static final String ARG_APPOINTMENT_ID = "appointment_id";

    private Spinner spinnerPatient, spinnerStatus;
    private EditText editDate, editTime, editTreatmentType, editNotes;
    private AppointmentViewModel viewModel;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    private List<Patient> patientList = new ArrayList<>();
    private List<String> statusList = Arrays.asList(
            Appointment.STATUS_SCHEDULED,
            Appointment.STATUS_COMPLETED,
            Appointment.STATUS_CANCELLED
    );
    
    private int preselectedPatientId = -1;
    private int appointmentId = -1;
    private Appointment existingAppointment;

    public static AddAppointmentFragment newInstance(int patientId) {
        AddAppointmentFragment fragment = new AddAppointmentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddAppointmentFragment newInstanceForEdit(int appointmentId) {
        AddAppointmentFragment fragment = new AddAppointmentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_APPOINTMENT_ID, appointmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_appointment, container, false);

        if (getArguments() != null) {
            preselectedPatientId = getArguments().getInt(ARG_PATIENT_ID, -1);
            appointmentId = getArguments().getInt(ARG_APPOINTMENT_ID, -1);
        }

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        spinnerPatient = root.findViewById(R.id.spinner_patient);
        spinnerStatus = root.findViewById(R.id.spinner_status);
        editDate = root.findViewById(R.id.edit_date);
        editTime = root.findViewById(R.id.edit_time);
        editTreatmentType = root.findViewById(R.id.edit_treatment_type);
        editNotes = root.findViewById(R.id.edit_notes);

        ImageView btnBack = root.findViewById(R.id.btn_back);
        ImageView btnSaveTop = root.findViewById(R.id.btn_save_top);
        MaterialButton btnSaveAppointment = root.findViewById(R.id.btn_save_appointment);
        TextView textTitle = root.findViewById(R.id.text_title);

        if (appointmentId != -1) {
            textTitle.setText(R.string.appointment_edit_title);
            btnSaveAppointment.setText(R.string.appointment_update_button);
        }

        // Setup status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_custom, statusList);
        statusAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        spinnerStatus.setAdapter(statusAdapter);
        spinnerStatus.setSelection(0); // Default to SCHEDULED

        // Setup patient spinner
        viewModel.getAllPatients().observe(getViewLifecycleOwner(), patients -> {
            if (patients != null) {
                patientList = patients;
                List<String> patientNames = new ArrayList<>();
                for (Patient p : patients) {
                    patientNames.add(NameUtils.formatFirstLastName(p));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_custom, patientNames);
                adapter.setDropDownViewResource(R.layout.spinner_item_custom);
                spinnerPatient.setAdapter(adapter);
                
                if (appointmentId != -1) {
                    loadAppointmentData();
                } else if (preselectedPatientId != -1) {
                    for (int i = 0; i < patients.size(); i++) {
                        if (patients.get(i).getId() == preselectedPatientId) {
                            spinnerPatient.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });

        editDate.setOnClickListener(v -> showDatePicker());
        editTime.setOnClickListener(v -> showTimePicker());

        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        btnSaveTop.setOnClickListener(v -> saveAppointment());
        btnSaveAppointment.setOnClickListener(v -> saveAppointment());

        return root;
    }

    private void loadAppointmentData() {
        viewModel.getAppointmentById(appointmentId).observe(getViewLifecycleOwner(), appointment -> {
            if (appointment != null && existingAppointment == null) {
                existingAppointment = appointment;
                if (appointment.getDateTime() != null) {
                    calendar.setTime(appointment.getDateTime());
                    editDate.setText(dateFormat.format(appointment.getDateTime()));
                    editTime.setText(timeFormat.format(appointment.getDateTime()));
                }
                editTreatmentType.setText(appointment.getTreatmentType());
                editNotes.setText(appointment.getNotes());
                
                for (int i = 0; i < patientList.size(); i++) {
                    if (patientList.get(i).getId() == appointment.getPatientId()) {
                        spinnerPatient.setSelection(i);
                        break;
                    }
                }

                if (appointment.getStatus() != null) {
                    int statusIndex = statusList.indexOf(appointment.getStatus());
                    if (statusIndex >= 0) {
                        spinnerStatus.setSelection(statusIndex);
                    }
                }
            }
        });
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editDate.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            editTime.setText(timeFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void saveAppointment() {
        if (patientList.isEmpty()) {
            Toast.makeText(requireContext(), R.string.appointment_error_no_patients, Toast.LENGTH_SHORT).show();
            return;
        }

        int patientPos = spinnerPatient.getSelectedItemPosition();
        int statusPos = spinnerStatus.getSelectedItemPosition();
        if (patientPos < 0 || statusPos < 0) return;
        
        Patient selectedPatient = patientList.get(patientPos);
        String selectedStatus = statusList.get(statusPos);
        String dateStr = editDate.getText().toString();
        String timeStr = editTime.getText().toString();
        String treatmentType = editTreatmentType.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(timeStr)) {
            Toast.makeText(requireContext(), R.string.appointment_error_datetime_required, Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = existingAppointment != null ? existingAppointment : new Appointment();
        appointment.setPatientId(selectedPatient.getId());
        appointment.setDateTime(calendar.getTime());
        appointment.setTreatmentType(treatmentType);
        appointment.setNotes(notes);
        appointment.setStatus(selectedStatus);
        
        if (existingAppointment == null) {
            appointment.setCreatedAt(new Date());
            viewModel.insert(appointment);
            Toast.makeText(requireContext(), R.string.appointment_scheduled_success, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.update(appointment);
            Toast.makeText(requireContext(), R.string.appointment_updated_success, Toast.LENGTH_SHORT).show();
        }
        
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }
}
