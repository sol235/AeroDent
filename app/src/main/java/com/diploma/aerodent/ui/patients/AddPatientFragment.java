package com.diploma.aerodent.ui.patients;

import android.app.DatePickerDialog;
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
import com.diploma.aerodent.data.local.entity.Patient;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPatientFragment extends Fragment {

    private EditText editFirstName, editLastName, editEgn, editDob, editPhone, editEmail, editNhifNumber;
    private Spinner spinnerNhifStatus;
    private PatientViewModel patientViewModel;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private Date selectedDob;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_patient, container, false);

        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        editFirstName = root.findViewById(R.id.edit_first_name);
        editLastName = root.findViewById(R.id.edit_last_name);
        editEgn = root.findViewById(R.id.edit_egn);
        editDob = root.findViewById(R.id.edit_dob);
        editPhone = root.findViewById(R.id.edit_phone);
        editEmail = root.findViewById(R.id.edit_email);
        editNhifNumber = root.findViewById(R.id.edit_nhif_number);
        spinnerNhifStatus = root.findViewById(R.id.spinner_nhif_status);

        ImageView btnBack = root.findViewById(R.id.btn_back);
        ImageView btnSaveTop = root.findViewById(R.id.btn_save_top);
        MaterialButton btnSavePatient = root.findViewById(R.id.btn_save_patient);

        // Setup dropdown menu with custom layout
        String[] nhifStatuses = {"Active — verified via NHIF", "Inactive", "Unknown"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_custom, nhifStatuses);
        adapter.setDropDownViewResource(R.layout.spinner_item_custom);
        spinnerNhifStatus.setAdapter(adapter);

        editDob.setOnClickListener(v -> showDatePicker());

        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        btnSaveTop.setOnClickListener(v -> savePatient());
        btnSavePatient.setOnClickListener(v -> savePatient());

        return root;
    }

    private void showDatePicker() {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectedDob = calendar.getTime();
            editDob.setText(dateFormat.format(selectedDob));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void savePatient() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String egn = editEgn.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String nhifNumber = editNhifNumber.getText().toString().trim();
        String nhifStatus = spinnerNhifStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(egn)) {
            Toast.makeText(requireContext(), "EGN is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setEgn(egn);
        patient.setPhoneNumber(phone);
        patient.setEmail(email);
        patient.setNhifNumber(nhifNumber);
        patient.setNhifStatus(nhifStatus);
        patient.setDateOfBirth(selectedDob);
        patient.setCreatedAt(new Date());

        patientViewModel.insert(patient);
        Toast.makeText(requireContext(), "Patient saved", Toast.LENGTH_SHORT).show();
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }
}
