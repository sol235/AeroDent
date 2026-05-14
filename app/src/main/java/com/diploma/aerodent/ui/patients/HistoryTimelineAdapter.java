package com.diploma.aerodent.ui.patients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diploma.aerodent.R;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Payment;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryTimelineAdapter extends RecyclerView.Adapter<HistoryTimelineAdapter.ViewHolder> {

    private List<PatientHistoryItem> items = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientHistoryItem item = items.get(position);
        Appointment appointment = item.getAppointment();
        Payment payment = item.getPayment();

        if (appointment.getDateTime() != null) {
            holder.textDate.setText(dateFormat.format(appointment.getDateTime()));
        }

        holder.textDescription.setText(appointment.getTreatmentType() != null ? appointment.getTreatmentType() : holder.itemView.getContext().getString(R.string.checkup));

        if (payment != null) {
            String statusText;
            int bgColor;
            int textColor;

            if ("PAID".equals(payment.getStatus())) {
                statusText = holder.itemView.getContext().getString(R.string.payment_status_paid, payment.getAmount());
                bgColor = holder.itemView.getContext().getColor(R.color.chip_green_bg);
                textColor = holder.itemView.getContext().getColor(R.color.chip_green_text);
            } else if (payment.getNhifCovered() > 0) {
                statusText = holder.itemView.getContext().getString(R.string.nzok_short);
                bgColor = holder.itemView.getContext().getColor(R.color.chip_green_bg);
                textColor = holder.itemView.getContext().getColor(R.color.chip_green_text);
            } else {
                statusText = holder.itemView.getContext().getString(R.string.payment_status_due, payment.getAmount());
                bgColor = holder.itemView.getContext().getColor(R.color.chip_orange_bg);
                textColor = holder.itemView.getContext().getColor(R.color.chip_orange_text);
            }

            holder.textPaymentStatus.setText(statusText);
            holder.cardPaymentStatus.setCardBackgroundColor(bgColor);
            holder.textPaymentStatus.setTextColor(textColor);
            holder.cardPaymentStatus.setVisibility(View.VISIBLE);
        } else {
            holder.cardPaymentStatus.setVisibility(View.GONE);
        }

        holder.timelineLine.setVisibility(position == items.size() - 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<PatientHistoryItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate;
        TextView textDescription;
        TextView textPaymentStatus;
        MaterialCardView cardPaymentStatus;
        View timelineLine;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textDescription = itemView.findViewById(R.id.text_description);
            textPaymentStatus = itemView.findViewById(R.id.text_payment_status);
            cardPaymentStatus = itemView.findViewById(R.id.card_payment_status);
            timelineLine = itemView.findViewById(R.id.timeline_line);
        }
    }
}
