package com.diploma.aerodent.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.diploma.aerodent.data.local.entity.Payment;

import java.util.Date;
import java.util.List;

@Dao
public interface PaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Payment payment);

    @Update
    void update(Payment payment);

    @Query("UPDATE payments SET status = 'PAID' WHERE id = :paymentId")
    void markAsPaid(int paymentId);

    @Delete
    void delete(Payment payment);

    @Query("DELETE FROM payments WHERE id = :paymentId")
    void deleteById(int paymentId);

    @Query("SELECT * FROM payments ORDER BY date DESC")
    LiveData<List<Payment>> getAllPayments();

    @Query("SELECT * FROM payments WHERE id = :paymentId LIMIT 1")
    LiveData<Payment> getPaymentById(int paymentId);

    @Query("SELECT * FROM payments WHERE patientId = :patientId ORDER BY date DESC")
    LiveData<List<Payment>> getPaymentsForPatient(int patientId);

    @Query("SELECT * FROM payments WHERE patientId = :patientId ORDER BY date DESC")
    List<Payment> getPaymentsForPatientSync(int patientId);

    @Query("SELECT * FROM payments ORDER BY date DESC")
    List<Payment> getAllPaymentsSync();

    @Query("SELECT * FROM payments WHERE appointmentId = :appointmentId LIMIT 1")
    LiveData<Payment> getPaymentForAppointment(int appointmentId);

    @Query("SELECT * FROM payments WHERE status = :status ORDER BY date DESC")
    LiveData<List<Payment>> getPaymentsByStatus(String status);

    @Query("SELECT * FROM payments WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    LiveData<List<Payment>> getPaymentsBetweenDates(Date startDate, Date endDate);

    @Query("SELECT * FROM payments WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    List<Payment> getPaymentsBetweenDatesSync(Date startDate, Date endDate);

    // Aggregations
    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE patientId = :patientId")
    LiveData<Double> getTotalAmountForPatient(int patientId);

    @Query("SELECT COALESCE(SUM(nhifCovered), 0) FROM payments WHERE patientId = :patientId")
    LiveData<Double> getTotalNhifCoveredForPatient(int patientId);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'PENDING'")
    LiveData<Double> getTotalPendingAmount();

    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalRevenueBetween(Date startDate, Date endDate);
}