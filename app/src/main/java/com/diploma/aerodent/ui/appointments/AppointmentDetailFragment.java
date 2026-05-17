package com.diploma.aerodent.ui.appointments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Photo;
import com.diploma.aerodent.ui.patients.PatientViewModel;
import com.diploma.aerodent.ui.photos.FullScreenPhotoDialogFragment;
import com.diploma.aerodent.ui.photos.PhotoAdapter;
import com.diploma.aerodent.ui.photos.PhotoViewModel;
import com.diploma.aerodent.util.CameraHelper;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppointmentDetailFragment extends Fragment {

    private static final String ARG_APPOINTMENT_ID = "appointment_id";

    private AppointmentViewModel viewModel;
    private PhotoViewModel photoViewModel;
    private int appointmentId;
    private Appointment currentAppointment;
    private CameraHelper cameraHelper;

    private TextView textPatientName;
    private TextView textDate;
    private TextView textTime;
    private TextView textTreatment;
    private TextView textStatus;
    private MaterialCardView cardStatus;
    private TextView textNotes;

    private View textPhotosLabel;
    private RecyclerView recyclerPhotos;
    private PhotoAdapter photoAdapter;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && currentAppointment != null) {
                    photoViewModel.savePhotoFromUri(uri, currentAppointment.getPatientId(), currentAppointment.getId());
                    Toast.makeText(requireContext(), R.string.photo_uploaded_success, Toast.LENGTH_SHORT).show();
                }
            }
    );

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
        
        photoViewModel = new ViewModelProvider(requireActivity()).get(PhotoViewModel.class);
        cameraHelper = new CameraHelper(this, photoViewModel);
        
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
        return inflater.inflate(R.layout.fragment_appointment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

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

        textPhotosLabel = view.findViewById(R.id.text_photos_label);
        recyclerPhotos = view.findViewById(R.id.recycler_appointment_photos);
        
        recyclerPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        photoAdapter = new PhotoAdapter();
        photoAdapter.setOnPhotoClickListener(this::openFullScreen);
        recyclerPhotos.setAdapter(photoAdapter);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btn_delete_appointment).setOnClickListener(v -> showDeleteConfirmationDialog());
        
        view.findViewById(R.id.fab_take_photo).setOnClickListener(v -> {
            if (currentAppointment != null) {
                cameraHelper.takePhoto(currentAppointment.getPatientId(), currentAppointment.getId(), null);
            }
        });

        view.findViewById(R.id.btn_upload_photo).setOnClickListener(v -> pickImageLauncher.launch("image/*"));

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

    private void openFullScreen(Photo photo) {
        FullScreenPhotoDialogFragment dialog = FullScreenPhotoDialogFragment.newInstance(photo.getId());
        dialog.show(getChildFragmentManager(), "FullScreenPhoto");
    }

    private void observeData() {
        viewModel.getAppointmentWithPatient(appointmentId).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                currentAppointment = result.appointment;
                bindAppointmentData(result.appointment);
                if (result.patient != null) {
                    bindPatientData(result.patient);
                }
            }
        });

        photoViewModel.getPhotosForAppointment(appointmentId).observe(getViewLifecycleOwner(), photos -> {
            if (photos != null && !photos.isEmpty()) {
                photoAdapter.setPhotos(photos);
                textPhotosLabel.setVisibility(View.VISIBLE);
                recyclerPhotos.setVisibility(View.VISIBLE);
            } else {
                textPhotosLabel.setVisibility(View.GONE);
                recyclerPhotos.setVisibility(View.GONE);
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
        
        int statusResId;
        if (Appointment.STATUS_COMPLETED.equals(status)) {
            statusResId = R.string.status_completed;
            cardStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chip_green_bg));
            textStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_green_text));
        } else if (Appointment.STATUS_CANCELLED.equals(status)) {
            statusResId = R.string.status_cancelled;
            cardStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chip_red_bg));
            textStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_red_text));
        } else { // SCHEDULED
            statusResId = R.string.status_scheduled;
            cardStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chip_orange_bg));
            textStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.chip_orange_text));
        }
        
        textStatus.setText(getString(statusResId));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textPatientName = null;
        textDate = null;
        textTime = null;
        textTreatment = null;
        textStatus = null;
        cardStatus = null;
        textNotes = null;
        textPhotosLabel = null;
        recyclerPhotos = null;
    }
}
