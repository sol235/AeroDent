package com.diploma.aerodent.ui.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Payment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class PaymentEntryFragment extends BottomSheetDialogFragment {

    private static final String ARG_APPOINTMENT_ID = "appointment_id";
    private static final String ARG_PATIENT_ID = "patient_id";
    private static final String ARG_PAYMENT_ID = "payment_id";

    private PaymentViewModel paymentViewModel;
    private int appointmentId;
    private int patientId;
    private int paymentId = -1;

    private TextInputEditText inputTotalAmount;
    private TextInputEditText inputAmountPaid;
    private TextInputEditText inputNhifCovered;
    private AutoCompleteTextView dropdownPaymentMethod;
    private android.widget.TextView textAlreadyPaid;
    private List<Payment> loadedPayments;
    private com.google.android.material.button.MaterialButton btnSavePayment;
    private com.google.android.material.textfield.TextInputLayout layoutAmountPaid;

    public static PaymentEntryFragment newInstance(int appointmentId, int patientId) {
        return newInstance(appointmentId, patientId, -1);
    }

    public static PaymentEntryFragment newInstance(int appointmentId, int patientId, int paymentId) {
        PaymentEntryFragment fragment = new PaymentEntryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_APPOINTMENT_ID, appointmentId);
        args.putInt(ARG_PATIENT_ID, patientId);
        args.putInt(ARG_PAYMENT_ID, paymentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointmentId = getArguments().getInt(ARG_APPOINTMENT_ID);
            patientId = getArguments().getInt(ARG_PATIENT_ID);
            paymentId = getArguments().getInt(ARG_PAYMENT_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);

        inputTotalAmount = view.findViewById(R.id.input_total_amount);
        inputAmountPaid = view.findViewById(R.id.input_amount_paid);
        inputNhifCovered = view.findViewById(R.id.input_nhif_covered);
        dropdownPaymentMethod = view.findViewById(R.id.dropdown_payment_method);
        textAlreadyPaid = view.findViewById(R.id.text_already_paid);
        btnSavePayment = view.findViewById(R.id.btn_save_payment);
        layoutAmountPaid = view.findViewById(R.id.layout_amount_paid);

        setupDropdown();

        View btnDeletePayment = view.findViewById(R.id.btn_delete_payment);

        if (paymentId != -1) {
            if (btnDeletePayment != null) {
                btnDeletePayment.setVisibility(View.VISIBLE);
                btnDeletePayment.setOnClickListener(v -> confirmDeletePayment());
            }
            if (layoutAmountPaid != null) {
                layoutAmountPaid.setHint(getString(R.string.payment_amount_paid_edit_hint));
            }
        }

        paymentViewModel.getPaymentsByAppointmentId(appointmentId).observe(getViewLifecycleOwner(),
                this::populatePaymentData);

        android.text.TextWatcher watcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                validateAmount();
            }
        };

        inputTotalAmount.addTextChangedListener(watcher);
        inputAmountPaid.addTextChangedListener(watcher);
        inputNhifCovered.addTextChangedListener(watcher);

        btnSavePayment.setOnClickListener(v -> savePayment());
    }

    private Payment getExistingPayment() {
        return paymentViewModel.findPaymentInList(loadedPayments, paymentId);
    }

    private void populatePaymentData(List<Payment> payments) {
        loadedPayments = payments;
        if (payments != null && !payments.isEmpty()) {
            Payment firstPayment = payments.get(0);

            if (isInputEmpty(inputTotalAmount)) {
                inputTotalAmount.setText(String.valueOf(firstPayment.getTotalAmount()));
            }

            Payment existingPayment = getExistingPayment();
            if (existingPayment != null && isInputEmpty(inputAmountPaid)) {
                inputAmountPaid.setText(String.valueOf(existingPayment.getAmountPaid()));
                inputNhifCovered.setText(String.valueOf(existingPayment.getNhifCovered()));
                dropdownPaymentMethod.setText(existingPayment.getPaymentMethod(), false);
                TextInputEditText inputDesc = getView().findViewById(R.id.input_description);
                if (inputDesc != null && existingPayment.getDescription() != null) {
                    inputDesc.setText(existingPayment.getDescription());
                }
            }

            double otherPaid = paymentViewModel.getOtherPaymentsTotal(payments, paymentId);
            double balance = paymentViewModel.getRemainingBalance(payments, paymentId);

            if (otherPaid > 0) {
                textAlreadyPaid.setVisibility(View.VISIBLE);
                textAlreadyPaid.setText(String.format(java.util.Locale.US,
                        "Вече платено: %.2f EUR  -  Остават: %.2f EUR", otherPaid, balance));
            } else {
                textAlreadyPaid.setVisibility(View.GONE);
            }
        }
        validateAmount();
    }

    private void setupDropdown() {
        String[] methods = { getString(R.string.payment_method_cash), getString(R.string.payment_method_card) };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line,
                methods);
        dropdownPaymentMethod.setAdapter(adapter);
    }

    private void savePayment() {
        double totalAmount = parseInput(inputTotalAmount);
        double amountPaid = parseInput(inputAmountPaid);
        double nhifCovered = parseInput(inputNhifCovered);

        String paymentMethod = dropdownPaymentMethod.getText().toString();
        if (paymentMethod.isEmpty()) {
            paymentMethod = getString(R.string.payment_method_cash);
        }

        TextInputEditText inputDescription = getView().findViewById(R.id.input_description);
        String description = inputDescription.getText() != null ? inputDescription.getText().toString() : "";

        paymentViewModel.saveOrUpdatePayment(appointmentId, patientId, totalAmount, amountPaid, nhifCovered,
                paymentMethod, description, getExistingPayment());
        dismiss();
    }

    private void validateAmount() {
        double total = parseInput(inputTotalAmount);
        double paid = parseInput(inputAmountPaid);
        double nhif = parseInput(inputNhifCovered);

        boolean isValid = paymentViewModel.isPaymentValid(loadedPayments, paymentId, total, paid, nhif);
        boolean showWarning = !isValid && total > 0;

        if (btnSavePayment != null)
            btnSavePayment.setEnabled(!showWarning);

        View view = getView();
        if (view != null) {
            android.widget.TextView textTotalError = view.findViewById(R.id.text_total_error);
            android.widget.TextView textPaidError = view.findViewById(R.id.text_paid_error);
            android.widget.TextView textNhifError = view.findViewById(R.id.text_nhif_error);

            if (textTotalError != null) {
                textTotalError.setVisibility(showWarning && inputTotalAmount.hasFocus() ? View.VISIBLE : View.GONE);
            }
            if (textNhifError != null) {
                textNhifError.setVisibility(showWarning && inputNhifCovered.hasFocus() ? View.VISIBLE : View.GONE);
            }
            if (textPaidError != null) {
                boolean paidError = showWarning && !inputTotalAmount.hasFocus() && !inputNhifCovered.hasFocus();
                textPaidError.setVisibility(paidError ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void confirmDeletePayment() {
        Payment existingPayment = getExistingPayment();
        if (existingPayment == null)
            return;

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_payment_title).setMessage(R.string.delete_payment_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    paymentViewModel.deletePayment(existingPayment);
                    dismiss();
                }).setNegativeButton(R.string.cancel, null).show();
    }

    private boolean isInputEmpty(TextInputEditText input) {
        return input.getText() == null || input.getText().toString().isEmpty();
    }

    private double parseInput(TextInputEditText input) {
        if (!isInputEmpty(input)) {
            try {
                return Double.parseDouble(input.getText().toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
