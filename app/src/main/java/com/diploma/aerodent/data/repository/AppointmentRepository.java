package com.diploma.aerodent.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.Date;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.AppointmentDao;
import com.diploma.aerodent.data.local.entity.Appointment;



public class AppointmentRepository {

    private AppointmentDao appointmentDao;
    private LiveData<List<Appointment>> allAppointments;

    public AppointmentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        appointmentDao = db.appointmentDao();
        allAppointments = appointmentDao.getAllAppointments();
    }

    public LiveData<List<Appointment>> getAllAppointments() {
        return allAppointments;
    }

    public LiveData<Appointment> getAppointmentById(int id) {
        return appointmentDao.getAppointmentById(id);
    }

    public LiveData<List<Appointment>> getAppointmentsForPatient(int patientId) {
        return appointmentDao.getAppointmentsForPatient(patientId);
    }

    public LiveData<List<Appointment>> getAppointmentsByStatus(String status) {
        return appointmentDao.getAppointmentsByStatus(status);
    }

    public LiveData<List<Appointment>> getAppointmentsBetweenDates(Date startDate, Date endDate) {
        return appointmentDao.getAppointmentsBetweenDates(startDate, endDate);
    }

    public LiveData<Integer> getAppointmentCountBetween(Date startDate, Date endDate) {
        return appointmentDao.getAppointmentCountBetween(startDate, endDate);
    }

    public void insert(Appointment appointment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            appointmentDao.insert(appointment);
        });
    }

    public void update(Appointment appointment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            appointmentDao.update(appointment);
        });
    }

    public void updateStatus(int appointmentId, String status) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            appointmentDao.updateStatus(appointmentId, status);
        });
    }

    public void delete(Appointment appointment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            appointmentDao.delete(appointment);
        });
    }
}
