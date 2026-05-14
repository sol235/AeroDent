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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddAppointmentFragment extends Fragment {

    private Spinner spinnerPatient;
    private EditText editDate, editTime, editTreatmentType, editNotes;
    private AppointmentViewModel viewModel;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    private List<Patient> patientList = new ArrayList<>();
    private int preselectedPatientId = -1;

    public static AddAppointmentFragment newInstance(int patientId) {
        AddAppointmentFragment fragment = new AddAppointmentFragment();
        Bundle args = new Bundle();
        args.putInt("patient_id", patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_appointment, container, false);

        if (getArguments() != null) {
            preselectedPatientId = getArguments().getInt("patient_id", -1);
        }

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        spinnerPatient = root.findViewById(R.id.spinner_patient);
        editDate = root.findViewById(R.id.edit_date);
        editTime = root.findViewById(R.id.edit_time);
        editTreatmentType = root.findViewById(R.id.edit_treatment_type);
        editNotes = root.findViewById(R.id.edit_notes);

        ImageView btnBack = root.findViewById(R.id.btn_back);
        ImageView btnSaveTop = root.findViewById(R.id.btn_save_top);
        MaterialButton btnSaveAppointment = root.findViewById(R.id.btn_save_appointment);

        // Setup patient spinner
        viewModel.getAllPatients().observe(getViewLifecycleOwner(), patients -> {
            if (patients != null) {
                patientList = patients;
                List<String> patientNames = new ArrayList<>();
                int selection = 0;
                for (int i = 0; i < patients.size(); i++) {
                    Patient p = patients.get(i);
                    patientNames.add(p.getFirstName() + " " + p.getLastName());
                    if (p.getId() == preselectedPatientId) {
                        selection = i;
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_custom, patientNames);
                adapter.setDropDownViewResource(R.layout.spinner_item_custom);
                spinnerPatient.setAdapter(adapter);
                if (preselectedPatientId != -1) {
                    spinnerPatient.setSelection(selection);
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

        int selectedPosition = spinnerPatient.getSelectedItemPosition();
        if (selectedPosition < 0) return;
        
        Patient selectedPatient = patientList.get(selectedPosition);
        String dateStr = editDate.getText().toString();
        String timeStr = editTime.getText().toString();
        String treatmentType = editTreatmentType.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

        if (TextUtils.isEmpty(dateStr) || TextUtils.isEmpty(timeStr)) {
            Toast.makeText(requireContext(), R.string.appointment_error_datetime_required, Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(selectedPatient.getId());
        appointment.setDateTime(calendar.getTime());
        appointment.setTreatmentType(treatmentType);
        appointment.setNotes(notes);
        appointment.setStatus(Appointment.STATUS_SCHEDULED);
        appointment.setCreatedAt(new Date());

        viewModel.insert(appointment);
        Toast.makeText(requireContext(), R.string.appointment_scheduled_success, Toast.LENGTH_SHORT).show();
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }
}
