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

    public PaymentViewModel(@NonNull Application application, PaymentRepository repository) {
        super(application);
        this.repository = repository;
    }

    public LiveData<List<Payment>> getPaymentsByAppointmentId(int appointmentId) {
        return repository.getPaymentsByAppointmentId(appointmentId);
    }

    public LiveData<List<Payment>> getAllPayments() {
        return repository.getAllPayments();
    }



    public void saveOrUpdatePayment(int appointmentId, int patientId, double totalAmount, double amountPaid, double zokCovered, String paymentMethod, String description, Payment existingPayment) {
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
        payment.setZokCovered(zokCovered);
        payment.setPaymentMethod(paymentMethod);
        payment.setDescription(description);

        double balance = calculatePaymentBalance(payment);
        if (balance <= 0) {
            payment.setStatus("PAID");
        } else if (amountPaid > 0 || zokCovered > 0) {
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
                    total += p.getAmountPaid() + p.getZokCovered();
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

    public List<Payment> getPendingAppointments(List<Payment> allPayments) {
        if (allPayments == null) return new java.util.ArrayList<>();

        java.util.Map<Integer, Payment> appointmentBalances = new java.util.HashMap<>();
        for (Payment payment : allPayments) {
            Payment summary = appointmentBalances.get(payment.getAppointmentId());
            if (summary == null) {
                summary = new Payment();
                summary.setAppointmentId(payment.getAppointmentId());
                summary.setPatientId(payment.getPatientId());
                summary.setTotalAmount(payment.getTotalAmount());
                summary.setDate(payment.getDate());
                appointmentBalances.put(payment.getAppointmentId(), summary);
            }
            summary.setAmountPaid(summary.getAmountPaid() + payment.getAmountPaid());
            summary.setZokCovered(summary.getZokCovered() + payment.getZokCovered());
        }

        List<Payment> pendingList = new java.util.ArrayList<>();
        for (Payment summary : appointmentBalances.values()) {
            double balance = calculatePaymentBalance(summary);
            if (balance > 0) {
                summary.setStatus(summary.getAmountPaid() + summary.getZokCovered() > 0 ? "PARTIAL" : "PENDING");
                pendingList.add(summary);
            }
        }
        pendingList.sort((p1, p2) -> {
            if (p1.getDate() == null || p2.getDate() == null) return 0;
            return p2.getDate().compareTo(p1.getDate());
        });
        return pendingList;
    }

    public double getTotalOutstanding(List<Payment> pendingPayments) {
        double totalOutstanding = 0;
        if (pendingPayments != null) {
            for (Payment summary : pendingPayments) {
                totalOutstanding += calculatePaymentBalance(summary);
            }
        }
        return totalOutstanding;
    }

    public int getUnpaidAccountsCount(List<Payment> pendingPayments) {
        java.util.Set<Integer> uniquePatients = new java.util.HashSet<>();
        if (pendingPayments != null) {
            for (Payment summary : pendingPayments) {
                uniquePatients.add(summary.getPatientId());
            }
        }
        return uniquePatients.size();
    }
    public LiveData<List<Payment>> getPendingAppointmentsLiveData() {
        return androidx.lifecycle.Transformations.map(repository.getAllPayments(), payments -> getPendingAppointments(payments));
    }

    public static double calculatePaymentBalance(Payment payment) {
        if (payment == null) return 0.0;
        return payment.getTotalAmount() - payment.getAmountPaid() - payment.getZokCovered();
    }
}
