package com.diploma.aerodent.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diploma.aerodent.data.local.entity.Appointment;

import java.util.Date;
import java.util.List;


@Dao
public interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Appointment appointment);

    @Update
    void update(Appointment appointment);

    @Query("UPDATE appointments SET status = :status WHERE id = :appointmentId")
    void updateStatus(int appointmentId, String status);

    @Delete
    void delete(Appointment appointment);

    @Query("SELECT * FROM appointments ORDER BY dateTime DESC")
    LiveData<List<Appointment>> getAllAppointments();

    @Query("SELECT * FROM appointments WHERE id = :appointmentId LIMIT 1")
    LiveData<Appointment> getAppointmentById(int appointmentId);

    @Query("SELECT * FROM appointments WHERE id = :appointmentId LIMIT 1")
    Appointment getAppointmentByIdSync(int appointmentId);

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY dateTime DESC")
    LiveData<List<Appointment>> getAppointmentsForPatient(int patientId);

    @Query("SELECT * FROM appointments WHERE patientId = :patientId ORDER BY dateTime DESC")
    List<Appointment> getAppointmentsForPatientSync(int patientId);

    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY dateTime ASC")
    LiveData<List<Appointment>> getAppointmentsByStatus(String status);

    //Used for calendar view integration

    @Query("SELECT * FROM appointments WHERE dateTime BETWEEN :startDate AND :endDate " +
           "ORDER BY dateTime ASC")
    LiveData<List<Appointment>> getAppointmentsBetweenDates(Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM appointments WHERE dateTime BETWEEN :startDate AND :endDate")
    LiveData<Integer> getAppointmentCountBetween(Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM appointments WHERE dateTime BETWEEN :startDate AND :endDate AND status = :status")
    LiveData<Integer> getAppointmentCountBetweenByStatus(Date startDate, Date endDate, String status);
}
