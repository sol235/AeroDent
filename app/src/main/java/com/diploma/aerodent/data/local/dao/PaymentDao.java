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

    @Delete
    void delete(Payment payment);

    @Query("SELECT * FROM payments WHERE id = :paymentId LIMIT 1")
    LiveData<Payment> getPaymentById(int paymentId);

    @Query("SELECT * FROM payments WHERE patientId = :patientId ORDER BY date DESC")
    LiveData<List<Payment>> getPaymentsByPatientId(int patientId);

    @Query("SELECT * FROM payments WHERE appointmentId = :appointmentId ORDER BY date DESC")
    LiveData<List<Payment>> getPaymentsByAppointmentId(int appointmentId);

    @Query("SELECT * FROM payments WHERE status != 'PAID' ORDER BY date DESC")
    LiveData<List<Payment>> getPendingPayments();

}