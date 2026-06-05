package com.diploma.aerodent.data;

import android.app.Application;
import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.repository.*;

public class AppContainer {
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final ProcedureLogRepository procedureLogRepository;
    private final ToothStatusRepository toothStatusRepository;
    private final ExportRepository exportRepository;

    public AppContainer(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        
        userRepository = new UserRepository(database.userDao());
        photoRepository = new PhotoRepository(database.photoDao(), application);
        patientRepository = new PatientRepository(database.patientDao(), photoRepository);
        appointmentRepository = new AppointmentRepository(database.appointmentDao());
        paymentRepository = new PaymentRepository(database.paymentDao());
        procedureLogRepository = new ProcedureLogRepository(database.procedureLogDao());
        toothStatusRepository = new ToothStatusRepository(database.toothStatusDao(), database);
        
        exportRepository = new ExportRepository(
            application,
            patientRepository,
            appointmentRepository,
            paymentRepository,
            userRepository,
            toothStatusRepository,
            procedureLogRepository
        );
    }

    public UserRepository getUserRepository() { return userRepository; }
    public PhotoRepository getPhotoRepository() { return photoRepository; }
    public PatientRepository getPatientRepository() { return patientRepository; }
    public AppointmentRepository getAppointmentRepository() { return appointmentRepository; }
    public PaymentRepository getPaymentRepository() { return paymentRepository; }
    public ProcedureLogRepository getProcedureLogRepository() { return procedureLogRepository; }
    public ToothStatusRepository getToothStatusRepository() { return toothStatusRepository; }
    public ExportRepository getExportRepository() { return exportRepository; }
}
