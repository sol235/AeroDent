package com.diploma.aerodent.ui.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.util.NameUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PendingPaymentAdapter extends RecyclerView.Adapter<PendingPaymentAdapter.ViewHolder> {

    private List<Payment> payments = new ArrayList<>();
    private Map<Integer, Patient> patientsMap;
    private final Locale locale = new Locale("bg", "BG");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", locale);

    public interface OnPaymentClickListener {
        void onPaymentClick(Payment payment);
    }

    private OnPaymentClickListener listener;

    public void setOnPaymentClickListener(OnPaymentClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = payments.get(position);

        if (payment.getDate() != null) {
            holder.textDate.setText(dateFormat.format(payment.getDate()));
        }

        // Calculate Balance
        double balance = PaymentViewModel.calculatePaymentBalance(payment);
        holder.textAmount.setText(String.format(Locale.getDefault(), "%.2f EUR", balance));
        holder.textAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_red_text));

        // Status
        String status = payment.getStatus();
        if ("PARTIAL".equals(status)) {
            holder.textMethod.setText(R.string.status_partial);
            holder.textMethod.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_orange_text));
        } else {
            holder.textMethod.setText(R.string.status_pending);
            holder.textMethod.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.chip_red_text));
        }

        // Patient Name
        if (patientsMap != null && patientsMap.containsKey(payment.getPatientId())) {
            Patient patient = patientsMap.get(payment.getPatientId());
            if (patient != null) {
                holder.textDesc.setText(NameUtils.formatFirstLastName(patient));
            }
        } else {
            holder.textDesc.setText(R.string.payment_loading);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPaymentClick(payment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public void setData(List<Payment> payments, Map<Integer, Patient> patientsMap) {
        this.payments = payments;
        this.patientsMap = patientsMap;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;
        TextView textMethod;
        TextView textDesc;
        TextView textAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_transaction_date);
            textMethod = itemView.findViewById(R.id.text_transaction_method);
            textDesc = itemView.findViewById(R.id.text_transaction_desc);
            textAmount = itemView.findViewById(R.id.text_transaction_amount);
        }
    }
}
