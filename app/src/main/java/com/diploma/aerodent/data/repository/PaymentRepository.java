package com.diploma.aerodent.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.Date;
import java.util.List;

import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.PaymentDao;
import com.diploma.aerodent.data.local.entity.Payment;



public class PaymentRepository {

    private PaymentDao paymentDao;
    private LiveData<List<Payment>> allPayments;

    public PaymentRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        paymentDao = db.paymentDao();
        allPayments = paymentDao.getAllPayments();
    }

    public LiveData<List<Payment>> getAllPayments() {
        return allPayments;
    }

    public LiveData<Payment> getPaymentById(int paymentId) {
        return paymentDao.getPaymentById(paymentId);
    }

    public LiveData<List<Payment>> getPaymentsForPatient(int patientId) {
        return paymentDao.getPaymentsForPatient(patientId);
    }

    public LiveData<Payment> getPaymentForAppointment(int appointmentId) {
        return paymentDao.getPaymentForAppointment(appointmentId);
    }

    public LiveData<List<Payment>> getPaymentsByStatus(String status) {
        return paymentDao.getPaymentsByStatus(status);
    }

    public LiveData<List<Payment>> getPaymentsBetweenDates(Date startDate, Date endDate) {
        return paymentDao.getPaymentsBetweenDates(startDate, endDate);
    }

    public LiveData<Double> getTotalAmountForPatient(int patientId) {
        return paymentDao.getTotalAmountForPatient(patientId);
    }

    public LiveData<Double> getTotalNhifCoveredForPatient(int patientId) {
        return paymentDao.getTotalNhifCoveredForPatient(patientId);
    }

    public LiveData<Double> getTotalPendingAmount() {
        return paymentDao.getTotalPendingAmount();
    }

    public LiveData<Double> getTotalRevenueBetween(Date startDate, Date endDate) {
        return paymentDao.getTotalRevenueBetween(startDate, endDate);
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

    public void markAsPaid(int paymentId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.markAsPaid(paymentId);
        });
    }

    public void delete(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            paymentDao.delete(payment);
        });
    }
}
