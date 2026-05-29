package com.diploma.aerodent.ui.settings;

import com.diploma.aerodent.AeroDentApplication;
import com.diploma.aerodent.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ExportFragment extends Fragment {

    private ExportViewModel viewModel;
    private Uri pendingFileUri;

    private ActivityResultLauncher<Intent> saveFileLauncher;

    public ExportFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri targetUri = result.getData().getData();
                if (targetUri != null && pendingFileUri != null) {
                    viewModel.saveFileToTarget(pendingFileUri, targetUri);
                }
            }
            pendingFileUri = null;
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_export, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        viewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(ExportViewModel.class);

        View cardExportDb = view.findViewById(R.id.card_export_db);
        View cardExportPatients = view.findViewById(R.id.card_export_patients);
        View cardExportAppointments = view.findViewById(R.id.card_export_appointments);
        View cardExportPayments = view.findViewById(R.id.card_export_payments);
        View cardExportUsers = view.findViewById(R.id.card_export_users);
        View cardExportDentalCharts = view.findViewById(R.id.card_export_dental_charts);
        View cardExportProcedureLogs = view.findViewById(R.id.card_export_procedure_logs);

        if (cardExportDb != null) {
            cardExportDb.setOnClickListener(v -> {
                viewModel.exportDatabase();
            });
        }

        if (cardExportPatients != null) {
            cardExportPatients.setOnClickListener(v -> {
                viewModel.exportJson(ExportViewModel.ExportType.PATIENTS);
            });
        }

        if (cardExportAppointments != null) {
            cardExportAppointments.setOnClickListener(v -> {
                viewModel.exportJson(ExportViewModel.ExportType.APPOINTMENTS);
            });
        }

        if (cardExportPayments != null) {
            cardExportPayments.setOnClickListener(v -> {
                viewModel.exportJson(ExportViewModel.ExportType.PAYMENTS);
            });
        }

        if (cardExportUsers != null) {
            cardExportUsers.setOnClickListener(v -> {
                viewModel.exportJson(ExportViewModel.ExportType.USERS);
            });
        }

        if (cardExportDentalCharts != null) {
            cardExportDentalCharts.setOnClickListener(v -> {
                viewModel.exportJson(ExportViewModel.ExportType.DENTAL_CHARTS);
            });
        }

        if (cardExportProcedureLogs != null) {
            cardExportProcedureLogs.setOnClickListener(v -> {
                viewModel.exportJson(ExportViewModel.ExportType.PROCEDURE_LOGS);
            });
        }

        viewModel.getExportResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            if (result.isSuccess() && result.getFileUri() != null) {
                pendingFileUri = result.getFileUri();
                promptSaveFile(result.getType());
            } else {
                String errorMsg = result.getErrorMessage();
                Toast.makeText(getContext(),
                        getString(R.string.export_error) + (errorMsg != null ? ": " + errorMsg : ""), Toast.LENGTH_LONG)
                        .show();
            }
            viewModel.resetExportResult();
        });

        viewModel.getSaveResult().observe(getViewLifecycleOwner(), success -> {
            if (success == null)
                return;
            if (success) {
                Toast.makeText(getContext(), R.string.export_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.export_error, Toast.LENGTH_LONG).show();
            }
            viewModel.resetSaveResult();
        });
    }

    private void promptSaveFile(ExportViewModel.ExportType type) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        String mimeType;
        String fileName;

        if (type == ExportViewModel.ExportType.DATABASE) {
            mimeType = "application/x-sqlite3";
            fileName = "aerodent_backup.db";
        } else {
            mimeType = "application/json";
            if (type == ExportViewModel.ExportType.PATIENTS) {
                fileName = "patients_export.json";
            } else if (type == ExportViewModel.ExportType.APPOINTMENTS) {
                fileName = "appointments_export.json";
            } else if (type == ExportViewModel.ExportType.PAYMENTS) {
                fileName = "payments_export.json";
            } else if (type == ExportViewModel.ExportType.USERS) {
                fileName = "users_export.json";
            } else if (type == ExportViewModel.ExportType.DENTAL_CHARTS) {
                fileName = "dental_charts_export.json";
            } else if (type == ExportViewModel.ExportType.PROCEDURE_LOGS) {
                fileName = "procedure_logs_export.json";
            } else {
                fileName = "data_export.json";
            }
        }

        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        saveFileLauncher.launch(intent);
    }

}
