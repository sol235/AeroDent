package com.diploma.aerodent.ui.patients;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import androidx.appcompat.app.AlertDialog;
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

    private static final String ARG_PATIENT_ID = "patient_id";

    private EditText editFirstName, editMiddleName, editLastName, editEgn, editDob, editPhone, editEmail, editNhifNumber, editNotes;
    private Spinner spinnerNhifStatus, spinnerGender;
    private TextView textEgnError, textPhoneError, textEmailError;
    private PatientViewModel patientViewModel;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private Date selectedDob;
    
    private int patientId = -1;
    private Patient existingPatient;

    public static AddPatientFragment newInstance(int patientId) {
        AddPatientFragment fragment = new AddPatientFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_patient, container, false);

        if (getArguments() != null) {
            patientId = getArguments().getInt(ARG_PATIENT_ID, -1);
        }

        patientViewModel = new ViewModelProvider(this).get(PatientViewModel.class);

        editFirstName = root.findViewById(R.id.edit_first_name);
        editMiddleName = root.findViewById(R.id.edit_middle_name);
        editLastName = root.findViewById(R.id.edit_last_name);
        editEgn = root.findViewById(R.id.edit_egn);
        textEgnError = root.findViewById(R.id.text_egn_error);
        editDob = root.findViewById(R.id.edit_dob);
        editPhone = root.findViewById(R.id.edit_phone);
        textPhoneError = root.findViewById(R.id.text_phone_error);
        editEmail = root.findViewById(R.id.edit_email);
        textEmailError = root.findViewById(R.id.text_email_error);
        editNhifNumber = root.findViewById(R.id.edit_nhif_number);
        editNotes = root.findViewById(R.id.edit_notes);
        spinnerNhifStatus = root.findViewById(R.id.spinner_nhif_status);
        spinnerGender = root.findViewById(R.id.spinner_gender);
        TextView textTitle = root.findViewById(R.id.text_title);

        ImageView btnBack = root.findViewById(R.id.btn_back);
        ImageView btnSaveTop = root.findViewById(R.id.btn_save_top);
        ImageView btnDelete = root.findViewById(R.id.btn_delete);
        MaterialButton btnSavePatient = root.findViewById(R.id.btn_save_patient);

        setupSpinners();
        observeCalculatedFields();

        if (patientId != -1) {
            textTitle.setText(R.string.patient_edit_title);
            btnSavePatient.setText(R.string.patient_update_button);
            btnDelete.setVisibility(View.VISIBLE);
            patientViewModel.getPatientById(patientId).observe(getViewLifecycleOwner(), patient -> {
                if (patient != null && existingPatient == null) {
                    existingPatient = patient;
                    populateFields(patient);
                }
            });
        }

        setupEgnWatcher();

        editDob.setOnClickListener(v -> showDatePicker());

        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
        btnSaveTop.setOnClickListener(v -> savePatient());
        btnSavePatient.setOnClickListener(v -> savePatient());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());

        return root;
    }

    private void setupSpinners() {
        // NHIF dropdown
        String[] nhifStatuses = {
                getString(R.string.patient_nhif_status_active),
                getString(R.string.patient_nhif_status_inactive),
                getString(R.string.unknown)
        };
        ArrayAdapter<String> nhifAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_custom, nhifStatuses);
        nhifAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        spinnerNhifStatus.setAdapter(nhifAdapter);

        // Gender dropdown
        String[] genders = {
                getString(R.string.gender_male),
                getString(R.string.gender_female),
                getString(R.string.gender_unknown)
        };
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_custom, genders);
        genderAdapter.setDropDownViewResource(R.layout.spinner_item_custom);
        spinnerGender.setAdapter(genderAdapter);
        spinnerGender.setSelection(2); // Set to Unknown
    }

    private void observeCalculatedFields() {
        patientViewModel.getCalculatedGender().observe(getViewLifecycleOwner(), gender -> {
            if (gender != null) {
                if (gender.equals(Patient.GENDER_MALE)) {
                    spinnerGender.setSelection(0);
                } else if (gender.equals(Patient.GENDER_FEMALE)) {
                    spinnerGender.setSelection(1);
                }
            }
        });

        patientViewModel.getCalculatedDob().observe(getViewLifecycleOwner(), dob -> {
            if (dob != null) {
                selectedDob = dob;
                calendar.setTime(selectedDob);
                editDob.setText(dateFormat.format(selectedDob));
            }
        });

        patientViewModel.getIsEgnDuplicate().observe(getViewLifecycleOwner(), isDuplicate -> {
            if (isDuplicate) {
                textEgnError.setText(R.string.patient_error_duplicate_egn);
                textEgnError.setVisibility(View.VISIBLE);
            } else {
                Boolean isValid = patientViewModel.getIsEgnValid().getValue();
                if (isValid != null && !isValid) {
                    textEgnError.setText(R.string.patient_error_egn_invalid);
                    textEgnError.setVisibility(View.VISIBLE);
                } else {
                    textEgnError.setVisibility(View.GONE);
                }
            }
        });

        patientViewModel.getIsEgnValid().observe(getViewLifecycleOwner(), isValid -> {
            if (!isValid) {
                textEgnError.setText(R.string.patient_error_egn_invalid);
                textEgnError.setVisibility(View.VISIBLE);
            } else {
                Boolean isDuplicate = patientViewModel.getIsEgnDuplicate().getValue();
                if (isDuplicate != null && isDuplicate) {
                    textEgnError.setText(R.string.patient_error_duplicate_egn);
                    textEgnError.setVisibility(View.VISIBLE);
                } else {
                    textEgnError.setVisibility(View.GONE);
                }
            }
        });

        patientViewModel.getIsPhoneValid().observe(getViewLifecycleOwner(), isValid -> {
            textPhoneError.setVisibility(isValid ? View.GONE : View.VISIBLE);
        });

        patientViewModel.getIsEmailValid().observe(getViewLifecycleOwner(), isValid -> {
            textEmailError.setVisibility(isValid ? View.GONE : View.VISIBLE);
        });
    }

    private void setupEgnWatcher() {
        editEgn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int excludeId = existingPatient != null ? existingPatient.getId() : -1;
                patientViewModel.processEgn(s.toString(), excludeId);
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_patient_title)
                .setMessage(R.string.delete_patient_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> deletePatient())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deletePatient() {
        if (existingPatient != null) {
            patientViewModel.delete(existingPatient);
            Toast.makeText(requireContext(), R.string.patient_deleted_success, Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack(); // pop edit
            getParentFragmentManager().popBackStack(); // pop details
        }
    }

    private void populateFields(Patient patient) {
        editFirstName.setText(patient.getFirstName());
        editMiddleName.setText(patient.getMiddleName());
        editLastName.setText(patient.getLastName());
        editEgn.setText(patient.getEgn());
        editPhone.setText(patient.getPhoneNumber());
        editEmail.setText(patient.getEmail());
        editNhifNumber.setText(patient.getNhifNumber());
        editNotes.setText(patient.getNotes());
        
        if (patient.getGender() != null) {
            if (patient.getGender().equals(Patient.GENDER_MALE)) {
                spinnerGender.setSelection(0);
            } else if (patient.getGender().equals(Patient.GENDER_FEMALE)) {
                spinnerGender.setSelection(1);
            } else {
                spinnerGender.setSelection(2);
            }
        }

        if (patient.getDateOfBirth() != null) {
            selectedDob = patient.getDateOfBirth();
            calendar.setTime(selectedDob);
            editDob.setText(dateFormat.format(selectedDob));
        }

        if (patient.getNhifStatus() != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinnerNhifStatus.getAdapter();
            int position = adapter.getPosition(patient.getNhifStatus());
            if (position >= 0) {
                spinnerNhifStatus.setSelection(position);
            }
        }
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
        String middleName = editMiddleName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String egn = editEgn.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String nhifNumber = editNhifNumber.getText().toString().trim();
        String nhifStatus = spinnerNhifStatus.getSelectedItem().toString();
        String notes = editNotes.getText().toString().trim();
        
        String gender = Patient.GENDER_UNKNOWN;
        if (spinnerGender.getSelectedItemPosition() == 0) gender = Patient.GENDER_MALE;
        else if (spinnerGender.getSelectedItemPosition() == 1) gender = Patient.GENDER_FEMALE;

        if (TextUtils.isEmpty(egn)) {
            Toast.makeText(requireContext(), R.string.patient_error_egn_required, Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!patientViewModel.validatePatientData(egn, phone, email)) {
            return;
        }

        patientViewModel.savePatientWithCheck(existingPatient, firstName, middleName, lastName, egn, gender, phone, email, nhifNumber, nhifStatus, selectedDob, notes, new PatientViewModel.SaveCallback() {
            @Override
            public void onSuccess() {
                if (existingPatient == null) {
                    Toast.makeText(requireContext(), R.string.patient_saved_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), R.string.patient_updated_success, Toast.LENGTH_SHORT).show();
                }
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }

            @Override
            public void onDuplicateEgn() {
                textEgnError.setText(R.string.patient_error_duplicate_egn);
                textEgnError.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editFirstName = null;
        editMiddleName = null;
        editLastName = null;
        editEgn = null;
        textEgnError = null;
        editDob = null;
        editPhone = null;
        textPhoneError = null;
        editEmail = null;
        textEmailError = null;
        editNhifNumber = null;
        editNotes = null;
        spinnerNhifStatus = null;
        spinnerGender = null;
    }
}
