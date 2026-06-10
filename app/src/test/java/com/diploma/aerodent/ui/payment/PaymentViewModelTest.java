package com.diploma.aerodent.ui.payment;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.data.repository.PaymentRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class PaymentViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application mockApplication;

    @Mock
    private PaymentRepository mockRepository;

    private PaymentViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new PaymentViewModel(mockApplication, mockRepository);
    }

    @Test
    public void testCalculatePaymentBalance() {
        // Balance = Total 100 - Paid 50 - ZokCovered 20 = 30
        Payment payment = new Payment();
        payment.setTotalAmount(100.0);
        payment.setAmountPaid(50.0);
        payment.setZokCovered(20.0);

        double balance = PaymentViewModel.calculatePaymentBalance(payment);
        assertEquals(30.0, balance, 0.001);
    }

    @Test
    public void testGetPaymentStatusForPaid() {
        Payment payment = new Payment();
        payment.setTotalAmount(150.0);
        payment.setAmountPaid(150.0);
        payment.setZokCovered(0.0);

        List<Payment> payments = new ArrayList<>();
        payments.add(payment);

        String status = viewModel.getPaymentStatus(payments);
        assertEquals("PAID", status);
    }

    @Test
    public void testGetPaymentStatusForPartial() {
        // Paid amount - 50 less than total 150 so status is PARTIAL
        Payment payment = new Payment();
        payment.setTotalAmount(150.0);
        payment.setAmountPaid(50.0);
        payment.setZokCovered(0.0);

        List<Payment> payments = new ArrayList<>();
        payments.add(payment);

        String status = viewModel.getPaymentStatus(payments);
        assertEquals("PARTIAL", status);
    }

    @Test
    public void testGetPaymentStatusForPending() {
        Payment payment = new Payment();
        payment.setTotalAmount(150.0);
        payment.setAmountPaid(0.0);
        payment.setZokCovered(0.0);

        List<Payment> payments = new ArrayList<>();
        payments.add(payment);

        String status = viewModel.getPaymentStatus(payments);
        assertEquals("PENDING", status);
    }

    @Test
    public void testCalculateUnpaidAccountsCount() {
        List<Payment> pendingPayments = new ArrayList<>();

        Payment payment1 = new Payment();
        payment1.setPatientId(1);
        payment1.setTotalAmount(50.0);

        Payment payment2 = new Payment();
        payment2.setPatientId(1);
        payment2.setTotalAmount(100.0);

        Payment payment3 = new Payment();
        payment3.setPatientId(2);
        payment3.setTotalAmount(20.0);

        // 3 payments across 2 unique patients
        pendingPayments.add(payment1);
        pendingPayments.add(payment2);
        pendingPayments.add(payment3);

        int count = viewModel.getUnpaidAccountsCount(pendingPayments);
        assertEquals(2, count);
    }

    @Test
    public void testSumOfOtherPayments() {
        List<Payment> payments = new ArrayList<>();

        Payment p1 = new Payment();
        p1.setId(1);
        p1.setAmountPaid(50.0);
        p1.setZokCovered(10.0);

        Payment p2 = new Payment();
        p2.setId(2);
        p2.setAmountPaid(30.0);
        p2.setZokCovered(0.0);

        payments.add(p1);
        payments.add(p2);

        double total = viewModel.getOtherPaymentsTotal(payments, 1);
        assertEquals(30.0, total, 0.001);
    }

    @Test
    public void testIsPaymentValidLogic() {
        List<Payment> payments = new ArrayList<>();
        Payment p1 = new Payment();
        p1.setId(1);
        p1.setAmountPaid(50.0);
        p1.setZokCovered(0.0);
        payments.add(p1);

        // Valid - Previously Paid 50 + New Paid 30 + New Zok 20 = 100 Total
        boolean isValid = viewModel.isPaymentValid(payments, 2, 100.0, 30.0, 20.0);
        assertTrue(isValid);

        // Invalid - Previously Paid 50 + New Paid 60 = 110 > 100 Total
        boolean isInvalid = viewModel.isPaymentValid(payments, 2, 100.0, 60.0, 0.0);
        assertFalse(isInvalid);
    }

    @Test
    public void testTotalAmountAndBalanceCalculations() {
        List<Payment> payments = new ArrayList<>();
        Payment p1 = new Payment();
        p1.setTotalAmount(200.0);
        p1.setAmountPaid(50.0);
        p1.setZokCovered(10.0);
        payments.add(p1);

        Payment p2 = new Payment();
        p2.setAmountPaid(40.0);
        p2.setZokCovered(0.0);
        payments.add(p2);

        assertEquals(200.0, viewModel.getTotalAmount(payments), 0.001);
        assertEquals(100.0, viewModel.getTotalPaid(payments), 0.001);
        assertEquals(100.0, viewModel.getBalance(payments), 0.001);
    }

    @Test
    public void testGetPendingAppointmentsList() {
        List<Payment> allPayments = new ArrayList<>();

        Payment p1 = new Payment();
        p1.setAppointmentId(10);
        p1.setTotalAmount(100.0);
        p1.setAmountPaid(100.0);

        Payment p2 = new Payment();
        p2.setAppointmentId(20);
        p2.setTotalAmount(200.0);
        p2.setAmountPaid(50.0);

        allPayments.add(p1);
        allPayments.add(p2);

        List<Payment> pending = viewModel.getPendingAppointments(allPayments);
        assertEquals(1, pending.size());
        assertEquals(20, pending.get(0).getAppointmentId());
    }

    @Test
    public void testSaveNewPaymentInRepository() {
        viewModel.saveOrUpdatePayment(1, 2, 100.0, 100.0, 0.0, "CASH", "Note", null);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(mockRepository).insert(captor.capture());

        assertEquals(1, captor.getValue().getAppointmentId());
    }

    @Test
    public void testUpdateExistingPaymentInRepository() {
        Payment existing = new Payment();
        existing.setId(5);

        viewModel.saveOrUpdatePayment(1, 2, 100.0, 50.0, 0.0, "CARD", "", existing);

        verify(mockRepository).update(existing);
        assertEquals("PARTIAL", existing.getStatus());
    }
}
