package com.diploma.aerodent.data.repository;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import androidx.core.content.FileProvider;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.ui.settings.ExportViewModel.ExportType;
import com.diploma.aerodent.ui.settings.ExportViewModel.ExportResult;
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

public class ExportRepository {
    private final Application application;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ToothStatusRepository toothStatusRepository;
    private final ProcedureLogRepository procedureLogRepository;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ExportRepository(Application application,
                            PatientRepository patientRepository,
                            AppointmentRepository appointmentRepository,
                            PaymentRepository paymentRepository,
                            UserRepository userRepository,
                            ToothStatusRepository toothStatusRepository,
                            ProcedureLogRepository procedureLogRepository) {
        this.application = application;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.toothStatusRepository = toothStatusRepository;
        this.procedureLogRepository = procedureLogRepository;
    }

    public interface ExportCallback {
        void onResult(ExportResult result);
    }

    public interface SaveCallback {
        void onResult(boolean success);
    }

    public void exportDatabase(ExportCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Context context = application.getApplicationContext();

                AppDatabase db = AppDatabase.getDatabase(context);
                db.getOpenHelper().getWritableDatabase().query("PRAGMA checkpoint(FULL)");

                File dbFile = context.getDatabasePath("aerodent_database");
                if (!dbFile.exists()) {
                    callback.onResult(new ExportResult(ExportType.DATABASE, false, null, "Database file does not exist"));
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

                callback.onResult(new ExportResult(ExportType.DATABASE, true, uri, null));
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(new ExportResult(ExportType.DATABASE, false, null, e.getMessage()));
            }
        });
    }

    public void exportJson(ExportType type, ExportCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Context context = application.getApplicationContext();
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
                        callback.onResult(new ExportResult(type, false, null, "Unknown export type"));
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

                callback.onResult(new ExportResult(type, true, uri, null));
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(new ExportResult(type, false, null, e.getMessage()));
            }
        });
    }

    public void saveFileToTarget(Uri sourceUri, Uri targetUri, SaveCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Context context = application.getApplicationContext();
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
                callback.onResult(true);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(false);
            }
        });
    }

    private void copyFile(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }
}
