package com.diploma.aerodent.ui.patients;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.ui.appointments.AppointmentDetailFragment;
import com.diploma.aerodent.ui.dentalchart.DentalChartFragment;
import com.diploma.aerodent.ui.photos.PatientGalleryFragment;
import com.diploma.aerodent.ui.photos.PhotoViewModel;
import com.diploma.aerodent.ui.payment.PaymentTransactionAdapter;
import com.diploma.aerodent.util.CameraHelper;
import com.diploma.aerodent.util.NameUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PatientDetailFragment extends Fragment {

    private static final String ARG_PATIENT_ID = "patient_id";
    private int patientId;
    private PatientDetailViewModel viewModel;
    private PhotoViewModel photoViewModel;
    private CameraHelper cameraHelper;
    private HistoryTimelineAdapter historyAdapter;
    private PaymentTransactionAdapter paymentAdapter;
    private SimpleDateFormat dobFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private MaterialCardView tabHistory, tabDentalChart, tabPhotos, tabPayments;
    private View recyclerHistory, photosContainer, recyclerPayments;
    private FloatingActionButton fabTakePhoto;
    private ImageView btnUploadPhoto;
    private View root;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && patientId != -1) {
                    photoViewModel.savePhotoFromUri(uri, patientId, null);
                    Toast.makeText(requireContext(), R.string.photo_uploaded_success, Toast.LENGTH_SHORT).show();
                }
            }
    );

    public static PatientDetailFragment newInstance(int patientId) {
        PatientDetailFragment fragment = new PatientDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            patientId = getArguments().getInt(ARG_PATIENT_ID);
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
        root = inflater.inflate(R.layout.fragment_patient_detail, container, false);

        viewModel = new ViewModelProvider(this).get(PatientDetailViewModel.class);
        viewModel.setPatientId(patientId);

        tabHistory = root.findViewById(R.id.tab_history);
        tabDentalChart = root.findViewById(R.id.tab_dental_chart);
        tabPhotos = root.findViewById(R.id.tab_photos);
        tabPayments = root.findViewById(R.id.tab_payments);

        recyclerHistory = root.findViewById(R.id.recycler_history);
        photosContainer = root.findViewById(R.id.photos_container);
        recyclerPayments = root.findViewById(R.id.recycler_payments);
        fabTakePhoto = root.findViewById(R.id.fab_take_photo);
        btnUploadPhoto = root.findViewById(R.id.btn_upload_photo);

        setupToolbar(root);
        setupRecyclerView(root);
        setupTabs();
        setupFab();
        observeViewModel(root);

        return root;
    }

    private void setupToolbar(View root) {
        root.findViewById(R.id.btn_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        root.findViewById(R.id.btn_edit_patient).setOnClickListener(v -> {
            if (patientId != -1) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, AddPatientFragment.newInstance(patientId))
                        .addToBackStack(null)
                        .commit();
            }
        });
        
        btnUploadPhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
    }

    private void setupRecyclerView(View root) {
        RecyclerView recyclerHistoryView = root.findViewById(R.id.recycler_history);
        recyclerHistoryView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryTimelineAdapter();
        historyAdapter.setOnHistoryItemClickListener(appointment -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, AppointmentDetailFragment.newInstance(appointment.getId()))
                    .addToBackStack(null)
                    .commit();
        });
        recyclerHistoryView.setAdapter(historyAdapter);

        RecyclerView recyclerPaymentsView = root.findViewById(R.id.recycler_payments);
        recyclerPaymentsView.setLayoutManager(new LinearLayoutManager(getContext()));
        paymentAdapter = new PaymentTransactionAdapter();
        paymentAdapter.setOnTransactionClickListener(payment -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, AppointmentDetailFragment.newInstanceWithPaymentsTab(payment.getAppointmentId()))
                    .addToBackStack(null)
                    .commit();
        });
        recyclerPaymentsView.setAdapter(paymentAdapter);
    }

    private void setupTabs() {
        tabHistory.setOnClickListener(v -> selectTab(tabHistory));
        tabDentalChart.setOnClickListener(v -> selectTab(tabDentalChart));
        tabPhotos.setOnClickListener(v -> selectTab(tabPhotos));
        tabPayments.setOnClickListener(v -> selectTab(tabPayments));
    }

    private void setupFab() {
        fabTakePhoto.setOnClickListener(v -> {
            cameraHelper.takePhoto(patientId, null, null);
        });
    }

    private void selectTab(MaterialCardView selectedTab) {
        // Reset all tabs
        resetTabStyle(tabHistory, root.findViewById(R.id.text_tab_history));
        resetTabStyle(tabDentalChart, root.findViewById(R.id.text_tab_dental_chart));
        resetTabStyle(tabPhotos, root.findViewById(R.id.text_tab_photos));
        resetTabStyle(tabPayments, root.findViewById(R.id.text_tab_payments));

        // Highlight selected tab
        selectedTab.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        selectedTab.setStrokeWidth(0);
        
        TextView label = null;
        if (selectedTab == tabHistory) label = root.findViewById(R.id.text_tab_history);
        else if (selectedTab == tabDentalChart) label = root.findViewById(R.id.text_tab_dental_chart);
        else if (selectedTab == tabPhotos) label = root.findViewById(R.id.text_tab_photos);
        else if (selectedTab == tabPayments) label = root.findViewById(R.id.text_tab_payments);
        
        if (label != null) {
            label.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        }

        // Show or Hide content
        if (selectedTab == tabHistory) {
            recyclerHistory.setVisibility(View.VISIBLE);
            photosContainer.setVisibility(View.GONE);
            recyclerPayments.setVisibility(View.GONE);
            fabTakePhoto.setVisibility(View.GONE);
            btnUploadPhoto.setVisibility(View.GONE);
        } else if (selectedTab == tabPhotos) {
            recyclerHistory.setVisibility(View.GONE);
            photosContainer.setVisibility(View.VISIBLE);
            recyclerPayments.setVisibility(View.GONE);
            fabTakePhoto.setVisibility(View.VISIBLE);
            btnUploadPhoto.setVisibility(View.VISIBLE);
            
            if (getChildFragmentManager().findFragmentById(R.id.photos_container) == null) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.photos_container, PatientGalleryFragment.newInstance(patientId))
                        .commit();
            }
        } else if (selectedTab == tabDentalChart) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, DentalChartFragment.newInstance(patientId))
                    .addToBackStack(null)
                    .commit();
        } else if (selectedTab == tabPayments) {
            recyclerHistory.setVisibility(View.GONE);
            photosContainer.setVisibility(View.GONE);
            recyclerPayments.setVisibility(View.VISIBLE);
            fabTakePhoto.setVisibility(View.GONE);
            btnUploadPhoto.setVisibility(View.GONE);
        } else {
            recyclerHistory.setVisibility(View.GONE);
            photosContainer.setVisibility(View.GONE);
            recyclerPayments.setVisibility(View.GONE);
            fabTakePhoto.setVisibility(View.GONE);
            btnUploadPhoto.setVisibility(View.GONE);
        }
    }

    private void resetTabStyle(MaterialCardView tab, TextView label) {
        if (tab == null || label == null) return;
        tab.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        tab.setStrokeWidth(1);
        tab.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.border_grey));
        label.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
    }

    private void observeViewModel(View root) {
        TextView textNameHeader = root.findViewById(R.id.text_patient_name_header);
        TextView textNameProfile = root.findViewById(R.id.text_patient_name_profile);
        TextView textInitials = root.findViewById(R.id.text_avatar_initials);
        TextView textIds = root.findViewById(R.id.text_patient_ids);
        TextView textDob = root.findViewById(R.id.text_patient_dob);
        TextView textGender = root.findViewById(R.id.text_patient_gender);
        TextView textPhone = root.findViewById(R.id.text_phone);
        TextView textEmail = root.findViewById(R.id.text_email);
        TextView textNotes = root.findViewById(R.id.text_notes);
        View cardNotes = root.findViewById(R.id.card_notes);
        View textNotesLabel = root.findViewById(R.id.text_notes_label);

        viewModel.getPatient().observe(getViewLifecycleOwner(), patient -> {
            if (patient != null) {
                String fullName = NameUtils.formatFullName(patient);
                textNameHeader.setText(R.string.patient_details_title);
                textNameProfile.setText(fullName);

                textInitials.setText(NameUtils.getInitials(patient));

                String egn = patient.getEgn() != null ? patient.getEgn() : "---";
                String nhif = patient.getNhifNumber() != null ? patient.getNhifNumber() : "---";
                String ids = getString(R.string.patient_details_egn, egn) + " - " + getString(R.string.patient_details_nhif, nhif);
                textIds.setText(ids);

                if (patient.getDateOfBirth() != null) {
                    textDob.setText(getString(R.string.patient_details_dob, dobFormat.format(patient.getDateOfBirth())));
                    textDob.setVisibility(View.VISIBLE);
                } else {
                    textDob.setVisibility(View.GONE);
                }

                if (patient.getGender() != null) {
                    String genderStr = getString(NameUtils.getGenderResourceId(patient.getGender()));
                    textGender.setText(getString(R.string.patient_details_gender, genderStr));
                    textGender.setVisibility(View.VISIBLE);
                } else {
                    textGender.setVisibility(View.GONE);
                }

                textPhone.setText(patient.getPhoneNumber() != null && !patient.getPhoneNumber().isEmpty() 
                        ? patient.getPhoneNumber() : getString(R.string.not_available_short));
                textEmail.setText(patient.getEmail() != null && !patient.getEmail().isEmpty() 
                        ? patient.getEmail() : getString(R.string.not_available_short));

                if (patient.getNotes() != null && !patient.getNotes().trim().isEmpty()) {
                    textNotes.setText(patient.getNotes());
                    cardNotes.setVisibility(View.VISIBLE);
                    textNotesLabel.setVisibility(View.VISIBLE);
                } else {
                    cardNotes.setVisibility(View.GONE);
                    textNotesLabel.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getHistoryItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                historyAdapter.setItems(items);
            }
        });

        viewModel.getPayments().observe(getViewLifecycleOwner(), payments -> {
            if (payments != null) {
                paymentAdapter.setPayments(payments);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabHistory = null;
        tabDentalChart = null;
        tabPhotos = null;
        tabPayments = null;
        recyclerHistory = null;
        photosContainer = null;
        recyclerPayments = null;
        fabTakePhoto = null;
        btnUploadPhoto = null;
        root = null;
    }
}
