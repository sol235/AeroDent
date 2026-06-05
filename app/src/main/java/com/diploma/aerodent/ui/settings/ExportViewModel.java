package com.diploma.aerodent.ui.settings;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.diploma.aerodent.data.repository.ExportRepository;

public class ExportViewModel extends AndroidViewModel {

    public enum ExportType {
        DATABASE,
        PATIENTS,
        APPOINTMENTS,
        PAYMENTS,
        USERS,
        DENTAL_CHARTS,
        PROCEDURE_LOGS
    }

    public static class ExportResult {
        private final ExportType type;
        private final boolean success;
        private final Uri fileUri;
        private final String errorMessage;

        public ExportResult(ExportType type, boolean success, Uri fileUri, String errorMessage) {
            this.type = type;
            this.success = success;
            this.fileUri = fileUri;
            this.errorMessage = errorMessage;
        }

        public ExportType getType() { return type; }
        public boolean isSuccess() { return success; }
        public Uri getFileUri() { return fileUri; }
        public String getErrorMessage() { return errorMessage; }
    }

    private final ExportRepository repository;
    private final MutableLiveData<ExportResult> exportResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    public ExportViewModel(@NonNull Application application,
                           ExportRepository repository) {
        super(application);
        this.repository = repository;
    }

    public LiveData<ExportResult> getExportResult() {
        return exportResult;
    }

    public void resetExportResult() {
        exportResult.setValue(null);
    }

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    public void resetSaveResult() {
        saveResult.setValue(null);
    }

    public void exportDatabase() {
        repository.exportDatabase(result -> {
            exportResult.postValue(result);
        });
    }

    public void exportJson(ExportType type) {
        repository.exportJson(type, result -> {
            exportResult.postValue(result);
        });
    }

    public void saveFileToTarget(Uri sourceUri, Uri targetUri) {
        repository.saveFileToTarget(sourceUri, targetUri, success -> {
            saveResult.postValue(success);
        });
    }
}
