package com.diploma.aerodent.ui.payment;

import com.diploma.aerodent.AeroDentApplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Payment;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class PaymentFragment extends Fragment {

    private static final String ARG_APPOINTMENT_ID = "appointment_id";
    private static final String ARG_PATIENT_ID = "patient_id";

    private int appointmentId;
    private int patientId;
    private PaymentViewModel paymentViewModel;

    private View layoutNoPayment;
    private MaterialCardView cardPaymentDetails;
    private TextView textTotalAmount;
    private TextView textAmountPaid;
    private TextView textBalance;
    private MaterialCardView chipStatus;
    private TextView textStatus;
    private TextView textHistoryTitle;
    private RecyclerView recyclerPayments;
    private PaymentTransactionAdapter adapter;

    public static PaymentFragment newInstance(int appointmentId, int patientId) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_APPOINTMENT_ID, appointmentId);
        args.putInt(ARG_PATIENT_ID, patientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointmentId = getArguments().getInt(ARG_APPOINTMENT_ID);
            patientId = getArguments().getInt(ARG_PATIENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        paymentViewModel = new ViewModelProvider(requireActivity(), app.getViewModelFactory()).get(PaymentViewModel.class);

        layoutNoPayment = view.findViewById(R.id.layout_no_payment);
        cardPaymentDetails = view.findViewById(R.id.card_payment_details);

        textTotalAmount = view.findViewById(R.id.text_total_amount);
        textAmountPaid = view.findViewById(R.id.text_amount_paid);
        textBalance = view.findViewById(R.id.text_balance);
        chipStatus = view.findViewById(R.id.chip_status);
        textStatus = view.findViewById(R.id.text_status);
        textHistoryTitle = view.findViewById(R.id.text_history_title);
        recyclerPayments = view.findViewById(R.id.recycler_payments);

        adapter = new PaymentTransactionAdapter();
        adapter.setOnTransactionClickListener(payment -> {
            PaymentEntryFragment bottomSheet = PaymentEntryFragment.newInstance(appointmentId, patientId, payment.getId());
            bottomSheet.show(getParentFragmentManager(), "PaymentEntryFragment");
        });
        recyclerPayments.setAdapter(adapter);
        recyclerPayments.setNestedScrollingEnabled(false);

        view.findViewById(R.id.btn_create_payment).setOnClickListener(v -> openBottomSheet());
        view.findViewById(R.id.btn_edit_payment).setOnClickListener(v -> openBottomSheet());

        observePayment();
    }

    private void observePayment() {
        paymentViewModel.getPaymentsByAppointmentId(appointmentId).observe(getViewLifecycleOwner(), payments -> {
            if (payments != null && !payments.isEmpty()) {
                layoutNoPayment.setVisibility(View.GONE);
                cardPaymentDetails.setVisibility(View.VISIBLE);
                textHistoryTitle.setVisibility(View.VISIBLE);
                recyclerPayments.setVisibility(View.VISIBLE);
                bindPaymentData(payments);
            } else {
                layoutNoPayment.setVisibility(View.VISIBLE);
                cardPaymentDetails.setVisibility(View.GONE);
                textHistoryTitle.setVisibility(View.GONE);
                recyclerPayments.setVisibility(View.GONE);
            }
        });
    }

    private void bindPaymentData(List<Payment> payments) {
        double totalAmount = paymentViewModel.getTotalAmount(payments);
        double totalPaid = paymentViewModel.getTotalPaid(payments);
        double balance = paymentViewModel.getBalance(payments);
        String status = paymentViewModel.getPaymentStatus(payments);

        textTotalAmount.setText(String.format(java.util.Locale.getDefault(), "%.2f EUR", totalAmount));
        textAmountPaid.setText(String.format(java.util.Locale.getDefault(), "%.2f EUR", totalPaid));
        textBalance.setText(String.format(java.util.Locale.getDefault(), "%.2f EUR", balance));
        int statusTextRes = R.string.status_pending;
        int colorBgRes = R.color.chip_red_bg;
        int colorTextRes = R.color.chip_red_text;

        if ("PAID".equals(status)) {
            statusTextRes = R.string.status_paid;
            colorBgRes = R.color.chip_green_bg;
            colorTextRes = R.color.chip_green_text;
        } else if ("PARTIAL".equals(status)) {
            statusTextRes = R.string.status_partial;
            colorBgRes = R.color.chip_orange_bg;
            colorTextRes = R.color.chip_orange_text;
        }

        textStatus.setText(getString(statusTextRes));
        chipStatus.setCardBackgroundColor(ContextCompat.getColor(requireContext(), colorBgRes));
        textStatus.setTextColor(ContextCompat.getColor(requireContext(), colorTextRes));
        
        View btnEditPayment = getView() != null ? getView().findViewById(R.id.btn_edit_payment) : null;
        if (btnEditPayment != null) {
            btnEditPayment.setVisibility(paymentViewModel.isFullyPaid(payments) ? View.GONE : View.VISIBLE);
        }
        
        adapter.setPayments(payments);
    }

    private void openBottomSheet() {
        PaymentEntryFragment bottomSheet = PaymentEntryFragment.newInstance(appointmentId, patientId);
        bottomSheet.show(getParentFragmentManager(), "PaymentEntryFragment");
    }
}
