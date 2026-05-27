package com.diploma.aerodent.data.repository;

import androidx.lifecycle.LiveData;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.PaymentDao;
import com.diploma.aerodent.data.local.entity.Payment;

public class PaymentRepository {
    private final PaymentDao paymentDao;

    public PaymentRepository(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    public LiveData<List<Payment>> getPaymentsByPatientId(int patientId) {
        return paymentDao.getPaymentsByPatientId(patientId);
    }

    public LiveData<List<Payment>> getPaymentsByAppointmentId(int appointmentId) {
        return paymentDao.getPaymentsByAppointmentId(appointmentId);
    }

    public LiveData<List<Payment>> getAllPayments() {
        return paymentDao.getAllPayments();
    }

    public void insert(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.insert(payment);
        });
    }

    public void update(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.update(payment);
        });
    }

    public void delete(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.delete(payment);
        });
    }
}
