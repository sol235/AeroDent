package com.diploma.aerodent.ui.payment;

import com.diploma.aerodent.AeroDentApplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.ui.appointments.AppointmentDetailFragment;
import com.diploma.aerodent.ui.patients.PatientViewModel;

import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentDetailsFragment extends Fragment {

    private PaymentViewModel paymentViewModel;
    private PatientViewModel patientViewModel;
    private PendingPaymentAdapter adapter;

    private TextView textTotalOutstanding;
    private TextView textUnpaidAccountsCount;
    private View layoutNoPendingPayments;
    private RecyclerView recyclerPendingPayments;

    private List<Payment> currentPendingPayments;
    private Map<Integer, Patient> currentPatientsMap = new HashMap<>();

    public PaymentDetailsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textTotalOutstanding = view.findViewById(R.id.text_total_outstanding);
        textUnpaidAccountsCount = view.findViewById(R.id.text_unpaid_accounts_count);
        layoutNoPendingPayments = view.findViewById(R.id.layout_no_pending_payments);
        recyclerPendingPayments = view.findViewById(R.id.recycler_pending_payments);

        adapter = new PendingPaymentAdapter();
        recyclerPendingPayments.setAdapter(adapter);

        com.google.android.material.appbar.MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        AeroDentApplication app = (AeroDentApplication) requireActivity().getApplication();
        paymentViewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(PaymentViewModel.class);
        patientViewModel = new ViewModelProvider(this, app.getViewModelFactory()).get(PatientViewModel.class);

        patientViewModel.setSearchQuery("");

        patientViewModel.getSearchResults().observe(getViewLifecycleOwner(), patients -> {
            if (patients != null) {
                currentPatientsMap.clear();
                for (Patient p : patients) {
                    currentPatientsMap.put(p.getId(), p);
                }
                updateUI();
            }
        });

        paymentViewModel.getPendingAppointmentsLiveData().observe(getViewLifecycleOwner(), pendingPayments -> {
            processPendingPayments(pendingPayments);
        });

        adapter.setOnPaymentClickListener(payment -> {
            AppointmentDetailFragment fragment = AppointmentDetailFragment
                    .newInstanceWithPaymentsTab(payment.getAppointmentId());
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragment)
                    .addToBackStack(null).commit();
        });
    }

    private void updateUI() {
        if (currentPendingPayments != null) {
            adapter.setData(currentPendingPayments, currentPatientsMap);
        }
    }

    private void processPendingPayments(List<Payment> pendingPayments) {
        if (pendingPayments == null)
            return;

        currentPendingPayments = pendingPayments;

        double totalOutstanding = paymentViewModel.getTotalOutstanding(currentPendingPayments);
        int unpaidAccountsCount = paymentViewModel.getUnpaidAccountsCount(currentPendingPayments);

        textTotalOutstanding.setText(String.format(Locale.getDefault(), "%.2f EUR", totalOutstanding));
        textUnpaidAccountsCount.setText(String.valueOf(unpaidAccountsCount));

        if (currentPendingPayments.isEmpty()) {
            layoutNoPendingPayments.setVisibility(View.VISIBLE);
            recyclerPendingPayments.setVisibility(View.GONE);
        } else {
            layoutNoPendingPayments.setVisibility(View.GONE);
            recyclerPendingPayments.setVisibility(View.VISIBLE);
            adapter.setData(currentPendingPayments, currentPatientsMap);
        }
    }
}
