package com.diploma.aerodent.ui.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentTransactionAdapter extends RecyclerView.Adapter<PaymentTransactionAdapter.ViewHolder> {

    private List<Payment> payments = new ArrayList<>();
    private final Locale locale = new Locale("bg", "BG");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", locale);

    public interface OnTransactionClickListener {
        void onTransactionClick(Payment payment);
    }

    private OnTransactionClickListener listener;

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
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

        double transactionTotal = payment.getAmountPaid() + payment.getZokCovered();
        holder.textAmount.setText(String.format(Locale.getDefault(), "+ %.2f EUR", transactionTotal));

        String methodText;
        if (payment.getAmountPaid() == 0 && payment.getZokCovered() > 0) {
            methodText = "НЗОК";
        } else {
            methodText = payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "";
            if (payment.getZokCovered() > 0) {
                methodText += " + НЗОК";
            }
        }
        holder.textMethod.setText(methodText);

        String desc = payment.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            holder.textDesc.setText(holder.itemView.getContext().getString(R.string.payment_details_title));
        } else {
            holder.textDesc.setText(desc);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(payment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
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
