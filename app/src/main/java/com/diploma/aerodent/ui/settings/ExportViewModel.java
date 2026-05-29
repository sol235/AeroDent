package com.diploma.aerodent.ui.settings;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.repository.AppointmentRepository;
import com.diploma.aerodent.data.repository.PatientRepository;
import com.diploma.aerodent.data.repository.PaymentRepository;
import com.diploma.aerodent.data.repository.ProcedureLogRepository;
import com.diploma.aerodent.data.repository.ToothStatusRepository;
import com.diploma.aerodent.data.repository.UserRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

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

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ToothStatusRepository toothStatusRepository;
    private final ProcedureLogRepository procedureLogRepository;

    private final MutableLiveData<ExportResult> exportResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ExportViewModel(@NonNull Application application,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository,
                           PaymentRepository paymentRepository,
                           UserRepository userRepository,
                           ToothStatusRepository toothStatusRepository,
                           ProcedureLogRepository procedureLogRepository) {
        super(application);
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.toothStatusRepository = toothStatusRepository;
        this.procedureLogRepository = procedureLogRepository;
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
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Context context = getApplication().getApplicationContext();


                AppDatabase db = AppDatabase.getDatabase(context);
                db.getOpenHelper().getWritableDatabase().query("PRAGMA checkpoint(FULL)");


                File dbFile = context.getDatabasePath("aerodent_database");
                if (!dbFile.exists()) {
                    exportResult.postValue(new ExportResult(ExportType.DATABASE, false, null, "Database file does not exist"));
                    return;
                }


                File cacheDir = new File(context.getCacheDir(), "exports");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                File backupFile = new File(cacheDir, "aerodent_backup.db");
                
                copyFile(dbFile, backupFile);


                Uri uri = FileProvider.getUriForFile(context, 
                        context.getPackageName() + ".fileprovider", backupFile);

                exportResult.postValue(new ExportResult(ExportType.DATABASE, true, uri, null));
            } catch (Exception e) {
                e.printStackTrace();
                exportResult.postValue(new ExportResult(ExportType.DATABASE, false, null, e.getMessage()));
            }
        });
    }

    public void exportJson(ExportType type) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Context context = getApplication().getApplicationContext();
                String filename;
                String jsonContent;

                switch (type) {
                    case PATIENTS:
                        filename = "patients_export.json";
                        jsonContent = gson.toJson(patientRepository.getAllPatientsSync());
                        break;
                    case APPOINTMENTS:
                        filename = "appointments_export.json";
                        jsonContent = gson.toJson(appointmentRepository.getAllAppointmentsSync());
                        break;
                    case PAYMENTS:
                        filename = "payments_export.json";
                        jsonContent = gson.toJson(paymentRepository.getAllPaymentsSync());
                        break;
                    case USERS:
                        filename = "users_export.json";
                        jsonContent = gson.toJson(userRepository.getAllUsersSync());
                        break;
                    case DENTAL_CHARTS:
                        filename = "dental_charts_export.json";
                        jsonContent = gson.toJson(toothStatusRepository.getAllToothStatusesSync());
                        break;
                    case PROCEDURE_LOGS:
                        filename = "procedure_logs_export.json";
                        jsonContent = gson.toJson(procedureLogRepository.getAllProcedureLogsSync());
                        break;
                    default:
                        exportResult.postValue(new ExportResult(type, false, null, "Unknown export type"));
                        return;
                }

                File cacheDir = new File(context.getCacheDir(), "exports");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                File exportFile = new File(cacheDir, filename);
                
                FileWriter writer = new FileWriter(exportFile);
                writer.write(jsonContent);
                writer.flush();
                writer.close();

                Uri uri = FileProvider.getUriForFile(context, 
                        context.getPackageName() + ".fileprovider", exportFile);

                exportResult.postValue(new ExportResult(type, true, uri, null));
            } catch (Exception e) {
                e.printStackTrace();
                exportResult.postValue(new ExportResult(type, false, null, e.getMessage()));
            }
        });
    }

    private void copyFile(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }

    public void saveFileToTarget(Uri sourceUri, Uri targetUri) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Context context = getApplication().getApplicationContext();
                try (InputStream in = context.getContentResolver().openInputStream(sourceUri);
                     OutputStream out = context.getContentResolver().openOutputStream(targetUri)) {
                     
                    if (in == null || out == null) {
                        throw new IOException("Unable to open streams");
                    }
                    
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
                saveResult.postValue(true);
            } catch (Exception e) {
                e.printStackTrace();
                saveResult.postValue(false);
            }
        });
    }

}
