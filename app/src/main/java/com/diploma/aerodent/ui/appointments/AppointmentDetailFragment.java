package com.diploma.aerodent.ui.appointments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Photo;
import com.diploma.aerodent.ui.dentalchart.DentalChartFragment;
import com.diploma.aerodent.ui.dentalchart.ProcedureLogAdapter;
import com.diploma.aerodent.ui.photos.FullScreenPhotoActivity;
import com.diploma.aerodent.ui.photos.PhotoAdapter;
import com.diploma.aerodent.ui.photos.PhotoViewModel;
import com.diploma.aerodent.util.CameraHelper;
import com.diploma.aerodent.util.DialogUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

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
    private TextInputLayout layoutStatus;
    private AutoCompleteTextView dropdownStatus;
    private TextView textNotes;

    private RecyclerView recyclerPhotos;
    private PhotoAdapter photoAdapter;
    
    private RecyclerView recyclerProcedureLogs;
    private ProcedureLogAdapter procedureLogAdapter;

    private MaterialCardView tabProcedures, tabPhotos, tabPayments;
    private View containerPayment;
    private View fabTakePhoto;
    private View btnUploadPhoto;
    private View fabDentalChart;

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
        layoutStatus = view.findViewById(R.id.layout_status);
        dropdownStatus = view.findViewById(R.id.dropdown_status);
        textNotes = view.findViewById(R.id.text_notes);
        
        setupStatusDropdown();

        recyclerPhotos = view.findViewById(R.id.recycler_appointment_photos);
        
        recyclerPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        photoAdapter = new PhotoAdapter();
        photoAdapter.setOnPhotoClickListener(this::openFullScreen);
        recyclerPhotos.setAdapter(photoAdapter);

        recyclerProcedureLogs = view.findViewById(R.id.recycler_procedure_logs);
        recyclerProcedureLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        procedureLogAdapter = new ProcedureLogAdapter(new ProcedureLogAdapter.OnProcedureLogInteractionListener() {
            @Override
            public void onAnnulClick(com.diploma.aerodent.data.local.entity.ProcedureLog log) {
                showAnnulDialog(log);
            }

            @Override
            public void onLogClick(com.diploma.aerodent.data.local.entity.ProcedureLog log) {
            }
        });
        recyclerProcedureLogs.setAdapter(procedureLogAdapter);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.btn_delete_appointment).setOnClickListener(v -> showDeleteConfirmationDialog());
        
        view.findViewById(R.id.fab_take_photo).setOnClickListener(v -> {
            if (currentAppointment != null) {
                cameraHelper.takePhoto(currentAppointment.getPatientId(), currentAppointment.getId(), null);
            }
        });

        view.findViewById(R.id.btn_upload_photo).setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        view.findViewById(R.id.fab_dental_chart).setOnClickListener(v -> {
            if (currentAppointment != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, DentalChartFragment.newInstance(currentAppointment.getPatientId(), currentAppointment.getId()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageView btnEdit = view.findViewById(R.id.btn_edit_appointment);
        btnEdit.setOnClickListener(v -> {
            if (currentAppointment != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, AddAppointmentFragment.newInstanceForEdit(currentAppointment.getId()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        tabProcedures = view.findViewById(R.id.tab_procedures);
        tabPhotos = view.findViewById(R.id.tab_photos);
        tabPayments = view.findViewById(R.id.tab_payments);
        containerPayment = view.findViewById(R.id.container_payment);
        
        fabTakePhoto = view.findViewById(R.id.fab_take_photo);
        btnUploadPhoto = view.findViewById(R.id.btn_upload_photo);
        fabDentalChart = view.findViewById(R.id.fab_dental_chart);

        setupTabs(view);
    }

    private void setupTabs(View view) {
        tabProcedures.setOnClickListener(v -> selectTab(tabProcedures, view));
        tabPhotos.setOnClickListener(v -> selectTab(tabPhotos, view));
        tabPayments.setOnClickListener(v -> selectTab(tabPayments, view));

        selectTab(tabProcedures, view);
    }

    private void selectTab(MaterialCardView selectedTab, View view) {
        resetTabStyle(tabProcedures, view.findViewById(R.id.text_tab_procedures));
        resetTabStyle(tabPhotos, view.findViewById(R.id.text_tab_photos));
        resetTabStyle(tabPayments, view.findViewById(R.id.text_tab_payments));

        selectedTab.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        selectedTab.setStrokeWidth(0);

        TextView label = null;
        if (selectedTab == tabProcedures) label = view.findViewById(R.id.text_tab_procedures);
        else if (selectedTab == tabPhotos) label = view.findViewById(R.id.text_tab_photos);
        else if (selectedTab == tabPayments) label = view.findViewById(R.id.text_tab_payments);

        if (label != null) {
            label.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }

        // Toggle visibilities
        recyclerProcedureLogs.setVisibility(selectedTab == tabProcedures ? View.VISIBLE : View.GONE);
        fabDentalChart.setVisibility(selectedTab == tabProcedures ? View.VISIBLE : View.GONE);

        recyclerPhotos.setVisibility(selectedTab == tabPhotos ? View.VISIBLE : View.GONE);
        fabTakePhoto.setVisibility(selectedTab == tabPhotos ? View.VISIBLE : View.GONE);
        btnUploadPhoto.setVisibility(selectedTab == tabPhotos ? View.VISIBLE : View.GONE);

        containerPayment.setVisibility(selectedTab == tabPayments ? View.VISIBLE : View.GONE);
    }

    private void resetTabStyle(MaterialCardView tab, TextView label) {
        if (tab == null || label == null) return;
        tab.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        tab.setStrokeWidth(1);
        tab.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.border_grey));
        label.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
    }

    private void openFullScreen(Photo photo) {
        android.content.Intent intent = new android.content.Intent(requireContext(), FullScreenPhotoActivity.class);
        intent.putExtra(FullScreenPhotoActivity.EXTRA_PHOTO_ID, photo.getId());
        startActivity(intent);
    }
    
    private void setupStatusDropdown() {
        String[] statuses = {
            getString(R.string.status_scheduled),
            getString(R.string.status_completed),
            getString(R.string.status_cancelled)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(), android.R.layout.simple_dropdown_item_1line, statuses
        );
        dropdownStatus.setAdapter(adapter);
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

        viewModel.getProcedureLogsForAppointment(appointmentId).observe(getViewLifecycleOwner(), logs -> {
            if (logs != null) {
                procedureLogAdapter.setProcedureLogs(logs);
            }
        });

        photoViewModel.getPhotosForAppointment(appointmentId).observe(getViewLifecycleOwner(), photos -> {
            if (photos != null) {
                photoAdapter.setPhotos(photos);
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
        int bgColor;
        int textColor;
        if (Appointment.STATUS_COMPLETED.equals(status)) {
            statusResId = R.string.status_completed;
            bgColor = R.color.chip_green_bg;
            textColor = R.color.chip_green_text;
        } else if (Appointment.STATUS_CANCELLED.equals(status)) {
            statusResId = R.string.status_cancelled;
            bgColor = R.color.chip_red_bg;
            textColor = R.color.chip_red_text;
        } else { // SCHEDULED
            statusResId = R.string.status_scheduled;
            bgColor = R.color.chip_orange_bg;
            textColor = R.color.chip_orange_text;
        }
        
        layoutStatus.setBoxBackgroundColor(ContextCompat.getColor(requireContext(), bgColor));
        dropdownStatus.setTextColor(ContextCompat.getColor(requireContext(), textColor));
        
        dropdownStatus.setOnItemClickListener(null);
        dropdownStatus.setText(getString(statusResId), false);
        
        dropdownStatus.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            String newStatus = Appointment.STATUS_SCHEDULED;
            if (selected.equals(getString(R.string.status_completed))) {
                newStatus = Appointment.STATUS_COMPLETED;
            } else if (selected.equals(getString(R.string.status_cancelled))) {
                newStatus = Appointment.STATUS_CANCELLED;
            }
            
            if (!newStatus.equals(appointment.getStatus())) {
                appointment.setStatus(newStatus);
                viewModel.update(appointment);
                Toast.makeText(requireContext(), R.string.appointment_updated_success, Toast.LENGTH_SHORT).show();
            }
        });
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

    private void showAnnulDialog(com.diploma.aerodent.data.local.entity.ProcedureLog log) {
        DialogUtils.showAnnulDialog(requireContext(), () -> {
            log.setAnnulled(true);
            viewModel.updateProcedureLog(log);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textPatientName = null;
        textDate = null;
        textTime = null;
        textTreatment = null;
        layoutStatus = null;
        dropdownStatus = null;
        textNotes = null;
        recyclerPhotos = null;
        recyclerProcedureLogs = null;
        tabProcedures = null;
        tabPhotos = null;
        tabPayments = null;
        containerPayment = null;
        fabTakePhoto = null;
        btnUploadPhoto = null;
        fabDentalChart = null;
    }
}
