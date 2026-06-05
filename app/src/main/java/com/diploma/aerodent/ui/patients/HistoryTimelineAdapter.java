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
import com.diploma.aerodent.ui.patients.PatientDetailViewModel.PatientHistoryItem;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryTimelineAdapter extends RecyclerView.Adapter<HistoryTimelineAdapter.ViewHolder> {

    public interface OnHistoryItemClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    private List<PatientHistoryItem> items = new ArrayList<>();
    private final Locale bulgarianLocale = new Locale("bg", "BG");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", bulgarianLocale);
    private OnHistoryItemClickListener listener;

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientHistoryItem item = items.get(position);
        Appointment appointment = item.getAppointment();
        List<Payment> payments = item.getPayments();

        if (appointment.getDateTime() != null) {
            holder.textDate.setText(dateFormat.format(appointment.getDateTime()));
        }

        holder.textDescription.setText(appointment.getTreatmentType() != null ? appointment.getTreatmentType()
                : holder.itemView.getContext().getString(R.string.checkup));

        if (payments != null && !payments.isEmpty()) {
            Payment firstPayment = payments.get(0);
            double totalAmount = firstPayment.getTotalAmount();
            double zokCovered = 0;
            double amountPaid = 0;
            
            for (Payment p : payments) {
                amountPaid += p.getAmountPaid();
                zokCovered += p.getZokCovered();
            }
            
            double totalPaid = amountPaid + zokCovered;
            double balance = totalAmount - totalPaid;

            String statusText;
            int bgColor;
            int textColor;

            if (balance <= 0 && totalAmount > 0) {
                statusText = holder.itemView.getContext().getString(R.string.status_paid) + " - " + 
                        String.format(java.util.Locale.getDefault(), "%.2f EUR", totalAmount);
                bgColor = holder.itemView.getContext().getColor(R.color.chip_green_bg);
                textColor = holder.itemView.getContext().getColor(R.color.chip_green_text);
            } else if (totalPaid > 0) {
                statusText = holder.itemView.getContext().getString(R.string.status_partial) + " - "
                        + holder.itemView.getContext().getString(R.string.payment_status_due, Math.max(0.0, balance));
                bgColor = holder.itemView.getContext().getColor(R.color.chip_orange_bg);
                textColor = holder.itemView.getContext().getColor(R.color.chip_orange_text);
            } else {
                statusText = holder.itemView.getContext().getString(R.string.status_pending) + " - " + 
                        holder.itemView.getContext().getString(R.string.payment_status_due, Math.max(0.0, balance));
                bgColor = holder.itemView.getContext().getColor(R.color.chip_red_bg);
                textColor = holder.itemView.getContext().getColor(R.color.chip_red_text);
            }

            holder.textPaymentStatus.setText(statusText);
            holder.cardPaymentStatus.setCardBackgroundColor(bgColor);
            holder.textPaymentStatus.setTextColor(textColor);
            holder.cardPaymentStatus.setVisibility(View.VISIBLE);
        } else {
            holder.cardPaymentStatus.setVisibility(View.GONE);
        }

        holder.timelineLineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.timelineLineBottom.setVisibility(position == items.size() - 1 ? View.INVISIBLE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppointmentClick(appointment);
            }
        });
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
        View timelineLineTop;
        View timelineLineBottom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textDescription = itemView.findViewById(R.id.text_description);
            textPaymentStatus = itemView.findViewById(R.id.text_payment_status);
            cardPaymentStatus = itemView.findViewById(R.id.card_payment_status);
            timelineLineTop = itemView.findViewById(R.id.timeline_line_top);
            timelineLineBottom = itemView.findViewById(R.id.timeline_line_bottom);
        }
    }
}
