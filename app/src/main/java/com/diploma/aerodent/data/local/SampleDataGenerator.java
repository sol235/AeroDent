package com.diploma.aerodent.data.local;

import com.diploma.aerodent.data.local.dao.AppointmentDao;
import com.diploma.aerodent.data.local.dao.PatientDao;
import com.diploma.aerodent.data.local.dao.PaymentDao;
import com.diploma.aerodent.data.local.dao.ToothStatusDao;
import com.diploma.aerodent.data.local.dao.ProcedureLogDao;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.entity.ProcedureLog;
import com.diploma.aerodent.data.local.model.DentalCondition;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SampleDataGenerator {

    public static void seedDatabase(AppDatabase db) {
        PatientDao patientDao = db.patientDao();
        AppointmentDao appointmentDao = db.appointmentDao();
        ProcedureLogDao procedureLogDao = db.procedureLogDao();
        PaymentDao paymentDao = db.paymentDao();
        ToothStatusDao toothStatusDao = db.toothStatusDao();

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Иван", "Димитров", "Иванов", "9001011201", "0888123456", "ivan.ivanov@email.com", "1234567890", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Мария", "Петрова", "Георгиева", "9505056711", "0877987654", "maria.petrova@email.com", "0987654321", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Георги", "Тодоров", "Димитров", "8812123303", "0899112233", "georgi.d@email.com", "1122334455", "INACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Елена", "Кирова", "Стоянова", "9203155519", "0888556677", "elena.k@email.com", "5566778899", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Стефан", "Антонов", "Тонев", "8509097701", "0877001122", "stefan.t@email.com", "7788990011", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Димитър", "Колев", "Василев", "7501011103", "0888998877", "dimitar.k@email.com", "2233445566", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Анна", "Попова", "Михайлова", "8202022211", "0877112233", "anna.p@email.com", "3344556677", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Тодор", "Тодоров", "Христов", "6803033306", "0899445566", "todor.t@email.com", "4455667788", "INACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Мария", "Маринова", "Николова", "9104044413", "0888776655", "maria.m@email.com", "5566778899", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Иван", "Петров", "Илиев", "8505055502", "0877334455", "ivan.p@email.com", "6677889900", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Христо", "Петров", "Стоянов", "7206154402", "0888112233", "hristo.s@email.com", "1234567891", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Десислава", "Колева", "Йорданова", "8408201234", "0877223344", "desi.y@email.com", "2345678901", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Николай", "Ангелов", "Костов", "7811105566", "0899334455", "niko.k@email.com", "3456789012", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Радослава", "Стефанова", "Димова", "9302142233", "0887445566", "rada.d@email.com", "4567890123", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Васил", "Георгиев", "Иванов", "6504033344", "0876556677", "vasil.i@email.com", "5678901234", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Моника", "Ивайлова", "Петрова", "9709094455", "0895667788", "moni.p@email.com", "6789012345", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Александър", "Николов", "Христов", "8107251122", "0885778899", "sasho.h@email.com", "7890123456", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Виктория", "Даниелова", "Тодорова", "8912126677", "0879889900", "viki.t@email.com", "8901234567", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Борис", "Валентинов", "Георгиев", "7403158899", "0897990011", "boris.g@email.com", "9012345678", "ACTIVE", true);

        seedPatient(patientDao, appointmentDao, procedureLogDao, paymentDao, toothStatusDao,
                "Силвия", "Красимирова", "Иванова", "9110101133", "0889112244", "silvi.i@email.com", "0123456789", "ACTIVE", true);
    }

    private static void seedPatient(PatientDao pDao, AppointmentDao aDao, ProcedureLogDao plDao, PaymentDao payDao, ToothStatusDao tsDao,
                                   String first, String middle, String last, String egn, String phone, String email, String nhif, String nhifStatus, boolean addDetails) {
        List<Patient> existing = pDao.getAllPatientsSync();
        for (Patient p : existing) {
            if (egn.equals(p.getEgn())) return;
        }

        Patient p = new Patient();
        p.setFirstName(first);
        p.setMiddleName(middle);
        p.setLastName(last);
        p.setEgn(egn);
        p.setPhoneNumber(phone);
        p.setEmail(email);
        p.setNhifNumber(nhif);
        p.setNhifStatus(nhifStatus);
        p.setCreatedAt(new Date());
        long pId = pDao.insert(p);

        if (addDetails) {
            addSampleDetails(pId, egn, aDao, plDao, payDao, tsDao);
        }
    }

    private static void addSampleDetails(long pId, String egn, AppointmentDao aDao, ProcedureLogDao plDao, PaymentDao payDao, ToothStatusDao tsDao) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();

        if ("9001011201".equals(egn)) {
            long aIdPast = insertAppointment(aDao, (int)pId, 0, 9, 0, Appointment.STATUS_COMPLETED, "Follow-up", null);
            insertProcedureLog(plDao, (int)pId, (int)aIdPast, 16, "Checkup", "Status post filling", null);
            insertAppointment(aDao, (int)pId, 1, 10, 0, Appointment.STATUS_SCHEDULED, "Checkup", "First visit");
            
            tsDao.insert(new ToothStatus((int)pId, 18, DentalCondition.MISSING, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 16, DentalCondition.CARIES, "O,M", now, null));
            tsDao.insert(new ToothStatus((int)pId, 11, DentalCondition.OBTURATION, "D", now, null));
            tsDao.insert(new ToothStatus((int)pId, 21, DentalCondition.OBTURATION, "M", now, null));
            tsDao.insert(new ToothStatus((int)pId, 36, DentalCondition.CROWN, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 46, DentalCondition.IMPLANT, null, now, null));
        } else if ("9505056711".equals(egn)) {
            insertAppointment(aDao, (int)pId, 2, 14, 30, Appointment.STATUS_SCHEDULED, "Cleaning", "Regular cleaning");
            tsDao.insert(new ToothStatus((int)pId, 24, DentalCondition.PULPITIS, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 25, DentalCondition.FRACTURE, "B", now, null));
            tsDao.insert(new ToothStatus((int)pId, 31, DentalCondition.CALCULUS, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 32, DentalCondition.CALCULUS, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 41, DentalCondition.CALCULUS, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 42, DentalCondition.CALCULUS, null, now, null));
        } else if ("8812123303".equals(egn)) {
            long aId = insertAppointment(aDao, (int)pId, -5, 11, 0, Appointment.STATUS_COMPLETED, "Treatment", "Caries treatment");
            insertProcedureLog(plDao, (int)pId, (int)aId, 14, "Composite Filling", "Caries profunda", null);
            insertPayment(payDao, (int)pId, (int)aId, 80.0, 0.0, 80.0, "PAID");
            tsDao.insert(new ToothStatus((int)pId, 14, DentalCondition.OBTURATION, "O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 26, DentalCondition.CROWN, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 27, DentalCondition.CROWN, null, now, null));
        } else if ("8509097701".equals(egn)) {
            long aId = insertAppointment(aDao, (int)pId, 0, 16, 0, Appointment.STATUS_SCHEDULED, "Emergency", "Toothache");
            insertPayment(payDao, (int)pId, (int)aId, 50.0, 0.0, 50.0, "PENDING");
            tsDao.insert(new ToothStatus((int)pId, 47, DentalCondition.CARIES, "O,D", now, null));
            tsDao.insert(new ToothStatus((int)pId, 48, DentalCondition.IMPACTED, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 38, DentalCondition.IMPACTED, null, now, null));
        } else if ("7501011103".equals(egn)) {
            long aId = insertAppointment(aDao, (int)pId, -1, 15, 0, Appointment.STATUS_COMPLETED, "Extraction", "Tooth 38 extraction");
            insertProcedureLog(plDao, (int)pId, (int)aId, 38, "Simple Extraction", "Impacted wisdom tooth", null);
            insertPayment(payDao, (int)pId, (int)aId, 120.0, 50.0, 70.0, "PAID");
            tsDao.insert(new ToothStatus((int)pId, 38, DentalCondition.MISSING, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 37, DentalCondition.PONTIC_FIXED, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 36, DentalCondition.CROWN_RETAINER, null, now, null));
        } else if ("6803033306".equals(egn)) {
            long aId = insertAppointment(aDao, (int)pId, -10, 10, 0, Appointment.STATUS_COMPLETED, "Cleaning", null);
            insertProcedureLog(plDao, (int)pId, (int)aId, 0, "Ultrasonic Scaling", "Gingivitis", null);
            insertPayment(payDao, (int)pId, (int)aId, 60.0, 60.0, 0.0, "PAID");
            tsDao.insert(new ToothStatus((int)pId, 11, DentalCondition.PERIODONTITIS, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 12, DentalCondition.PERIODONTITIS, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 21, DentalCondition.PERIODONTITIS, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 22, DentalCondition.PERIODONTITIS, null, now, null));
        } else if ("7206154402".equals(egn)) {
            long aId = insertAppointment(aDao, (int)pId, -3, 9, 30, Appointment.STATUS_COMPLETED, "Consultation", "New patient");
            tsDao.insert(new ToothStatus((int)pId, 11, DentalCondition.CARIES, "M", now, null));
            tsDao.insert(new ToothStatus((int)pId, 21, DentalCondition.CARIES, "D", now, null));
            tsDao.insert(new ToothStatus((int)pId, 16, DentalCondition.ROOT_CANAL, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 16, DentalCondition.OBTURATION, "O", now, null));
        } else if ("8408201234".equals(egn)) {
            insertAppointment(aDao, (int)pId, 5, 11, 0, Appointment.STATUS_SCHEDULED, "Filling", "Tooth 45");
            tsDao.insert(new ToothStatus((int)pId, 45, DentalCondition.CARIES, "O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 46, DentalCondition.OBTURATION, "M,O,D", now, null));
        } else if ("7811105566".equals(egn)) {
            long aId = insertAppointment(aDao, (int)pId, -20, 14, 0, Appointment.STATUS_COMPLETED, "Scaling", null);
            insertPayment(payDao, (int)pId, (int)aId, 70.0, 0.0, 70.0, "PAID");
            tsDao.insert(new ToothStatus((int)pId, 17, DentalCondition.OBTURATION, "O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 27, DentalCondition.OBTURATION, "O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 37, DentalCondition.OBTURATION, "O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 47, DentalCondition.OBTURATION, "O", now, null));
        } else if ("9302142233".equals(egn)) {
            tsDao.insert(new ToothStatus((int)pId, 18, DentalCondition.MISSING, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 28, DentalCondition.MISSING, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 38, DentalCondition.MISSING, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 48, DentalCondition.MISSING, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 14, DentalCondition.CARIES, "O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 24, DentalCondition.CARIES, "O", now, null));
        } else if ("6504033344".equals(egn)) {
            tsDao.insert(new ToothStatus((int)pId, 11, DentalCondition.CROWN, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 12, DentalCondition.CROWN, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 21, DentalCondition.CROWN, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 22, DentalCondition.CROWN, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 13, DentalCondition.CROWN_RETAINER, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 23, DentalCondition.CROWN_RETAINER, null, now, null));
        } else if ("9709094455".equals(egn)) {
            tsDao.insert(new ToothStatus((int)pId, 16, DentalCondition.CARIES, "M,O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 26, DentalCondition.CARIES, "D,O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 36, DentalCondition.CARIES, "O", now, null));
            tsDao.insert(new ToothStatus((int)pId, 46, DentalCondition.CARIES, "O", now, null));
        } else if ("7403158899".equals(egn)) {
            tsDao.insert(new ToothStatus((int)pId, 11, DentalCondition.FRACTURE, "I", now, null));
            tsDao.insert(new ToothStatus((int)pId, 21, DentalCondition.FRACTURE, "I", now, null));
            tsDao.insert(new ToothStatus((int)pId, 16, DentalCondition.MISSING, null, now, null));
            tsDao.insert(new ToothStatus((int)pId, 46, DentalCondition.MISSING, null, now, null));
        }
    }

    private static long insertAppointment(AppointmentDao dao, int pId, int daysOffset, int hour, int minute, String status, String type, String notes) {
        Appointment a = new Appointment();
        a.setPatientId(pId);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, daysOffset);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        a.setDateTime(cal.getTime());
        a.setStatus(status);
        a.setTreatmentType(type);
        a.setNotes(notes);
        a.setCreatedAt(new Date());
        return dao.insert(a);
    }

    private static void insertProcedureLog(ProcedureLogDao dao, int pId, int aId, int tooth, String proc, String diag, String notes) {
        ProcedureLog log = new ProcedureLog();
        log.setPatientId(pId);
        log.setAppointmentId(aId);
        log.setToothNumber(tooth);
        log.setDateLogged(new Date());
        log.setEntryType(ProcedureLog.TYPE_PROCEDURE);
        log.setActionTaken(proc);
        log.setDiagnosis(diag);
        log.setNotes(notes);
        dao.insert(log);
    }

    private static void insertPayment(PaymentDao dao, int pId, int aId, double amount, double nhif, double patient, String status) {
        Payment pay = new Payment();
        pay.setPatientId(pId);
        pay.setAppointmentId(aId);
        pay.setAmount(amount);
        pay.setNhifCovered(nhif);
        pay.setPatientPays(patient);
        pay.setStatus(status);
        pay.setDate(new Date());
        dao.insert(pay);
    }
}
