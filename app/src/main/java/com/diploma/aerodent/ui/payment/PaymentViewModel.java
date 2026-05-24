package com.diploma.aerodent.ui.payment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.data.repository.PaymentRepository;

import java.util.Date;
import java.util.List;

public class PaymentViewModel extends AndroidViewModel {

    private final PaymentRepository repository;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        repository = new PaymentRepository(application);
    }

    public LiveData<List<Payment>> getPaymentsByAppointmentId(int appointmentId) {
        return repository.getPaymentsByAppointmentId(appointmentId);
    }

    public LiveData<Payment> getPaymentById(int paymentId) {
        return repository.getPaymentById(paymentId);
    }

    public LiveData<List<Payment>> getPaymentsByPatientId(int patientId) {
        return repository.getPaymentsByPatientId(patientId);
    }

    public void saveOrUpdatePayment(int appointmentId, int patientId, double totalAmount, double amountPaid, double nhifCovered, String paymentMethod, String description, Payment existingPayment) {
        Payment payment;
        if (existingPayment != null) {
            payment = existingPayment;
        } else {
            payment = new Payment();
            payment.setAppointmentId(appointmentId);
            payment.setPatientId(patientId);
            payment.setDate(new Date()); 
        }

        payment.setTotalAmount(totalAmount);
        payment.setAmountPaid(amountPaid);
        payment.setNhifCovered(nhifCovered);
        payment.setPaymentMethod(paymentMethod);
        payment.setDescription(description);

        double balance = totalAmount - amountPaid - nhifCovered;
        if (balance <= 0) {
            payment.setStatus("PAID");
        } else if (amountPaid > 0 || nhifCovered > 0) {
            payment.setStatus("PARTIAL");
        } else {
            payment.setStatus("PENDING");
        }

        if (existingPayment != null) {
            repository.update(payment);
        } else {
            repository.insert(payment);
        }
    }

    public void deletePayment(Payment payment) {
        repository.delete(payment);
    }

    public boolean isPaymentValid(List<Payment> existingPayments, int currentPaymentId, double inputTotal, double inputPaid, double inputNhif) {
        double otherPaymentsTotal = getOtherPaymentsTotal(existingPayments, currentPaymentId);
        return (otherPaymentsTotal + inputPaid + inputNhif) <= inputTotal;
    }

    public double getOtherPaymentsTotal(List<Payment> existingPayments, int currentPaymentId) {
        double total = 0;
        if (existingPayments != null) {
            for (Payment p : existingPayments) {
                if (currentPaymentId == -1 || p.getId() != currentPaymentId) {
                    total += p.getAmountPaid() + p.getNhifCovered();
                }
            }
        }
        return total;
    }

    public double getTotalAmount(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) return 0;
        return payments.get(0).getTotalAmount();
    }

    public double getTotalPaid(List<Payment> payments) {
        return getOtherPaymentsTotal(payments, -1);
    }

    public double getBalance(List<Payment> payments) {
        return Math.max(0.0, getTotalAmount(payments) - getTotalPaid(payments));
    }

    public double getRemainingBalance(List<Payment> payments, int currentPaymentId) {
        if (payments == null || payments.isEmpty()) return 0;
        double currentTotalAmount = payments.get(0).getTotalAmount();
        double otherPaid = getOtherPaymentsTotal(payments, currentPaymentId);
        return Math.max(0.0, currentTotalAmount - otherPaid);
    }

    public Payment findPaymentInList(List<Payment> payments, int paymentId) {
        if (payments != null && paymentId != -1) {
            for (Payment p : payments) {
                if (p.getId() == paymentId) {
                    return p;
                }
            }
        }
        return null;
    }

    public boolean isFullyPaid(List<Payment> payments) {
        return "PAID".equals(getPaymentStatus(payments));
    }

    public String getPaymentStatus(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) return "PENDING";
        double balance = getBalance(payments);
        double totalPaid = getTotalPaid(payments);
        if (balance <= 0) return "PAID";
        if (totalPaid > 0) return "PARTIAL";
        return "PENDING";
    }
}
