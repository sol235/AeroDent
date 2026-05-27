package com.diploma.aerodent.data;

import android.app.Application;
import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.repository.*;

public class AppContainer {
    private final AppDatabase database;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final ProcedureLogRepository procedureLogRepository;
    private final ToothStatusRepository toothStatusRepository;

    public AppContainer(Application application) {
        database = AppDatabase.getDatabase(application);
        
        userRepository = new UserRepository(database.userDao());
        photoRepository = new PhotoRepository(database.photoDao(), application);
        patientRepository = new PatientRepository(database.patientDao(), photoRepository);
        appointmentRepository = new AppointmentRepository(database.appointmentDao());
        paymentRepository = new PaymentRepository(database.paymentDao());
        procedureLogRepository = new ProcedureLogRepository(database.procedureLogDao());
        toothStatusRepository = new ToothStatusRepository(database.toothStatusDao());
    }

    public UserRepository getUserRepository() { return userRepository; }
    public PhotoRepository getPhotoRepository() { return photoRepository; }
    public PatientRepository getPatientRepository() { return patientRepository; }
    public AppointmentRepository getAppointmentRepository() { return appointmentRepository; }
    public PaymentRepository getPaymentRepository() { return paymentRepository; }
    public ProcedureLogRepository getProcedureLogRepository() { return procedureLogRepository; }
    public ToothStatusRepository getToothStatusRepository() { return toothStatusRepository; }
}
